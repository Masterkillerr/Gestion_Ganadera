package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.model.Animal;
import com.gestionganadera.backend.service.AnimalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.gestionganadera.backend.dto.AnimalDTO;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/animales")
@RequiredArgsConstructor
public class AnimalController {

    private final AnimalService animalService;

    @GetMapping
    public ResponseEntity<List<AnimalDTO>> getAllAnimales() {
        List<AnimalDTO> animales = animalService.findAll().stream()
                .map(AnimalDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(animales);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnimalDTO> getAnimalById(@PathVariable @NonNull Integer id) {
        return animalService.findById(id)
                .map(AnimalDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AnimalDTO> createAnimal(@RequestBody @NonNull Animal animal) {
        return ResponseEntity.ok(AnimalDTO.fromEntity(animalService.save(animal)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnimalDTO> updateAnimal(@PathVariable @NonNull Integer id, @RequestBody @NonNull Animal animal) {
        return ResponseEntity.ok(AnimalDTO.fromEntity(animalService.update(id, animal)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnimal(@PathVariable @NonNull Integer id) {
        animalService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
