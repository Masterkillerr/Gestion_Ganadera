package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateLoteRequest;
import com.gestionganadera.backend.model.Finca;
import com.gestionganadera.backend.model.Lote;
import com.gestionganadera.backend.model.Usuario;
import com.gestionganadera.backend.repository.FincaRepository;
import com.gestionganadera.backend.repository.LoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoteService {

    private final LoteRepository loteRepository;
    private final FincaRepository fincaRepository;

    private Usuario getCurrentUser() {
        return (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
    }

    private List<Integer> getUserFincaIds() {
        return fincaRepository.findByPropietario(getCurrentUser())
                .stream().map(Finca::getId).collect(Collectors.toList());
    }

    public List<Lote> findAll() {
        return loteRepository.findByFincaIdIn(getUserFincaIds());
    }

    public Optional<Lote> findById(@NonNull Integer id) {
        return loteRepository.findByIdAndFincaIdIn(id, getUserFincaIds());
    }

    private Lote fromRequest(CreateLoteRequest request) {
        Lote lote = new Lote();
        lote.setNombre(request.getNombre());
        lote.setHectareas(request.getHectareas());
        lote.setCapacidadMaxima(request.getCapacidadMaxima());
        lote.setTipoPasto(request.getTipoPasto());
        lote.setEstado(request.getEstado());

        if (request.getFincaId() != null) {
            fincaRepository.findByIdAndPropietario(request.getFincaId(), getCurrentUser())
                    .ifPresentOrElse(lote::setFinca,
                        () -> { throw new RuntimeException("Finca no encontrada o no autorizada"); });
        }
        return lote;
    }

    public Lote save(@NonNull CreateLoteRequest request) {
        return loteRepository.save(fromRequest(request));
    }

    public Lote update(@NonNull Integer id, @NonNull CreateLoteRequest request) {
        List<Integer> userFincaIds = getUserFincaIds();
        return loteRepository.findByIdAndFincaIdIn(id, userFincaIds)
                .map(existing -> {
                    existing.setNombre(request.getNombre());
                    existing.setHectareas(request.getHectareas());
                    existing.setCapacidadMaxima(request.getCapacidadMaxima());
                    existing.setTipoPasto(request.getTipoPasto());
                    existing.setEstado(request.getEstado());
                    if (request.getFincaId() != null) {
                        fincaRepository.findByIdAndPropietario(request.getFincaId(), getCurrentUser())
                                .ifPresentOrElse(existing::setFinca,
                                    () -> { throw new RuntimeException("Finca no encontrada o no autorizada"); });
                    }
                    return loteRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Lote no encontrado"));
    }

    public void delete(@NonNull Integer id) {
        loteRepository.findByIdAndFincaIdIn(id, getUserFincaIds())
                .ifPresentOrElse(
                    lote -> loteRepository.deleteById(id),
                    () -> { throw new RuntimeException("Lote no encontrado"); }
                );
    }

    public List<Lote> findByFincaId(@NonNull Integer fincaId) {
        Usuario currentUser = getCurrentUser();
        return fincaRepository.findByIdAndPropietario(fincaId, currentUser)
                .map(finca -> loteRepository.findByFincaId(fincaId))
                .orElseThrow(() -> new RuntimeException("Finca no encontrada o no autorizada"));
    }
}
