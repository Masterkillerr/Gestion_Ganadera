package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.CreateAlimentoRequest;
import com.gestionganadera.backend.model.Alimento;
import com.gestionganadera.backend.service.AlimentoService;
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
@RequestMapping("/alimento")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Alimentos", description = "CRUD de alimentos")
public class AlimentoController {

    private final AlimentoService service;

    @GetMapping
    @Operation(summary = "Listar alimentos")
    public ResponseEntity<List<Alimento>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener alimento por ID")
    public ResponseEntity<Alimento> findById(@PathVariable @NonNull Integer id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @Operation(summary = "Crear alimento")
    public ResponseEntity<Alimento> create(@Valid @RequestBody @NonNull CreateAlimentoRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar alimento")
    public ResponseEntity<Alimento> update(@PathVariable @NonNull Integer id,
                                           @Valid @RequestBody @NonNull CreateAlimentoRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar alimento")
    public ResponseEntity<Void> delete(@PathVariable @NonNull Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
