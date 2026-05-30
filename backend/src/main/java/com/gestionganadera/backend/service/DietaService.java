package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateDietaRequest;
import com.gestionganadera.backend.dto.DietaDTO;
import com.gestionganadera.backend.model.Dieta;
import com.gestionganadera.backend.repository.DietaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DietaService {

    private final DietaRepository repository;

    public List<DietaDTO> findAll() {
        return repository.findAll().stream()
                .map(DietaDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public DietaDTO findById(@NonNull Integer id) {
        return repository.findById(id)
                .map(DietaDTO::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("Dieta no encontrada"));
    }

    @Transactional
    public DietaDTO create(@NonNull CreateDietaRequest request) {
        Dieta entity = new Dieta();
        entity.setNombre(request.getNombre());
        entity.setDescripcion(request.getDescripcion());
        return DietaDTO.fromEntity(repository.save(entity));
    }

    @Transactional
    public DietaDTO update(@NonNull Integer id, @NonNull CreateDietaRequest request) {
        Dieta entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dieta no encontrada"));
        entity.setNombre(request.getNombre());
        entity.setDescripcion(request.getDescripcion());
        return DietaDTO.fromEntity(repository.save(entity));
    }

    @Transactional
    public void delete(@NonNull Integer id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Dieta no encontrada");
        }
        repository.deleteById(id);
    }
}
