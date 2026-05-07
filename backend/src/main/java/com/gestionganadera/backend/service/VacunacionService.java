package com.gestionganadera.backend.service;

import com.gestionganadera.backend.model.Vacunacion;
import com.gestionganadera.backend.repository.VacunacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VacunacionService {
    private final VacunacionRepository repository;

    public List<Vacunacion> findByAnimalId(@NonNull Integer animalId) {
        return repository.findByAnimalId(animalId);
    }

    public Vacunacion save(@NonNull Vacunacion entity) {
        return repository.save(entity);
    }

    public void delete(@NonNull Integer id) {
        repository.deleteById(id);
    }
}
