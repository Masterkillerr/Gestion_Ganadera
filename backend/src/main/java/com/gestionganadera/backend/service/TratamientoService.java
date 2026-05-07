package com.gestionganadera.backend.service;

import com.gestionganadera.backend.model.Tratamiento;
import com.gestionganadera.backend.repository.TratamientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TratamientoService {
    private final TratamientoRepository repository;

    public List<Tratamiento> findByAnimalId(@NonNull Integer animalId) {
        return repository.findByAnimalId(animalId);
    }

    public Tratamiento save(@NonNull Tratamiento entity) {
        return repository.save(entity);
    }

    public void delete(@NonNull Integer id) {
        repository.deleteById(id);
    }
}
