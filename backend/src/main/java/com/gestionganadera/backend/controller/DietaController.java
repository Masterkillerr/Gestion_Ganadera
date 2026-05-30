package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.CreateDietaRequest;
import com.gestionganadera.backend.dto.DietaDTO;
import com.gestionganadera.backend.service.DietaService;
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
@RequestMapping("/dieta")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Dietas", description = "CRUD de dietas")
public class DietaController {

    private final DietaService service;

    @GetMapping
    @Operation(summary = "Listar dietas")
    public ResponseEntity<List<DietaDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener dieta por ID")
    public ResponseEntity<DietaDTO> findById(@PathVariable @NonNull Integer id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @Operation(summary = "Crear dieta")
    public ResponseEntity<DietaDTO> create(@Valid @RequestBody @NonNull CreateDietaRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar dieta")
    public ResponseEntity<DietaDTO> update(@PathVariable @NonNull Integer id,
                                        @Valid @RequestBody @NonNull CreateDietaRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar dieta")
    public ResponseEntity<Void> delete(@PathVariable @NonNull Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
