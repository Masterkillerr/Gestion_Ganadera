package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreatePartoRequest;
import com.gestionganadera.backend.dto.PartoDTO;
import com.gestionganadera.backend.model.Evento;
import com.gestionganadera.backend.model.Parto;
import com.gestionganadera.backend.model.Reproduccion;
import com.gestionganadera.backend.repository.EventoRepository;
import com.gestionganadera.backend.repository.PartoRepository;
import com.gestionganadera.backend.repository.ReproduccionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartoService {

    private final PartoRepository partoRepository;
    private final ReproduccionRepository reproduccionRepository;
    private final EventoRepository eventoRepository;

    public List<PartoDTO> findAll() {
        return partoRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public PartoDTO findById(Integer id) {
        Parto p = partoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Parto no encontrado"));
        return toDTO(p);
    }

    public List<PartoDTO> findByReproduccionId(Integer reproduccionId) {
        return partoRepository.findByReproduccionId(reproduccionId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PartoDTO create(CreatePartoRequest request) {
        Evento evento = eventoRepository.findById(request.getEventoId())
                .orElseThrow(() -> new EntityNotFoundException("Evento no encontrado"));
        Reproduccion r = reproduccionRepository.findById(request.getReproduccionId())
                .orElseThrow(() -> new EntityNotFoundException("Reproducción no encontrada"));

        Parto p = new Parto();
        p.setEvento(evento);
        p.setReproduccion(r);
        p.setCantidadCrias(request.getCantidadCrias());
        p.setObservacion(request.getObservacion());

        return toDTO(partoRepository.save(p));
    }

    @Transactional
    public PartoDTO update(Integer id, CreatePartoRequest request) {
        Parto p = partoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Parto no encontrado"));

        if (request.getEventoId() != null) {
            p.setEvento(eventoRepository.findById(request.getEventoId())
                    .orElseThrow(() -> new EntityNotFoundException("Evento no encontrado")));
        }
        if (request.getCantidadCrias() != null) p.setCantidadCrias(request.getCantidadCrias());
        if (request.getObservacion() != null) p.setObservacion(request.getObservacion());

        return toDTO(partoRepository.save(p));
    }

    @Transactional
    public void delete(Integer id) {
        Parto p = partoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Parto no encontrado"));
        partoRepository.delete(p);
    }

    private PartoDTO toDTO(Parto p) {
        PartoDTO dto = new PartoDTO();
        dto.setId(p.getId());
        dto.setEventoId(p.getEvento() != null ? p.getEvento().getId() : null);
        dto.setFechaParto(p.getEvento() != null && p.getEvento().getFecha() != null
                ? p.getEvento().getFecha().toLocalDate().toString() : null);
        dto.setReproduccionId(p.getReproduccion() != null ? p.getReproduccion().getId() : null);
        dto.setVacaNombre(p.getReproduccion() != null && p.getReproduccion().getVaca() != null
                ? p.getReproduccion().getVaca().getNombre() : null);
        dto.setVacaArete(p.getReproduccion() != null && p.getReproduccion().getVaca() != null
                ? p.getReproduccion().getVaca().getIdentificadorArete() : null);
        dto.setCantidadCrias(p.getCantidadCrias());
        dto.setObservacion(p.getObservacion());
        return dto;
    }
}
