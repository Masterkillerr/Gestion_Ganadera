package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.model.TipoEvento;
import com.gestionganadera.backend.repository.TipoEventoRepository;
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
@RequestMapping("/tipo-evento")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Catálogos", description = "Catálogos base del sistema")
public class TipoEventoController {
    private final TipoEventoRepository repository;

    @GetMapping
    @Operation(summary = "Listar tipos de evento")
    public ResponseEntity<List<TipoEvento>> findAll() {
        return ResponseEntity.ok(repository.findAll());
    }
}
