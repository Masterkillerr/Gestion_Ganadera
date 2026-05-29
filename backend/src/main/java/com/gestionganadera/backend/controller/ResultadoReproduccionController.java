package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.model.ResultadoReproduccion;
import com.gestionganadera.backend.repository.ResultadoReproduccionRepository;
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
@RequestMapping("/resultado-reproduccion")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Catálogos", description = "Catálogos base del sistema")
public class ResultadoReproduccionController {
    private final ResultadoReproduccionRepository repository;

    @GetMapping
    @Operation(summary = "Listar resultados de reproducción")
    public ResponseEntity<List<ResultadoReproduccion>> findAll() {
        return ResponseEntity.ok(repository.findAll());
    }
}
