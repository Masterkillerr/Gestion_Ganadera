package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.model.TurnoProduccion;
import com.gestionganadera.backend.repository.TurnoProduccionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/turno-produccion")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Catálogos", description = "Catálogos base del sistema")
public class TurnoProduccionController {
    private final TurnoProduccionRepository repository;

    @GetMapping
    @Operation(summary = "Listar turnos de producción")
    public ResponseEntity<List<TurnoProduccion>> findAll() {
        return ResponseEntity.ok(repository.findAll());
    }
}
