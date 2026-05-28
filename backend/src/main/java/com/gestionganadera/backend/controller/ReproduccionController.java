package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.CreateReproduccionRequest;
import com.gestionganadera.backend.dto.PartosProximosDTO;
import com.gestionganadera.backend.dto.ReproduccionDTO;
import com.gestionganadera.backend.service.ReproduccionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reproducciones")
@RequiredArgsConstructor

@PreAuthorize("isAuthenticated()")
public class ReproduccionController {

    private final ReproduccionService reproduccionService;

    @GetMapping("/proximos-partos")
    public ResponseEntity<List<PartosProximosDTO>> getProximosPartos() {
        return ResponseEntity.ok(reproduccionService.getProximosPartos());
    }

    @GetMapping
    public ResponseEntity<List<ReproduccionDTO>> findAll() {
        return ResponseEntity.ok(reproduccionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReproduccionDTO> findById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(reproduccionService.findById(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<ReproduccionDTO> create(@RequestBody CreateReproduccionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reproduccionService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReproduccionDTO> update(@PathVariable Integer id, @RequestBody CreateReproduccionRequest request) {
        try {
            return ResponseEntity.ok(reproduccionService.update(id, request));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            reproduccionService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
