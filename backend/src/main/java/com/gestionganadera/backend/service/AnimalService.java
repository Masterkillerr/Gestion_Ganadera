package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateAnimalRequest;
import com.gestionganadera.backend.model.Animal;
import com.gestionganadera.backend.model.Sexo;
import com.gestionganadera.backend.model.EstadoAnimal;
import com.gestionganadera.backend.repository.AnimalRepository;
import com.gestionganadera.backend.repository.RazaRepository;
import com.gestionganadera.backend.repository.SexoRepository;
import com.gestionganadera.backend.repository.EstadoAnimalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnimalService {

    private final AnimalRepository animalRepository;
    private final RazaRepository razaRepository;
    private final SexoRepository sexoRepository;
    private final EstadoAnimalRepository estadoAnimalRepository;

    public List<Animal> findAll() {
        return animalRepository.findAll();
    }

    public Optional<Animal> findById(@NonNull Integer id) {
        return animalRepository.findById(id);
    }

    public Optional<Animal> findByIdentificadorArete(@NonNull String arete) {
        return animalRepository.findByIdentificadorArete(arete);
    }

    private Animal fromRequest(CreateAnimalRequest request) {
        Animal animal = new Animal();
        animal.setIdentificadorArete(request.getIdentificadorArete());
        animal.setNombre(request.getNombre());
        animal.setFechaNacimiento(request.getFechaNacimiento());
        animal.setPesoActualKg(request.getPesoActualKg());
        animal.setFotoUrl(request.getFotoUrl());

        if (request.getSexoId() != null) {
            sexoRepository.findById(request.getSexoId()).ifPresent(animal::setSexo);
        }
        if (request.getEstadoAnimalId() != null) {
            estadoAnimalRepository.findById(request.getEstadoAnimalId()).ifPresent(animal::setEstadoAnimal);
        }
        if (request.getRazaId() != null) {
            razaRepository.findById(request.getRazaId()).ifPresent(animal::setRaza);
        }
        if (request.getMadreId() != null) {
            animalRepository.findById(request.getMadreId()).ifPresent(animal::setMadre);
        }
        if (request.getPadreId() != null) {
            animalRepository.findById(request.getPadreId()).ifPresent(animal::setPadre);
        }

        return animal;
    }

    public Animal save(@NonNull CreateAnimalRequest request) {
        return animalRepository.save(fromRequest(request));
    }

    public Animal update(@NonNull Integer id, @NonNull CreateAnimalRequest request) {
        return animalRepository.findById(id)
                .map(existing -> {
                    existing.setIdentificadorArete(request.getIdentificadorArete());
                    existing.setNombre(request.getNombre());
                    existing.setFechaNacimiento(request.getFechaNacimiento());
                    existing.setPesoActualKg(request.getPesoActualKg());
                    existing.setFotoUrl(request.getFotoUrl());

                    if (request.getSexoId() != null) {
                        sexoRepository.findById(request.getSexoId()).ifPresent(existing::setSexo);
                    }
                    if (request.getEstadoAnimalId() != null) {
                        estadoAnimalRepository.findById(request.getEstadoAnimalId()).ifPresent(existing::setEstadoAnimal);
                    }
                    if (request.getRazaId() != null) {
                        razaRepository.findById(request.getRazaId()).ifPresent(existing::setRaza);
                    }
                    if (request.getMadreId() != null) {
                        animalRepository.findById(request.getMadreId()).ifPresent(existing::setMadre);
                    }
                    if (request.getPadreId() != null) {
                        animalRepository.findById(request.getPadreId()).ifPresent(existing::setPadre);
                    }

                    return animalRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Animal no encontrado"));
    }

    public void delete(@NonNull Integer id) {
        animalRepository.findById(id)
                .ifPresentOrElse(
                    animal -> animalRepository.deleteById(id),
                    () -> { throw new RuntimeException("Animal no encontrado"); }
                );
    }
}
