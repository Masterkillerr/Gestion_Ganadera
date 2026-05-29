package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.CreateLoteRequest;
import com.gestionganadera.backend.model.Lote;
import com.gestionganadera.backend.service.LoteService;
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
@RequestMapping("/lote")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Lotes", description = "CRUD de lotes dentro de las fincas")
public class LoteController {

    private final LoteService loteService;

    @GetMapping
    @Operation(summary = "Listar lotes")
    public ResponseEntity<List<Lote>> getAllLotes() {
        return ResponseEntity.ok(loteService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener lote por ID")
    public ResponseEntity<Lote> getLoteById(@PathVariable @NonNull Integer id) {
        return loteService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear lote")
    public ResponseEntity<Lote> createLote(@Valid @RequestBody @NonNull CreateLoteRequest request) {
        return ResponseEntity.ok(loteService.save(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar lote")
    public ResponseEntity<Lote> updateLote(@PathVariable @NonNull Integer id, @Valid @RequestBody @NonNull CreateLoteRequest request) {
        return ResponseEntity.ok(loteService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar lote")
    public ResponseEntity<Void> deleteLote(@PathVariable @NonNull Integer id) {
        loteService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/finca/{fincaId}")
    @Operation(summary = "Lotes por finca")
    public ResponseEntity<List<Lote>> getLotesByFinca(@PathVariable @NonNull Integer fincaId) {
        return ResponseEntity.ok(loteService.findByFincaId(fincaId));
    }
}
