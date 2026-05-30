package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.AnimalDTO;
import com.gestionganadera.backend.dto.CreateMovimientoRequest;
import com.gestionganadera.backend.dto.MovimientoDTO;
import com.gestionganadera.backend.service.MovimientoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movimiento")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Movimientos", description = "Movimientos y traslados de animales entre lotes")
public class MovimientoController {

    private final MovimientoService movimientoService;

    @GetMapping("/lote/{loteId}/animales")
    @Operation(summary = "Animales por lote", description = "Obtiene los animales cuyo último movimiento tiene como destino el lote indicado")
    public ResponseEntity<List<AnimalDTO>> getAnimalesByLote(@PathVariable @NonNull Integer loteId) {
        return ResponseEntity.ok(movimientoService.getAnimalesByLote(loteId));
    }

    @GetMapping("/animal/{id}/ultimo-lote-nombre")
    @Operation(summary = "Nombre del último lote por animal", description = "Obtiene el nombre del último lote donde estuvo el animal")
    public ResponseEntity<String> getUltimoLoteNombreByAnimal(@PathVariable @NonNull Integer id) {
        String nombreLote = movimientoService.getUltimoLoteNombreByAnimalId(id);
        return nombreLote != null ? ResponseEntity.ok(nombreLote) : ResponseEntity.noContent().build();
    }

    @GetMapping("/lote/{loteId}/capacity")
    @Operation(summary = "Verificar capacidad de lote", description = "Verifica si un lote tiene espacio disponible y devuelve ocupación")
    public ResponseEntity<java.util.Map<String, Object>> checkCapacity(@PathVariable @NonNull Integer loteId) {
        boolean hasSpace = movimientoService.hasLoteCapacity(loteId);
        int occupancy = movimientoService.getLoteOccupancy(loteId);
        Integer capacidadMaxima = movimientoService.getLoteCapacidadMaxima(loteId);
        return ResponseEntity.ok(java.util.Map.of(
            "hasSpace", hasSpace,
            "occupancy", occupancy,
            "capacidadMaxima", capacidadMaxima != null ? capacidadMaxima : 0
        ));
    }

    @GetMapping("/recent")
    @Operation(summary = "Movimientos recientes")
    public ResponseEntity<List<MovimientoDTO>> getRecent() {
        return ResponseEntity.ok(movimientoService.getRecent());
    }

    @GetMapping
    @Operation(summary = "Listar movimientos")
    public ResponseEntity<List<MovimientoDTO>> getAll() {
        return ResponseEntity.ok(movimientoService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener movimiento por ID")
    public ResponseEntity<MovimientoDTO> getById(@PathVariable @NonNull Integer id) {
        return ResponseEntity.ok(movimientoService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Crear movimiento", description = "Registra un traslado de animal entre lotes")
    public ResponseEntity<MovimientoDTO> create(@Valid @RequestBody @NonNull CreateMovimientoRequest request) {
        return ResponseEntity.ok(movimientoService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar movimiento")
    public ResponseEntity<MovimientoDTO> update(@PathVariable @NonNull Integer id,
                                                  @Valid @RequestBody @NonNull CreateMovimientoRequest request) {
        return ResponseEntity.ok(movimientoService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar movimiento")
    public ResponseEntity<Void> delete(@PathVariable @NonNull Integer id) {
        movimientoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
