package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.CreateProduccionRequest;
import com.gestionganadera.backend.dto.ProduccionDTO;
import com.gestionganadera.backend.dto.ProduccionResumenDTO;
import com.gestionganadera.backend.service.ProduccionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produccion")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Producción", description = "Registro de producción de leche diaria")
public class ProduccionController {
    private final ProduccionService service;

    @GetMapping
    @Operation(summary = "Listar producción", description = "Obtiene todos los registros de producción de leche")
    public ResponseEntity<List<ProduccionDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/animal/{animalId}")
    @Operation(summary = "Producción por animal")
    public ResponseEntity<List<ProduccionDTO>> findByAnimalId(@PathVariable @NonNull Integer animalId) {
        return ResponseEntity.ok(service.findByAnimalId(animalId));
    }

    @GetMapping("/resumen")
    @Operation(summary = "Resumen de producción", description = "Agrupa producción de leche por mes para un año específico")
    public ResponseEntity<List<ProduccionResumenDTO>> getResumen(
            @RequestParam(name = "year", defaultValue = "#{T(java.time.Year).now().getValue()}") @NonNull Integer year) {
        return ResponseEntity.ok(service.getResumen(year));
    }

    @PostMapping
    @Operation(summary = "Registrar producción")
    public ResponseEntity<ProduccionDTO> create(@Valid @RequestBody @NonNull CreateProduccionRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar registro de producción")
    public ResponseEntity<ProduccionDTO> update(@PathVariable @NonNull Integer id,
                                                 @Valid @RequestBody @NonNull CreateProduccionRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar registro de producción")
    public ResponseEntity<Void> delete(@PathVariable @NonNull Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
