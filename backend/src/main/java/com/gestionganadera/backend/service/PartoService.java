package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.PartoDTO;
import com.gestionganadera.backend.model.Parto;
import com.gestionganadera.backend.model.Reproduccion;
import com.gestionganadera.backend.model.Usuario;
import com.gestionganadera.backend.repository.PartoRepository;
import com.gestionganadera.backend.repository.ReproduccionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartoService {

    private final PartoRepository partoRepository;
    private final ReproduccionService reproduccionService;
    private final ReproduccionRepository reproduccionRepository;

    private Usuario getCurrentUser() {
        return (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
    }

    private boolean canAccessReproduccion(Integer reproduccionId) {
        try {
            reproduccionService.findById(reproduccionId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<PartoDTO> findAll() {
        return partoRepository.findAll().stream()
                .filter(p -> {
                    try {
                        reproduccionService.findById(p.getReproduccion().getId());
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                })
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public PartoDTO findById(Integer id) {
        Parto p = partoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Parto no encontrado"));
        reproduccionService.findById(p.getReproduccion().getId());
        return toDTO(p);
    }

    public List<PartoDTO> findByReproduccionId(Integer reproduccionId) {
        reproduccionService.findById(reproduccionId);
        return partoRepository.findByReproduccionId(reproduccionId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PartoDTO create(com.gestionganadera.backend.dto.CreatePartoRequest request) {
        canAccessReproduccion(request.getReproduccionId());

        Reproduccion r = reproduccionRepository.findById(request.getReproduccionId())
                .orElseThrow(() -> new EntityNotFoundException("Reproducción no encontrada"));

        Parto p = new Parto();
        p.setReproduccion(r);
        p.setFechaParto(request.getFechaParto());
        p.setCantidadCrias(request.getCantidadCrias());
        p.setObservaciones(request.getObservaciones());

        return toDTO(partoRepository.save(p));
    }

    @Transactional
    public PartoDTO update(Integer id, com.gestionganadera.backend.dto.CreatePartoRequest request) {
        Parto p = partoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Parto no encontrado"));
        reproduccionService.findById(p.getReproduccion().getId());

        if (request.getFechaParto() != null) p.setFechaParto(request.getFechaParto());
        if (request.getCantidadCrias() != null) p.setCantidadCrias(request.getCantidadCrias());
        if (request.getObservaciones() != null) p.setObservaciones(request.getObservaciones());

        return toDTO(partoRepository.save(p));
    }

    @Transactional
    public void delete(Integer id) {
        Parto p = partoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Parto no encontrado"));
        reproduccionService.findById(p.getReproduccion().getId());
        partoRepository.delete(p);
    }

    private PartoDTO toDTO(Parto p) {
        PartoDTO dto = new PartoDTO();
        dto.setId(p.getId());
        dto.setReproduccionId(p.getReproduccion().getId());
        dto.setVacaNombre(p.getReproduccion().getVaca() != null ? p.getReproduccion().getVaca().getNombre() : null);
        dto.setVacaArete(p.getReproduccion().getVaca() != null ? p.getReproduccion().getVaca().getIdentificadorArete() : null);
        dto.setFechaParto(p.getFechaParto());
        dto.setCantidadCrias(p.getCantidadCrias());
        dto.setObservaciones(p.getObservaciones());
        return dto;
    }
}
