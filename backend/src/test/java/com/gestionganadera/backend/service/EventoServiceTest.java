package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateEventoRequest;
import com.gestionganadera.backend.dto.EventoDTO;
import com.gestionganadera.backend.model.Animal;
import com.gestionganadera.backend.model.Evento;
import com.gestionganadera.backend.model.TipoEvento;
import com.gestionganadera.backend.repository.AnimalRepository;
import com.gestionganadera.backend.repository.EventoRepository;
import com.gestionganadera.backend.repository.TipoEventoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventoServiceTest {

    @Mock
    private EventoRepository repository;
    @Mock
    private AnimalRepository animalRepository;
    @Mock
    private TipoEventoRepository tipoEventoRepository;

    @InjectMocks
    private EventoService eventoService;

    private Animal animal;
    private TipoEvento tipoSalud;
    private Evento evento;

    @BeforeEach
    void setUp() {
        animal = new Animal();
        animal.setId(10);
        animal.setNombre("Vaca Test");
        animal.setIdentificadorArete("AR-10");

        tipoSalud = new TipoEvento();
        tipoSalud.setId(1);
        tipoSalud.setNombre("Salud");

        evento = new Evento();
        evento.setId(1);
        evento.setAnimal(animal);
        evento.setTipoEvento(tipoSalud);
        evento.setDescripcion("Revision veterinaria");
        evento.setFecha(LocalDateTime.of(2025, 1, 15, 10, 0));
    }

    // --- findAll tests ---

    @Test
    void findAll_returnsEventoDTOs() {
        when(repository.findAll()).thenReturn(List.of(evento));

        List<EventoDTO> result = eventoService.findAll();

        assertEquals(1, result.size());
        assertEquals("Salud", result.get(0).getTipoEvento());
        assertEquals("Revision veterinaria", result.get(0).getDescripcion());
    }

    // --- findById tests ---

    @Test
    void findById_existing_returnsEvento() {
        when(repository.findById(1)).thenReturn(Optional.of(evento));

        Evento result = eventoService.findById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    void findById_nonExistent_throws() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> eventoService.findById(999));
    }

    // --- findByAnimalId tests ---

    @Test
    void findByAnimalId_returnsEventos() {
        when(repository.findByAnimalId(10)).thenReturn(List.of(evento));

        List<EventoDTO> result = eventoService.findByAnimalId(10);

        assertEquals(1, result.size());
        assertEquals("Salud", result.get(0).getTipoEvento());
    }

    @Test
    void findByAnimalId_noEventos_returnsEmptyList() {
        when(repository.findByAnimalId(10)).thenReturn(List.of());

        assertTrue(eventoService.findByAnimalId(10).isEmpty());
    }

    // --- getRecent tests ---

    @Test
    void getRecent_returnsRecentEventos() {
        when(repository.findAll()).thenReturn(List.of(evento));

        List<EventoDTO> result = eventoService.getRecent();

        assertEquals(1, result.size());
    }

    // --- save tests ---

    @Test
    void save_createsEvento() {
        when(animalRepository.findById(10)).thenReturn(Optional.of(animal));
        when(tipoEventoRepository.findById(1)).thenReturn(Optional.of(tipoSalud));

        CreateEventoRequest request = new CreateEventoRequest();
        request.setAnimalId(10);
        request.setTipoEventoId(1);
        request.setDescripcion("Inseminacion artificial");

        Evento saved = new Evento();
        saved.setId(2);
        saved.setAnimal(animal);
        saved.setTipoEvento(tipoSalud);
        saved.setDescripcion("Inseminacion artificial");

        when(repository.save(any(Evento.class))).thenReturn(saved);

        Evento result = eventoService.save(request);

        assertEquals("Inseminacion artificial", result.getDescripcion());
        verify(repository).save(any(Evento.class));
    }

    @Test
    void save_withoutDescription_savesSuccessfully() {
        when(animalRepository.findById(10)).thenReturn(Optional.of(animal));

        CreateEventoRequest request = new CreateEventoRequest();
        request.setAnimalId(10);
        request.setTipoEventoId(null);
        request.setDescripcion(null);

        Evento saved = new Evento();
        saved.setId(3);
        saved.setAnimal(animal);
        saved.setTipoEvento(null);

        when(repository.save(any(Evento.class))).thenReturn(saved);

        Evento result = eventoService.save(request);

        assertNull(result.getDescripcion());
        assertNull(result.getTipoEvento());
        verify(repository).save(any(Evento.class));
    }

    @Test
    void save_nonExistentAnimal_throws() {
        when(animalRepository.findById(99)).thenReturn(Optional.empty());

        CreateEventoRequest request = new CreateEventoRequest();
        request.setAnimalId(99);

        assertThrows(EntityNotFoundException.class, () -> eventoService.save(request));
        verify(repository, never()).save(any());
    }

    // --- delete tests ---

    @Test
    void delete_existingEvento_deletes() {
        when(repository.findById(1)).thenReturn(Optional.of(evento));

        eventoService.delete(1);

        verify(repository).deleteById(1);
    }

    @Test
    void delete_nonExistent_throws() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> eventoService.delete(999));
        verify(repository, never()).deleteById(anyInt());
    }

    // --- toDTO tests ---

    @Test
    void toDTO_mapsCorrectly() {
        EventoDTO dto = eventoService.toDTO(evento);

        assertEquals(1, dto.getId());
        assertEquals("Salud", dto.getTipoEvento());
        assertEquals("Revision veterinaria", dto.getDescripcion());
        assertEquals(10, dto.getAnimalId());
        assertEquals("Vaca Test", dto.getAnimalNombre());
        assertEquals("AR-10", dto.getAnimalArete());
    }

    @Test
    void toDTO_nullFields() {
        Evento e = new Evento();
        e.setId(2);

        EventoDTO dto = eventoService.toDTO(e);

        assertEquals(2, dto.getId());
        assertNull(dto.getTipoEvento());
        assertNull(dto.getAnimalId());
    }
}
