package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.AlimentacionDTO;
import com.gestionganadera.backend.dto.CreateAlimentacionRequest;
import com.gestionganadera.backend.model.Alimentacion;
import com.gestionganadera.backend.model.Animal;
import com.gestionganadera.backend.model.Dieta;
import com.gestionganadera.backend.repository.AlimentacionRepository;
import com.gestionganadera.backend.repository.AnimalRepository;
import com.gestionganadera.backend.repository.DietaRepository;
import com.gestionganadera.backend.util.UserContext;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlimentacionService {
    private final AlimentacionRepository repository;
    private final AnimalRepository animalRepository;
    private final DietaRepository dietaRepository;
    private final UserContext userContext;

    public List<AlimentacionDTO> findByAnimalId(Integer animalId) {
        return repository.findByAnimalId(animalId).stream()
                .map(AlimentacionDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public Page<AlimentacionDTO> findAll(Pageable pageable) {
        Integer userId = userContext.getCurrentUserId();
        return repository.findByUsuarioId(userId, pageable).map(AlimentacionDTO::fromEntity);
    }

    public AlimentacionDTO findById(@NonNull Integer id) {
        return repository.findById(id)
                .map(AlimentacionDTO::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("Alimentación no encontrada"));
    }

    public AlimentacionDTO save(@NonNull CreateAlimentacionRequest request) {
        Animal animal = animalRepository.findById(request.getAnimalId())
                .orElseThrow(() -> new EntityNotFoundException("Animal no encontrado: " + request.getAnimalId()));
        Dieta dieta = null;
        if (request.getDietaId() != null) {
            dieta = dietaRepository.findById(request.getDietaId())
                    .orElseThrow(() -> new EntityNotFoundException("Dieta no encontrada: " + request.getDietaId()));
        }

        Alimentacion entity = new Alimentacion();
        entity.setAnimal(animal);
        entity.setDieta(dieta);
        entity.setFecha(request.getFecha() != null ? request.getFecha() : LocalDateTime.now());
        entity.setObservacion(request.getObservacion());
        entity.setUsuario(userContext.getCurrentUser());
        return AlimentacionDTO.fromEntity(repository.save(entity));
    }

    @NonNull
    public AlimentacionDTO update(@NonNull Integer id, @NonNull CreateAlimentacionRequest request) {
        Alimentacion entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Alimentación no encontrada"));

        Integer userId = userContext.getCurrentUserId();
        if (!entity.getUsuario().getId().equals(userId)) {
            throw new IllegalArgumentException("No tienes permiso para actualizar esta alimentación");
        }

        if (request.getAnimalId() != null) {
            entity.setAnimal(animalRepository.findById(request.getAnimalId())
                    .orElseThrow(() -> new EntityNotFoundException("Animal no encontrado: " + request.getAnimalId())));
        }
        if (request.getDietaId() != null) {
            entity.setDieta(dietaRepository.findById(request.getDietaId())
                    .orElseThrow(() -> new EntityNotFoundException("Dieta no encontrada: " + request.getDietaId())));
        }
        if (request.getFecha() != null) entity.setFecha(request.getFecha());
        if (request.getObservacion() != null) entity.setObservacion(request.getObservacion());

        return AlimentacionDTO.fromEntity(repository.save(entity));
    }

    public void delete(@NonNull Integer id) {
        Alimentacion entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Alimentación no encontrada"));
        Integer userId = userContext.getCurrentUserId();
        if (!entity.getUsuario().getId().equals(userId)) {
            throw new IllegalArgumentException("No tienes permiso para eliminar esta alimentación");
        }
        repository.deleteById(id);
    }
}
