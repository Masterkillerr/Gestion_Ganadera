package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.CreateDietaAlimentoRequest;
import com.gestionganadera.backend.dto.DietaAlimentoDTO;
import com.gestionganadera.backend.service.DietaAlimentoService;
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
@RequestMapping("/dieta-alimento")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Dieta-Alimento", description = "Asignación de alimentos a dietas")
public class DietaAlimentoController {

    private final DietaAlimentoService service;

    @GetMapping
    @Operation(summary = "Listar todas las asignaciones")
    public ResponseEntity<List<DietaAlimentoDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/dieta/{dietaId}")
    @Operation(summary = "Listar alimentos de una dieta")
    public ResponseEntity<List<DietaAlimentoDTO>> findByDietaId(@PathVariable @NonNull Integer dietaId) {
        return ResponseEntity.ok(service.findByDietaId(dietaId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener asignación por ID")
    public ResponseEntity<DietaAlimentoDTO> findById(@PathVariable @NonNull Integer id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @Operation(summary = "Asignar alimento a dieta")
    public ResponseEntity<DietaAlimentoDTO> create(@Valid @RequestBody @NonNull CreateDietaAlimentoRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar asignación")
    public ResponseEntity<DietaAlimentoDTO> update(@PathVariable @NonNull Integer id,
                                                @Valid @RequestBody @NonNull CreateDietaAlimentoRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar asignación")
    public ResponseEntity<Void> delete(@PathVariable @NonNull Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/dieta/{dietaId}")
    @Operation(summary = "Eliminar todas las asignaciones de una dieta (cascada)")
    public ResponseEntity<Void> deleteByDietaId(@PathVariable @NonNull Integer dietaId) {
        service.deleteByDietaId(dietaId);
        return ResponseEntity.noContent().build();
    }
}
