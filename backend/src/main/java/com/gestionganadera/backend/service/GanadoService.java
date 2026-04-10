package com.gestionganadera.backend.service;

import com.gestionganadera.backend.model.Ganado;
import com.gestionganadera.backend.repository.GanadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GanadoService {

    private final GanadoRepository ganadoRepository;

    public List<Ganado> findAll() {
        return ganadoRepository.findAll();
    }

    public Optional<Ganado> findById(String id) {
        return ganadoRepository.findById(id);
    }

    public Ganado save(Ganado ganado) {
        return ganadoRepository.save(ganado);
    }

    public Ganado update(String id, Ganado ganado) {
        return ganadoRepository.findById(id)
                .map(existing -> {
                    existing.setIdentificador(ganado.getIdentificador());
                    existing.setRaza(ganado.getRaza());
                    existing.setSexo(ganado.getSexo());
                    existing.setFechaNacimiento(ganado.getFechaNacimiento());
                    existing.setPeso(ganado.getPeso());
                    existing.setEstado(ganado.getEstado());
                    existing.setLote(ganado.getLote());
                    existing.setFotoUrl(ganado.getFotoUrl());
                    return ganadoRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Ganado no encontrado"));
    }

    public void delete(String id) {
        ganadoRepository.deleteById(id);
    }
}
