package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.model.EstadoAnimal;
import com.gestionganadera.backend.repository.EstadoAnimalRepository;
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
@RequestMapping("/estado-animal")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Catálogos", description = "Catálogos base del sistema")
public class EstadoAnimalController {
    private final EstadoAnimalRepository repository;

    @GetMapping
    @Operation(summary = "Listar estados de animal")
    public ResponseEntity<List<EstadoAnimal>> findAll() {
        return ResponseEntity.ok(repository.findAll());
    }
}
