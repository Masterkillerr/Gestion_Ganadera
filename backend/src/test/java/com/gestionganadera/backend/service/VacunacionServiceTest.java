package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateVacunacionRequest;
import com.gestionganadera.backend.dto.VacunacionDTO;
import com.gestionganadera.backend.model.*;
import com.gestionganadera.backend.repository.EventoRepository;
import com.gestionganadera.backend.repository.VacunaRepository;
import com.gestionganadera.backend.repository.VacunacionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VacunacionServiceTest {

    @Mock
    private VacunacionRepository repository;
    @Mock
    private EventoRepository eventoRepository;
    @Mock
    private VacunaRepository vacunaRepository;

    @InjectMocks
    private VacunacionService vacunacionService;

    private Evento evento;
    private Vacuna vacuna;
    private Vacunacion vacunacion;

    @BeforeEach
    void setUp() {
        evento = new Evento();
        evento.setId(1);
        evento.setFecha(LocalDateTime.now());

        vacuna = new Vacuna();
        vacuna.setId(1);
        vacuna.setNombre("Aftosa");

        vacunacion = new Vacunacion();
        vacunacion.setId(1);
        vacunacion.setEvento(evento);
        vacunacion.setVacuna(vacuna);
        vacunacion.setProximaDosis(LocalDate.of(2025, 7, 10));
        vacunacion.setObservacion("Vacunacion test");
    }

    // --- findAll tests ---

    @Test
    void findAll_returnsAllVacunaciones() {
        when(repository.findAll()).thenReturn(List.of(vacunacion));

        List<VacunacionDTO> result = vacunacionService.findAll();

        assertEquals(1, result.size());
        assertEquals("Aftosa", result.get(0).getVacunaNombre());
    }

    @Test
    void findAll_emptyList_whenNoVacunaciones() {
        when(repository.findAll()).thenReturn(List.of());

        assertTrue(vacunacionService.findAll().isEmpty());
    }

    // --- findById tests ---

    @Test
    void findById_returnsVacunacion_whenFound() {
        when(repository.findById(1)).thenReturn(Optional.of(vacunacion));

        VacunacionDTO result = vacunacionService.findById(1);

        assertNotNull(result);
        assertEquals("Aftosa", result.getVacunaNombre());
    }

    @Test
    void findById_throws_whenNotFound() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> vacunacionService.findById(999));
    }

    // --- save tests ---

    @Test
    void save_createsVacunacion() {
        when(eventoRepository.findById(1)).thenReturn(Optional.of(evento));
        when(vacunaRepository.findById(1)).thenReturn(Optional.of(vacuna));

        CreateVacunacionRequest request = new CreateVacunacionRequest();
        request.setEventoId(1);
        request.setVacunaId(1);
        request.setProximaDosis(LocalDate.of(2025, 9, 1));
        request.setObservacion("Refuerzo anual");

        Vacunacion saved = new Vacunacion();
        saved.setId(2);
        saved.setEvento(evento);
        saved.setVacuna(vacuna);
        saved.setProximaDosis(LocalDate.of(2025, 9, 1));
        saved.setObservacion("Refuerzo anual");

        when(repository.save(any(Vacunacion.class))).thenReturn(saved);

        VacunacionDTO result = vacunacionService.save(request);

        assertEquals(LocalDate.of(2025, 9, 1), result.getProximaDosis());
        assertEquals("Refuerzo anual", result.getObservacion());
        verify(repository).save(any(Vacunacion.class));
    }

    @Test
    void save_withoutOptionalFields_savesSuccessfully() {
        when(eventoRepository.findById(1)).thenReturn(Optional.of(evento));
        when(vacunaRepository.findById(1)).thenReturn(Optional.of(vacuna));

        CreateVacunacionRequest request = new CreateVacunacionRequest();
        request.setEventoId(1);
        request.setVacunaId(1);

        Vacunacion saved = new Vacunacion();
        saved.setId(3);
        saved.setEvento(evento);
        saved.setVacuna(vacuna);

        when(repository.save(any(Vacunacion.class))).thenReturn(saved);

        VacunacionDTO result = vacunacionService.save(request);

        assertNotNull(result);
        assertNull(result.getProximaDosis());
        assertNull(result.getObservacion());
        verify(repository).save(any(Vacunacion.class));
    }

    @Test
    void save_nonExistentEvento_throws() {
        when(eventoRepository.findById(999)).thenReturn(Optional.empty());

        CreateVacunacionRequest request = new CreateVacunacionRequest();
        request.setEventoId(999);
        request.setVacunaId(1);

        assertThrows(EntityNotFoundException.class, () -> vacunacionService.save(request));
        verify(repository, never()).save(any());
    }

    @Test
    void save_nonExistentVacuna_throws() {
        when(eventoRepository.findById(1)).thenReturn(Optional.of(evento));
        when(vacunaRepository.findById(999)).thenReturn(Optional.empty());

        CreateVacunacionRequest request = new CreateVacunacionRequest();
        request.setEventoId(1);
        request.setVacunaId(999);

        assertThrows(EntityNotFoundException.class, () -> vacunacionService.save(request));
        verify(repository, never()).save(any());
    }

    // --- update tests ---

    @Test
    void update_updatesAllFields() {
        when(repository.findById(1)).thenReturn(Optional.of(vacunacion));
        when(eventoRepository.findById(2)).thenReturn(Optional.of(evento));
        when(vacunaRepository.findById(2)).thenReturn(Optional.of(vacuna));
        when(repository.save(any(Vacunacion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreateVacunacionRequest request = new CreateVacunacionRequest();
        request.setEventoId(2);
        request.setVacunaId(2);
        request.setProximaDosis(LocalDate.of(2026, 1, 1));
        request.setObservacion("Actualizado");

        VacunacionDTO result = vacunacionService.update(1, request);

        assertEquals(LocalDate.of(2026, 1, 1), result.getProximaDosis());
        assertEquals("Actualizado", result.getObservacion());
        verify(repository).save(any(Vacunacion.class));
    }

    @Test
    void update_partialFields_onlyUpdatesProvided() {
        when(repository.findById(1)).thenReturn(Optional.of(vacunacion));
        when(repository.save(any(Vacunacion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreateVacunacionRequest request = new CreateVacunacionRequest();
        request.setObservacion("Solo observacion");

        VacunacionDTO result = vacunacionService.update(1, request);

        assertEquals("Solo observacion", result.getObservacion());
        assertEquals(LocalDate.of(2025, 7, 10), result.getProximaDosis()); // unchanged
        verify(repository).save(any(Vacunacion.class));
    }

    @Test
    void update_nonExistent_throws() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        CreateVacunacionRequest request = new CreateVacunacionRequest();

        assertThrows(EntityNotFoundException.class, () -> vacunacionService.update(999, request));
        verify(repository, never()).save(any());
    }

    // --- delete tests ---

    @Test
    void delete_existingVacunacion_deletes() {
        when(repository.findById(1)).thenReturn(Optional.of(vacunacion));

        vacunacionService.delete(1);

        verify(repository).deleteById(1);
    }

    @Test
    void delete_nonExistent_throws() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> vacunacionService.delete(999));
        verify(repository, never()).deleteById(anyInt());
    }
}
