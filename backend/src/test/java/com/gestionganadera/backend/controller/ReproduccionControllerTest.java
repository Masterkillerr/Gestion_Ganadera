package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.CreateReproduccionRequest;
import com.gestionganadera.backend.dto.PartosProximosDTO;
import com.gestionganadera.backend.dto.ReproduccionDTO;
import com.gestionganadera.backend.service.ReproduccionService;
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
class ReproduccionControllerTest {

    @Mock
    private ReproduccionService reproduccionService;

    @InjectMocks
    private ReproduccionController controller;

    private ReproduccionDTO createDTO(Integer id) {
        ReproduccionDTO dto = new ReproduccionDTO();
        dto.setId(id);
        dto.setVacaId(1);
        dto.setVacaNombre("Vaca Test");
        dto.setVacaArete("AR-001");
        dto.setTipoReproduccion("Natural");
        return dto;
    }

    @Test
    void getProximosPartos_returnsList() {
        PartosProximosDTO p = new PartosProximosDTO();
        p.setReproduccionId(1);
        p.setVacaNombre("Vaca Test");
        p.setFechaPartoEstimada(LocalDate.of(2026, 7, 1));
        p.setDiasRestantes(34L);

        when(reproduccionService.getProximosPartos()).thenReturn(List.of(p));

        ResponseEntity<List<PartosProximosDTO>> response = controller.getProximosPartos();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        assertEquals("Vaca Test", response.getBody().get(0).getVacaNombre());
    }

    @Test
    void findAll_returnsList() {
        when(reproduccionService.findAll()).thenReturn(List.of(createDTO(1), createDTO(2)));

        ResponseEntity<List<ReproduccionDTO>> response = controller.findAll();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void findById_returnsDTO() {
        ReproduccionDTO dto = createDTO(1);
        when(reproduccionService.findById(1)).thenReturn(dto);

        ResponseEntity<ReproduccionDTO> response = controller.findById(1);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().getId());
        assertEquals("Natural", response.getBody().getTipoReproduccion());
    }

    @Test
    void create_returns201() {
        CreateReproduccionRequest request = new CreateReproduccionRequest();
        request.setVacaId(1);
        request.setTipoReproduccionId(1);

        ReproduccionDTO saved = createDTO(3);
        when(reproduccionService.create(request)).thenReturn(saved);

        ResponseEntity<ReproduccionDTO> response = controller.create(request);

        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().getId());
    }

    @Test
    void update_returns200() {
        CreateReproduccionRequest request = new CreateReproduccionRequest();
        request.setResultadoReproduccionId(1);

        ReproduccionDTO updated = createDTO(1);
        updated.setResultadoReproduccion("Exitoso");
        when(reproduccionService.update(1, request)).thenReturn(updated);

        ResponseEntity<ReproduccionDTO> response = controller.update(1, request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Exitoso", response.getBody().getResultadoReproduccion());
    }

    @Test
    void delete_returns204() {
        doNothing().when(reproduccionService).delete(1);

        ResponseEntity<Void> response = controller.delete(1);

        assertEquals(204, response.getStatusCode().value());
        verify(reproduccionService).delete(1);
    }
}
