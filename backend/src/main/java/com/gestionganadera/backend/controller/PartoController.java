package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.CreatePartoRequest;
import com.gestionganadera.backend.dto.PartoDTO;
import com.gestionganadera.backend.service.PartoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/partos")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Partos", description = "Registro de partos asociados a reproducciones")
public class PartoController {

    private final PartoService partoService;

    @GetMapping
    @Operation(summary = "Listar partos")
    public ResponseEntity<List<PartoDTO>> findAll() {
        return ResponseEntity.ok(partoService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener parto por ID")
    public ResponseEntity<PartoDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(partoService.findById(id));
    }

    @GetMapping("/por-reproduccion/{reproduccionId}")
    @Operation(summary = "Partos por reproducción")
    public ResponseEntity<List<PartoDTO>> findByReproduccionId(@PathVariable Integer reproduccionId) {
        return ResponseEntity.ok(partoService.findByReproduccionId(reproduccionId));
    }

    @PostMapping
    @Operation(summary = "Registrar parto")
    public ResponseEntity<PartoDTO> create(@RequestBody CreatePartoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(partoService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar parto")
    public ResponseEntity<PartoDTO> update(@PathVariable Integer id, @RequestBody CreatePartoRequest request) {
        return ResponseEntity.ok(partoService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar parto")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        partoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
