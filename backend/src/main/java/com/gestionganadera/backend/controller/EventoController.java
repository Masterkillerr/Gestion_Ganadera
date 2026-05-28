package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.CreateEventoRequest;
import com.gestionganadera.backend.dto.EventoDTO;
import com.gestionganadera.backend.model.Evento;
import com.gestionganadera.backend.service.EventoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/eventos")
@RequiredArgsConstructor

@PreAuthorize("isAuthenticated()")
public class EventoController {
    private final EventoService service;

    @GetMapping("/recent")
    public ResponseEntity<List<EventoDTO>> getRecent() {
        return ResponseEntity.ok(service.getRecent());
    }

    @GetMapping("/animal/{animalId}")
    public ResponseEntity<List<Evento>> findByAnimalId(@PathVariable @NonNull Integer animalId) {
        return ResponseEntity.ok(service.findByAnimalId(animalId));
    }

    @PostMapping
    public ResponseEntity<Evento> create(@Valid @RequestBody @NonNull CreateEventoRequest request) {
        return ResponseEntity.ok(service.save(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @NonNull Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
