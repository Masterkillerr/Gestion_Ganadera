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
                    existing.setIdentificador(animal.getIdentificador());
                    existing.setEspecie(animal.getEspecie());
                    existing.setRaza(animal.getRaza());
                    existing.setCategoria(animal.getCategoria());
                    existing.setLote(animal.getLote());
                    existing.setFechaNacimiento(animal.getFechaNacimiento());
                    existing.setPeso(animal.getPeso());
                    existing.setEstado(animal.getEstado());
                    existing.setFinca(animal.getFinca());
                    return animalRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Animal no encontrado"));
    }

    public void delete(@NonNull Integer id) {
        animalRepository.deleteById(id);
    }
}
