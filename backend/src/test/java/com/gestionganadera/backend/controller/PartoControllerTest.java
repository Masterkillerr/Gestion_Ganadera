package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.CreatePartoRequest;
import com.gestionganadera.backend.dto.PartoDTO;
import com.gestionganadera.backend.service.PartoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartoControllerTest {

    @Mock
    private PartoService partoService;

    @InjectMocks
    private PartoController controller;

    private PartoDTO createDTO(Integer id, Integer reproduccionId) {
        PartoDTO dto = new PartoDTO();
        dto.setId(id);
        dto.setReproduccionId(reproduccionId);
        dto.setVacaNombre("Vaca Test");
        dto.setVacaArete("AR-001");
        dto.setCantidadCrias(1);
        dto.setObservacion("Parto normal");
        return dto;
    }

    @Test
    void findAll_returnsList() {
        when(partoService.findAll()).thenReturn(List.of(createDTO(1, 1), createDTO(2, 2)));

        ResponseEntity<List<PartoDTO>> response = controller.findAll();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void findById_returnsDTO() {
        PartoDTO dto = createDTO(1, 1);
        when(partoService.findById(1)).thenReturn(dto);

        ResponseEntity<PartoDTO> response = controller.findById(1);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().getId());
        assertEquals("Vaca Test", response.getBody().getVacaNombre());
    }

    @Test
    void findByReproduccionId_returnsList() {
        when(partoService.findByReproduccionId(10)).thenReturn(List.of(createDTO(1, 10)));

        ResponseEntity<List<PartoDTO>> response = controller.findByReproduccionId(10);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        assertEquals(10, response.getBody().get(0).getReproduccionId());
    }

    @Test
    void create_returns201() {
        CreatePartoRequest request = new CreatePartoRequest();
        request.setReproduccionId(1);
        request.setCantidadCrias(1);

        PartoDTO saved = createDTO(3, 1);
        when(partoService.create(request)).thenReturn(saved);

        ResponseEntity<PartoDTO> response = controller.create(request);

        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().getId());
    }

    @Test
    void update_returns200() {
        CreatePartoRequest request = new CreatePartoRequest();
        request.setCantidadCrias(2);

        PartoDTO updated = createDTO(1, 1);
        updated.setCantidadCrias(2);
        when(partoService.update(1, request)).thenReturn(updated);

        ResponseEntity<PartoDTO> response = controller.update(1, request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(2, response.getBody().getCantidadCrias());
    }

    @Test
    void delete_returns204() {
        doNothing().when(partoService).delete(1);

        ResponseEntity<Void> response = controller.delete(1);

        assertEquals(204, response.getStatusCode().value());
        verify(partoService).delete(1);
    }
}
