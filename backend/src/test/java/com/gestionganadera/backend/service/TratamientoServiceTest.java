package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateTratamientoRequest;
import com.gestionganadera.backend.dto.TratamientoDTO;
import com.gestionganadera.backend.model.*;
import com.gestionganadera.backend.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TratamientoServiceTest {

    @Mock
    private TratamientoRepository repository;
    @Mock
    private EventoRepository eventoRepository;
    @Mock
    private MedicamentoRepository medicamentoRepository;

    @InjectMocks
    private TratamientoService tratamientoService;

    private Evento evento;
    private Medicamento medicamento;
    private Tratamiento tratamiento;

    @BeforeEach
    void setUp() {
        Animal animal = new Animal();
        animal.setId(10);
        animal.setNombre("Vaca Test");

        evento = new Evento();
        evento.setId(1);
        evento.setAnimal(animal);

        medicamento = new Medicamento();
        medicamento.setId(100);
        medicamento.setNombre("Ivermectina");

        tratamiento = new Tratamiento();
        tratamiento.setId(1);
        tratamiento.setEvento(evento);
        tratamiento.setMedicamento(medicamento);
        tratamiento.setDosisMl("10ml");
        tratamiento.setFechaInicio(LocalDate.of(2025, 1, 1));
        tratamiento.setFechaFin(LocalDate.of(2025, 1, 15));
        tratamiento.setObservacion("Observacion test");
    }

    // --- findAll tests ---

    @Test
    void findAll_returnsTratamientos() {
        when(repository.findAll()).thenReturn(List.of(tratamiento));

        List<TratamientoDTO> result = tratamientoService.findAll();

        assertEquals(1, result.size());
        assertEquals("Ivermectina", result.get(0).getMedicamentoNombre());
        assertEquals("10ml", result.get(0).getDosisMl());
    }

    // --- findById tests ---

    @Test
    void findById_existing_returnsTratamiento() {
        when(repository.findById(1)).thenReturn(Optional.of(tratamiento));

        TratamientoDTO result = tratamientoService.findById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    void findById_nonExistent_throws() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> tratamientoService.findById(999));
    }

    // --- save tests ---

    @Test
    void save_createsTratamiento() {
        when(eventoRepository.findById(1)).thenReturn(Optional.of(evento));
        when(medicamentoRepository.findById(100)).thenReturn(Optional.of(medicamento));

        CreateTratamientoRequest request = new CreateTratamientoRequest();
        request.setEventoId(1);
        request.setMedicamentoId(100);
        request.setDosisMl("20ml");
        request.setFechaInicio(LocalDate.of(2025, 2, 1));
        request.setFechaFin(LocalDate.of(2025, 2, 14));
        request.setObservacion("Nuevo tratamiento");

        Tratamiento saved = new Tratamiento();
        saved.setId(2);
        saved.setEvento(evento);
        saved.setMedicamento(medicamento);
        saved.setDosisMl("20ml");
        saved.setFechaInicio(LocalDate.of(2025, 2, 1));
        saved.setFechaFin(LocalDate.of(2025, 2, 14));
        saved.setObservacion("Nuevo tratamiento");

        when(repository.save(any(Tratamiento.class))).thenReturn(saved);

        TratamientoDTO result = tratamientoService.save(request);

        assertEquals("20ml", result.getDosisMl());
        assertEquals("Nuevo tratamiento", result.getObservacion());
        verify(repository).save(any(Tratamiento.class));
    }

    @Test
    void save_withoutOptionalFields_savesSuccessfully() {
        when(eventoRepository.findById(1)).thenReturn(Optional.of(evento));
        when(medicamentoRepository.findById(100)).thenReturn(Optional.of(medicamento));

        CreateTratamientoRequest request = new CreateTratamientoRequest();
        request.setEventoId(1);
        request.setMedicamentoId(100);

        Tratamiento saved = new Tratamiento();
        saved.setId(3);
        saved.setEvento(evento);
        saved.setMedicamento(medicamento);

        when(repository.save(any(Tratamiento.class))).thenReturn(saved);

        TratamientoDTO result = tratamientoService.save(request);

        assertNotNull(result);
        verify(repository).save(any(Tratamiento.class));
    }

    @Test
    void save_nonExistentEvento_throws() {
        when(eventoRepository.findById(999)).thenReturn(Optional.empty());

        CreateTratamientoRequest request = new CreateTratamientoRequest();
        request.setEventoId(999);
        request.setMedicamentoId(100);

        assertThrows(EntityNotFoundException.class, () -> tratamientoService.save(request));
        verify(repository, never()).save(any());
    }

    @Test
    void save_nonExistentMedicamento_throws() {
        when(eventoRepository.findById(1)).thenReturn(Optional.of(evento));
        when(medicamentoRepository.findById(999)).thenReturn(Optional.empty());

        CreateTratamientoRequest request = new CreateTratamientoRequest();
        request.setEventoId(1);
        request.setMedicamentoId(999);

        assertThrows(EntityNotFoundException.class, () -> tratamientoService.save(request));
        verify(repository, never()).save(any());
    }

    // --- update tests ---

    @Test
    void update_existing_updatesFields() {
        when(repository.findById(1)).thenReturn(Optional.of(tratamiento));

        CreateTratamientoRequest request = new CreateTratamientoRequest();
        request.setDosisMl("15ml");
        request.setObservacion("Actualizado");

        Tratamiento updated = new Tratamiento();
        updated.setId(1);
        updated.setEvento(evento);
        updated.setMedicamento(medicamento);
        updated.setDosisMl("15ml");
        updated.setFechaInicio(LocalDate.of(2025, 1, 1));
        updated.setObservacion("Actualizado");

        when(repository.save(any(Tratamiento.class))).thenReturn(updated);

        TratamientoDTO result = tratamientoService.update(1, request);

        assertEquals("15ml", result.getDosisMl());
        assertEquals("Actualizado", result.getObservacion());
        verify(repository).save(any(Tratamiento.class));
    }

    @Test
    void update_nonExistent_throws() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        CreateTratamientoRequest request = new CreateTratamientoRequest();
        request.setMedicamentoId(100);

        assertThrows(EntityNotFoundException.class, () -> tratamientoService.update(999, request));
        verify(repository, never()).save(any());
    }

    // --- delete tests ---

    @Test
    void delete_existingTratamiento_deletes() {
        when(repository.findById(1)).thenReturn(Optional.of(tratamiento));

        tratamientoService.delete(1);

        verify(repository).deleteById(1);
    }

    @Test
    void delete_nonExistent_throws() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> tratamientoService.delete(999));
        verify(repository, never()).deleteById(anyInt());
    }
}
