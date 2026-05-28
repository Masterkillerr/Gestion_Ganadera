package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.CreateProduccionRequest;
import com.gestionganadera.backend.dto.ProduccionDTO;
import com.gestionganadera.backend.dto.ProduccionResumenDTO;
import com.gestionganadera.backend.model.Produccion;
import com.gestionganadera.backend.service.ProduccionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
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
        Produccion produccion = new Produccion();
        produccion.setId(1);
        produccion.setLitros(BigDecimal.TEN);
        when(service.findByAnimalId(10)).thenReturn(List.of(produccion));

        ResponseEntity<List<Produccion>> response = controller.findByAnimalId(10);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        assertEquals(BigDecimal.TEN, response.getBody().get(0).getLitros());
    }

    @Test
    void getResumen_returnsResumen() {
        ProduccionResumenDTO resumen = new ProduccionResumenDTO(2025, 1, BigDecimal.valueOf(100), 5L);
        when(service.getResumen(2025)).thenReturn(List.of(resumen));

        ResponseEntity<List<ProduccionResumenDTO>> response = controller.getResumen(2025);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        assertEquals(2025, response.getBody().get(0).getYear());
        assertEquals(1, response.getBody().get(0).getMonth());
        assertEquals(BigDecimal.valueOf(100), response.getBody().get(0).getTotalLitros());
        assertEquals(5L, response.getBody().get(0).getCantidad());
    }

    @Test
    void create_returnsProduccionDTO() {
        CreateProduccionRequest request = new CreateProduccionRequest();
        request.setAnimalId(100);
        request.setLitros(BigDecimal.valueOf(10));
        request.setTurno("Manana");
        request.setFecha(LocalDate.now());

        ProduccionDTO saved = new ProduccionDTO();
        saved.setId(1);
        saved.setAnimalId(100);
        saved.setLitros(BigDecimal.valueOf(10));
        when(service.create(request)).thenReturn(saved);

        ResponseEntity<ProduccionDTO> response = controller.create(request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getId());
    }

    @Test
    void update_returnsUpdated() {
        CreateProduccionRequest request = new CreateProduccionRequest();
        request.setAnimalId(100);
        request.setLitros(BigDecimal.valueOf(15));
        request.setTurno("Tarde");
        request.setFecha(LocalDate.of(2025, 2, 1));

        ProduccionDTO updated = new ProduccionDTO();
        updated.setId(1);
        updated.setAnimalId(100);
        updated.setLitros(BigDecimal.valueOf(15));
        updated.setTurno("Tarde");
        updated.setFecha(LocalDate.of(2025, 2, 1));
        when(service.update(1, request)).thenReturn(updated);

        ResponseEntity<ProduccionDTO> response = controller.update(1, request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(BigDecimal.valueOf(15), response.getBody().getLitros());
        assertEquals("Tarde", response.getBody().getTurno());
    }

    @Test
    void delete_returns204() {
        doNothing().when(service).delete(1);

        ResponseEntity<Void> response = controller.delete(1);

        assertEquals(204, response.getStatusCode().value());
        verify(service).delete(1);
    }
}
