package com.gestionganadera.backend.service;

import com.gestionganadera.backend.model.Alimentacion;
import com.gestionganadera.backend.repository.AlimentacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlimentacionService {
    private final AlimentacionRepository repository;

    public List<Alimentacion> findByAnimalId(@NonNull Integer animalId) {
        return repository.findByAnimalId(animalId);
    }

    public Alimentacion save(@NonNull Alimentacion entity) {
        return repository.save(entity);
    }

    public void delete(@NonNull Integer id) {
        repository.deleteById(id);
    }
}
