package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.CreateFincaRequest;
import com.gestionganadera.backend.dto.FincaStatsDTO;
import com.gestionganadera.backend.model.Finca;
import com.gestionganadera.backend.service.FincaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fincas")
@RequiredArgsConstructor

@PreAuthorize("isAuthenticated()")
public class FincaController {

    private final FincaService fincaService;

    @GetMapping
    public ResponseEntity<List<Finca>> getAllFincas() {
        return ResponseEntity.ok(fincaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Finca> getFincaById(@PathVariable @NonNull Integer id) {
        return fincaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Finca> createFinca(@Valid @RequestBody @NonNull CreateFincaRequest request) {
        return ResponseEntity.ok(fincaService.save(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Finca> updateFinca(@PathVariable @NonNull Integer id, @Valid @RequestBody @NonNull CreateFincaRequest request) {
        return ResponseEntity.ok(fincaService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFinca(@PathVariable @NonNull Integer id) {
        fincaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<FincaStatsDTO> getFincaStats(@PathVariable @NonNull Integer id) {
        return ResponseEntity.ok(fincaService.getStats(id));
    }
}
