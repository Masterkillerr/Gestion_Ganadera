package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.CreateTratamientoRequest;
import com.gestionganadera.backend.dto.TratamientoDTO;
import com.gestionganadera.backend.service.TratamientoService;
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
@RequestMapping("/tratamiento")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Tratamientos", description = "Registro de tratamientos veterinarios")
public class TratamientoController {
    private final TratamientoService service;

    @GetMapping("/animal/{animalId}")
    @Operation(summary = "Tratamientos por animal", description = "Obtiene el historial de tratamientos de un animal")
    public ResponseEntity<List<TratamientoDTO>> findByAnimalId(@PathVariable @NonNull Integer animalId) {
        return ResponseEntity.ok(service.findByAnimalId(animalId));
    }

    @GetMapping
    @Operation(summary = "Listar tratamientos")
    public ResponseEntity<List<TratamientoDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener tratamiento por ID")
    public ResponseEntity<TratamientoDTO> findById(@PathVariable @NonNull Integer id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @Operation(summary = "Registrar tratamiento")
    public ResponseEntity<TratamientoDTO> create(@Valid @RequestBody @NonNull CreateTratamientoRequest request) {
        return ResponseEntity.ok(service.save(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar tratamiento")
    public ResponseEntity<TratamientoDTO> update(@PathVariable @NonNull Integer id,
                                               @Valid @RequestBody @NonNull CreateTratamientoRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar tratamiento")
    public ResponseEntity<Void> delete(@PathVariable @NonNull Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
