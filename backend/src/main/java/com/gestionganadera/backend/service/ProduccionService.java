package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateProduccionRequest;
import com.gestionganadera.backend.dto.ProduccionDTO;
import com.gestionganadera.backend.dto.ProduccionResumenDTO;
import com.gestionganadera.backend.model.Animal;
import com.gestionganadera.backend.model.Finca;
import com.gestionganadera.backend.model.Produccion;
import com.gestionganadera.backend.model.Usuario;
import com.gestionganadera.backend.repository.AnimalRepository;
import com.gestionganadera.backend.repository.FincaRepository;
import com.gestionganadera.backend.repository.ProduccionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final FincaRepository fincaRepository;

    private Usuario getCurrentUser() {
        return (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
    }

    private List<Integer> getUserFincaIds() {
        return fincaRepository.findByPropietario(getCurrentUser())
                .stream().map(Finca::getId).collect(Collectors.toList());
    }

    private Animal getAuthorizedAnimal(Integer animalId) {
        return animalRepository.findByIdAndFincaIdIn(animalId, getUserFincaIds())
                .orElseThrow(() -> new EntityNotFoundException("Animal no encontrado o no autorizado"));
    }

    public List<ProduccionDTO> findAll() {
        List<Integer> fincaIds = getUserFincaIds();
        if (fincaIds.isEmpty()) return List.of();

        List<Integer> animalIds = animalRepository.findByFincaIdIn(fincaIds)
                .stream().map(Animal::getId).collect(Collectors.toList());
        if (animalIds.isEmpty()) return List.of();

        return repository.findByAnimalIdInOrderByFechaDesc(animalIds)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<Produccion> findByAnimalId(@NonNull Integer animalId) {
        getAuthorizedAnimal(animalId);
        return repository.findByAnimalId(animalId);
    }

    @Transactional
    public ProduccionDTO create(@NonNull CreateProduccionRequest request) {
        Animal animal = getAuthorizedAnimal(request.getAnimalId());

        Produccion entity = new Produccion();
        entity.setAnimal(animal);
        entity.setLitros(request.getLitros());
        entity.setTurno(request.getTurno());
        entity.setFecha(request.getFecha());
        return toDTO(repository.save(entity));
    }

    @Transactional
    public ProduccionDTO update(@NonNull Integer id, @NonNull CreateProduccionRequest request) {
        Produccion entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producción no encontrada"));
        getAuthorizedAnimal(entity.getAnimal().getId());

        if (request.getLitros() != null) entity.setLitros(request.getLitros());
        if (request.getTurno() != null) entity.setTurno(request.getTurno());
        if (request.getFecha() != null) entity.setFecha(request.getFecha());

        // If animal changed, verify authorization
        if (request.getAnimalId() != null && !request.getAnimalId().equals(entity.getAnimal().getId())) {
            Animal newAnimal = getAuthorizedAnimal(request.getAnimalId());
            entity.setAnimal(newAnimal);
        }

        return toDTO(repository.save(entity));
    }

    @Transactional
    public void delete(@NonNull Integer id) {
        Produccion entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produccion no encontrada"));
        getAuthorizedAnimal(entity.getAnimal().getId());
        repository.deleteById(id);
    }

    public List<ProduccionResumenDTO> getResumen(@NonNull Integer year) {
        List<Integer> fincaIds = getUserFincaIds();
        if (fincaIds.isEmpty()) return List.of();

        List<Animal> animales = animalRepository.findByFincaIdIn(fincaIds);
        if (animales.isEmpty()) return List.of();

        List<Integer> animalIds = animales.stream().map(Animal::getId).collect(Collectors.toList());
        List<Produccion> all = repository.findByAnimalIdIn(animalIds);

        Map<Integer, BigDecimal> monthlyTotals = new HashMap<>();
        Map<Integer, Long> monthlyCounts = new HashMap<>();

        for (Produccion p : all) {
            if (p.getFecha() != null && p.getFecha().getYear() == year && p.getLitros() != null) {
                int month = p.getFecha().getMonthValue();
                monthlyTotals.merge(month, p.getLitros(), BigDecimal::add);
                monthlyCounts.merge(month, 1L, Long::sum);
            }
        }

        return monthlyTotals.entrySet().stream()
                .map(e -> new ProduccionResumenDTO(year, e.getKey(), e.getValue(), monthlyCounts.getOrDefault(e.getKey(), 0L)))
                .sorted(Comparator.comparingInt(ProduccionResumenDTO::getMonth))
                .collect(Collectors.toList());
    }

    private ProduccionDTO toDTO(Produccion p) {
        ProduccionDTO dto = new ProduccionDTO();
        dto.setId(p.getId());
        dto.setAnimalId(p.getAnimal() != null ? p.getAnimal().getId() : null);
        dto.setAnimalNombre(p.getAnimal() != null ? p.getAnimal().getNombre() : null);
        dto.setAnimalArete(p.getAnimal() != null ? p.getAnimal().getIdentificadorArete() : null);
        dto.setLitros(p.getLitros());
        dto.setTurno(p.getTurno());
        dto.setFecha(p.getFecha());
        return dto;
    }
}
