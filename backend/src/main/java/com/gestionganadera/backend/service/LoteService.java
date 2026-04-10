package com.gestionganadera.backend.service;

import com.gestionganadera.backend.model.Lote;
import com.gestionganadera.backend.repository.LoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoteService {

    private final LoteRepository loteRepository;

    public List<Lote> findAll() {
        return loteRepository.findAll();
    }

    public Optional<Lote> findById(String id) {
        return loteRepository.findById(id);
    }

    public Lote save(Lote lote) {
        return loteRepository.save(lote);
    }

    public Lote update(String id, Lote lote) {
        return loteRepository.findById(id)
                .map(existing -> {
                    existing.setNombre(lote.getNombre());
                    existing.setCapacidad(lote.getCapacidad());
                    existing.setTipoPasto(lote.getTipoPasto());
                    existing.setArea(lote.getArea());
                    return loteRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Lote no encontrado"));
    }

    public void delete(String id) {
        loteRepository.deleteById(id);
    }
}
