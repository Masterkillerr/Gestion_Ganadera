package com.gestionganadera.backend.service;

import com.gestionganadera.backend.model.Finca;
import com.gestionganadera.backend.repository.FincaRepository;
import lombok.RequiredArgsConstructor;
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

    public Optional<Finca> findById(String id) {
        return fincaRepository.findById(id);
    }

    public Finca save(Finca finca) {
        return fincaRepository.save(finca);
    }

    public Finca update(String id, Finca finca) {
        return fincaRepository.findById(id)
                .map(existing -> {
                    existing.setNombre(finca.getNombre());
                    existing.setUbicacion(finca.getUbicacion());
                    existing.setArea(finca.getArea());
                    existing.setEncargado(finca.getEncargado());
                    return fincaRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Finca no encontrada"));
    }

    public void delete(String id) {
        fincaRepository.deleteById(id);
    }
}
