package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateAnimalRequest;
import com.gestionganadera.backend.model.Animal;
import com.gestionganadera.backend.model.Sexo;
import com.gestionganadera.backend.model.EstadoAnimal;
import com.gestionganadera.backend.repository.AnimalRepository;
import com.gestionganadera.backend.repository.RazaRepository;
import com.gestionganadera.backend.repository.SexoRepository;
import com.gestionganadera.backend.repository.EstadoAnimalRepository;
import com.gestionganadera.backend.exception.DuplicateResourceException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AnimalService {

    private final AnimalRepository animalRepository;
    private final RazaRepository razaRepository;
    private final SexoRepository sexoRepository;
    private final EstadoAnimalRepository estadoAnimalRepository;

    public Page<Animal> findAll(Pageable pageable) {
        return animalRepository.findAll(pageable);
    }

    public Page<Animal> findAllFiltered(String search, String estado, String sexo, Pageable pageable) {
        Specification<Animal> spec = (root, query, cb) -> cb.conjunction();

        if (search != null && !search.isBlank()) {
            String pattern = "%" + search.trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) ->
                cb.or(
                    cb.like(cb.lower(root.get("identificadorArete")), pattern),
                    cb.like(cb.lower(root.get("nombre")), pattern)
                )
            );
        }

        if (estado != null && !estado.isBlank()) {
            String finalEstado = estado;
            spec = spec.and((root, query, cb) ->
                cb.equal(cb.lower(root.join("estadoAnimal").get("nombre")), finalEstado.toLowerCase())
            );
        }

        if (sexo != null && !sexo.isBlank()) {
            String finalSexo = sexo;
            spec = spec.and((root, query, cb) ->
                cb.equal(cb.lower(root.join("sexo").get("nombre")), finalSexo.toLowerCase())
            );
        }

        return animalRepository.findAll(spec, pageable);
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
            animal.setSexo(sexoRepository.findById(request.getSexoId())
                    .orElseThrow(() -> new EntityNotFoundException("Sexo con ID " + request.getSexoId() + " no encontrado")));
        }
        if (request.getEstadoAnimalId() != null) {
            animal.setEstadoAnimal(estadoAnimalRepository.findById(request.getEstadoAnimalId())
                    .orElseThrow(() -> new EntityNotFoundException("Estado animal con ID " + request.getEstadoAnimalId() + " no encontrado")));
        }
        if (request.getRazaId() != null) {
            animal.setRaza(razaRepository.findById(request.getRazaId())
                    .orElseThrow(() -> new EntityNotFoundException("Raza con ID " + request.getRazaId() + " no encontrada")));
        }
        if (request.getMadreId() != null) {
            animal.setMadre(animalRepository.findById(request.getMadreId())
                    .orElseThrow(() -> new EntityNotFoundException("Animal madre con ID " + request.getMadreId() + " no encontrado")));
        }
        if (request.getPadreId() != null) {
            animal.setPadre(animalRepository.findById(request.getPadreId())
                    .orElseThrow(() -> new EntityNotFoundException("Animal padre con ID " + request.getPadreId() + " no encontrado")));
        }

        return animal;
    }

    @Transactional
    public Animal save(@NonNull CreateAnimalRequest request) {
        if (animalRepository.findByIdentificadorArete(request.getIdentificadorArete()).isPresent()) {
            throw new DuplicateResourceException("Ya existe un animal con el arete: " + request.getIdentificadorArete());
        }
        return animalRepository.save(fromRequest(request));
    }

    @Transactional
    public Animal update(@NonNull Integer id, @NonNull CreateAnimalRequest request) {
     Animal existingAnimal = animalRepository.findById(id)
     .orElseThrow(() -> new EntityNotFoundException("Animal con ID " + id + " no encontrado"));
  
     // Si el arete cambió, verificar que el nuevo no esté duplicado
     if (!existingAnimal.getIdentificadorArete().equals(request.getIdentificadorArete())) {
      if (animalRepository.findByIdentificadorArete(request.getIdentificadorArete()).isPresent()) {
       throw new DuplicateResourceException("Ya existe otro animal con el arete: " + request.getIdentificadorArete());
      }
     }

     existingAnimal.setIdentificadorArete(request.getIdentificadorArete());
     existingAnimal.setNombre(request.getNombre());
     existingAnimal.setFechaNacimiento(request.getFechaNacimiento());
     existingAnimal.setPesoActualKg(request.getPesoActualKg());
     existingAnimal.setFotoUrl(request.getFotoUrl());

     if (request.getSexoId() != null) {
     existingAnimal.setSexo(sexoRepository.findById(request.getSexoId())
     .orElseThrow(() -> new EntityNotFoundException("Sexo con ID " + request.getSexoId() + " no encontrado")));
     }
     if (request.getEstadoAnimalId() != null) {
     existingAnimal.setEstadoAnimal(estadoAnimalRepository.findById(request.getEstadoAnimalId())
     .orElseThrow(() -> new EntityNotFoundException("Estado animal con ID " + request.getEstadoAnimalId() + " no encontrado")));
     }
     if (request.getRazaId() != null) {
     existingAnimal.setRaza(razaRepository.findById(request.getRazaId())
     .orElseThrow(() -> new EntityNotFoundException("Raza con ID " + request.getRazaId() + " no encontrada")));
     }
     if (request.getMadreId() != null) {
     existingAnimal.setMadre(animalRepository.findById(request.getMadreId())
     .orElseThrow(() -> new EntityNotFoundException("Animal madre con ID " + request.getMadreId() + " no encontrado")));
     }
     if (request.getPadreId() != null) {
     existingAnimal.setPadre(animalRepository.findById(request.getPadreId())
     .orElseThrow(() -> new EntityNotFoundException("Animal padre con ID " + request.getPadreId() + " no encontrado")));
     }

     return animalRepository.save(existingAnimal);
    }

    public long getCountByEstado(String estado) {
        return animalRepository.countByEstadoAnimal_Nombre(estado);
    }

    public long getCountEnTratamiento() {
        return animalRepository.countByEstadoAnimal_NombreIgnoreCase("En Tratamiento");
    }

    public Map<String, Long> getDistribucionEdad() {
        long totalAnimales = animalRepository.count();
        List<Object[]> raw = animalRepository.findDistribucionEdadRaw();

        Map<String, Long> dist = new LinkedHashMap<>();
        dist.put("terneros", 0L);
        dist.put("novillos", 0L);
        dist.put("adultos", 0L);

        long conFecha = 0;
        for (Object[] row : raw) {
            String categoria = (String) row[0];
            Long count = ((Number) row[1]).longValue();
            dist.put(categoria, count);
            conFecha += count;
        }

        dist.put("sinDatos", totalAnimales - conFecha);
        return dist;
    }

    @Transactional
    public void delete(@NonNull Integer id) {
        animalRepository.findById(id)
                .ifPresentOrElse(
                    animal -> animalRepository.deleteById(animal.getId()),
                    () -> { throw new EntityNotFoundException("Animal no encontrado con ID " + id); }
                );
    }
}
