package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.CreateAlimentacionRequest;
import com.gestionganadera.backend.model.Alimentacion;
import com.gestionganadera.backend.service.AlimentacionService;
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
class AlimentacionControllerTest {

    @Mock
    private AlimentacionService service;

    @InjectMocks
    private AlimentacionController controller;

    @Test
    void findByAnimalId_returnsList() {
        when(service.findByAnimalId(100)).thenReturn(List.of());

        ResponseEntity<List<Alimentacion>> response = controller.findByAnimalId(100);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void create_returnsCreated() {
        CreateAlimentacionRequest request = new CreateAlimentacionRequest();
        request.setAnimalId(100);
        request.setAlimentoId(1);
        request.setCantidad(BigDecimal.valueOf(5));
        request.setFecha(LocalDate.now());

        Alimentacion saved = new Alimentacion();
        saved.setId(1);
        when(service.save(request)).thenReturn(saved);

        ResponseEntity<Alimentacion> response = controller.create(request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void delete_returns204() {
        doNothing().when(service).delete(1);

        ResponseEntity<Void> response = controller.delete(1);

        assertEquals(204, response.getStatusCode().value());
        verify(service).delete(1);
    }
}
