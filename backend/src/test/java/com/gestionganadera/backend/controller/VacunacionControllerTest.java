package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.CreateVacunacionRequest;
import com.gestionganadera.backend.dto.VacunacionDTO;
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
    void findAll_returnsList() {
        when(service.findAll()).thenReturn(List.of());

        ResponseEntity<List<VacunacionDTO>> response = controller.findAll();

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void findById_returnsVacunacion() {
        VacunacionDTO v = new VacunacionDTO();
        v.setId(1);
        when(service.findById(1)).thenReturn(v);

        ResponseEntity<VacunacionDTO> response = controller.findById(1);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void create_returnsCreated() {
        CreateVacunacionRequest request = new CreateVacunacionRequest();
        request.setEventoId(1);
        request.setVacunaId(1);

        VacunacionDTO saved = new VacunacionDTO();
        saved.setId(1);
        when(service.save(request)).thenReturn(saved);

        ResponseEntity<VacunacionDTO> response = controller.create(request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void update_returnsUpdated() {
        CreateVacunacionRequest request = new CreateVacunacionRequest();
        request.setEventoId(1);
        request.setVacunaId(1);

        VacunacionDTO updated = new VacunacionDTO();
        updated.setId(1);
        when(service.update(1, request)).thenReturn(updated);

        ResponseEntity<VacunacionDTO> response = controller.update(1, request);

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
