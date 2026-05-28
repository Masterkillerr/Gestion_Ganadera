package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.CreateRazaRequest;
import com.gestionganadera.backend.model.Raza;
import com.gestionganadera.backend.service.RazaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/razas")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Razas", description = "Catálogo de razas de ganado")
public class RazaController {
    private final RazaService razaService;

    @GetMapping
    @Operation(summary = "Listar razas")
    public ResponseEntity<List<Raza>> findAll() {
        return ResponseEntity.ok(razaService.findAll());
    }

    @PostMapping
    @Operation(summary = "Crear raza")
    public ResponseEntity<Raza> createRaza(@Valid @RequestBody CreateRazaRequest request) {
        return ResponseEntity.ok(razaService.save(request));
    }
}
