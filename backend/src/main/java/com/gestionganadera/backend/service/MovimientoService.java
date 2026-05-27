package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateMovimientoRequest;
import com.gestionganadera.backend.dto.MovimientoDTO;
import com.gestionganadera.backend.model.Animal;
import com.gestionganadera.backend.model.Finca;
import com.gestionganadera.backend.model.Lote;
import com.gestionganadera.backend.model.Movimiento;
import com.gestionganadera.backend.model.Usuario;
import com.gestionganadera.backend.repository.AnimalRepository;
import com.gestionganadera.backend.repository.FincaRepository;
import com.gestionganadera.backend.repository.LoteRepository;
import com.gestionganadera.backend.repository.MovimientoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final FincaRepository fincaRepository;
    private final AnimalRepository animalRepository;
    private final LoteRepository loteRepository;

    private Usuario getCurrentUser() {
        return (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
    }

    private List<Integer> getUserFincaIds() {
        return fincaRepository.findByPropietario(getCurrentUser())
                .stream().map(Finca::getId).collect(Collectors.toList());
    }

    private Animal getAuthorizedAnimal(Integer animalId) {
        return animalRepository.findByIdAndFincaIdIn(animalId, getUserFincaIds())
                .orElseThrow(() -> new EntityNotFoundException("Animal no encontrado o no autorizado"));
    }

    private Lote getAuthorizedLote(Integer loteId) {
        List<Integer> fincaIds = getUserFincaIds();
        Lote lote = loteRepository.findById(loteId)
                .orElseThrow(() -> new EntityNotFoundException("Lote no encontrado"));
        if (!fincaIds.contains(lote.getFinca().getId())) {
            throw new EntityNotFoundException("Lote no encontrado o no autorizado");
        }
        return lote;
    }

    public List<MovimientoDTO> getRecent() {
        List<Integer> fincaIds = getUserFincaIds();
        if (fincaIds.isEmpty()) return List.of();

        return movimientoRepository
                .findTop10ByAnimalFincaIdInOrderByFechaDesc(fincaIds)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<MovimientoDTO> findAll() {
        List<Integer> fincaIds = getUserFincaIds();
        if (fincaIds.isEmpty()) return List.of();

        return movimientoRepository
                .findByAnimalFincaIdInOrderByFechaDesc(fincaIds)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public MovimientoDTO findById(@NonNull Integer id) {
        Movimiento movimiento = movimientoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Movimiento no encontrado"));
        // Verify ownership
        if (!getUserFincaIds().contains(movimiento.getAnimal().getFinca().getId())) {
            throw new EntityNotFoundException("Movimiento no encontrado");
        }
        return toDTO(movimiento);
    }

    public MovimientoDTO create(@NonNull CreateMovimientoRequest request) {
        Animal animal = getAuthorizedAnimal(request.getAnimalId());
        Lote destino = getAuthorizedLote(request.getLoteDestinoId());

        Movimiento entity = new Movimiento();
        entity.setAnimal(animal);
        entity.setLoteDestino(destino);

        if (request.getLoteOrigenId() != null) {
            entity.setLoteOrigen(getAuthorizedLote(request.getLoteOrigenId()));
        }

        entity.setFecha(request.getFecha());
        entity.setTipoMovimiento(request.getTipoMovimiento());
        entity.setMotivo(request.getMotivo());

        Movimiento saved = movimientoRepository.save(entity);
        return toDTO(saved);
    }

    public MovimientoDTO update(@NonNull Integer id, @NonNull CreateMovimientoRequest request) {
        Movimiento existing = movimientoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Movimiento no encontrado"));

        // Verify ownership of the existing movimiento
        getAuthorizedAnimal(existing.getAnimal().getId());

        Animal animal = getAuthorizedAnimal(request.getAnimalId());
        Lote destino = getAuthorizedLote(request.getLoteDestinoId());

        existing.setAnimal(animal);
        existing.setLoteDestino(destino);

        if (request.getLoteOrigenId() != null) {
            existing.setLoteOrigen(getAuthorizedLote(request.getLoteOrigenId()));
        } else {
            existing.setLoteOrigen(null);
        }

        existing.setFecha(request.getFecha());
        existing.setTipoMovimiento(request.getTipoMovimiento());
        existing.setMotivo(request.getMotivo());

        Movimiento saved = movimientoRepository.save(existing);
        return toDTO(saved);
    }

    public void delete(@NonNull Integer id) {
        Movimiento entity = movimientoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Movimiento no encontrado"));
        // Verify ownership
        getAuthorizedAnimal(entity.getAnimal().getId());
        movimientoRepository.deleteById(id);
    }

    private MovimientoDTO toDTO(Movimiento m) {
        MovimientoDTO dto = new MovimientoDTO();
        dto.setId(m.getId());
        dto.setFecha(m.getFecha() != null ? m.getFecha().toString() : null);
        dto.setAnimalNombre(m.getAnimal() != null ? m.getAnimal().getNombre() : null);
        dto.setAnimalArete(m.getAnimal() != null ? m.getAnimal().getIdentificadorArete() : null);
        dto.setOrigen(m.getLoteOrigen() != null ? m.getLoteOrigen().getNombre() : null);
        dto.setDestino(m.getLoteDestino() != null ? m.getLoteDestino().getNombre() : null);
        dto.setTipoMovimiento(m.getTipoMovimiento());
        return dto;
    }
}
