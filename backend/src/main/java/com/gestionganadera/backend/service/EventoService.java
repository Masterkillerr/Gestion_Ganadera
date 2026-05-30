package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateEventoRequest;
import com.gestionganadera.backend.dto.EventoDTO;
import com.gestionganadera.backend.model.Animal;
import com.gestionganadera.backend.model.Evento;
import com.gestionganadera.backend.repository.AnimalRepository;
import com.gestionganadera.backend.repository.EventoRepository;
import com.gestionganadera.backend.repository.TipoEventoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventoService {
    private final EventoRepository repository;
    private final AnimalRepository animalRepository;
    private final TipoEventoRepository tipoEventoRepository;

    public List<EventoDTO> findAll() {
        return repository.findAll()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public Evento findById(@NonNull Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Evento no encontrado"));
    }

    public List<EventoDTO> findByAnimalId(@NonNull Integer animalId) {
        return repository.findByAnimalId(animalId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<EventoDTO> getRecent() {
        return findAll().stream()
                .sorted((a, b) -> {
                    if (a.getFecha() == null || b.getFecha() == null) return 0;
                    return b.getFecha().compareTo(a.getFecha());
                })
                .limit(20)
                .collect(Collectors.toList());
    }

    public Evento save(@NonNull CreateEventoRequest request) {
        Animal animal = animalRepository.findById(request.getAnimalId())
                .orElseThrow(() -> new EntityNotFoundException("Animal no encontrado"));

        Evento entity = new Evento();
        entity.setAnimal(animal);
        if (request.getTipoEventoId() != null) {
            tipoEventoRepository.findById(request.getTipoEventoId())
                    .ifPresent(entity::setTipoEvento);
        }
        entity.setDescripcion(request.getDescripcion());
        if (request.getFecha() != null) {
            entity.setFecha(request.getFecha());
        }
        return repository.save(entity);
    }

    public void delete(@NonNull Integer id) {
        Evento entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Evento no encontrado"));
        repository.deleteById(id);
    }

    public EventoDTO toDTO(Evento e) {
        EventoDTO dto = new EventoDTO();
        dto.setId(e.getId());
        dto.setFecha(e.getFecha() != null ? e.getFecha().toString() : null);
        String tipo = e.getTipoEvento() != null ? e.getTipoEvento().getNombre() : null;
        dto.setTipo(tipo);
        dto.setTipoEvento(tipo);
        dto.setDescripcion(e.getDescripcion());
        dto.setAnimalId(e.getAnimal() != null ? e.getAnimal().getId() : null);
        dto.setAnimalNombre(e.getAnimal() != null ? e.getAnimal().getNombre() : null);
        dto.setAnimalArete(e.getAnimal() != null ? e.getAnimal().getIdentificadorArete() : null);
        return dto;
    }
}
