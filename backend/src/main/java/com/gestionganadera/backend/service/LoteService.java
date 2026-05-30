package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateLoteRequest;
import com.gestionganadera.backend.dto.LoteDTO;
import com.gestionganadera.backend.model.Lote;
import com.gestionganadera.backend.repository.FincaRepository;
import com.gestionganadera.backend.repository.LoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoteService {

    private final LoteRepository loteRepository;
    private final FincaRepository fincaRepository;

    public List<LoteDTO> findAll() {
        return loteRepository.findAll().stream()
                .map(LoteDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public Optional<LoteDTO> findById(@NonNull Integer id) {
        return loteRepository.findById(id)
                .map(LoteDTO::fromEntity);
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

    public LoteDTO save(@NonNull CreateLoteRequest request) {
        return LoteDTO.fromEntity(loteRepository.save(fromRequest(request)));
    }

    public LoteDTO update(@NonNull Integer id, @NonNull CreateLoteRequest request) {
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
                    return LoteDTO.fromEntity(loteRepository.save(existing));
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

    public List<LoteDTO> findByFincaId(@NonNull Integer fincaId) {
        return fincaRepository.findById(fincaId)
                .map(finca -> loteRepository.findByFincaId(fincaId).stream()
                        .map(LoteDTO::fromEntity)
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new RuntimeException("Finca no encontrada"));
    }
}
