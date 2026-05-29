package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateTratamientoRequest;
import com.gestionganadera.backend.model.Evento;
import com.gestionganadera.backend.model.Medicamento;
import com.gestionganadera.backend.model.Tratamiento;
import com.gestionganadera.backend.repository.EventoRepository;
import com.gestionganadera.backend.repository.MedicamentoRepository;
import com.gestionganadera.backend.repository.TratamientoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TratamientoService {
    private final TratamientoRepository repository;
    private final EventoRepository eventoRepository;
    private final MedicamentoRepository medicamentoRepository;

    public List<Tratamiento> findAll() {
        return repository.findAll();
    }

    public List<Tratamiento> findByAnimalId(@NonNull Integer animalId) {
        return repository.findByEventoAnimalId(animalId);
    }

    public Tratamiento findById(@NonNull Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tratamiento no encontrado"));
    }

    public Tratamiento save(@NonNull CreateTratamientoRequest request) {
        Evento evento = eventoRepository.findById(request.getEventoId())
                .orElseThrow(() -> new EntityNotFoundException("Evento no encontrado: " + request.getEventoId()));
        Medicamento medicamento = medicamentoRepository.findById(request.getMedicamentoId())
                .orElseThrow(() -> new EntityNotFoundException("Medicamento no encontrado: " + request.getMedicamentoId()));

        Tratamiento entity = new Tratamiento();
        entity.setEvento(evento);
        entity.setMedicamento(medicamento);
        entity.setDosisMl(request.getDosisMl());
        entity.setFechaInicio(request.getFechaInicio() != null ? request.getFechaInicio() : LocalDate.now());
        entity.setFechaFin(request.getFechaFin());
        entity.setObservacion(request.getObservacion());
        return repository.save(entity);
    }

    @NonNull
    public Tratamiento update(@NonNull Integer id, @NonNull CreateTratamientoRequest request) {
        Tratamiento entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tratamiento no encontrado"));

        if (request.getEventoId() != null) {
            entity.setEvento(eventoRepository.findById(request.getEventoId())
                    .orElseThrow(() -> new EntityNotFoundException("Evento no encontrado: " + request.getEventoId())));
        }
        if (request.getMedicamentoId() != null) {
            entity.setMedicamento(medicamentoRepository.findById(request.getMedicamentoId())
                    .orElseThrow(() -> new EntityNotFoundException("Medicamento no encontrado: " + request.getMedicamentoId())));
        }
        if (request.getDosisMl() != null) entity.setDosisMl(request.getDosisMl());
        if (request.getFechaInicio() != null) entity.setFechaInicio(request.getFechaInicio());
        if (request.getFechaFin() != null) entity.setFechaFin(request.getFechaFin());
        if (request.getObservacion() != null) entity.setObservacion(request.getObservacion());

        return repository.save(entity);
    }

    public void delete(@NonNull Integer id) {
        Tratamiento entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tratamiento no encontrado"));
        repository.deleteById(id);
    }
}
