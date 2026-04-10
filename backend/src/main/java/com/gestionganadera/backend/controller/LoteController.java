package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.model.Lote;
import com.gestionganadera.backend.service.LoteService;
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
    public ResponseEntity<Lote> getLoteById(@PathVariable @NonNull String id) {
        return loteService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Lote> createLote(@RequestBody @NonNull Lote lote) {
        return ResponseEntity.ok(loteService.save(lote));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Lote> updateLote(@PathVariable @NonNull String id, @RequestBody @NonNull Lote lote) {
        return ResponseEntity.ok(loteService.update(id, lote));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLote(@PathVariable @NonNull String id) {
        loteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
