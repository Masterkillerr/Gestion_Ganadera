package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.model.Especie;
import com.gestionganadera.backend.service.EspecieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/especies")
@RequiredArgsConstructor
public class EspecieController {
    private final EspecieService especieService;

    @GetMapping
    public ResponseEntity<List<Especie>> findAll() {
        return ResponseEntity.ok(especieService.findAll());
    }
}
