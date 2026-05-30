package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateFincaRequest;
import com.gestionganadera.backend.dto.FincaStatsDTO;
import com.gestionganadera.backend.model.Animal;
import com.gestionganadera.backend.model.Finca;
import com.gestionganadera.backend.model.Lote;
import com.gestionganadera.backend.repository.AnimalRepository;
import com.gestionganadera.backend.repository.FincaRepository;
import com.gestionganadera.backend.repository.LoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FincaService {

    private final FincaRepository fincaRepository;
    private final AnimalRepository animalRepository;
    private final LoteRepository loteRepository;

    public List<Finca> findAll() {
        return fincaRepository.findAll();
    }

    public Optional<Finca> findById(@NonNull Integer id) {
        return fincaRepository.findById(id);
    }

    public Finca save(@NonNull CreateFincaRequest request) {
        Finca finca = new Finca();
        finca.setNombre(request.getNombre());
        finca.setUbicacion(request.getUbicacion());
        finca.setExtension(request.getExtension());
        return fincaRepository.save(finca);
    }

    public Finca update(@NonNull Integer id, @NonNull CreateFincaRequest request) {
        return fincaRepository.findById(id)
                .map(existing -> {
                    existing.setNombre(request.getNombre());
                    existing.setUbicacion(request.getUbicacion());
                    existing.setExtension(request.getExtension());
                    return fincaRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Finca no encontrada"));
    }

    public void delete(@NonNull Integer id) {
        fincaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Finca no encontrada"));
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
