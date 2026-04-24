package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.model.Animal;
import com.gestionganadera.backend.service.AnimalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/animales")
@RequiredArgsConstructor
public class AnimalController {

    private final AnimalService animalService;

    @GetMapping
    public ResponseEntity<List<Animal>> getAllAnimales() {
        return ResponseEntity.ok(animalService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Animal> getAnimalById(@PathVariable @NonNull Integer id) {
        return animalService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Animal> createAnimal(@RequestBody @NonNull Animal animal) {
        return ResponseEntity.ok(animalService.save(animal));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Animal> updateAnimal(@PathVariable @NonNull Integer id, @RequestBody @NonNull Animal animal) {
        return ResponseEntity.ok(animalService.update(id, animal));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAnimal(@PathVariable @NonNull Integer id) {
        animalService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
