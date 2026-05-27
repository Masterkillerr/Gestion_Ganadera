package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.CreateProduccionRequest;
import com.gestionganadera.backend.dto.ProduccionDTO;
import com.gestionganadera.backend.dto.ProduccionResumenDTO;
import com.gestionganadera.backend.model.Produccion;
import com.gestionganadera.backend.service.ProduccionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
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

    @GetMapping
    public ResponseEntity<List<ProduccionDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/animal/{animalId}")
    public ResponseEntity<List<Produccion>> findByAnimalId(@PathVariable @NonNull Integer animalId) {
        return ResponseEntity.ok(service.findByAnimalId(animalId));
    }

    @GetMapping("/resumen")
    public ResponseEntity<List<ProduccionResumenDTO>> getResumen(
            @RequestParam(name = "year", defaultValue = "#{T(java.time.Year).now().getValue()}") @NonNull Integer year) {
        return ResponseEntity.ok(service.getResumen(year));
    }

    @PostMapping
    public ResponseEntity<ProduccionDTO> create(@Valid @RequestBody @NonNull CreateProduccionRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProduccionDTO> update(@PathVariable @NonNull Integer id,
                                                 @Valid @RequestBody @NonNull CreateProduccionRequest request) {
        try {
            return ResponseEntity.ok(service.update(id, request));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @NonNull Integer id) {
        try {
            service.delete(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
