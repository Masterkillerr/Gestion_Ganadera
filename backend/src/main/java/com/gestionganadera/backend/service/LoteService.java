package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateLoteRequest;
import com.gestionganadera.backend.model.Lote;
import com.gestionganadera.backend.repository.FincaRepository;
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
    private final FincaRepository fincaRepository;

    public List<Lote> findAll() {
        return loteRepository.findAll();
    }

    public Optional<Lote> findById(@NonNull Integer id) {
        return loteRepository.findById(id);
    }

    private Lote fromRequest(CreateLoteRequest request) {
        Lote lote = new Lote();
        lote.setNombre(request.getNombre());
        lote.setHectareas(request.getHectareas());
        lote.setCapacidadMaxima(request.getCapacidadMaxima());
        lote.setTipoPasto(request.getTipoPasto());
        lote.setEstado(request.getEstado());

        if (request.getFincaId() != null) {
            fincaRepository.findById(request.getFincaId())
                    .ifPresentOrElse(lote::setFinca,
                        () -> { throw new RuntimeException("Finca no encontrada"); });
        }
        return lote;
    }

    public Lote save(@NonNull CreateLoteRequest request) {
        return loteRepository.save(fromRequest(request));
    }

    public Lote update(@NonNull Integer id, @NonNull CreateLoteRequest request) {
        return loteRepository.findById(id)
                .map(existing -> {
                    existing.setNombre(request.getNombre());
                    existing.setHectareas(request.getHectareas());
                    existing.setCapacidadMaxima(request.getCapacidadMaxima());
                    existing.setTipoPasto(request.getTipoPasto());
                    existing.setEstado(request.getEstado());
                    if (request.getFincaId() != null) {
                        fincaRepository.findById(request.getFincaId())
                                .ifPresentOrElse(existing::setFinca,
                                    () -> { throw new RuntimeException("Finca no encontrada"); });
                    }
                    return loteRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Lote no encontrado"));
    }

    public void delete(@NonNull Integer id) {
        loteRepository.findById(id)
                .ifPresentOrElse(
                    lote -> loteRepository.deleteById(id),
                    () -> { throw new RuntimeException("Lote no encontrado"); }
                );
    }

    public List<Lote> findByFincaId(@NonNull Integer fincaId) {
        return fincaRepository.findById(fincaId)
                .map(finca -> loteRepository.findByFincaId(fincaId))
                .orElseThrow(() -> new RuntimeException("Finca no encontrada"));
    }
}
