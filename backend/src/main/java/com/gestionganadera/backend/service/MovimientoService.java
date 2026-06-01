package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.AnimalDTO;
import com.gestionganadera.backend.dto.CreateMovimientoRequest;
import com.gestionganadera.backend.dto.MovimientoDTO;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
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
                .filter(m -> m.getEvento() != null && m.getEvento().getFecha() != null)
                .sorted((a, b) -> b.getEvento().getFecha().compareTo(a.getEvento().getFecha()))
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

    @Transactional
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
            entity.setTipoMovimiento(tipoMovimientoRepository.findById(request.getTipoMovimientoId())
                    .orElseThrow(() -> new EntityNotFoundException("Tipo de movimiento con ID " + request.getTipoMovimientoId() + " no encontrado")));
        }

        entity.setMotivo(request.getMotivo());

        Movimiento saved = movimientoRepository.save(entity);
        return toDTO(saved);
    }

    @Transactional
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
            existing.setTipoMovimiento(tipoMovimientoRepository.findById(request.getTipoMovimientoId())
                    .orElseThrow(() -> new EntityNotFoundException("Tipo de movimiento con ID " + request.getTipoMovimientoId() + " no encontrado")));
        }

        existing.setMotivo(request.getMotivo());

        Movimiento saved = movimientoRepository.save(existing);
        return toDTO(saved);
    }

    @Transactional
    public void delete(@NonNull Integer id) {
        Movimiento entity = movimientoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Movimiento no encontrado"));
        movimientoRepository.deleteById(id);
    }

    /**
     * Obtiene los animales cuyo último movimiento tiene como destino el lote indicado.
     */
    public List<AnimalDTO> getAnimalesByLote(@NonNull Integer loteId) {
        return movimientoRepository.findLatestByLoteDestino(loteId)
                .stream()
                .map(m -> m.getEvento().getAnimal())
                .distinct()
                .map(AnimalDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public Integer getUltimoLoteIdByAnimalId(@NonNull Integer animalId) {
        return movimientoRepository.findUltimoLoteIdByAnimalId(animalId);
    }

    /**
     * Obtiene el último movimiento de un animal.
     */
    public Optional<Movimiento> findUltimoByAnimalId(@NonNull Integer animalId) {
        return movimientoRepository.findUltimoByAnimalId(animalId);
    }

    /**
     * Verifica si un lote tiene espacio disponible.
     * @return true si hay espacio, false si está lleno o no tiene capacidad definida
     */
    public boolean hasLoteCapacity(@NonNull Integer loteId) {
        Lote lote = loteRepository.findById(loteId)
                .orElseThrow(() -> new EntityNotFoundException("Lote no encontrado"));

        Integer capacidadMaxima = lote.getCapacidadMaxima();
        if (capacidadMaxima == null || capacidadMaxima <= 0) {
            // No hay límite definido, siempre hay espacio
            return true;
        }

        Integer count = movimientoRepository.countAnimalesByLote(loteId);
        return count == null || count < capacidadMaxima;
    }

    /**
     * Obtiene la ocupación actual de un lote.
     */
    public int getLoteOccupancy(@NonNull Integer loteId) {
        Integer count = movimientoRepository.countAnimalesByLote(loteId);
        return count != null ? count : 0;
    }

    /**
     * Obtiene la capacidad máxima de un lote.
     */
    public Integer getLoteCapacidadMaxima(@NonNull Integer loteId) {
        return loteRepository.findById(loteId)
                .map(Lote::getCapacidadMaxima)
                .orElse(null);
    }

    /**
     * Versión simple de toDTO que incluye origenId y destinoId.
     * Usada para el endpoint /animal/{animalId}/ultimo.
     */
    public MovimientoDTO toSimpleDTO(Movimiento m) {
        MovimientoDTO dto = new MovimientoDTO();
        dto.setId(m.getId());
        dto.setEventoId(m.getEvento() != null ? m.getEvento().getId() : null);
        dto.setAnimalNombre(m.getEvento() != null && m.getEvento().getAnimal() != null ? m.getEvento().getAnimal().getNombre() : null);
        dto.setAnimalArete(m.getEvento() != null && m.getEvento().getAnimal() != null ? m.getEvento().getAnimal().getIdentificadorArete() : null);
        dto.setFecha(m.getEvento() != null && m.getEvento().getFecha() != null ? m.getEvento().getFecha().toString() : null);
        dto.setTipoMovimiento(m.getTipoMovimiento() != null ? m.getTipoMovimiento().getNombre() : null);
        dto.setOrigen(m.getLoteOrigen() != null ? m.getLoteOrigen().getNombre() : null);
        dto.setOrigenId(m.getLoteOrigen() != null ? m.getLoteOrigen().getId() : null);
        dto.setDestino(m.getLoteDestino() != null ? m.getLoteDestino().getNombre() : null);
        dto.setDestinoId(m.getLoteDestino() != null ? m.getLoteDestino().getId() : null);
        dto.setMotivo(m.getMotivo());
        return dto;
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
