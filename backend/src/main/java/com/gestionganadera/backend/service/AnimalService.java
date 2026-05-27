package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateAnimalRequest;
import com.gestionganadera.backend.model.Animal;
import com.gestionganadera.backend.model.Finca;
import com.gestionganadera.backend.model.Usuario;
import com.gestionganadera.backend.repository.AnimalRepository;
import com.gestionganadera.backend.repository.CategoriaRepository;
import com.gestionganadera.backend.repository.FincaRepository;
import com.gestionganadera.backend.repository.LoteRepository;
import com.gestionganadera.backend.repository.RazaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnimalService {

    private final AnimalRepository animalRepository;
    private final RazaRepository razaRepository;
    private final CategoriaRepository categoriaRepository;
    private final LoteRepository loteRepository;
    private final FincaRepository fincaRepository;

    private Usuario getCurrentUser() {
        return (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
    }

    private List<Integer> getUserFincaIds() {
        return fincaRepository.findByPropietario(getCurrentUser())
                .stream().map(Finca::getId).collect(Collectors.toList());
    }

    public List<Animal> findAll() {
        return animalRepository.findByFincaIdIn(getUserFincaIds());
    }

    public List<Animal> findByLoteId(@NonNull Integer loteId) {
        return animalRepository.findByLoteIdAndFincaIdIn(loteId, getUserFincaIds());
    }

    public List<Animal> findByFincaId(@NonNull Integer fincaId) {
        Usuario currentUser = getCurrentUser();
        return fincaRepository.findByIdAndPropietario(fincaId, currentUser)
                .map(finca -> animalRepository.findByFincaId(fincaId))
                .orElseThrow(() -> new RuntimeException("Finca no encontrada o no autorizada"));
    }

    public Optional<Animal> findById(@NonNull Integer id) {
        return animalRepository.findByIdAndFincaIdIn(id, getUserFincaIds());
    }

    private Animal fromRequest(CreateAnimalRequest request) {
        Usuario currentUser = getCurrentUser();

        Animal animal = new Animal();
        animal.setIdentificadorArete(request.getIdentificadorArete());
        animal.setNombre(request.getNombre());
        animal.setSexo(request.getSexo());
        animal.setFechaNacimiento(request.getFechaNacimiento());
        animal.setPesoActual(request.getPesoActual());
        animal.setEstado(request.getEstado());
        animal.setFotoUrl(request.getFotoUrl());

        if (request.getRazaId() != null) {
            razaRepository.findById(request.getRazaId()).ifPresent(animal::setRaza);
        }
        if (request.getCategoriaId() != null) {
            categoriaRepository.findById(request.getCategoriaId()).ifPresent(animal::setCategoria);
        }
        if (request.getLoteId() != null) {
            loteRepository.findByIdAndFincaIdIn(request.getLoteId(), getUserFincaIds())
                    .ifPresentOrElse(animal::setLote,
                        () -> { throw new RuntimeException("Lote no encontrado o no autorizado"); });
        }
        if (request.getFincaId() != null) {
            fincaRepository.findByIdAndPropietario(request.getFincaId(), currentUser)
                    .ifPresentOrElse(animal::setFinca,
                        () -> { throw new RuntimeException("Finca no encontrada o no autorizada"); });
        }
        if (request.getMadreId() != null) {
            animalRepository.findByIdAndFincaIdIn(request.getMadreId(), getUserFincaIds())
                    .ifPresent(animal::setMadre);
        }
        if (request.getPadreId() != null) {
            animalRepository.findByIdAndFincaIdIn(request.getPadreId(), getUserFincaIds())
                    .ifPresent(animal::setPadre);
        }

        return animal;
    }

    public Animal save(@NonNull CreateAnimalRequest request) {
        return animalRepository.save(fromRequest(request));
    }

    public Animal update(@NonNull Integer id, @NonNull CreateAnimalRequest request) {
        Usuario currentUser = getCurrentUser();
        List<Integer> userFincaIds = getUserFincaIds();

        return animalRepository.findByIdAndFincaIdIn(id, userFincaIds)
                .map(existing -> {
                    existing.setIdentificadorArete(request.getIdentificadorArete());
                    existing.setNombre(request.getNombre());
                    existing.setSexo(request.getSexo());
                    existing.setFechaNacimiento(request.getFechaNacimiento());
                    existing.setPesoActual(request.getPesoActual());
                    existing.setEstado(request.getEstado());
                    existing.setFotoUrl(request.getFotoUrl());

                    if (request.getRazaId() != null) {
                        razaRepository.findById(request.getRazaId()).ifPresent(existing::setRaza);
                    }
                    if (request.getCategoriaId() != null) {
                        categoriaRepository.findById(request.getCategoriaId()).ifPresent(existing::setCategoria);
                    }
                    if (request.getLoteId() != null) {
                        loteRepository.findByIdAndFincaIdIn(request.getLoteId(), userFincaIds)
                                .ifPresentOrElse(existing::setLote,
                                    () -> { throw new RuntimeException("Lote no encontrado o no autorizado"); });
                    }
                    if (request.getFincaId() != null) {
                        fincaRepository.findByIdAndPropietario(request.getFincaId(), currentUser)
                                .ifPresentOrElse(existing::setFinca,
                                    () -> { throw new RuntimeException("Finca no encontrada o no autorizada"); });
                    }
                    if (request.getMadreId() != null) {
                        animalRepository.findByIdAndFincaIdIn(request.getMadreId(), userFincaIds)
                                .ifPresent(existing::setMadre);
                    }
                    if (request.getPadreId() != null) {
                        animalRepository.findByIdAndFincaIdIn(request.getPadreId(), userFincaIds)
                                .ifPresent(existing::setPadre);
                    }

                    return animalRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Animal no encontrado"));
    }

    public void delete(@NonNull Integer id) {
        animalRepository.findByIdAndFincaIdIn(id, getUserFincaIds())
                .ifPresentOrElse(
                    animal -> animalRepository.deleteById(id),
                    () -> { throw new RuntimeException("Animal no encontrado"); }
                );
    }
}
