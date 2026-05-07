package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.model.Alimentacion;
import com.gestionganadera.backend.service.AlimentacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alimentaciones")
@RequiredArgsConstructor
public class AlimentacionController {
    private final AlimentacionService service;

    @GetMapping("/animal/{animalId}")
    public ResponseEntity<List<Alimentacion>> findByAnimalId(@PathVariable @NonNull Integer animalId) {
        return ResponseEntity.ok(service.findByAnimalId(animalId));
    }

    @PostMapping
    public ResponseEntity<Alimentacion> create(@RequestBody @NonNull Alimentacion entity) {
        return ResponseEntity.ok(service.save(entity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @NonNull Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
