package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.model.Produccion;
import com.gestionganadera.backend.service.ProduccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/producciones")
@RequiredArgsConstructor
public class ProduccionController {
    private final ProduccionService service;

    @GetMapping("/animal/{animalId}")
    public ResponseEntity<List<Produccion>> findByAnimalId(@PathVariable @NonNull Integer animalId) {
        return ResponseEntity.ok(service.findByAnimalId(animalId));
    }

    @PostMapping
    public ResponseEntity<Produccion> create(@RequestBody @NonNull Produccion entity) {
        return ResponseEntity.ok(service.save(entity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @NonNull Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
