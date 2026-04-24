package com.gestionganadera.backend.service;

import com.gestionganadera.backend.model.Finca;
import com.gestionganadera.backend.repository.FincaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FincaService {

    private final FincaRepository fincaRepository;

    public List<Finca> findAll() {
        return fincaRepository.findAll();
    }

    public Optional<Finca> findById(@NonNull Integer id) {
        return fincaRepository.findById(id);
    }

    public Finca save(@NonNull Finca finca) {
        return fincaRepository.save(finca);
    }

    public Finca update(@NonNull Integer id, @NonNull Finca finca) {
        return fincaRepository.findById(id)
                .map(existing -> {
                    existing.setNombre(finca.getNombre());
                    existing.setUbicacion(finca.getUbicacion());
                    return fincaRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Finca no encontrada"));
    }

    public void delete(@NonNull Integer id) {
        fincaRepository.deleteById(id);
    }
}
