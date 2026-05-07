package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.model.Tratamiento;
import com.gestionganadera.backend.service.TratamientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tratamientos")
@RequiredArgsConstructor
public class TratamientoController {
    private final TratamientoService service;

    @GetMapping("/animal/{animalId}")
    public ResponseEntity<List<Tratamiento>> findByAnimalId(@PathVariable @NonNull Integer animalId) {
        return ResponseEntity.ok(service.findByAnimalId(animalId));
    }

    @PostMapping
    public ResponseEntity<Tratamiento> create(@RequestBody @NonNull Tratamiento entity) {
        return ResponseEntity.ok(service.save(entity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @NonNull Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
