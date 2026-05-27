package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.CreateProduccionRequest;
import com.gestionganadera.backend.dto.ProduccionDTO;
import com.gestionganadera.backend.service.ProduccionService;
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
class ProduccionControllerTest {

    @Mock
    private ProduccionService service;

    @InjectMocks
    private ProduccionController controller;

    @Test
    void findAll_returnsList() {
        ProduccionDTO dto = new ProduccionDTO();
        dto.setId(1);
        when(service.findAll()).thenReturn(List.of(dto));

        ResponseEntity<List<ProduccionDTO>> response = controller.findAll();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void findByAnimalId_returnsList() {
        ProduccionDTO dto = new ProduccionDTO();
        dto.setId(1);
        when(service.findAll()).thenReturn(List.of(dto));

        ResponseEntity<List<ProduccionDTO>> response = controller.findAll();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void create_returnsCreated() {
        CreateProduccionRequest request = new CreateProduccionRequest();
        request.setAnimalId(100);
        request.setLitros(java.math.BigDecimal.valueOf(10));
        request.setTurno("Manana");
        request.setFecha(java.time.LocalDate.now());

        ProduccionDTO saved = new ProduccionDTO();
        saved.setId(1);
        saved.setAnimalId(100);
        saved.setLitros(java.math.BigDecimal.valueOf(10));
        when(service.create(request)).thenReturn(saved);

        ResponseEntity<ProduccionDTO> response = controller.create(request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getId());
    }

    @Test
    void delete_returns204() {
        doNothing().when(service).delete(1);

        ResponseEntity<Void> response = controller.delete(1);

        assertEquals(204, response.getStatusCode().value());
        verify(service).delete(1);
    }
}
