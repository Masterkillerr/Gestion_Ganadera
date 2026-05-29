package com.gestionganadera.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@Tag(name = "Health", description = "Health check endpoint to wake up the backend")
public class HealthController {

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Returns OK to wake the backend from idle")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "ok",
            "timestamp", Instant.now().toString()
        ));
    }
}
