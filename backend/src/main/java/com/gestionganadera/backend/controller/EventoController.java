package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.CreateEventoRequest;
import com.gestionganadera.backend.dto.EventoDTO;
import com.gestionganadera.backend.service.EventoService;
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
@RequestMapping("/evento")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Eventos", description = "Registro de eventos y novedades de animales")
public class EventoController {
    private final EventoService service;

    @GetMapping
    @Operation(summary = "Listar eventos", description = "Obtiene todos los eventos registrados")
    public ResponseEntity<List<EventoDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/recent")
    @Operation(summary = "Eventos recientes", description = "Obtiene los eventos más recientes")
    public ResponseEntity<List<EventoDTO>> getRecent() {
        return ResponseEntity.ok(service.getRecent());
    }

    @GetMapping("/animal/{animalId}")
    @Operation(summary = "Eventos por animal")
    public ResponseEntity<List<EventoDTO>> findByAnimalId(@PathVariable @NonNull Integer animalId) {
        return ResponseEntity.ok(service.findByAnimalId(animalId));
    }

    @PostMapping
    @Operation(summary = "Crear evento")
    public ResponseEntity<EventoDTO> create(@Valid @RequestBody @NonNull CreateEventoRequest request) {
        return ResponseEntity.ok(service.toDTO(service.save(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar evento")
    public ResponseEntity<EventoDTO> update(@PathVariable @NonNull Integer id,
                                              @Valid @RequestBody @NonNull CreateEventoRequest request) {
        return ResponseEntity.ok(service.toDTO(service.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar evento")
    public ResponseEntity<Void> delete(@PathVariable @NonNull Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
