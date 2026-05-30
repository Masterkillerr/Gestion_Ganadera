package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateAlimentacionRequest;
import com.gestionganadera.backend.dto.AlimentacionDTO;
import com.gestionganadera.backend.model.Alimentacion;
import com.gestionganadera.backend.model.Animal;
import com.gestionganadera.backend.model.Dieta;
import com.gestionganadera.backend.repository.AlimentacionRepository;
import com.gestionganadera.backend.repository.AnimalRepository;
import com.gestionganadera.backend.repository.DietaRepository;
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
class AlimentacionServiceTest {

    @Mock
    private AlimentacionRepository repository;
    @Mock
    private AnimalRepository animalRepository;
    @Mock
    private DietaRepository dietaRepository;

    @InjectMocks
    private AlimentacionService alimentacionService;

    private Animal animal;
    private Dieta dieta;
    private Alimentacion alimentacion;

    @BeforeEach
    void setUp() {
        animal = new Animal();
        animal.setId(10);
        animal.setNombre("Vaca Test");

        dieta = new Dieta();
        dieta.setId(100);
        dieta.setNombre("Pasto");

        alimentacion = new Alimentacion();
        alimentacion.setId(1);
        alimentacion.setAnimal(animal);
        alimentacion.setDieta(dieta);
        alimentacion.setFecha(LocalDateTime.of(2025, 1, 15, 8, 0));
        alimentacion.setObservacion("Alimentacion test");
    }

    // --- findByAnimalId tests ---

    @Test
    void findByAnimalId_returnsAlimentaciones() {
        when(repository.findByAnimalId(10)).thenReturn(List.of(alimentacion));

        List<AlimentacionDTO> result = alimentacionService.findByAnimalId(10);

        assertEquals(1, result.size());
        assertEquals("Pasto", result.get(0).getDietaNombre());
        assertEquals("Alimentacion test", result.get(0).getObservacion());
    }

    @Test
    void findByAnimalId_noAlimentaciones_returnsEmptyList() {
        when(repository.findByAnimalId(10)).thenReturn(List.of());

        assertTrue(alimentacionService.findByAnimalId(10).isEmpty());
    }

    // --- findAll tests ---

    @Test
    void findAll_returnsAllAlimentaciones() {
        when(repository.findAll()).thenReturn(List.of(alimentacion));

        List<AlimentacionDTO> result = alimentacionService.findAll();

        assertEquals(1, result.size());
    }

    // --- findById tests ---

    @Test
    void findById_existing_returnsAlimentacion() {
        when(repository.findById(1)).thenReturn(Optional.of(alimentacion));

        AlimentacionDTO result = alimentacionService.findById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    void findById_nonExistent_throws() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> alimentacionService.findById(999));
    }

    // --- save tests ---

    @Test
    void save_createsAlimentacion() {
        when(animalRepository.findById(10)).thenReturn(Optional.of(animal));
        when(dietaRepository.findById(100)).thenReturn(Optional.of(dieta));

        CreateAlimentacionRequest request = new CreateAlimentacionRequest();
        request.setAnimalId(10);
        request.setDietaId(100);
        request.setFecha(LocalDateTime.of(2025, 2, 1, 6, 0));
        request.setObservacion("Racion diaria");

        Alimentacion saved = new Alimentacion();
        saved.setId(2);
        saved.setAnimal(animal);
        saved.setDieta(dieta);
        saved.setFecha(LocalDateTime.of(2025, 2, 1, 6, 0));
        saved.setObservacion("Racion diaria");

        when(repository.save(any(Alimentacion.class))).thenReturn(saved);

        AlimentacionDTO result = alimentacionService.save(request);

        assertEquals("Racion diaria", result.getObservacion());
        assertEquals(LocalDateTime.of(2025, 2, 1, 6, 0), result.getFecha());
        verify(repository).save(any(Alimentacion.class));
    }

    @Test
    void save_withoutOptionalFields_savesSuccessfully() {
        when(animalRepository.findById(10)).thenReturn(Optional.of(animal));

        CreateAlimentacionRequest request = new CreateAlimentacionRequest();
        request.setAnimalId(10);
        request.setDietaId(null);
        request.setFecha(LocalDateTime.of(2025, 3, 1, 7, 0));

        Alimentacion saved = new Alimentacion();
        saved.setId(3);
        saved.setAnimal(animal);
        saved.setDieta(null);
        saved.setFecha(LocalDateTime.of(2025, 3, 1, 7, 0));

        when(repository.save(any(Alimentacion.class))).thenReturn(saved);

        AlimentacionDTO result = alimentacionService.save(request);

        assertNotNull(result);
        assertNull(result.getDietaId());
        assertNull(result.getObservacion());
        verify(repository).save(any(Alimentacion.class));
    }

    @Test
    void save_nonExistentDieta_throws() {
        when(animalRepository.findById(10)).thenReturn(Optional.of(animal));
        when(dietaRepository.findById(999)).thenReturn(Optional.empty());

        CreateAlimentacionRequest request = new CreateAlimentacionRequest();
        request.setAnimalId(10);
        request.setDietaId(999);
        request.setFecha(LocalDateTime.now());

        assertThrows(EntityNotFoundException.class, () -> alimentacionService.save(request));
        verify(repository, never()).save(any());
    }

    @Test
    void save_nonExistentAnimal_throws() {
        when(animalRepository.findById(99)).thenReturn(Optional.empty());

        CreateAlimentacionRequest request = new CreateAlimentacionRequest();
        request.setAnimalId(99);
        request.setFecha(LocalDateTime.now());

        assertThrows(EntityNotFoundException.class, () -> alimentacionService.save(request));
        verify(dietaRepository, never()).findById(anyInt());
        verify(repository, never()).save(any());
    }

    // --- update tests ---

    @Test
    void update_existing_updatesFields() {
        when(repository.findById(1)).thenReturn(Optional.of(alimentacion));
        when(animalRepository.findById(20)).thenReturn(Optional.of(animal));

        CreateAlimentacionRequest request = new CreateAlimentacionRequest();
        request.setAnimalId(20);
        request.setObservacion("Actualizado");

        Alimentacion updated = new Alimentacion();
        updated.setId(1);
        updated.setAnimal(animal);
        updated.setObservacion("Actualizado");

        when(repository.save(any(Alimentacion.class))).thenReturn(updated);

        AlimentacionDTO result = alimentacionService.update(1, request);

        assertEquals("Actualizado", result.getObservacion());
        verify(repository).save(any(Alimentacion.class));
    }

    @Test
    void update_nonExistent_throws() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> alimentacionService.update(999, new CreateAlimentacionRequest()));
        verify(repository, never()).save(any());
    }

    // --- delete tests ---

    @Test
    void delete_existingAlimentacion_deletes() {
        when(repository.findById(1)).thenReturn(Optional.of(alimentacion));

        alimentacionService.delete(1);

        verify(repository).deleteById(1);
    }

    @Test
    void delete_nonExistent_throws() {
        when(repository.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> alimentacionService.delete(999));
        verify(repository, never()).deleteById(anyInt());
    }
}
