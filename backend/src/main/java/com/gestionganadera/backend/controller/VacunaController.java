package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.model.Vacuna;
import com.gestionganadera.backend.repository.VacunaRepository;
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
@RequestMapping("/vacuna")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Vacunas", description = "Catálogo de vacunas")
public class VacunaController {

    private final VacunaRepository repository;

    @GetMapping
    @Operation(summary = "Listar vacunas", description = "Obtiene todas las vacunas del catálogo")
    public ResponseEntity<List<Vacuna>> findAll() {
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener vacuna por ID")
    public ResponseEntity<Vacuna> findById(@PathVariable @NonNull Integer id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear vacuna")
    public ResponseEntity<Vacuna> create(@Valid @RequestBody @NonNull Vacuna vacuna) {
        return ResponseEntity.ok(repository.save(vacuna));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar vacuna")
    public ResponseEntity<Vacuna> update(@PathVariable @NonNull Integer id,
                                          @Valid @RequestBody @NonNull Vacuna vacuna) {
        Vacuna existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vacuna no encontrada"));
        existing.setNombre(vacuna.getNombre());
        return ResponseEntity.ok(repository.save(existing));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar vacuna")
    public ResponseEntity<Void> delete(@PathVariable @NonNull Integer id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
