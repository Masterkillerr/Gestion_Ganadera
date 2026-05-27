package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateAlimentacionRequest;
import com.gestionganadera.backend.model.Alimentacion;
import com.gestionganadera.backend.model.Alimento;
import com.gestionganadera.backend.model.Animal;
import com.gestionganadera.backend.model.Finca;
import com.gestionganadera.backend.model.Usuario;
import com.gestionganadera.backend.repository.AlimentacionRepository;
import com.gestionganadera.backend.repository.AlimentoRepository;
import com.gestionganadera.backend.repository.AnimalRepository;
import com.gestionganadera.backend.repository.FincaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlimentacionService {
    private final AlimentacionRepository repository;
    private final AnimalRepository animalRepository;
    private final FincaRepository fincaRepository;
    private final AlimentoRepository alimentoRepository;

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

    public List<Alimentacion> findByAnimalId(@NonNull Integer animalId) {
        getAuthorizedAnimal(animalId);
        return repository.findByAnimalId(animalId);
    }

    public Alimentacion save(@NonNull CreateAlimentacionRequest request) {
        Animal animal = getAuthorizedAnimal(request.getAnimalId());
        Alimento alimento = alimentoRepository.findById(request.getAlimentoId())
                .orElseThrow(() -> new EntityNotFoundException("Alimento no encontrado: " + request.getAlimentoId()));

        Alimentacion entity = new Alimentacion();
        entity.setAnimal(animal);
        entity.setAlimento(alimento);
        entity.setCantidad(request.getCantidad());
        entity.setFecha(request.getFecha());
        entity.setObservaciones(request.getObservaciones());
        return repository.save(entity);
    }

    public void delete(@NonNull Integer id) {
        Alimentacion entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Alimentacion no encontrada"));
        getAuthorizedAnimal(entity.getAnimal().getId());
        repository.deleteById(id);
    }
}
