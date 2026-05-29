package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.model.Sexo;
import com.gestionganadera.backend.repository.SexoRepository;
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
@RequestMapping("/sexo")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Catálogos", description = "Catálogos base del sistema")
public class SexoController {
    private final SexoRepository repository;

    @GetMapping
    @Operation(summary = "Listar sexos")
    public ResponseEntity<List<Sexo>> findAll() {
        return ResponseEntity.ok(repository.findAll());
    }
}
