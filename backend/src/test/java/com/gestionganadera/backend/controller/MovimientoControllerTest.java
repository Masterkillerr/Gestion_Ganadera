package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.CreateMovimientoRequest;
import com.gestionganadera.backend.dto.MovimientoDTO;
import com.gestionganadera.backend.service.MovimientoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovimientoControllerTest {

    @Mock
    private MovimientoService movimientoService;

    @InjectMocks
    private MovimientoController controller;

    private MovimientoDTO createDTO(Integer id) {
        MovimientoDTO dto = new MovimientoDTO();
        dto.setId(id);
        dto.setAnimalNombre("Vaca Test");
        dto.setAnimalArete("AR-001");
        dto.setFecha("2026-05-28");
        dto.setOrigen("Lote A");
        dto.setDestino("Lote B");
        dto.setTipoMovimiento("Traslado");
        return dto;
    }

    @Test
    void getRecent_returnsList() {
        when(movimientoService.getRecent()).thenReturn(List.of(createDTO(1), createDTO(2)));

        ResponseEntity<List<MovimientoDTO>> response = controller.getRecent();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void getAll_returnsPage() {
        Page<MovimientoDTO> page = new PageImpl<>(List.of(createDTO(1)));
        when(movimientoService.findAll(any(Pageable.class), isNull())).thenReturn(page);

        ResponseEntity<Page<MovimientoDTO>> response = controller.getAll(Pageable.unpaged(), null);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().getContent().size());
    }

    @Test
    void getById_returnsDTO() {
        MovimientoDTO dto = createDTO(1);
        when(movimientoService.findById(1)).thenReturn(dto);

        ResponseEntity<MovimientoDTO> response = controller.getById(1);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Vaca Test", response.getBody().getAnimalNombre());
        assertEquals("Lote B", response.getBody().getDestino());
    }

    @Test
    void create_returns200() {
        CreateMovimientoRequest request = new CreateMovimientoRequest();
        request.setEventoId(1);
        request.setTipoMovimientoId(1);
        request.setLoteDestinoId(2);
        request.setMotivo("Traslado");

        MovimientoDTO saved = createDTO(3);
        when(movimientoService.create(request)).thenReturn(saved);

        ResponseEntity<MovimientoDTO> response = controller.create(request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().getId());
    }

    @Test
    void update_returns200() {
        CreateMovimientoRequest request = new CreateMovimientoRequest();
        request.setEventoId(1);
        request.setLoteDestinoId(3);
        request.setMotivo("Cambio de lote");

        MovimientoDTO updated = createDTO(1);
        updated.setDestino("Lote C");

        when(movimientoService.update(1, request)).thenReturn(updated);

        ResponseEntity<MovimientoDTO> response = controller.update(1, request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Lote C", response.getBody().getDestino());
    }

    @Test
    void delete_returns204() {
        doNothing().when(movimientoService).delete(1);

        ResponseEntity<Void> response = controller.delete(1);

        assertEquals(204, response.getStatusCode().value());
        verify(movimientoService).delete(1);
    }
}
