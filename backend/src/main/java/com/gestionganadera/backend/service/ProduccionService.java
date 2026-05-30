package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateProduccionRequest;
import com.gestionganadera.backend.dto.ProduccionDTO;
import com.gestionganadera.backend.dto.ProduccionResumenDTO;
import com.gestionganadera.backend.model.Animal;
import com.gestionganadera.backend.model.Produccion;
import com.gestionganadera.backend.model.TurnoProduccion;
import com.gestionganadera.backend.repository.AnimalRepository;
import com.gestionganadera.backend.repository.ProduccionRepository;
import com.gestionganadera.backend.repository.TurnoProduccionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProduccionService {
    private final ProduccionRepository repository;
    private final AnimalRepository animalRepository;
    private final TurnoProduccionRepository turnoProduccionRepository;

    public List<ProduccionDTO> findAll() {
        return repository.findAll().stream()
                .map(this::toDTO).collect(Collectors.toList());
    }

    public List<ProduccionDTO> findByAnimalId(@NonNull Integer animalId) {
        return repository.findByAnimalId(animalId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Produccion findById(@NonNull Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producción no encontrada"));
    }

    @Transactional
    public ProduccionDTO create(@NonNull CreateProduccionRequest request) {
        Animal animal = animalRepository.findById(request.getAnimalId())
                .orElseThrow(() -> new EntityNotFoundException("Animal no encontrado"));

        Produccion entity = new Produccion();
        entity.setAnimal(animal);
        entity.setLitros(request.getLitros());
        if (request.getTurnoProduccionId() != null) {
            entity.setTurnoProduccion(turnoProduccionRepository.findById(request.getTurnoProduccionId())
                    .orElseThrow(() -> new EntityNotFoundException("Turno de producción no encontrado")));
        }
        entity.setFecha(request.getFecha());
        return toDTO(repository.save(entity));
    }

    @Transactional
    public ProduccionDTO update(@NonNull Integer id, @NonNull CreateProduccionRequest request) {
        Produccion entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producción no encontrada"));

        if (request.getAnimalId() != null && !request.getAnimalId().equals(entity.getAnimal().getId())) {
            entity.setAnimal(animalRepository.findById(request.getAnimalId())
                    .orElseThrow(() -> new EntityNotFoundException("Animal no encontrado")));
        }
        if (request.getLitros() != null) entity.setLitros(request.getLitros());
        if (request.getTurnoProduccionId() != null) {
            entity.setTurnoProduccion(turnoProduccionRepository.findById(request.getTurnoProduccionId())
                    .orElseThrow(() -> new EntityNotFoundException("Turno de producción no encontrado")));
        }
        if (request.getFecha() != null) entity.setFecha(request.getFecha());

        return toDTO(repository.save(entity));
    }

    @Transactional
    public void delete(@NonNull Integer id) {
        Produccion entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producción no encontrada"));
        repository.deleteById(id);
    }

    public java.math.BigDecimal getPromedioProduccionDiaria() {
        return repository.getPromedioProduccionDiaria();
    }


    public ProduccionDTO toDTO(Produccion p) {
        ProduccionDTO dto = new ProduccionDTO();
        dto.setId(p.getId());
        dto.setAnimalId(p.getAnimal() != null ? p.getAnimal().getId() : null);
        dto.setAnimalNombre(p.getAnimal() != null ? p.getAnimal().getNombre() : null);
        dto.setAnimalArete(p.getAnimal() != null ? p.getAnimal().getIdentificadorArete() : null);
        dto.setLitros(p.getLitros());
        dto.setTurno(p.getTurnoProduccion() != null ? p.getTurnoProduccion().getNombre() : null);
        dto.setFecha(p.getFecha());
        return dto;
    }
}
