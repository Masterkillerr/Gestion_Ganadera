package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.CreateTratamientoRequest;
import com.gestionganadera.backend.model.Tratamiento;
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
    void findByAnimalId_returnsList() {
        when(service.findByAnimalId(100)).thenReturn(List.of());

        ResponseEntity<List<Tratamiento>> response = controller.findByAnimalId(100);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void create_returnsCreated() {
        CreateTratamientoRequest request = new CreateTratamientoRequest();
        request.setAnimalId(100);
        request.setMedicamentoId(1);
        request.setDosis("10ml");
        request.setFechaInicio(LocalDate.now());

        Tratamiento saved = new Tratamiento();
        saved.setId(1);
        when(service.save(request)).thenReturn(saved);

        ResponseEntity<Tratamiento> response = controller.create(request);

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
