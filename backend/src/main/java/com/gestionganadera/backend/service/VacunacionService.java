package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateVacunacionRequest;
import com.gestionganadera.backend.model.Animal;
import com.gestionganadera.backend.model.Finca;
import com.gestionganadera.backend.model.Vacuna;
import com.gestionganadera.backend.model.Vacunacion;
import com.gestionganadera.backend.model.Usuario;
import com.gestionganadera.backend.repository.AnimalRepository;
import com.gestionganadera.backend.repository.FincaRepository;
import com.gestionganadera.backend.repository.VacunaRepository;
import com.gestionganadera.backend.repository.VacunacionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VacunacionService {
    private final VacunacionRepository repository;
    private final AnimalRepository animalRepository;
    private final FincaRepository fincaRepository;
    private final VacunaRepository vacunaRepository;

    private Usuario getCurrentUser() {
        return (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
    }

    private List<Integer> getUserFincaIds() {
        return fincaRepository.findByPropietario(getCurrentUser())
                .stream().map(Finca::getId).collect(Collectors.toList());
    }

    private Animal getAuthorizedAnimal(Integer animalId) {
        return animalRepository.findByIdAndFincaIdIn(animalId, getUserFincaIds())
                .orElseThrow(() -> new EntityNotFoundException("Animal no encontrado o no autorizado"));
    }

    public List<Vacunacion> findByAnimalId(@NonNull Integer animalId) {
        getAuthorizedAnimal(animalId);
        return repository.findByAnimalId(animalId);
    }

    public Vacunacion save(@NonNull CreateVacunacionRequest request) {
        Animal animal = getAuthorizedAnimal(request.getAnimalId());
        Vacuna vacuna = vacunaRepository.findById(request.getVacunaId())
                .orElseThrow(() -> new EntityNotFoundException("Vacuna no encontrada: " + request.getVacunaId()));

        Vacunacion entity = new Vacunacion();
        entity.setAnimal(animal);
        entity.setVacuna(vacuna);
        entity.setFecha(request.getFecha());
        entity.setProximaDosis(request.getProximaDosis());
        entity.setObservaciones(request.getObservaciones());
        return repository.save(entity);
    }

    public void delete(@NonNull Integer id) {
        Vacunacion entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vacunacion no encontrada"));
        getAuthorizedAnimal(entity.getAnimal().getId());
        repository.deleteById(id);
    }
}
