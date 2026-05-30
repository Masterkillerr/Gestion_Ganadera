package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateLoteRequest;
import com.gestionganadera.backend.model.Finca;
import com.gestionganadera.backend.dto.LoteDTO;
import com.gestionganadera.backend.model.Lote;
import com.gestionganadera.backend.repository.FincaRepository;
import com.gestionganadera.backend.repository.LoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoteServiceTest {

    @Mock
    private LoteRepository loteRepository;

    @Mock
    private FincaRepository fincaRepository;

    @InjectMocks
    private LoteService loteService;

    private Finca finca;
    private Lote lote;

    @BeforeEach
    void setUp() {
        finca = new Finca();
        finca.setId(1);
        finca.setNombre("Mi Finca");

        lote = new Lote();
        lote.setId(10);
        lote.setNombre("Lote A");
        lote.setFinca(finca);
        lote.setHectareas(BigDecimal.TEN);
        lote.setCapacidadMaxima(50);
        lote.setEstado("Activo");
    }

    @Test
    void findAll_returnsAllLotes() {
        when(loteRepository.findAll()).thenReturn(List.of(lote));

        List<LoteDTO> result = loteService.findAll();

        assertEquals(1, result.size());
        assertEquals("Lote A", result.get(0).getNombre());
    }

    @Test
    void findById_existing_returnsLote() {
        when(loteRepository.findById(10)).thenReturn(Optional.of(lote));

        Optional<LoteDTO> result = loteService.findById(10);

        assertTrue(result.isPresent());
        assertEquals("Lote A", result.get().getNombre());
    }

    @Test
    void findById_nonExistent_returnsEmpty() {
        when(loteRepository.findById(999)).thenReturn(Optional.empty());

        assertTrue(loteService.findById(999).isEmpty());
    }

    @Test
    void save_withFincaId_savesLote() {
        when(fincaRepository.findById(1)).thenReturn(Optional.of(finca));

        CreateLoteRequest request = new CreateLoteRequest();
        request.setNombre("Nuevo Lote");
        request.setFincaId(1);
        request.setHectareas(BigDecimal.valueOf(15.5));
        request.setCapacidadMaxima(30);
        request.setEstado("Activo");
        request.setTipoPasto("Ryegrass");

        Lote saved = new Lote();
        saved.setId(11);
        saved.setNombre("Nuevo Lote");
        saved.setFinca(finca);
        saved.setHectareas(BigDecimal.valueOf(15.5));
        saved.setCapacidadMaxima(30);
        saved.setTipoPasto("Ryegrass");
        saved.setEstado("Activo");

        when(loteRepository.save(any(Lote.class))).thenReturn(saved);

        LoteDTO result = loteService.save(request);

        assertEquals("Nuevo Lote", result.getNombre());
        assertEquals(15.5, result.getHectareas().doubleValue(), 0.01);
        assertEquals(30, result.getCapacidadMaxima());
        assertEquals("Ryegrass", result.getTipoPasto());
        verify(loteRepository).save(any(Lote.class));
    }

    @Test
    void save_withoutFincaId_savesLoteWithoutFinca() {
        CreateLoteRequest request = new CreateLoteRequest();
        request.setNombre("Lote Sin Finca");

        Lote saved = new Lote();
        saved.setId(12);
        saved.setNombre("Lote Sin Finca");

        when(loteRepository.save(any(Lote.class))).thenReturn(saved);

        LoteDTO result = loteService.save(request);

        assertEquals("Lote Sin Finca", result.getNombre());
        assertNull(result.getFincaId());
    }

    @Test
    void update_existingLote_updatesFields() {
        when(loteRepository.findById(10)).thenReturn(Optional.of(lote));

        CreateLoteRequest request = new CreateLoteRequest();
        request.setNombre("Lote Actualizado");
        request.setHectareas(BigDecimal.valueOf(20));
        request.setCapacidadMaxima(100);

        Lote updated = new Lote();
        updated.setId(10);
        updated.setNombre("Lote Actualizado");
        updated.setHectareas(BigDecimal.valueOf(20));
        updated.setCapacidadMaxima(100);

        when(loteRepository.save(any(Lote.class))).thenReturn(updated);

        LoteDTO result = loteService.update(10, request);

        assertEquals("Lote Actualizado", result.getNombre());
        assertEquals(20, result.getHectareas().doubleValue(), 0.01);
        assertEquals(100, result.getCapacidadMaxima());
    }

    @Test
    void update_nonExistent_throws() {
        when(loteRepository.findById(999)).thenReturn(Optional.empty());

        CreateLoteRequest request = new CreateLoteRequest();
        request.setNombre("Lote");

        assertThrows(RuntimeException.class, () -> loteService.update(999, request));
    }

    @Test
    void delete_existingLote_deletes() {
        when(loteRepository.findById(10)).thenReturn(Optional.of(lote));

        loteService.delete(10);

        verify(loteRepository).deleteById(10);
    }

    @Test
    void delete_nonExistent_throws() {
        when(loteRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> loteService.delete(999));
        verify(loteRepository, never()).deleteById(any());
    }

    @Test
    void update_withFincaId_updatesFinca() {
        when(loteRepository.findById(10)).thenReturn(Optional.of(lote));
        when(fincaRepository.findById(1)).thenReturn(Optional.of(finca));

        CreateLoteRequest request = new CreateLoteRequest();
        request.setNombre("Lote con Finca");
        request.setHectareas(BigDecimal.valueOf(25));
        request.setCapacidadMaxima(75);
        request.setTipoPasto("Bermuda");
        request.setEstado("Activo");
        request.setFincaId(1);

        Lote updated = new Lote();
        updated.setId(10);
        updated.setNombre("Lote con Finca");
        updated.setFinca(finca);
        updated.setHectareas(BigDecimal.valueOf(25));
        updated.setCapacidadMaxima(75);
        updated.setTipoPasto("Bermuda");
        updated.setEstado("Activo");

        when(loteRepository.save(any(Lote.class))).thenReturn(updated);

        LoteDTO result = loteService.update(10, request);

        assertEquals("Lote con Finca", result.getNombre());
        assertNotNull(result.getFincaId());
        assertEquals(25, result.getHectareas().doubleValue(), 0.01);
        assertEquals("Bermuda", result.getTipoPasto());
        verify(fincaRepository).findById(1);
        verify(loteRepository).save(any(Lote.class));
    }

    @Test
    void update_nonExistentFincaInUpdate_throws() {
        when(loteRepository.findById(10)).thenReturn(Optional.of(lote));
        when(fincaRepository.findById(999)).thenReturn(Optional.empty());

        CreateLoteRequest request = new CreateLoteRequest();
        request.setNombre("Fails");
        request.setFincaId(999);

        assertThrows(RuntimeException.class, () -> loteService.update(10, request));
        verify(loteRepository, never()).save(any());
    }

    @Test
    void save_nonExistentFinca_throws() {
        when(fincaRepository.findById(999)).thenReturn(Optional.empty());

        CreateLoteRequest request = new CreateLoteRequest();
        request.setNombre("Bad Lote");
        request.setFincaId(999);

        assertThrows(RuntimeException.class, () -> loteService.save(request));
        verify(loteRepository, never()).save(any());
    }

    @Test
    void findByFincaId_returnsLotes() {
        when(fincaRepository.findById(1)).thenReturn(Optional.of(finca));
        when(loteRepository.findByFincaId(1)).thenReturn(List.of(lote));

        List<LoteDTO> result = loteService.findByFincaId(1);

        assertEquals(1, result.size());
        assertEquals("Lote A", result.get(0).getNombre());
    }

    @Test
    void findByFincaId_nonExistentFinca_throws() {
        when(fincaRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> loteService.findByFincaId(999));
    }
}
