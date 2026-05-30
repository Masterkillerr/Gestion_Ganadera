package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateDietaRequest;
import com.gestionganadera.backend.model.Dieta;
import com.gestionganadera.backend.repository.DietaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DietaService {

    private final DietaRepository repository;

    public List<Dieta> findAll() {
        return repository.findAll();
    }

    public Dieta findById(@NonNull Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dieta no encontrada"));
    }

    @Transactional
    public Dieta create(@NonNull CreateDietaRequest request) {
        Dieta entity = new Dieta();
        entity.setNombre(request.getNombre());
        entity.setDescripcion(request.getDescripcion());
        return repository.save(entity);
    }

    @Transactional
    public Dieta update(@NonNull Integer id, @NonNull CreateDietaRequest request) {
        Dieta entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dieta no encontrada"));
        entity.setNombre(request.getNombre());
        entity.setDescripcion(request.getDescripcion());
        return repository.save(entity);
    }

    @Transactional
    public void delete(@NonNull Integer id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Dieta no encontrada");
        }
        repository.deleteById(id);
    }
}
