package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateReproduccionRequest;
import com.gestionganadera.backend.dto.PartosProximosDTO;
import com.gestionganadera.backend.dto.ReproduccionDTO;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReproduccionServiceTest {

    @Mock
    private ReproduccionRepository reproduccionRepository;
    @Mock
    private AnimalRepository animalRepository;
    @Mock
    private EventoRepository eventoRepository;
    @Mock
    private TipoReproduccionRepository tipoReproduccionRepository;
    @Mock
    private ResultadoReproduccionRepository resultadoReproduccionRepository;

    @InjectMocks
    private ReproduccionService reproduccionService;

    private Animal vaca;
    private Animal toro;
    private Evento evento;
    private TipoReproduccion tipoNatural;
    private ResultadoReproduccion resultadoPendiente;
    private Reproduccion reproduccion;

    @BeforeEach
    void setUp() {
        vaca = new Animal();
        vaca.setId(1);
        vaca.setNombre("Vaca Lechera");
        vaca.setIdentificadorArete("AR-1");

        toro = new Animal();
        toro.setId(2);
        toro.setNombre("Toro Bravo");
        toro.setIdentificadorArete("AR-2");

        evento = new Evento();
        evento.setId(1);

        tipoNatural = new TipoReproduccion();
        tipoNatural.setId(1);
        tipoNatural.setNombre("Natural");

        resultadoPendiente = new ResultadoReproduccion();
        resultadoPendiente.setId(1);
        resultadoPendiente.setNombre("Pendiente");

        reproduccion = new Reproduccion();
        reproduccion.setId(10);
        reproduccion.setEvento(evento);
        reproduccion.setVaca(vaca);
        reproduccion.setToro(toro);
        reproduccion.setTipoReproduccion(tipoNatural);
        reproduccion.setResultadoReproduccion(resultadoPendiente);
        reproduccion.setFechaPartoEstimada(LocalDate.of(2026, 8, 1));
        reproduccion.setObservacion("Pendiente");
    }

    // --- findAll tests ---

    @Test
    void findAll_returnsList() {
        when(reproduccionRepository.findAll()).thenReturn(List.of(reproduccion));

        List<ReproduccionDTO> result = reproduccionService.findAll();

        assertEquals(1, result.size());
        assertEquals(10, result.get(0).getId());
        assertEquals("Vaca Lechera", result.get(0).getVacaNombre());
        assertEquals("Natural", result.get(0).getTipoReproduccion());
    }

    @Test
    void findAll_empty_whenNoReproducciones() {
        when(reproduccionRepository.findAll()).thenReturn(List.of());

        assertTrue(reproduccionService.findAll().isEmpty());
    }

    // --- findById tests ---

    @Test
    void findById_returnsDTO() {
        when(reproduccionRepository.findById(10)).thenReturn(Optional.of(reproduccion));

        ReproduccionDTO result = reproduccionService.findById(10);

        assertEquals(10, result.getId());
        assertEquals("Vaca Lechera", result.getVacaNombre());
        assertEquals("Toro Bravo", result.getToroNombre());
        assertEquals("Natural", result.getTipoReproduccion());
        assertEquals("Pendiente", result.getResultadoReproduccion());
    }

    @Test
    void findById_nonExistent_throws() {
        when(reproduccionRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> reproduccionService.findById(999));
    }

    // --- create tests ---

    @Test
    void create_createsReproduccion() {
        when(eventoRepository.findById(1)).thenReturn(Optional.of(evento));
        when(animalRepository.findById(1)).thenReturn(Optional.of(vaca));
        when(animalRepository.findById(2)).thenReturn(Optional.of(toro));
        when(tipoReproduccionRepository.findById(1)).thenReturn(Optional.of(tipoNatural));
        when(resultadoReproduccionRepository.findById(1)).thenReturn(Optional.of(resultadoPendiente));

        CreateReproduccionRequest request = new CreateReproduccionRequest();
        request.setEventoId(1);
        request.setVacaId(1);
        request.setToroId(2);
        request.setTipoReproduccionId(1);
        request.setResultadoReproduccionId(1);
        request.setFechaPartoEstimada(LocalDate.of(2026, 8, 15));
        request.setObservacion("En espera");

        when(reproduccionRepository.save(any(Reproduccion.class))).thenAnswer(invocation -> {
            Reproduccion saved = invocation.getArgument(0);
            saved.setId(20);
            return saved;
        });

        ReproduccionDTO result = reproduccionService.create(request);

        assertEquals("Vaca Lechera", result.getVacaNombre());
        assertEquals("Toro Bravo", result.getToroNombre());
        assertEquals("Natural", result.getTipoReproduccion());
        assertEquals("En espera", result.getObservacion());
        verify(reproduccionRepository).save(any(Reproduccion.class));
    }

    @Test
    void create_vacaNotFound_throws() {
        when(eventoRepository.findById(1)).thenReturn(Optional.of(evento));
        when(animalRepository.findById(999)).thenReturn(Optional.empty());

        CreateReproduccionRequest request = new CreateReproduccionRequest();
        request.setEventoId(1);
        request.setVacaId(999);

        assertThrows(EntityNotFoundException.class, () -> reproduccionService.create(request));
        verify(reproduccionRepository, never()).save(any());
    }

    @Test
    void create_eventoNotFound_throws() {
        when(eventoRepository.findById(999)).thenReturn(Optional.empty());

        CreateReproduccionRequest request = new CreateReproduccionRequest();
        request.setEventoId(999);
        request.setVacaId(1);

        assertThrows(EntityNotFoundException.class, () -> reproduccionService.create(request));
        verify(reproduccionRepository, never()).save(any());
    }

    // --- update tests ---

    @Test
    void update_updatesFields() {
        when(reproduccionRepository.findById(10)).thenReturn(Optional.of(reproduccion));
        when(resultadoReproduccionRepository.findById(2)).thenReturn(Optional.of(new ResultadoReproduccion(2, "Exitoso")));

        CreateReproduccionRequest request = new CreateReproduccionRequest();
        request.setResultadoReproduccionId(2);
        request.setObservacion("Todo bien");

        Reproduccion updated = new Reproduccion();
        updated.setId(10);
        updated.setEvento(evento);
        updated.setVaca(vaca);
        updated.setToro(toro);
        updated.setTipoReproduccion(tipoNatural);
        updated.setResultadoReproduccion(new ResultadoReproduccion(2, "Exitoso"));
        updated.setObservacion("Todo bien");

        when(reproduccionRepository.save(any(Reproduccion.class))).thenReturn(updated);

        ReproduccionDTO result = reproduccionService.update(10, request);

        assertEquals("Exitoso", result.getResultadoReproduccion());
        assertEquals("Todo bien", result.getObservacion());
        verify(reproduccionRepository).save(any(Reproduccion.class));
    }

    @Test
    void update_nonExistent_throws() {
        when(reproduccionRepository.findById(999)).thenReturn(Optional.empty());

        CreateReproduccionRequest request = new CreateReproduccionRequest();
        request.setObservacion("Test");

        assertThrows(EntityNotFoundException.class, () -> reproduccionService.update(999, request));
        verify(reproduccionRepository, never()).save(any());
    }

    // --- delete tests ---

    @Test
    void delete_existing_deletes() {
        when(reproduccionRepository.findById(10)).thenReturn(Optional.of(reproduccion));

        reproduccionService.delete(10);

        verify(reproduccionRepository).delete(reproduccion);
    }

    @Test
    void delete_nonExistent_throws() {
        when(reproduccionRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> reproduccionService.delete(999));
        verify(reproduccionRepository, never()).delete(any());
    }

    // --- getProximosPartos tests ---

    @Test
    void getProximosPartos_returnsList() {
        when(reproduccionRepository.findByFechaPartoEstimadaBetween(any(), any()))
                .thenReturn(List.of(reproduccion));

        List<PartosProximosDTO> result = reproduccionService.getProximosPartos();

        assertEquals(1, result.size());
        assertEquals(10, result.get(0).getReproduccionId());
        assertEquals("Vaca Lechera", result.get(0).getVacaNombre());
        assertEquals("Toro Bravo", result.get(0).getToroNombre());
        assertNotNull(result.get(0).getDiasRestantes());
    }

    @Test
    void getProximosPartos_empty_whenNoReproducciones() {
        when(reproduccionRepository.findByFechaPartoEstimadaBetween(any(), any()))
                .thenReturn(List.of());

        List<PartosProximosDTO> result = reproduccionService.getProximosPartos();

        assertTrue(result.isEmpty());
    }
}
