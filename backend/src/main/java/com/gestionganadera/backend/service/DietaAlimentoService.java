package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateDietaAlimentoRequest;
import com.gestionganadera.backend.dto.DietaAlimentoDTO;
import com.gestionganadera.backend.model.Alimento;
import com.gestionganadera.backend.model.Dieta;
import com.gestionganadera.backend.model.DietaAlimento;
import com.gestionganadera.backend.repository.AlimentoRepository;
import com.gestionganadera.backend.repository.DietaAlimentoRepository;
import com.gestionganadera.backend.repository.DietaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DietaAlimentoService {

    private final DietaAlimentoRepository repository;
    private final DietaRepository dietaRepository;
    private final AlimentoRepository alimentoRepository;

    public List<DietaAlimentoDTO> findAll() {
        return repository.findAll().stream()
                .map(DietaAlimentoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<DietaAlimentoDTO> findByDietaId(@NonNull Integer dietaId) {
        return repository.findByDietaId(dietaId).stream()
                .map(DietaAlimentoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public DietaAlimentoDTO findById(@NonNull Integer id) {
        return repository.findById(id)
                .map(DietaAlimentoDTO::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("DietaAlimento no encontrado"));
    }

    @Transactional
    public DietaAlimentoDTO create(@NonNull CreateDietaAlimentoRequest request) {
        Dieta dieta = dietaRepository.findById(request.getDietaId())
                .orElseThrow(() -> new EntityNotFoundException("Dieta no encontrada"));
        Alimento alimento = alimentoRepository.findById(request.getAlimentoId())
                .orElseThrow(() -> new EntityNotFoundException("Alimento no encontrado"));

        DietaAlimento entity = new DietaAlimento();
        entity.setDieta(dieta);
        entity.setAlimento(alimento);
        entity.setCantidad(request.getCantidad());
        entity.setUnidad(request.getUnidad());
        return DietaAlimentoDTO.fromEntity(repository.save(entity));
    }

    @Transactional
    public DietaAlimentoDTO update(@NonNull Integer id, @NonNull CreateDietaAlimentoRequest request) {
        DietaAlimento entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("DietaAlimento no encontrado"));

        if (request.getDietaId() != null && !request.getDietaId().equals(entity.getDieta().getId())) {
            entity.setDieta(dietaRepository.findById(request.getDietaId())
                    .orElseThrow(() -> new EntityNotFoundException("Dieta no encontrada")));
        }
        if (request.getAlimentoId() != null && !request.getAlimentoId().equals(entity.getAlimento().getId())) {
            entity.setAlimento(alimentoRepository.findById(request.getAlimentoId())
                    .orElseThrow(() -> new EntityNotFoundException("Alimento no encontrado")));
        }
        if (request.getCantidad() != null) entity.setCantidad(request.getCantidad());
        if (request.getUnidad() != null) entity.setUnidad(request.getUnidad());

        return DietaAlimentoDTO.fromEntity(repository.save(entity));
    }

    @Transactional
    public void delete(@NonNull Integer id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("DietaAlimento no encontrado");
        }
        repository.deleteById(id);
    }

    @Transactional
    public void deleteByDietaId(@NonNull Integer dietaId) {
        repository.deleteByDietaId(dietaId);
    }
}
