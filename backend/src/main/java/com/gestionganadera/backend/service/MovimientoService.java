package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateMovimientoRequest;
import com.gestionganadera.backend.dto.MovimientoDTO;
import com.gestionganadera.backend.model.Animal;
import com.gestionganadera.backend.model.Evento;
import com.gestionganadera.backend.model.Lote;
import com.gestionganadera.backend.model.Movimiento;
import com.gestionganadera.backend.repository.EventoRepository;
import com.gestionganadera.backend.repository.LoteRepository;
import com.gestionganadera.backend.repository.MovimientoRepository;
import com.gestionganadera.backend.repository.TipoMovimientoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final EventoRepository eventoRepository;
    private final LoteRepository loteRepository;
    private final TipoMovimientoRepository tipoMovimientoRepository;

    public List<MovimientoDTO> getRecent() {
        return movimientoRepository.findAll().stream()
                .sorted((a, b) -> {
                    if (a.getEvento() == null || b.getEvento() == null) return 0;
                    return b.getEvento().getFecha().compareTo(a.getEvento().getFecha());
                })
                .limit(20)
                .map(this::toDTO).collect(Collectors.toList());
    }

    public List<MovimientoDTO> findAll() {
        return movimientoRepository.findAll()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public MovimientoDTO findById(@NonNull Integer id) {
        Movimiento movimiento = movimientoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Movimiento no encontrado"));
        return toDTO(movimiento);
    }

    public MovimientoDTO create(@NonNull CreateMovimientoRequest request) {
        Evento evento = eventoRepository.findById(request.getEventoId())
                .orElseThrow(() -> new EntityNotFoundException("Evento no encontrado"));
        Lote destino = loteRepository.findById(request.getLoteDestinoId())
                .orElseThrow(() -> new EntityNotFoundException("Lote destino no encontrado"));

        Movimiento entity = new Movimiento();
        entity.setEvento(evento);
        entity.setLoteDestino(destino);

        if (request.getLoteOrigenId() != null) {
            entity.setLoteOrigen(loteRepository.findById(request.getLoteOrigenId())
                    .orElseThrow(() -> new EntityNotFoundException("Lote origen no encontrado")));
        }

        if (request.getTipoMovimientoId() != null) {
            tipoMovimientoRepository.findById(request.getTipoMovimientoId())
                    .ifPresent(entity::setTipoMovimiento);
        }

        entity.setMotivo(request.getMotivo());

        Movimiento saved = movimientoRepository.save(entity);
        return toDTO(saved);
    }

    public MovimientoDTO update(@NonNull Integer id, @NonNull CreateMovimientoRequest request) {
        Movimiento existing = movimientoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Movimiento no encontrado"));

        Evento evento = eventoRepository.findById(request.getEventoId())
                .orElseThrow(() -> new EntityNotFoundException("Evento no encontrado"));
        Lote destino = loteRepository.findById(request.getLoteDestinoId())
                .orElseThrow(() -> new EntityNotFoundException("Lote destino no encontrado"));

        existing.setEvento(evento);
        existing.setLoteDestino(destino);

        if (request.getLoteOrigenId() != null) {
            existing.setLoteOrigen(loteRepository.findById(request.getLoteOrigenId())
                    .orElseThrow(() -> new EntityNotFoundException("Lote origen no encontrado")));
        } else {
            existing.setLoteOrigen(null);
        }

        if (request.getTipoMovimientoId() != null) {
            tipoMovimientoRepository.findById(request.getTipoMovimientoId())
                    .ifPresent(existing::setTipoMovimiento);
        }

        existing.setMotivo(request.getMotivo());

        Movimiento saved = movimientoRepository.save(existing);
        return toDTO(saved);
    }

    public void delete(@NonNull Integer id) {
        Movimiento entity = movimientoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Movimiento no encontrado"));
        movimientoRepository.deleteById(id);
    }

    /**
     * Obtiene los animales cuyo último movimiento tiene como destino el lote indicado.
     */
    public List<Animal> getAnimalesByLote(@NonNull Integer loteId) {
        return movimientoRepository.findLatestByLoteDestino(loteId)
                .stream()
                .map(m -> m.getEvento().getAnimal())
                .distinct()
                .collect(Collectors.toList());
    }

    private MovimientoDTO toDTO(Movimiento m) {
        MovimientoDTO dto = new MovimientoDTO();
        dto.setId(m.getId());
        dto.setEventoId(m.getEvento() != null ? m.getEvento().getId() : null);
        dto.setAnimalNombre(m.getEvento() != null && m.getEvento().getAnimal() != null ? m.getEvento().getAnimal().getNombre() : null);
        dto.setAnimalArete(m.getEvento() != null && m.getEvento().getAnimal() != null ? m.getEvento().getAnimal().getIdentificadorArete() : null);
        dto.setFecha(m.getEvento() != null && m.getEvento().getFecha() != null ? m.getEvento().getFecha().toString() : null);
        dto.setTipoMovimiento(m.getTipoMovimiento() != null ? m.getTipoMovimiento().getNombre() : null);
        dto.setOrigen(m.getLoteOrigen() != null ? m.getLoteOrigen().getNombre() : null);
        dto.setDestino(m.getLoteDestino() != null ? m.getLoteDestino().getNombre() : null);
        dto.setMotivo(m.getMotivo());
        return dto;
    }
}
