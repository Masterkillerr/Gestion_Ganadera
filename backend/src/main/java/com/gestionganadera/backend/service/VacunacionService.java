package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateVacunacionRequest;
import com.gestionganadera.backend.model.Evento;
import com.gestionganadera.backend.model.Vacuna;
import com.gestionganadera.backend.model.Vacunacion;
import com.gestionganadera.backend.repository.EventoRepository;
import com.gestionganadera.backend.repository.VacunaRepository;
import com.gestionganadera.backend.repository.VacunacionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VacunacionService {
    private final VacunacionRepository repository;
    private final EventoRepository eventoRepository;
    private final VacunaRepository vacunaRepository;

    public List<Vacunacion> findAll() {
        return repository.findAll();
    }

    public List<Vacunacion> findByAnimalId(@NonNull Integer animalId) {
        return repository.findByEventoAnimalId(animalId);
    }

    public Vacunacion findById(@NonNull Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vacunación no encontrada"));
    }

    public Vacunacion save(@NonNull CreateVacunacionRequest request) {
        Evento evento = eventoRepository.findById(request.getEventoId())
                .orElseThrow(() -> new EntityNotFoundException("Evento no encontrado: " + request.getEventoId()));
        Vacuna vacuna = vacunaRepository.findById(request.getVacunaId())
                .orElseThrow(() -> new EntityNotFoundException("Vacuna no encontrada: " + request.getVacunaId()));

        Vacunacion entity = new Vacunacion();
        entity.setEvento(evento);
        entity.setVacuna(vacuna);
        entity.setProximaDosis(request.getProximaDosis());
        entity.setObservacion(request.getObservacion());
        return repository.save(entity);
    }

    @NonNull
    public Vacunacion update(@NonNull Integer id, @NonNull CreateVacunacionRequest request) {
        Vacunacion entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vacunación no encontrada"));

        if (request.getEventoId() != null) {
            entity.setEvento(eventoRepository.findById(request.getEventoId())
                    .orElseThrow(() -> new EntityNotFoundException("Evento no encontrado: " + request.getEventoId())));
        }
        if (request.getVacunaId() != null) {
            entity.setVacuna(vacunaRepository.findById(request.getVacunaId())
                    .orElseThrow(() -> new EntityNotFoundException("Vacuna no encontrada: " + request.getVacunaId())));
        }
        if (request.getProximaDosis() != null) entity.setProximaDosis(request.getProximaDosis());
        if (request.getObservacion() != null) entity.setObservacion(request.getObservacion());

        return repository.save(entity);
    }

    public void delete(@NonNull Integer id) {
        Vacunacion entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vacunación no encontrada"));
        repository.deleteById(id);
    }
}
