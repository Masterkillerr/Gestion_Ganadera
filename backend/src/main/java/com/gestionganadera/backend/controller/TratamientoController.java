package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.CreateTratamientoRequest;
import com.gestionganadera.backend.model.Tratamiento;
import com.gestionganadera.backend.service.TratamientoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tratamientos")
@RequiredArgsConstructor

@PreAuthorize("isAuthenticated()")
public class TratamientoController {
    private final TratamientoService service;

    @GetMapping("/animal/{animalId}")
    public ResponseEntity<List<Tratamiento>> findByAnimalId(@PathVariable @NonNull Integer animalId) {
        return ResponseEntity.ok(service.findByAnimalId(animalId));
    }

    @PostMapping
    public ResponseEntity<Tratamiento> create(@Valid @RequestBody @NonNull CreateTratamientoRequest request) {
        return ResponseEntity.ok(service.save(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @NonNull Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
