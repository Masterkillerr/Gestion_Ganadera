package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.CreateLoteRequest;
import com.gestionganadera.backend.model.Lote;
import com.gestionganadera.backend.service.LoteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoteControllerTest {

    @Mock
    private LoteService loteService;

    @InjectMocks
    private LoteController loteController;

    @Test
    void getAllLotes_returnsList() {
        Lote lote = new Lote();
        lote.setId(1);
        lote.setNombre("Lote A");

        when(loteService.findAll()).thenReturn(List.of(lote));

        ResponseEntity<List<Lote>> response = loteController.getAllLotes();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getLoteById_found_returns200() {
        Lote lote = new Lote();
        lote.setId(1);
        when(loteService.findById(1)).thenReturn(Optional.of(lote));

        ResponseEntity<Lote> response = loteController.getLoteById(1);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void getLoteById_notFound_returns404() {
        when(loteService.findById(999)).thenReturn(Optional.empty());

        ResponseEntity<Lote> response = loteController.getLoteById(999);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void createLote_returnsCreated() {
        CreateLoteRequest request = new CreateLoteRequest();
        request.setNombre("Nuevo Lote");

        Lote saved = new Lote();
        saved.setId(2);
        saved.setNombre("Nuevo Lote");

        when(loteService.save(request)).thenReturn(saved);

        ResponseEntity<Lote> response = loteController.createLote(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Nuevo Lote", response.getBody().getNombre());
    }

    @Test
    void updateLote_returnsUpdated() {
        CreateLoteRequest request = new CreateLoteRequest();
        request.setNombre("Actualizado");

        Lote updated = new Lote();
        updated.setId(1);
        updated.setNombre("Actualizado");

        when(loteService.update(1, request)).thenReturn(updated);

        ResponseEntity<Lote> response = loteController.updateLote(1, request);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void deleteLote_returns204() {
        doNothing().when(loteService).delete(1);

        ResponseEntity<Void> response = loteController.deleteLote(1);

        assertEquals(204, response.getStatusCode().value());
        verify(loteService).delete(1);
    }
}
