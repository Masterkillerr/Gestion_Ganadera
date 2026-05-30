package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateProduccionRequest;
import com.gestionganadera.backend.dto.ProduccionDTO;
import com.gestionganadera.backend.model.Animal;
import com.gestionganadera.backend.model.Produccion;
import com.gestionganadera.backend.model.TurnoProduccion;
import com.gestionganadera.backend.repository.AnimalRepository;
import com.gestionganadera.backend.repository.ProduccionRepository;
import com.gestionganadera.backend.repository.TurnoProduccionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProduccionServiceTest {

    @Mock
    private ProduccionRepository repository;
    @Mock
    private AnimalRepository animalRepository;
    @Mock
    private TurnoProduccionRepository turnoProduccionRepository;

    @InjectMocks
    private ProduccionService produccionService;

    private Animal animal;
    private TurnoProduccion turnoManana;
    private Produccion produccion;

    @BeforeEach
    void setUp() {
        animal = new Animal();
        animal.setId(10);
        animal.setNombre("Vaca Test");
        animal.setIdentificadorArete("AR-10");

        turnoManana = new TurnoProduccion();
        turnoManana.setId(1);
        turnoManana.setNombre("Mañana");

        produccion = new Produccion();
        produccion.setId(1);
        produccion.setAnimal(animal);
        produccion.setLitros(BigDecimal.valueOf(25.5));
        produccion.setTurnoProduccion(turnoManana);
        produccion.setFecha(LocalDate.of(2025, 1, 20));
    }

    // --- findAll tests ---

    @Test
    void findAll_returnsProducciones() {
        when(repository.findAll()).thenReturn(List.of(produccion));

        List<ProduccionDTO> result = produccionService.findAll();

        assertEquals(1, result.size());
        assertEquals(BigDecimal.valueOf(25.5), result.get(0).getLitros());
        assertEquals("Mañana", result.get(0).getTurno());
    }

    @Test
    void findAll_emptyList_whenNoProducciones() {
        when(repository.findAll()).thenReturn(List.of());

        assertTrue(produccionService.findAll().isEmpty());
    }

    // --- findByAnimalId tests ---

    @Test
    void findByAnimalId_returnsProducciones() {
        when(repository.findByAnimalId(10)).thenReturn(List.of(produccion));

        List<ProduccionDTO> result = produccionService.findByAnimalId(10);

        assertEquals(1, result.size());
        assertEquals(BigDecimal.valueOf(25.5), result.get(0).getLitros());
    }

    @Test
    void findByAnimalId_noProducciones_returnsEmptyList() {
        when(repository.findByAnimalId(10)).thenReturn(List.of());

        assertTrue(produccionService.findByAnimalId(10).isEmpty());
    }

    // --- findById tests ---

    @Test
    void findById_existing_returnsProduccion() {
        when(repository.findById(1)).thenReturn(Optional.of(produccion));

        Produccion result = produccionService.findById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    void findById_nonExistent_throws() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> produccionService.findById(999));
    }

    // --- create tests ---

    @Test
    void create_createsProduccion() {
        when(animalRepository.findById(10)).thenReturn(Optional.of(animal));
        when(turnoProduccionRepository.findById(1)).thenReturn(Optional.of(turnoManana));

        CreateProduccionRequest request = new CreateProduccionRequest();
        request.setAnimalId(10);
        request.setLitros(BigDecimal.valueOf(30.0));
        request.setTurnoProduccionId(1);
        request.setFecha(LocalDate.of(2025, 2, 1));

        Produccion saved = new Produccion();
        saved.setId(2);
        saved.setAnimal(animal);
        saved.setLitros(BigDecimal.valueOf(30.0));
        saved.setTurnoProduccion(turnoManana);
        saved.setFecha(LocalDate.of(2025, 2, 1));

        when(repository.save(any(Produccion.class))).thenReturn(saved);

        ProduccionDTO result = produccionService.create(request);

        assertEquals(BigDecimal.valueOf(30.0), result.getLitros());
        assertEquals("Mañana", result.getTurno());
        assertEquals(LocalDate.of(2025, 2, 1), result.getFecha());
        verify(repository).save(any(Produccion.class));
    }

    @Test
    void create_nonExistentAnimal_throws() {
        when(animalRepository.findById(99)).thenReturn(Optional.empty());

        CreateProduccionRequest request = new CreateProduccionRequest();
        request.setAnimalId(99);
        request.setLitros(BigDecimal.TEN);
        request.setFecha(LocalDate.now());

        assertThrows(EntityNotFoundException.class, () -> produccionService.create(request));
        verify(repository, never()).save(any());
    }

    // --- delete tests ---

    @Test
    void delete_existingProduccion_deletes() {
        when(repository.findById(1)).thenReturn(Optional.of(produccion));

        produccionService.delete(1);

        verify(repository).deleteById(1);
    }

    @Test
    void delete_nonExistent_throws() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> produccionService.delete(999));
        verify(repository, never()).deleteById(anyInt());
    }

    // --- update tests ---

    @Test
    void update_updatesLitrosAndTurno() {
        when(repository.findById(1)).thenReturn(Optional.of(produccion));
        when(turnoProduccionRepository.findById(2)).thenReturn(Optional.of(new TurnoProduccion(2, "Tarde")));

        CreateProduccionRequest request = new CreateProduccionRequest();
        request.setLitros(BigDecimal.valueOf(35.0));
        request.setTurnoProduccionId(2);

        Produccion updated = new Produccion();
        updated.setId(1);
        updated.setAnimal(animal);
        updated.setLitros(BigDecimal.valueOf(35.0));
        updated.setTurnoProduccion(new TurnoProduccion(2, "Tarde"));
        updated.setFecha(LocalDate.of(2025, 1, 20));

        when(repository.save(any(Produccion.class))).thenReturn(updated);

        ProduccionDTO result = produccionService.update(1, request);

        assertEquals(BigDecimal.valueOf(35.0), result.getLitros());
        assertEquals("Tarde", result.getTurno());
        verify(repository).save(any(Produccion.class));
    }

    @Test
    void update_nonExistent_throws() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        CreateProduccionRequest request = new CreateProduccionRequest();
        request.setLitros(BigDecimal.TEN);

        assertThrows(EntityNotFoundException.class, () -> produccionService.update(999, request));
        verify(repository, never()).save(any());
    }
}
