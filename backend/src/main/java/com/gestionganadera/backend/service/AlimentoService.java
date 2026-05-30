package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateAlimentoRequest;
import com.gestionganadera.backend.model.Alimento;
import com.gestionganadera.backend.repository.AlimentoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlimentoService {

    private final AlimentoRepository repository;

    public List<Alimento> findAll() {
        return repository.findAll();
    }

    public Alimento findById(@NonNull Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Alimento no encontrado"));
    }

    @Transactional
    public Alimento create(@NonNull CreateAlimentoRequest request) {
        Alimento entity = new Alimento();
        entity.setNombre(request.getNombre());
        return repository.save(entity);
    }

    @Transactional
    public Alimento update(@NonNull Integer id, @NonNull CreateAlimentoRequest request) {
        Alimento entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Alimento no encontrado"));
        entity.setNombre(request.getNombre());
        return repository.save(entity);
    }

    @Transactional
    public void delete(@NonNull Integer id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Alimento no encontrado");
        }
        repository.deleteById(id);
    }
}
