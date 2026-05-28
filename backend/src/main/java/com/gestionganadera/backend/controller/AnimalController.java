package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.AnimalDTO;
import com.gestionganadera.backend.dto.CreateAnimalRequest;
import com.gestionganadera.backend.service.AnimalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/animales")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Animales", description = "CRUD de animales del inventario ganadero")
public class AnimalController {

    private final AnimalService animalService;

    @GetMapping
    @Operation(summary = "Listar animales", description = "Obtiene todos los animales del usuario autenticado")
    public ResponseEntity<List<AnimalDTO>> getAllAnimales() {
        List<AnimalDTO> animales = animalService.findAll().stream()
                .map(AnimalDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(animales);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener animal por ID")
    public ResponseEntity<AnimalDTO> getAnimalById(@PathVariable @NonNull Integer id) {
        return animalService.findById(id)
                .map(AnimalDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear animal", description = "Crea un nuevo animal en el inventario")
    public ResponseEntity<AnimalDTO> createAnimal(@Valid @RequestBody @NonNull CreateAnimalRequest request) {
        return ResponseEntity.ok(AnimalDTO.fromEntity(animalService.save(request)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar animal")
    public ResponseEntity<AnimalDTO> updateAnimal(@PathVariable @NonNull Integer id, @Valid @RequestBody @NonNull CreateAnimalRequest request) {
        return ResponseEntity.ok(AnimalDTO.fromEntity(animalService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar animal")
    public ResponseEntity<Void> deleteAnimal(@PathVariable @NonNull Integer id) {
        animalService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/lote/{loteId}")
    @Operation(summary = "Animales por lote", description = "Obtiene animales filtrados por ID de lote")
    public ResponseEntity<List<AnimalDTO>> getAnimalesByLote(@PathVariable @NonNull Integer loteId) {
        List<AnimalDTO> animales = animalService.findByLoteId(loteId).stream()
                .map(AnimalDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(animales);
    }

    @GetMapping("/finca/{fincaId}")
    @Operation(summary = "Animales por finca", description = "Obtiene animales filtrados por ID de finca")
    public ResponseEntity<List<AnimalDTO>> getAnimalesByFinca(@PathVariable @NonNull Integer fincaId) {
        List<AnimalDTO> animales = animalService.findByFincaId(fincaId).stream()
                .map(AnimalDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(animales);
    }
}
