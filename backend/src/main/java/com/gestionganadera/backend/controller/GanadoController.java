package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.model.Ganado;
import com.gestionganadera.backend.service.GanadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ganado")
@RequiredArgsConstructor
public class GanadoController {

    private final GanadoService ganadoService;

    @GetMapping
    public ResponseEntity<List<Ganado>> getAllGanado() {
        return ResponseEntity.ok(ganadoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ganado> getGanadoById(@PathVariable String id) {
        return ganadoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Ganado> createGanado(@RequestBody Ganado ganado) {
        return ResponseEntity.ok(ganadoService.save(ganado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ganado> updateGanado(@PathVariable String id, @RequestBody Ganado ganado) {
        return ResponseEntity.ok(ganadoService.update(id, ganado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGanado(@PathVariable String id) {
        ganadoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
