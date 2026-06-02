package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateFincaRequest;
import com.gestionganadera.backend.dto.FincaDTO;
import com.gestionganadera.backend.dto.FincaStatsDTO;
import com.gestionganadera.backend.model.Finca;
import com.gestionganadera.backend.model.Lote;
import com.gestionganadera.backend.repository.AnimalRepository;
import com.gestionganadera.backend.repository.FincaRepository;
import com.gestionganadera.backend.repository.LoteRepository;
import com.gestionganadera.backend.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FincaService {

    private final FincaRepository fincaRepository;
    private final AnimalRepository animalRepository;
    private final LoteRepository loteRepository;
    private final UserContext userContext;

    public List<FincaDTO> findAll() {
        Integer userId = userContext.getCurrentUserId();
        return fincaRepository.findByUsuarioId(userId).stream()
                .map(FincaDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public Optional<FincaDTO> findById(@NonNull Integer id) {
        return fincaRepository.findById(id)
                .map(FincaDTO::fromEntity);
    }

    @Transactional
    public FincaDTO save(@NonNull CreateFincaRequest request) {
        Finca finca = new Finca();
        finca.setNombre(request.getNombre());
        finca.setUbicacion(request.getUbicacion());
        finca.setExtension(request.getExtension());
        finca.setUsuario(userContext.getCurrentUser());
        return FincaDTO.fromEntity(fincaRepository.save(finca));
    }

    @Transactional
    public FincaDTO update(@NonNull Integer id, @NonNull CreateFincaRequest request) {
        return fincaRepository.findById(id)
                .map(existing -> {
                    Integer userId = userContext.getCurrentUserId();
                    if (!existing.getUsuario().getId().equals(userId)) {
                        throw new IllegalArgumentException("No tienes permiso para actualizar esta finca");
                    }
                    existing.setNombre(request.getNombre());
                    existing.setUbicacion(request.getUbicacion());
                    existing.setExtension(request.getExtension());
                    return FincaDTO.fromEntity(fincaRepository.save(existing));
                })
                .orElseThrow(() -> new RuntimeException("Finca no encontrada"));
    }

    @Transactional
    public void delete(@NonNull Integer id) {
        Finca finca = fincaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Finca no encontrada"));
        Integer userId = userContext.getCurrentUserId();
        if (!finca.getUsuario().getId().equals(userId)) {
            throw new IllegalArgumentException("No tienes permiso para eliminar esta finca");
        }
        fincaRepository.deleteById(id);
    }

    public FincaStatsDTO getStats(@NonNull Integer id) {
        fincaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Finca no encontrada"));

        List<Lote> lotes = loteRepository.findByFincaId(id);

        FincaStatsDTO stats = new FincaStatsDTO();
        stats.setTotalLotes(lotes.size());
        return stats;
    }
}
