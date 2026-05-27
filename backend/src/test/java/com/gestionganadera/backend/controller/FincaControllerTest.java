package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.CreateFincaRequest;
import com.gestionganadera.backend.model.Finca;
import com.gestionganadera.backend.model.Usuario;
import com.gestionganadera.backend.service.FincaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FincaControllerTest {

    @Mock
    private FincaService fincaService;

    @InjectMocks
    private FincaController fincaController;

    @Test
    void getAllFincas_returnsList() {
        Finca finca = new Finca();
        finca.setId(1);
        finca.setNombre("Mi Finca");

        when(fincaService.findAll()).thenReturn(List.of(finca));

        ResponseEntity<List<Finca>> response = fincaController.getAllFincas();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        assertEquals("Mi Finca", response.getBody().get(0).getNombre());
    }

    @Test
    void getFincaById_found_returns200() {
        Finca finca = new Finca();
        finca.setId(1);
        when(fincaService.findById(1)).thenReturn(Optional.of(finca));

        ResponseEntity<Finca> response = fincaController.getFincaById(1);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void getFincaById_notFound_returns404() {
        when(fincaService.findById(999)).thenReturn(Optional.empty());

        ResponseEntity<Finca> response = fincaController.getFincaById(999);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void createFinca_returnsCreatedFinca() {
        CreateFincaRequest request = new CreateFincaRequest();
        request.setNombre("Nueva");

        Finca saved = new Finca();
        saved.setId(2);
        saved.setNombre("Nueva");

        when(fincaService.save(request)).thenReturn(saved);

        ResponseEntity<Finca> response = fincaController.createFinca(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Nueva", response.getBody().getNombre());
    }

    @Test
    void updateFinca_returnsUpdated() {
        CreateFincaRequest request = new CreateFincaRequest();
        request.setNombre("Actualizada");

        Finca updated = new Finca();
        updated.setId(1);
        updated.setNombre("Actualizada");

        when(fincaService.update(1, request)).thenReturn(updated);

        ResponseEntity<Finca> response = fincaController.updateFinca(1, request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Actualizada", response.getBody().getNombre());
    }

    @Test
    void deleteFinca_returns204() {
        doNothing().when(fincaService).delete(1);

        ResponseEntity<Void> response = fincaController.deleteFinca(1);

        assertEquals(204, response.getStatusCode().value());
        verify(fincaService).delete(1);
    }
}
