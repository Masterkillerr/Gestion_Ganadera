package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.CreateEventoRequest;
import com.gestionganadera.backend.dto.EventoDTO;
import com.gestionganadera.backend.model.Evento;
import com.gestionganadera.backend.service.EventoService;
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
class EventoControllerTest {

    @Mock
    private EventoService service;

    @InjectMocks
    private EventoController controller;

    @Test
    void getRecent_returnsList() {
        when(service.getRecent()).thenReturn(List.of());

        ResponseEntity<List<EventoDTO>> response = controller.getRecent();

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void findByAnimalId_returnsList() {
        when(service.findByAnimalId(100)).thenReturn(List.of());

        ResponseEntity<List<Evento>> response = controller.findByAnimalId(100);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void create_returnsCreated() {
        CreateEventoRequest request = new CreateEventoRequest();
        request.setAnimalId(100);
        request.setTipoEventoId(1);
        request.setDescripcion("Revision");

        Evento saved = new Evento();
        saved.setId(1);
        when(service.save(request)).thenReturn(saved);

        ResponseEntity<Evento> response = controller.create(request);

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
