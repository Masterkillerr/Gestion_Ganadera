package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.UsuarioDTO;
import com.gestionganadera.backend.service.UsuarioDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioDetailsService usuarioDetailsService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsuarioDTO>> getAllUsuarios() {
        // TODO: Implement with proper service and DTO mapping
        return ResponseEntity.ok(List.of());
    }
}
