package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.CreateFincaRequest;
import com.gestionganadera.backend.dto.FincaStatsDTO;
import com.gestionganadera.backend.model.Finca;
import com.gestionganadera.backend.service.FincaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Fincas", description = "CRUD de fincas y estadísticas")
public class FincaController {

    private final FincaService fincaService;

    @GetMapping
    @Operation(summary = "Listar fincas", description = "Obtiene todas las fincas del usuario autenticado")
    public ResponseEntity<List<Finca>> getAllFincas() {
        return ResponseEntity.ok(fincaService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener finca por ID")
    public ResponseEntity<Finca> getFincaById(@PathVariable @NonNull Integer id) {
        return fincaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear finca", description = "Crea una nueva finca asignada al usuario autenticado")
    public ResponseEntity<Finca> createFinca(@Valid @RequestBody @NonNull CreateFincaRequest request) {
        return ResponseEntity.ok(fincaService.save(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar finca")
    public ResponseEntity<Finca> updateFinca(@PathVariable @NonNull Integer id, @Valid @RequestBody @NonNull CreateFincaRequest request) {
        return ResponseEntity.ok(fincaService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar finca")
    public ResponseEntity<Void> deleteFinca(@PathVariable @NonNull Integer id) {
        fincaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/stats")
    @Operation(summary = "Estadísticas de finca", description = "Obtiene total de animales, lotes y distribución por sexo/estado")
    public ResponseEntity<FincaStatsDTO> getFincaStats(@PathVariable @NonNull Integer id) {
        return ResponseEntity.ok(fincaService.getStats(id));
    }
}
