package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateEventoRequest;
import com.gestionganadera.backend.dto.EventoDTO;
import com.gestionganadera.backend.model.Animal;
import com.gestionganadera.backend.model.Finca;
import com.gestionganadera.backend.model.Evento;
import com.gestionganadera.backend.model.Usuario;
import com.gestionganadera.backend.repository.AnimalRepository;
import com.gestionganadera.backend.repository.EventoRepository;
import com.gestionganadera.backend.repository.FincaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventoService {
    private final EventoRepository repository;
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

    public List<EventoDTO> getRecent() {
        List<Integer> fincaIds = getUserFincaIds();
        if (fincaIds.isEmpty()) return List.of();

        return repository.findTop10ByAnimalFincaIdInOrderByFechaDesc(fincaIds)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<Evento> findByAnimalId(@NonNull Integer animalId) {
        getAuthorizedAnimal(animalId);
        return repository.findByAnimalId(animalId);
    }

    public Evento save(@NonNull CreateEventoRequest request) {
        Animal animal = getAuthorizedAnimal(request.getAnimalId());

        Evento entity = new Evento();
        entity.setAnimal(animal);
        entity.setTipo(request.getTipo());
        entity.setDescripcion(request.getDescripcion());
        return repository.save(entity);
    }

    public void delete(@NonNull Integer id) {
        Evento entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Evento no encontrado"));
        getAuthorizedAnimal(entity.getAnimal().getId());
        repository.deleteById(id);
    }

    private EventoDTO toDTO(Evento e) {
        EventoDTO dto = new EventoDTO();
        dto.setId(e.getId());
        dto.setFecha(e.getFecha() != null ? e.getFecha().toString() : null);
        dto.setTipo(e.getTipo());
        dto.setDescripcion(e.getDescripcion());
        dto.setAnimalNombre(e.getAnimal() != null ? e.getAnimal().getNombre() : null);
        dto.setAnimalArete(e.getAnimal() != null ? e.getAnimal().getIdentificadorArete() : null);
        return dto;
    }
}
