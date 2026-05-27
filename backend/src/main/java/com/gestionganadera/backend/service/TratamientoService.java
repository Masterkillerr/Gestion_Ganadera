package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateTratamientoRequest;
import com.gestionganadera.backend.model.Animal;
import com.gestionganadera.backend.model.Finca;
import com.gestionganadera.backend.model.Medicamento;
import com.gestionganadera.backend.model.Tratamiento;
import com.gestionganadera.backend.model.Usuario;
import com.gestionganadera.backend.repository.AnimalRepository;
import com.gestionganadera.backend.repository.FincaRepository;
import com.gestionganadera.backend.repository.MedicamentoRepository;
import com.gestionganadera.backend.repository.TratamientoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TratamientoService {
    private final TratamientoRepository repository;
    private final AnimalRepository animalRepository;
    private final FincaRepository fincaRepository;
    private final MedicamentoRepository medicamentoRepository;

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

    public List<Tratamiento> findByAnimalId(@NonNull Integer animalId) {
        getAuthorizedAnimal(animalId);
        return repository.findByAnimalId(animalId);
    }

    public Tratamiento save(@NonNull CreateTratamientoRequest request) {
        Animal animal = getAuthorizedAnimal(request.getAnimalId());
        Medicamento medicamento = medicamentoRepository.findById(request.getMedicamentoId())
                .orElseThrow(() -> new EntityNotFoundException("Medicamento no encontrado: " + request.getMedicamentoId()));

        Tratamiento entity = new Tratamiento();
        entity.setAnimal(animal);
        entity.setMedicamento(medicamento);
        entity.setDosis(request.getDosis());
        entity.setFechaInicio(request.getFechaInicio());
        entity.setFechaFin(request.getFechaFin());
        entity.setDiasRetiro(request.getDiasRetiro());
        entity.setObservaciones(request.getObservaciones());
        return repository.save(entity);
    }

    public void delete(@NonNull Integer id) {
        Tratamiento entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tratamiento no encontrado"));
        getAuthorizedAnimal(entity.getAnimal().getId());
        repository.deleteById(id);
    }
}
