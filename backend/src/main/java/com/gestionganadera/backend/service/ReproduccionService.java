package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateReproduccionRequest;
import com.gestionganadera.backend.dto.PartosProximosDTO;
import com.gestionganadera.backend.dto.ReproduccionDTO;
import com.gestionganadera.backend.model.Animal;
import com.gestionganadera.backend.model.Finca;
import com.gestionganadera.backend.model.Reproduccion;
import com.gestionganadera.backend.model.Usuario;
import com.gestionganadera.backend.repository.AnimalRepository;
import com.gestionganadera.backend.repository.FincaRepository;
import com.gestionganadera.backend.repository.ReproduccionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReproduccionService {

    private final ReproduccionRepository reproduccionRepository;
    private final AnimalRepository animalRepository;
    private final FincaRepository fincaRepository;

    private Usuario getCurrentUser() {
        return (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
    }

    private List<Integer> getUserFincaIds() {
        return fincaRepository.findByPropietario(getCurrentUser())
                .stream().map(Finca::getId).collect(Collectors.toList());
    }

    private List<Integer> getUserAnimalIds() {
        List<Integer> fincaIds = getUserFincaIds();
        if (fincaIds.isEmpty()) return List.of();
        return animalRepository.findByFincaIdIn(fincaIds)
                .stream().map(Animal::getId).collect(Collectors.toList());
    }

    private boolean animalBelongsToUser(Integer animalId) {
        List<Integer> fincaIds = getUserFincaIds();
        if (fincaIds.isEmpty()) return false;
        return animalRepository.findByIdAndFincaIdIn(animalId, fincaIds).isPresent();
    }

    public List<ReproduccionDTO> findAll() {
        List<Integer> animalIds = getUserAnimalIds();
        if (animalIds.isEmpty()) return List.of();
        return reproduccionRepository.findByVacaIdInOrderByFechaMontaDesc(animalIds)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public ReproduccionDTO findById(Integer id) {
        Reproduccion r = reproduccionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reproducción no encontrada"));
        if (!animalBelongsToUser(r.getVaca().getId())) {
            throw new SecurityException("Acceso denegado");
        }
        return toDTO(r);
    }

    @Transactional
    public ReproduccionDTO create(CreateReproduccionRequest request) {
        if (!animalBelongsToUser(request.getVacaId())) {
            throw new SecurityException("Acceso denegado");
        }

        Reproduccion r = new Reproduccion();
        r.setVaca(animalRepository.findById(request.getVacaId())
                .orElseThrow(() -> new EntityNotFoundException("Vaca no encontrada")));
        if (request.getToroId() != null) {
            r.setToro(animalRepository.findById(request.getToroId())
                    .orElseThrow(() -> new EntityNotFoundException("Toro no encontrado")));
        }
        r.setFechaMonta(request.getFechaMonta());
        r.setTipo(request.getTipo());
        r.setResultado(request.getResultado());
        r.setFechaPartoEstimada(request.getFechaPartoEstimada());
        r.setObservaciones(request.getObservaciones());

        return toDTO(reproduccionRepository.save(r));
    }

    @Transactional
    public ReproduccionDTO update(Integer id, CreateReproduccionRequest request) {
        Reproduccion r = reproduccionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reproducción no encontrada"));
        if (!animalBelongsToUser(r.getVaca().getId())) {
            throw new SecurityException("Acceso denegado");
        }

        if (request.getVacaId() != null && !request.getVacaId().equals(r.getVaca().getId())) {
            if (!animalBelongsToUser(request.getVacaId())) {
                throw new SecurityException("Acceso denegado");
            }
            r.setVaca(animalRepository.findById(request.getVacaId())
                    .orElseThrow(() -> new EntityNotFoundException("Vaca no encontrada")));
        }
        if (request.getToroId() != null) {
            r.setToro(animalRepository.findById(request.getToroId())
                    .orElseThrow(() -> new EntityNotFoundException("Toro no encontrado")));
        }
        if (request.getFechaMonta() != null) r.setFechaMonta(request.getFechaMonta());
        if (request.getTipo() != null) r.setTipo(request.getTipo());
        if (request.getResultado() != null) r.setResultado(request.getResultado());
        if (request.getFechaPartoEstimada() != null) r.setFechaPartoEstimada(request.getFechaPartoEstimada());
        if (request.getObservaciones() != null) r.setObservaciones(request.getObservaciones());

        return toDTO(reproduccionRepository.save(r));
    }

    @Transactional
    public void delete(Integer id) {
        Reproduccion r = reproduccionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reproducción no encontrada"));
        if (!animalBelongsToUser(r.getVaca().getId())) {
            throw new SecurityException("Acceso denegado");
        }
        reproduccionRepository.delete(r);
    }

    // ── Existing method for upcoming births ──

    public List<PartosProximosDTO> getProximosPartos() {
        List<Integer> fincaIds = getUserFincaIds();
        if (fincaIds.isEmpty()) return List.of();

        LocalDate today = LocalDate.now();
        LocalDate twoMonthsFromNow = today.plusMonths(2);

        List<Integer> animalIds = animalRepository.findByFincaIdIn(fincaIds)
                .stream().map(Animal::getId).collect(Collectors.toList());
        if (animalIds.isEmpty()) return List.of();

        return reproduccionRepository
                .findByFechaPartoEstimadaBetweenAndVacaIdIn(today, twoMonthsFromNow, animalIds)
                .stream().map(this::toPartosProximosDTO).collect(Collectors.toList());
    }

    private ReproduccionDTO toDTO(Reproduccion r) {
        ReproduccionDTO dto = new ReproduccionDTO();
        dto.setId(r.getId());
        dto.setVacaId(r.getVaca() != null ? r.getVaca().getId() : null);
        dto.setVacaNombre(r.getVaca() != null ? r.getVaca().getNombre() : null);
        dto.setVacaArete(r.getVaca() != null ? r.getVaca().getIdentificadorArete() : null);
        dto.setToroId(r.getToro() != null ? r.getToro().getId() : null);
        dto.setToroNombre(r.getToro() != null ? r.getToro().getNombre() : null);
        dto.setToroArete(r.getToro() != null ? r.getToro().getIdentificadorArete() : null);
        dto.setFechaMonta(r.getFechaMonta());
        dto.setTipo(r.getTipo());
        dto.setResultado(r.getResultado());
        dto.setFechaPartoEstimada(r.getFechaPartoEstimada());
        dto.setObservaciones(r.getObservaciones());
        return dto;
    }

    private PartosProximosDTO toPartosProximosDTO(Reproduccion r) {
        PartosProximosDTO dto = new PartosProximosDTO();
        dto.setReproduccionId(r.getId());
        dto.setVacaNombre(r.getVaca() != null ? r.getVaca().getNombre() : null);
        dto.setVacaArete(r.getVaca() != null ? r.getVaca().getIdentificadorArete() : null);
        dto.setToroNombre(r.getToro() != null ? r.getToro().getNombre() : null);
        dto.setToroArete(r.getToro() != null ? r.getToro().getIdentificadorArete() : null);
        dto.setFechaPartoEstimada(r.getFechaPartoEstimada());
        if (r.getFechaPartoEstimada() != null) {
            dto.setDiasRestantes(ChronoUnit.DAYS.between(LocalDate.now(), r.getFechaPartoEstimada()));
        }
        return dto;
    }
}
