package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreatePartoRequest;
import com.gestionganadera.backend.dto.PartoDTO;
import com.gestionganadera.backend.model.*;
import com.gestionganadera.backend.repository.EventoRepository;
import com.gestionganadera.backend.repository.PartoRepository;
import com.gestionganadera.backend.repository.ReproduccionRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartoServiceTest {

    @Mock
    private PartoRepository partoRepository;
    @Mock
    private ReproduccionRepository reproduccionRepository;
    @Mock
    private EventoRepository eventoRepository;

    @InjectMocks
    private PartoService partoService;

    private Animal vaca;
    private Evento evento;
    private Reproduccion reproduccion;
    private Parto parto;

    @BeforeEach
    void setUp() {
        vaca = new Animal();
        vaca.setId(1);
        vaca.setNombre("Vaca Lechera");
        vaca.setIdentificadorArete("AR-1");

        evento = new Evento();
        evento.setId(1);
        evento.setAnimal(vaca);
        evento.setFecha(LocalDateTime.of(2026, 5, 28, 10, 0));

        reproduccion = new Reproduccion();
        reproduccion.setId(10);
        reproduccion.setEvento(evento);
        reproduccion.setVaca(vaca);

        parto = new Parto();
        parto.setId(100);
        parto.setEvento(evento);
        parto.setReproduccion(reproduccion);
        parto.setCantidadCrias(1);
        parto.setObservacion("Normal");
    }

    // --- findAll tests ---

    @Test
    void findAll_returnsList() {
        when(partoRepository.findAll()).thenReturn(List.of(parto));

        List<PartoDTO> result = partoService.findAll();

        assertEquals(1, result.size());
        assertEquals(100, result.get(0).getId());
        assertEquals("Vaca Lechera", result.get(0).getVacaNombre());
    }

    @Test
    void findAll_emptyList_whenNoPartos() {
        when(partoRepository.findAll()).thenReturn(List.of());

        assertTrue(partoService.findAll().isEmpty());
    }

    // --- findById tests ---

    @Test
    void findById_returnsDTO() {
        when(partoRepository.findById(100)).thenReturn(Optional.of(parto));

        PartoDTO result = partoService.findById(100);

        assertEquals(100, result.getId());
        assertEquals("Vaca Lechera", result.getVacaNombre());
        assertEquals(1, result.getCantidadCrias());
    }

    @Test
    void findById_nonExistent_throws() {
        when(partoRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> partoService.findById(999));
    }

    // --- findByReproduccionId tests ---

    @Test
    void findByReproduccionId_returnsList() {
        when(partoRepository.findByReproduccionId(10)).thenReturn(List.of(parto));

        List<PartoDTO> result = partoService.findByReproduccionId(10);

        assertEquals(1, result.size());
        assertEquals(100, result.get(0).getId());
    }

    @Test
    void findByReproduccionId_empty_whenNoPartos() {
        when(partoRepository.findByReproduccionId(10)).thenReturn(List.of());

        assertTrue(partoService.findByReproduccionId(10).isEmpty());
    }

    // --- create tests ---

    @Test
    void create_createsParto() {
        when(eventoRepository.findById(1)).thenReturn(Optional.of(evento));
        when(reproduccionRepository.findById(10)).thenReturn(Optional.of(reproduccion));

        CreatePartoRequest request = new CreatePartoRequest();
        request.setEventoId(1);
        request.setReproduccionId(10);
        request.setCantidadCrias(2);
        request.setObservacion("Gemelos");

        when(partoRepository.save(any(Parto.class))).thenAnswer(invocation -> {
            Parto saved = invocation.getArgument(0);
            saved.setId(200);
            return saved;
        });

        PartoDTO result = partoService.create(request);

        assertEquals("Vaca Lechera", result.getVacaNombre());
        assertEquals(2, result.getCantidadCrias());
        assertEquals("Gemelos", result.getObservacion());
        verify(partoRepository).save(any(Parto.class));
    }

    @Test
    void create_eventoNotFound_throws() {
        when(eventoRepository.findById(999)).thenReturn(Optional.empty());

        CreatePartoRequest request = new CreatePartoRequest();
        request.setEventoId(999);
        request.setReproduccionId(10);
        request.setCantidadCrias(1);

        assertThrows(EntityNotFoundException.class, () -> partoService.create(request));
        verify(partoRepository, never()).save(any());
    }

    @Test
    void create_reproduccionNotFound_throws() {
        when(eventoRepository.findById(1)).thenReturn(Optional.of(evento));
        when(reproduccionRepository.findById(999)).thenReturn(Optional.empty());

        CreatePartoRequest request = new CreatePartoRequest();
        request.setEventoId(1);
        request.setReproduccionId(999);
        request.setCantidadCrias(1);

        assertThrows(EntityNotFoundException.class, () -> partoService.create(request));
        verify(partoRepository, never()).save(any());
    }

    // --- update tests ---

    @Test
    void update_updatesFields() {
        when(partoRepository.findById(100)).thenReturn(Optional.of(parto));

        CreatePartoRequest request = new CreatePartoRequest();
        request.setCantidadCrias(3);
        request.setObservacion("Triples");

        Parto updatedParto = new Parto();
        updatedParto.setId(100);
        updatedParto.setEvento(evento);
        updatedParto.setReproduccion(reproduccion);
        updatedParto.setCantidadCrias(3);
        updatedParto.setObservacion("Triples");

        when(partoRepository.save(any(Parto.class))).thenReturn(updatedParto);

        PartoDTO result = partoService.update(100, request);

        assertEquals(3, result.getCantidadCrias());
        assertEquals("Triples", result.getObservacion());
        verify(partoRepository).save(any(Parto.class));
    }

    @Test
    void update_nonExistent_throws() {
        when(partoRepository.findById(999)).thenReturn(Optional.empty());

        CreatePartoRequest request = new CreatePartoRequest();
        request.setCantidadCrias(1);

        assertThrows(EntityNotFoundException.class, () -> partoService.update(999, request));
        verify(partoRepository, never()).save(any());
    }

    // --- delete tests ---

    @Test
    void delete_existingParto_deletes() {
        when(partoRepository.findById(100)).thenReturn(Optional.of(parto));

        partoService.delete(100);

        verify(partoRepository).delete(parto);
    }

    @Test
    void delete_nonExistent_throws() {
        when(partoRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> partoService.delete(999));
        verify(partoRepository, never()).delete(any());
    }

    // --- toDTO edge cases ---

    @Test
    void toDTO_handlesNullFields() {
        Parto p = new Parto();
        p.setId(1);

        // Use findById to trigger toDTO internally
        when(partoRepository.findById(1)).thenReturn(Optional.of(p));

        PartoDTO result = partoService.findById(1);

        assertEquals(1, result.getId());
        assertNull(result.getVacaNombre());
        assertNull(result.getObservacion());
    }
}
