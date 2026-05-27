package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.CreateVacunacionRequest;
import com.gestionganadera.backend.model.Vacunacion;
import com.gestionganadera.backend.service.VacunacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vacunaciones")
@RequiredArgsConstructor
public class VacunacionController {
    private final VacunacionService service;

    @GetMapping("/animal/{animalId}")
    public ResponseEntity<List<Vacunacion>> findByAnimalId(@PathVariable @NonNull Integer animalId) {
        return ResponseEntity.ok(service.findByAnimalId(animalId));
    }

    @PostMapping
    public ResponseEntity<Vacunacion> create(@Valid @RequestBody @NonNull CreateVacunacionRequest request) {
        return ResponseEntity.ok(service.save(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @NonNull Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
