package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateReproduccionRequest;
import com.gestionganadera.backend.dto.PartosProximosDTO;
import com.gestionganadera.backend.dto.ReproduccionDTO;
import com.gestionganadera.backend.model.Animal;
import com.gestionganadera.backend.model.Evento;
import com.gestionganadera.backend.model.Reproduccion;
import com.gestionganadera.backend.model.ResultadoReproduccion;
import com.gestionganadera.backend.model.TipoReproduccion;
import com.gestionganadera.backend.repository.AnimalRepository;
import com.gestionganadera.backend.repository.EventoRepository;
import com.gestionganadera.backend.repository.ReproduccionRepository;
import com.gestionganadera.backend.repository.ResultadoReproduccionRepository;
import com.gestionganadera.backend.repository.TipoReproduccionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReproduccionService {

    private final ReproduccionRepository reproduccionRepository;
    private final AnimalRepository animalRepository;
    private final EventoRepository eventoRepository;
    private final TipoReproduccionRepository tipoReproduccionRepository;
    private final ResultadoReproduccionRepository resultadoReproduccionRepository;

    public List<ReproduccionDTO> findAll() {
        return reproduccionRepository.findAll()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public ReproduccionDTO findById(Integer id) {
        Reproduccion r = reproduccionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reproducción no encontrada"));
        return toDTO(r);
    }

    @Transactional
    public ReproduccionDTO create(CreateReproduccionRequest request) {
        Evento evento = eventoRepository.findById(request.getEventoId())
                .orElseThrow(() -> new EntityNotFoundException("Evento no encontrado"));

        Reproduccion r = new Reproduccion();
        r.setEvento(evento);
        r.setVaca(animalRepository.findById(request.getVacaId())
                .orElseThrow(() -> new EntityNotFoundException("Vaca no encontrada")));
        if (request.getToroId() != null) {
            r.setToro(animalRepository.findById(request.getToroId())
                    .orElseThrow(() -> new EntityNotFoundException("Toro no encontrado")));
        }
        if (request.getTipoReproduccionId() != null) {
            r.setTipoReproduccion(tipoReproduccionRepository.findById(request.getTipoReproduccionId())
                    .orElseThrow(() -> new EntityNotFoundException("Tipo de reproducción no encontrado")));
        }
        if (request.getResultadoReproduccionId() != null) {
            r.setResultadoReproduccion(resultadoReproduccionRepository.findById(request.getResultadoReproduccionId())
                    .orElseThrow(() -> new EntityNotFoundException("Resultado de reproducción no encontrado")));
        }
        r.setFechaPartoEstimada(request.getFechaPartoEstimada());
        r.setObservacion(request.getObservacion());

        return toDTO(reproduccionRepository.save(r));
    }

    @Transactional
    public ReproduccionDTO update(Integer id, CreateReproduccionRequest request) {
        Reproduccion r = reproduccionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reproducción no encontrada"));

        if (request.getEventoId() != null) {
            r.setEvento(eventoRepository.findById(request.getEventoId())
                    .orElseThrow(() -> new EntityNotFoundException("Evento no encontrado")));
        }
        if (request.getVacaId() != null) {
            r.setVaca(animalRepository.findById(request.getVacaId())
                    .orElseThrow(() -> new EntityNotFoundException("Vaca no encontrada")));
        }
        if (request.getToroId() != null) {
            r.setToro(animalRepository.findById(request.getToroId())
                    .orElseThrow(() -> new EntityNotFoundException("Toro no encontrado")));
        }
        if (request.getTipoReproduccionId() != null) {
            r.setTipoReproduccion(tipoReproduccionRepository.findById(request.getTipoReproduccionId())
                    .orElseThrow(() -> new EntityNotFoundException("Tipo de reproducción no encontrado")));
        }
        if (request.getResultadoReproduccionId() != null) {
            r.setResultadoReproduccion(resultadoReproduccionRepository.findById(request.getResultadoReproduccionId())
                    .orElseThrow(() -> new EntityNotFoundException("Resultado de reproducción no encontrado")));
        }
        if (request.getFechaPartoEstimada() != null) r.setFechaPartoEstimada(request.getFechaPartoEstimada());
        if (request.getObservacion() != null) r.setObservacion(request.getObservacion());

        return toDTO(reproduccionRepository.save(r));
    }

    @Transactional
    public void delete(Integer id) {
        Reproduccion r = reproduccionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reproducción no encontrada"));
        reproduccionRepository.delete(r);
    }

    public List<PartosProximosDTO> getProximosPartos() {
        LocalDate today = LocalDate.now();
        LocalDate twoMonthsFromNow = today.plusMonths(2);

        return reproduccionRepository
                .findByFechaPartoEstimadaBetween(today, twoMonthsFromNow)
                .stream().map(this::toPartosProximosDTO).collect(Collectors.toList());
    }

    private ReproduccionDTO toDTO(Reproduccion r) {
        ReproduccionDTO dto = new ReproduccionDTO();
        dto.setId(r.getId());
        dto.setEventoId(r.getEvento() != null ? r.getEvento().getId() : null);
        dto.setFechaMonta(r.getEvento() != null && r.getEvento().getFecha() != null
                ? r.getEvento().getFecha().toLocalDate().toString() : null);
        dto.setVacaId(r.getVaca() != null ? r.getVaca().getId() : null);
        dto.setVacaNombre(r.getVaca() != null ? r.getVaca().getNombre() : null);
        dto.setVacaArete(r.getVaca() != null ? r.getVaca().getIdentificadorArete() : null);
        dto.setToroId(r.getToro() != null ? r.getToro().getId() : null);
        dto.setToroNombre(r.getToro() != null ? r.getToro().getNombre() : null);
        dto.setToroArete(r.getToro() != null ? r.getToro().getIdentificadorArete() : null);
        dto.setTipoReproduccion(r.getTipoReproduccion() != null ? r.getTipoReproduccion().getNombre() : null);
        dto.setResultadoReproduccion(r.getResultadoReproduccion() != null ? r.getResultadoReproduccion().getNombre() : null);
        dto.setFechaPartoEstimada(r.getFechaPartoEstimada());
        dto.setObservacion(r.getObservacion());
        return dto;
    }

    private PartosProximosDTO toPartosProximosDTO(Reproduccion r) {
        PartosProximosDTO dto = new PartosProximosDTO();
        dto.setReproduccionId(r.getId());
        dto.setVacaNombre(r.getVaca() != null ? r.getVaca().getNombre() : null);
        dto.setVacaArete(r.getVaca() != null ? r.getVaca().getIdentificadorArete() : null);
        dto.setToroNombre(r.getToro() != null ? r.getToro().getNombre() : null);
        dto.setToroArete(r.getToro() != null ? r.getToro().getIdentificadorArete() : null);
        dto.setFechaPartoEstimada(r.getFechaPartoEstimada());
        if (r.getFechaPartoEstimada() != null) {
            dto.setDiasRestantes(ChronoUnit.DAYS.between(LocalDate.now(), r.getFechaPartoEstimada()));
        }
        return dto;
    }
}
