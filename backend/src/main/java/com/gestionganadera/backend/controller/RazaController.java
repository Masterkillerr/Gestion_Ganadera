package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.CreateRazaRequest;
import com.gestionganadera.backend.model.Raza;
import com.gestionganadera.backend.service.RazaService;
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
public class RazaController {
    private final RazaService razaService;

    @GetMapping
    public ResponseEntity<List<Raza>> findAll() {
        return ResponseEntity.ok(razaService.findAll());
    }

    @PostMapping
    public ResponseEntity<Raza> createRaza(@Valid @RequestBody CreateRazaRequest request) {
        return ResponseEntity.ok(razaService.save(request));
    }
}
