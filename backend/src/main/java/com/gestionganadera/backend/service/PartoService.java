package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreatePartoRequest;
import com.gestionganadera.backend.dto.PartoDTO;
import com.gestionganadera.backend.model.Animal;
import com.gestionganadera.backend.model.Evento;
import com.gestionganadera.backend.model.Parto;
import com.gestionganadera.backend.model.Reproduccion;
import com.gestionganadera.backend.model.TipoEvento;
import com.gestionganadera.backend.repository.AnimalRepository;
import com.gestionganadera.backend.repository.EventoRepository;
import com.gestionganadera.backend.repository.PartoRepository;
import com.gestionganadera.backend.repository.ReproduccionRepository;
import com.gestionganadera.backend.repository.TipoEventoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartoService {

    private final PartoRepository partoRepository;
    private final ReproduccionRepository reproduccionRepository;
    private final EventoRepository eventoRepository;
    private final AnimalRepository animalRepository;
    private final TipoEventoRepository tipoEventoRepository;

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
        Reproduccion r = reproduccionRepository.findById(request.getReproduccionId())
                .orElseThrow(() -> new EntityNotFoundException("Reproducción no encontrada"));

        // Crear Evento propio para el Parto (no reusar el de Reproducción)
        Evento evento;
        if (request.getEventoId() != null) {
            evento = eventoRepository.findById(request.getEventoId())
                    .orElseThrow(() -> new EntityNotFoundException("Evento no encontrado"));
        } else {
            // Crear un nuevo Evento con la fecha de parto y el animal de la reproducción
            Animal vaca = r.getVaca();
            TipoEvento tipoParto = tipoEventoRepository.findAll().stream()
                    .filter(te -> te.getNombre() != null && te.getNombre().toLowerCase().contains("parto"))
                    .findFirst().orElse(null);

            evento = new Evento();
            evento.setAnimal(vaca);
            evento.setTipoEvento(tipoParto);
            evento.setDescripcion("Parto asociado a reproducción");
            evento.setFecha(request.getFechaParto() != null
                    ? request.getFechaParto().atStartOfDay()
                    : java.time.LocalDateTime.now());
            evento = eventoRepository.save(evento);
        }

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

        // Actualizar fecha del Evento si se proporciona fechaParto
        if (request.getFechaParto() != null && p.getEvento() != null) {
            p.getEvento().setFecha(request.getFechaParto().atStartOfDay());
            eventoRepository.save(p.getEvento());
        }

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
