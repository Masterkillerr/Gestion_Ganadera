package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.model.Raza;
import com.gestionganadera.backend.service.RazaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/razas")
@RequiredArgsConstructor
public class RazaController {
    private final RazaService razaService;

    @GetMapping
    public ResponseEntity<List<Raza>> findAll() {
        return ResponseEntity.ok(razaService.findAll());
    }
}
