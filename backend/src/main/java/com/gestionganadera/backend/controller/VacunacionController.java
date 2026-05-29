package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.CreateVacunacionRequest;
import com.gestionganadera.backend.model.Vacunacion;
import com.gestionganadera.backend.service.VacunacionService;
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
@RequestMapping("/vacunacion")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Vacunaciones", description = "Registro de vacunación de animales")
public class VacunacionController {
    private final VacunacionService service;

    @GetMapping("/animal/{animalId}")
    @Operation(summary = "Vacunaciones por animal", description = "Obtiene el historial de vacunaciones de un animal")
    public ResponseEntity<List<Vacunacion>> findByAnimalId(@PathVariable @NonNull Integer animalId) {
        return ResponseEntity.ok(service.findByAnimalId(animalId));
    }

    @GetMapping
    @Operation(summary = "Listar vacunaciones")
    public ResponseEntity<List<Vacunacion>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener vacunación por ID")
    public ResponseEntity<Vacunacion> findById(@PathVariable @NonNull Integer id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @Operation(summary = "Registrar vacunación")
    public ResponseEntity<Vacunacion> create(@Valid @RequestBody @NonNull CreateVacunacionRequest request) {
        return ResponseEntity.ok(service.save(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar vacunación")
    public ResponseEntity<Vacunacion> update(@PathVariable @NonNull Integer id,
                                              @Valid @RequestBody @NonNull CreateVacunacionRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar vacunación")
    public ResponseEntity<Void> delete(@PathVariable @NonNull Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
