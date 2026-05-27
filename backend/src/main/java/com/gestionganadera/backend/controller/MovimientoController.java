package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.CreateMovimientoRequest;
import com.gestionganadera.backend.dto.MovimientoDTO;
import com.gestionganadera.backend.service.MovimientoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movimientos")
@RequiredArgsConstructor
public class MovimientoController {

    private final MovimientoService movimientoService;

    @GetMapping("/recent")
    public ResponseEntity<List<MovimientoDTO>> getRecent() {
        return ResponseEntity.ok(movimientoService.getRecent());
    }

    @GetMapping
    public ResponseEntity<List<MovimientoDTO>> getAll() {
        return ResponseEntity.ok(movimientoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimientoDTO> getById(@PathVariable @NonNull Integer id) {
        return ResponseEntity.ok(movimientoService.findById(id));
    }

    @PostMapping
    public ResponseEntity<MovimientoDTO> create(@Valid @RequestBody @NonNull CreateMovimientoRequest request) {
        return ResponseEntity.ok(movimientoService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovimientoDTO> update(@PathVariable @NonNull Integer id,
                                                  @Valid @RequestBody @NonNull CreateMovimientoRequest request) {
        return ResponseEntity.ok(movimientoService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @NonNull Integer id) {
        movimientoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
