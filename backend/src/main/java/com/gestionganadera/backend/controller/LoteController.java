package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.CreateLoteRequest;
import com.gestionganadera.backend.model.Lote;
import com.gestionganadera.backend.service.LoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lotes")
@RequiredArgsConstructor
public class LoteController {

    private final LoteService loteService;

    @GetMapping
    public ResponseEntity<List<Lote>> getAllLotes() {
        return ResponseEntity.ok(loteService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Lote> getLoteById(@PathVariable @NonNull Integer id) {
        return loteService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Lote> createLote(@Valid @RequestBody @NonNull CreateLoteRequest request) {
        return ResponseEntity.ok(loteService.save(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Lote> updateLote(@PathVariable @NonNull Integer id, @Valid @RequestBody @NonNull CreateLoteRequest request) {
        return ResponseEntity.ok(loteService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLote(@PathVariable @NonNull Integer id) {
        loteService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/finca/{fincaId}")
    public ResponseEntity<List<Lote>> getLotesByFinca(@PathVariable @NonNull Integer fincaId) {
        return ResponseEntity.ok(loteService.findByFincaId(fincaId));
    }
}
