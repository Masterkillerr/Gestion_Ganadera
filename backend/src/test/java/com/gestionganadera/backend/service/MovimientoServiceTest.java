package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateMovimientoRequest;
import com.gestionganadera.backend.dto.MovimientoDTO;
import com.gestionganadera.backend.model.*;
import com.gestionganadera.backend.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovimientoServiceTest {

    @Mock
    private MovimientoRepository movimientoRepository;
    @Mock
    private EventoRepository eventoRepository;
    @Mock
    private LoteRepository loteRepository;
    @Mock
    private TipoMovimientoRepository tipoMovimientoRepository;

    @InjectMocks
    private MovimientoService movimientoService;

    private Animal animal;
    private Evento evento;
    private Lote loteOrigen;
    private Lote loteDestino;
    private TipoMovimiento tipoTraslado;
    private Movimiento movimiento;

    @BeforeEach
    void setUp() {
        animal = new Animal();
        animal.setId(100);
        animal.setNombre("Vaca Test");
        animal.setIdentificadorArete("AR-100");

        evento = new Evento();
        evento.setId(1);
        evento.setAnimal(animal);
        evento.setFecha(LocalDateTime.of(2026, 5, 28, 10, 0));

        loteOrigen = new Lote();
        loteOrigen.setId(10);
        loteOrigen.setNombre("Lote Origen");

        loteDestino = new Lote();
        loteDestino.setId(20);
        loteDestino.setNombre("Lote Destino");

        tipoTraslado = new TipoMovimiento();
        tipoTraslado.setId(1);
        tipoTraslado.setNombre("Traslado");

        movimiento = new Movimiento();
        movimiento.setId(1);
        movimiento.setEvento(evento);
        movimiento.setTipoMovimiento(tipoTraslado);
        movimiento.setLoteOrigen(loteOrigen);
        movimiento.setLoteDestino(loteDestino);
        movimiento.setMotivo("Cambio de alimentacion");
    }

    // --- getRecent tests ---

    @Test
    void getRecent_returnsList() {
        when(movimientoRepository.findAll()).thenReturn(List.of(movimiento));

        List<MovimientoDTO> result = movimientoService.getRecent();

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals("Vaca Test", result.get(0).getAnimalNombre());
        assertEquals("Traslado", result.get(0).getTipoMovimiento());
    }

    @Test
    void getRecent_empty_whenNoMovimientos() {
        when(movimientoRepository.findAll()).thenReturn(List.of());

        assertTrue(movimientoService.getRecent().isEmpty());
    }

    // --- findAll tests ---

    @Test
    void findAll_returnsPage() {
        Page<Movimiento> page = new PageImpl<>(List.of(movimiento));
        when(movimientoRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<MovimientoDTO> result = movimientoService.findAll(Pageable.unpaged());

        assertEquals(1, result.getContent().size());
        assertEquals("Vaca Test", result.getContent().get(0).getAnimalNombre());
        assertEquals("Lote Origen", result.getContent().get(0).getOrigen());
        assertEquals("Lote Destino", result.getContent().get(0).getDestino());
        assertEquals("Traslado", result.getContent().get(0).getTipoMovimiento());
    }

    @Test
    void findAll_empty_whenNoMovimientos() {
        Page<Movimiento> emptyPage = new PageImpl<>(List.of());
        when(movimientoRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        assertTrue(movimientoService.findAll(Pageable.unpaged()).isEmpty());
    }

    // --- findById tests ---

    @Test
    void findById_returnsDTO() {
        when(movimientoRepository.findById(1)).thenReturn(Optional.of(movimiento));

        MovimientoDTO result = movimientoService.findById(1);

        assertEquals(1, result.getId());
        assertEquals("Vaca Test", result.getAnimalNombre());
        assertEquals("Lote Origen", result.getOrigen());
        assertEquals("Lote Destino", result.getDestino());
    }

    @Test
    void findById_nonExistent_throws() {
        when(movimientoRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> movimientoService.findById(999));
    }

    // --- create tests ---

    @Test
    void create_createsMovimiento() {
        when(eventoRepository.findById(1)).thenReturn(Optional.of(evento));
        when(loteRepository.findById(20)).thenReturn(Optional.of(loteDestino));
        when(loteRepository.findById(10)).thenReturn(Optional.of(loteOrigen));
        when(tipoMovimientoRepository.findById(1)).thenReturn(Optional.of(tipoTraslado));

        CreateMovimientoRequest request = new CreateMovimientoRequest();
        request.setEventoId(1);
        request.setLoteDestinoId(20);
        request.setLoteOrigenId(10);
        request.setTipoMovimientoId(1);
        request.setMotivo("Venta a otro productor");

        when(movimientoRepository.save(any(Movimiento.class))).thenAnswer(invocation -> {
            Movimiento saved = invocation.getArgument(0);
            saved.setId(50);
            return saved;
        });

        MovimientoDTO result = movimientoService.create(request);

        assertEquals("Vaca Test", result.getAnimalNombre());
        assertEquals("Lote Destino", result.getDestino());
        assertEquals("Lote Origen", result.getOrigen());
        assertEquals("Traslado", result.getTipoMovimiento());
        verify(movimientoRepository).save(any(Movimiento.class));
    }

    @Test
    void create_withoutLoteOrigen_creates() {
        when(eventoRepository.findById(1)).thenReturn(Optional.of(evento));
        when(loteRepository.findById(20)).thenReturn(Optional.of(loteDestino));

        CreateMovimientoRequest request = new CreateMovimientoRequest();
        request.setEventoId(1);
        request.setLoteDestinoId(20);

        when(movimientoRepository.save(any(Movimiento.class))).thenAnswer(invocation -> {
            Movimiento saved = invocation.getArgument(0);
            saved.setId(50);
            return saved;
        });

        MovimientoDTO result = movimientoService.create(request);

        assertNull(result.getOrigen());
        assertEquals("Lote Destino", result.getDestino());
        verify(movimientoRepository).save(any(Movimiento.class));
    }

    @Test
    void create_nonExistentEvento_throws() {
        when(eventoRepository.findById(999)).thenReturn(Optional.empty());

        CreateMovimientoRequest request = new CreateMovimientoRequest();
        request.setEventoId(999);
        request.setLoteDestinoId(20);

        assertThrows(EntityNotFoundException.class, () -> movimientoService.create(request));
        verify(movimientoRepository, never()).save(any());
    }

    @Test
    void create_nonExistentLoteDestino_throws() {
        when(eventoRepository.findById(1)).thenReturn(Optional.of(evento));
        when(loteRepository.findById(999)).thenReturn(Optional.empty());

        CreateMovimientoRequest request = new CreateMovimientoRequest();
        request.setEventoId(1);
        request.setLoteDestinoId(999);

        assertThrows(EntityNotFoundException.class, () -> movimientoService.create(request));
        verify(movimientoRepository, never()).save(any());
    }

    @Test
    void create_nonExistentLoteOrigen_throws() {
        when(eventoRepository.findById(1)).thenReturn(Optional.of(evento));
        when(loteRepository.findById(20)).thenReturn(Optional.of(loteDestino));
        when(loteRepository.findById(999)).thenReturn(Optional.empty());

        CreateMovimientoRequest request = new CreateMovimientoRequest();
        request.setEventoId(1);
        request.setLoteDestinoId(20);
        request.setLoteOrigenId(999);

        assertThrows(EntityNotFoundException.class, () -> movimientoService.create(request));
        verify(movimientoRepository, never()).save(any());
    }

    // --- update tests ---

    @Test
    void update_updatesFields() {
        when(movimientoRepository.findById(1)).thenReturn(Optional.of(movimiento));
        when(eventoRepository.findById(1)).thenReturn(Optional.of(evento));
        when(loteRepository.findById(20)).thenReturn(Optional.of(loteDestino));
        when(tipoMovimientoRepository.findById(1)).thenReturn(Optional.of(tipoTraslado));

        CreateMovimientoRequest request = new CreateMovimientoRequest();
        request.setEventoId(1);
        request.setLoteDestinoId(20);
        request.setTipoMovimientoId(1);
        request.setMotivo("Nuevo motivo");

        Movimiento updated = new Movimiento();
        updated.setId(1);
        updated.setEvento(evento);
        updated.setTipoMovimiento(tipoTraslado);
        updated.setLoteDestino(loteDestino);
        updated.setMotivo("Nuevo motivo");

        when(movimientoRepository.save(any(Movimiento.class))).thenReturn(updated);

        MovimientoDTO result = movimientoService.update(1, request);

        assertEquals("Traslado", result.getTipoMovimiento());
        assertEquals("Nuevo motivo", result.getMotivo());
        verify(movimientoRepository).save(any(Movimiento.class));
    }

    @Test
    void update_nonExistent_throws() {
        when(movimientoRepository.findById(999)).thenReturn(Optional.empty());

        CreateMovimientoRequest request = new CreateMovimientoRequest();
        request.setEventoId(1);
        request.setLoteDestinoId(20);

        assertThrows(EntityNotFoundException.class, () -> movimientoService.update(999, request));
        verify(movimientoRepository, never()).save(any());
    }

    // --- delete tests ---

    @Test
    void delete_existing_deletes() {
        when(movimientoRepository.findById(1)).thenReturn(Optional.of(movimiento));

        movimientoService.delete(1);

        verify(movimientoRepository).deleteById(1);
    }

    @Test
    void delete_nonExistent_throws() {
        when(movimientoRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> movimientoService.delete(999));
        verify(movimientoRepository, never()).deleteById(anyInt());
    }
}
