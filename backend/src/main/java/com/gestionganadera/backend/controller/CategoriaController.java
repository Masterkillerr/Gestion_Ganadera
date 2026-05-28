package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.CreateCategoriaRequest;
import com.gestionganadera.backend.model.Categoria;
import com.gestionganadera.backend.service.CategoriaService;
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
@RequestMapping("/categorias")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Categorías", description = "Catálogo de categorías de animales")
public class CategoriaController {
    private final CategoriaService categoriaService;

    @GetMapping
    @Operation(summary = "Listar categorías")
    public ResponseEntity<List<Categoria>> findAll() {
        return ResponseEntity.ok(categoriaService.findAll());
    }

    @PostMapping
    @Operation(summary = "Crear categoría")
    public ResponseEntity<Categoria> createCategoria(@Valid @RequestBody CreateCategoriaRequest request) {
        return ResponseEntity.ok(categoriaService.save(request));
    }
}
