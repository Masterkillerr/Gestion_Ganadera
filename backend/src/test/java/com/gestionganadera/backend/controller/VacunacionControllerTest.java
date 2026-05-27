package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.CreateVacunacionRequest;
import com.gestionganadera.backend.model.Vacunacion;
import com.gestionganadera.backend.service.VacunacionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VacunacionControllerTest {

    @Mock
    private VacunacionService service;

    @InjectMocks
    private VacunacionController controller;

    @Test
    void findByAnimalId_returnsList() {
        when(service.findByAnimalId(100)).thenReturn(List.of());

        ResponseEntity<List<Vacunacion>> response = controller.findByAnimalId(100);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void create_returnsCreated() {
        CreateVacunacionRequest request = new CreateVacunacionRequest();
        request.setAnimalId(100);
        request.setVacunaId(1);
        request.setFecha(LocalDate.now());

        Vacunacion saved = new Vacunacion();
        saved.setId(1);
        when(service.save(request)).thenReturn(saved);

        ResponseEntity<Vacunacion> response = controller.create(request);

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
