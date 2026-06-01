package com.gestionganadera.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

import com.gestionganadera.backend.service.AnimalService;
import com.gestionganadera.backend.service.ProduccionService;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequiredArgsConstructor
@Tag(name = "Health", description = "Health check and dashboard metrics")
public class HealthController {
    private final ProduccionService produccionService;
    private final AnimalService animalService;

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Returns OK to wake the backend from idle")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "ok",
            "timestamp", Instant.now().toString()
        ));
    }

    @GetMapping("/metrics/promedio-leche")
    @Operation(summary = "Promedio de leche por día")
    public ResponseEntity<BigDecimal> getPromedioLeche() {
        return ResponseEntity.ok(produccionService.getPromedioProduccionDiaria());
    }

    @GetMapping("/metrics/vacas-lactancia")
    @Operation(summary = "Cantidad de animales en lactancia")
    public ResponseEntity<Long> getVacasLactancia() {
        return ResponseEntity.ok(animalService.getCountByEstado("Lactancia"));
    }

    @GetMapping("/metrics/en-tratamiento")
    @Operation(summary = "Cantidad de animales en tratamiento")
    public ResponseEntity<Long> getEnTratamiento() {
        return ResponseEntity.ok(animalService.getCountEnTratamiento());
    }

    @GetMapping("/metrics/distribucion-edad")
    @Operation(summary = "Distribución de animales por rango etario")
    public ResponseEntity<Map<String, Long>> getDistribucionEdad() {
        return ResponseEntity.ok(animalService.getDistribucionEdad());
    }
}
