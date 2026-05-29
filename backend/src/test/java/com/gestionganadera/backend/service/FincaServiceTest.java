package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateFincaRequest;
import com.gestionganadera.backend.dto.FincaStatsDTO;
import com.gestionganadera.backend.model.Finca;
import com.gestionganadera.backend.repository.AnimalRepository;
import com.gestionganadera.backend.repository.FincaRepository;
import com.gestionganadera.backend.repository.LoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FincaServiceTest {

    @Mock
    private FincaRepository fincaRepository;
    @Mock
    private AnimalRepository animalRepository;
    @Mock
    private LoteRepository loteRepository;

    @InjectMocks
    private FincaService fincaService;

    private Finca finca;

    @BeforeEach
    void setUp() {
        finca = new Finca();
        finca.setId(1);
        finca.setNombre("Mi Finca");
    }

    @Test
    void findAll_returnsAllFincas() {
        when(fincaRepository.findAll()).thenReturn(List.of(finca));

        List<Finca> result = fincaService.findAll();

        assertEquals(1, result.size());
        assertEquals("Mi Finca", result.get(0).getNombre());
    }

    @Test
    void findAll_returnsEmptyList_whenNoFincas() {
        when(fincaRepository.findAll()).thenReturn(List.of());

        assertTrue(fincaService.findAll().isEmpty());
    }

    @Test
    void findById_existing_returnsFinca() {
        when(fincaRepository.findById(1)).thenReturn(Optional.of(finca));

        Optional<Finca> result = fincaService.findById(1);

        assertTrue(result.isPresent());
        assertEquals("Mi Finca", result.get().getNombre());
    }

    @Test
    void findById_nonExistent_returnsEmpty() {
        when(fincaRepository.findById(999)).thenReturn(Optional.empty());

        assertTrue(fincaService.findById(999).isEmpty());
    }

    @Test
    void save_createsFinca() {
        CreateFincaRequest request = new CreateFincaRequest();
        request.setNombre("Nueva Finca");
        request.setUbicacion("Campo Verde");

        Finca saved = new Finca();
        saved.setId(2);
        saved.setNombre("Nueva Finca");
        saved.setUbicacion("Campo Verde");

        when(fincaRepository.save(any(Finca.class))).thenReturn(saved);

        Finca result = fincaService.save(request);

        assertEquals("Nueva Finca", result.getNombre());
        assertEquals("Campo Verde", result.getUbicacion());
        verify(fincaRepository).save(argThat(f ->
                f.getNombre().equals("Nueva Finca") &&
                f.getUbicacion().equals("Campo Verde")));
    }

    @Test
    void save_withoutUbicacion_savesSuccessfully() {
        CreateFincaRequest request = new CreateFincaRequest();
        request.setNombre("Minimal");

        Finca saved = new Finca();
        saved.setId(3);
        saved.setNombre("Minimal");

        when(fincaRepository.save(any(Finca.class))).thenReturn(saved);

        Finca result = fincaService.save(request);

        assertEquals("Minimal", result.getNombre());
    }

    @Test
    void update_existingFinca_updatesFields() {
        when(fincaRepository.findById(1)).thenReturn(Optional.of(finca));

        CreateFincaRequest request = new CreateFincaRequest();
        request.setNombre("Finca Actualizada");
        request.setUbicacion("Nueva Ubicación");

        Finca updated = new Finca();
        updated.setId(1);
        updated.setNombre("Finca Actualizada");
        updated.setUbicacion("Nueva Ubicación");

        when(fincaRepository.save(any(Finca.class))).thenReturn(updated);

        Finca result = fincaService.update(1, request);

        assertEquals("Finca Actualizada", result.getNombre());
        assertEquals("Nueva Ubicación", result.getUbicacion());
    }

    @Test
    void update_nonExistent_throws() {
        when(fincaRepository.findById(999)).thenReturn(Optional.empty());

        CreateFincaRequest request = new CreateFincaRequest();
        request.setNombre("Finca");

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> fincaService.update(999, request));
        assertEquals("Finca no encontrada", exception.getMessage());
    }

    @Test
    void delete_existingFinca_deletes() {
        when(fincaRepository.findById(1)).thenReturn(Optional.of(finca));

        fincaService.delete(1);

        verify(fincaRepository).deleteById(1);
    }

    @Test
    void delete_nonExistent_throws() {
        when(fincaRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> fincaService.delete(999));
        verify(fincaRepository, never()).deleteById(any());
    }

    @Test
    void getStats_returnsStats() {
        when(fincaRepository.findById(1)).thenReturn(Optional.of(finca));
        when(loteRepository.findByFincaId(1)).thenReturn(List.of());

        FincaStatsDTO stats = fincaService.getStats(1);

        assertEquals(0, stats.getTotalLotes());
    }
}
