package com.gestionganadera.backend.service;

import com.gestionganadera.backend.model.Produccion;
import com.gestionganadera.backend.repository.ProduccionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProduccionService {
    private final ProduccionRepository repository;

    public List<Produccion> findByAnimalId(@NonNull Integer animalId) {
        return repository.findByAnimalId(animalId);
    }

    public Produccion save(@NonNull Produccion entity) {
        return repository.save(entity);
    }

    public void delete(@NonNull Integer id) {
        repository.deleteById(id);
    }
}
