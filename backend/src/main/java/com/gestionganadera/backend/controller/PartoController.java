package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.CreatePartoRequest;
import com.gestionganadera.backend.dto.PartoDTO;
import com.gestionganadera.backend.service.PartoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/partos")
@RequiredArgsConstructor
public class PartoController {

    private final PartoService partoService;

    @GetMapping
    public ResponseEntity<List<PartoDTO>> findAll() {
        return ResponseEntity.ok(partoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartoDTO> findById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(partoService.findById(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/por-reproduccion/{reproduccionId}")
    public ResponseEntity<List<PartoDTO>> findByReproduccionId(@PathVariable Integer reproduccionId) {
        return ResponseEntity.ok(partoService.findByReproduccionId(reproduccionId));
    }

    @PostMapping
    public ResponseEntity<PartoDTO> create(@RequestBody CreatePartoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(partoService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PartoDTO> update(@PathVariable Integer id, @RequestBody CreatePartoRequest request) {
        try {
            return ResponseEntity.ok(partoService.update(id, request));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            partoService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
