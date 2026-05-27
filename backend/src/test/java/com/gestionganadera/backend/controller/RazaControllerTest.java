package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.CreateRazaRequest;
import com.gestionganadera.backend.model.Raza;
import com.gestionganadera.backend.service.RazaService;
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
class RazaControllerTest {

    @Mock
    private RazaService razaService;

    @InjectMocks
    private RazaController controller;

    @Test
    void findAll_returnsList() {
        Raza raza = new Raza();
        raza.setId(1);
        raza.setNombre("Holstein");
        when(razaService.findAll()).thenReturn(List.of(raza));

        ResponseEntity<List<Raza>> response = controller.findAll();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void createRaza_returnsCreated() {
        CreateRazaRequest request = new CreateRazaRequest();
        request.setNombre("Angus");

        Raza saved = new Raza();
        saved.setId(2);
        saved.setNombre("Angus");
        when(razaService.save(request)).thenReturn(saved);

        ResponseEntity<Raza> response = controller.createRaza(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Angus", response.getBody().getNombre());
    }
}
