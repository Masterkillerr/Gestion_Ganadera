package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.CreateReproduccionRequest;
import com.gestionganadera.backend.dto.PartosProximosDTO;
import com.gestionganadera.backend.dto.ReproduccionDTO;
import com.gestionganadera.backend.service.ReproduccionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reproduccion")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Reproducción", description = "Registro de montas, inseminaciones y seguimiento de gestación")
public class ReproduccionController {

    private final ReproduccionService reproduccionService;

    @GetMapping("/proximos-partos")
    @Operation(summary = "Próximos partos", description = "Obtiene animales gestantes con fecha de parto estimada")
    public ResponseEntity<List<PartosProximosDTO>> getProximosPartos() {
        return ResponseEntity.ok(reproduccionService.getProximosPartos());
    }

    @GetMapping
    @Operation(summary = "Listar reproducciones")
    public ResponseEntity<List<ReproduccionDTO>> findAll() {
        return ResponseEntity.ok(reproduccionService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener reproducción por ID")
    public ResponseEntity<ReproduccionDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(reproduccionService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Registrar reproducción", description = "Crea un nuevo registro de monta/inseminación")
    public ResponseEntity<ReproduccionDTO> create(@RequestBody CreateReproduccionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reproduccionService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar reproducción")
    public ResponseEntity<ReproduccionDTO> update(@PathVariable Integer id, @RequestBody CreateReproduccionRequest request) {
        return ResponseEntity.ok(reproduccionService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar reproducción")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        reproduccionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
