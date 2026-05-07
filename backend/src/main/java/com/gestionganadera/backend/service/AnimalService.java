package com.gestionganadera.backend.service;

import com.gestionganadera.backend.model.Animal;
import com.gestionganadera.backend.repository.AnimalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnimalService {

    private final AnimalRepository animalRepository;

    public List<Animal> findAll() {
        return animalRepository.findAll();
    }

    public Optional<Animal> findById(@NonNull Integer id) {
        return animalRepository.findById(id);
    }

    public Animal save(@NonNull Animal animal) {
        return animalRepository.save(animal);
    }

    public Animal update(@NonNull Integer id, @NonNull Animal animal) {
        return animalRepository.findById(id)
                .map(existing -> {
                    existing.setIdentificadorArete(animal.getIdentificadorArete());
                    existing.setNombre(animal.getNombre());
                    existing.setSexo(animal.getSexo());
                    existing.setEspecie(animal.getEspecie());
                    existing.setRaza(animal.getRaza());
                    existing.setCategoria(animal.getCategoria());
                    existing.setLote(animal.getLote());
                    existing.setFechaNacimiento(animal.getFechaNacimiento());
                    existing.setPesoNacimiento(animal.getPesoNacimiento());
                    existing.setPesoActual(animal.getPesoActual());
                    existing.setEstado(animal.getEstado());
                    existing.setFinca(animal.getFinca());
                    existing.setMadre(animal.getMadre());
                    existing.setPadre(animal.getPadre());
                    existing.setFotoUrl(animal.getFotoUrl());
                    return animalRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Animal no encontrado"));
    }

    public void delete(@NonNull Integer id) {
        animalRepository.deleteById(id);
    }
}
