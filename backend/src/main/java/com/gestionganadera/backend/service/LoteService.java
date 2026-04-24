package com.gestionganadera.backend.service;

import com.gestionganadera.backend.model.Lote;
import com.gestionganadera.backend.repository.LoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
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

    public Optional<Lote> findById(@NonNull Integer id) {
        return loteRepository.findById(id);
    }

    public Lote save(@NonNull Lote lote) {
        return loteRepository.save(lote);
    }

    public Lote update(@NonNull Integer id, @NonNull Lote lote) {
        return loteRepository.findById(id)
                .map(existing -> {
                    existing.setNombre(lote.getNombre());
                    existing.setEspecie(lote.getEspecie());
                    existing.setCantidad(lote.getCantidad());
                    existing.setFechaIngreso(lote.getFechaIngreso());
                    existing.setEstado(lote.getEstado());
                    return loteRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Lote no encontrado"));
    }

    public void delete(@NonNull Integer id) {
        loteRepository.deleteById(id);
    }
}
