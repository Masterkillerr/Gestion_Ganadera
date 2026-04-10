package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.model.Finca;
import com.gestionganadera.backend.service.FincaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fincas")
@RequiredArgsConstructor
public class FincaController {

    private final FincaService fincaService;

    @GetMapping
    public ResponseEntity<List<Finca>> getAllFincas() {
        return ResponseEntity.ok(fincaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Finca> getFincaById(@PathVariable @NonNull String id) {
        return fincaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Finca> createFinca(@RequestBody @NonNull Finca finca) {
        return ResponseEntity.ok(fincaService.save(finca));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Finca> updateFinca(@PathVariable @NonNull String id, @RequestBody @NonNull Finca finca) {
        return ResponseEntity.ok(fincaService.update(id, finca));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteFinca(@PathVariable @NonNull String id) {
        fincaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
