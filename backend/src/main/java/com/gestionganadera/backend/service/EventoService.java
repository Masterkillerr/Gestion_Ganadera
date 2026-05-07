package com.gestionganadera.backend.service;

import com.gestionganadera.backend.model.Evento;
import com.gestionganadera.backend.repository.EventoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventoService {
    private final EventoRepository repository;

    public List<Evento> findByAnimalId(@NonNull Integer animalId) {
        return repository.findByAnimalId(animalId);
    }

    public Evento save(@NonNull Evento entity) {
        return repository.save(entity);
    }

    public void delete(@NonNull Integer id) {
        repository.deleteById(id);
    }
}
