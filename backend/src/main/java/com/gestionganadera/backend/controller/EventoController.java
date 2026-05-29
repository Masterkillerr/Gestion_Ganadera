package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.CreateEventoRequest;
import com.gestionganadera.backend.dto.EventoDTO;
import com.gestionganadera.backend.model.Evento;
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
    public ResponseEntity<List<Evento>> findByAnimalId(@PathVariable @NonNull Integer animalId) {
        return ResponseEntity.ok(service.findByAnimalId(animalId));
    }

    @PostMapping
    @Operation(summary = "Crear evento")
    public ResponseEntity<Evento> create(@Valid @RequestBody @NonNull CreateEventoRequest request) {
        return ResponseEntity.ok(service.save(request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar evento")
    public ResponseEntity<Void> delete(@PathVariable @NonNull Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
