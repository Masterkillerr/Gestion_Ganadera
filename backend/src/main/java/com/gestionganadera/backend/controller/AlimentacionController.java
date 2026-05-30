package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.CreateAlimentacionRequest;
import com.gestionganadera.backend.model.Alimentacion;
import com.gestionganadera.backend.service.AlimentacionService;
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
@RequestMapping("/alimentacion")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Alimentaciones", description = "Registro de alimentación diaria de animales")
public class AlimentacionController {
    private final AlimentacionService service;

    @GetMapping
    @Operation(summary = "Listar alimentaciones", description = "Obtiene todos los registros de alimentación")
    public ResponseEntity<List<Alimentacion>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/animal/{animalId}")
    @Operation(summary = "Alimentaciones por animal", description = "Obtiene el historial de alimentación de un animal")
    public ResponseEntity<List<Alimentacion>> findByAnimalId(@PathVariable @NonNull Integer animalId) {
        return ResponseEntity.ok(service.findByAnimalId(animalId));
    }

    @PostMapping
    @Operation(summary = "Registrar alimentación")
    public ResponseEntity<Alimentacion> create(@Valid @RequestBody @NonNull CreateAlimentacionRequest request) {
        return ResponseEntity.ok(service.save(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar registro de alimentación")
    public ResponseEntity<Alimentacion> update(@PathVariable @NonNull Integer id,
                                               @Valid @RequestBody @NonNull CreateAlimentacionRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar registro de alimentación")
    public ResponseEntity<Void> delete(@PathVariable @NonNull Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
