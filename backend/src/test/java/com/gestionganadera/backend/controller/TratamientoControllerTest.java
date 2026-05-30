package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.CreateTratamientoRequest;
import com.gestionganadera.backend.dto.TratamientoDTO;
import com.gestionganadera.backend.service.TratamientoService;
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
class TratamientoControllerTest {

    @Mock
    private TratamientoService service;

    @InjectMocks
    private TratamientoController controller;

    @Test
    void findAll_returnsList() {
        when(service.findAll()).thenReturn(List.of());

        ResponseEntity<List<TratamientoDTO>> response = controller.findAll();

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void findById_returnsTratamiento() {
        TratamientoDTO t = new TratamientoDTO();
        t.setId(1);
        when(service.findById(1)).thenReturn(t);

        ResponseEntity<TratamientoDTO> response = controller.findById(1);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void create_returnsCreated() {
        CreateTratamientoRequest request = new CreateTratamientoRequest();
        request.setEventoId(1);
        request.setMedicamentoId(1);
        request.setDosisMl("10ml");
        request.setFechaInicio(LocalDate.now());

        TratamientoDTO saved = new TratamientoDTO();
        saved.setId(1);
        when(service.save(request)).thenReturn(saved);

        ResponseEntity<TratamientoDTO> response = controller.create(request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void update_returnsUpdated() {
        CreateTratamientoRequest request = new CreateTratamientoRequest();
        request.setDosisMl("15ml");

        TratamientoDTO updated = new TratamientoDTO();
        updated.setId(1);
        when(service.update(1, request)).thenReturn(updated);

        ResponseEntity<TratamientoDTO> response = controller.update(1, request);

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
