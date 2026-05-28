package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.EventoDTO;
import com.gestionganadera.backend.model.Evento;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EventoControllerIntegrationTest extends BaseIntegrationTest {

    @Test
    void getRecent_whenEmpty_returnsEmptyList() {
        ResponseEntity<List<EventoDTO>> response = restClient.get()
                .uri("/eventos/recent")
                .headers(withAuth())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<EventoDTO> list = response.getBody();
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    @Test
    void findByAnimalId_nonExistent_returns404() {
        ResponseEntity<String> response = restClient.get()
                .uri("/eventos/animal/99999")
                .headers(withAuth())
                .retrieve()
                .toEntity(String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void fullEventoLifecycle_shouldSucceed() {
        // ── Setup: create prerequisite entities ──
        Integer fincaId = createEntity("/fincas", "{\"nombre\":\"Finca Evt\",\"ubicacion\":\"Campo\"}");
        Integer loteId = createEntity("/lotes",
                "{\"nombre\":\"Lote Evt\",\"fincaId\":" + fincaId + ",\"hectareas\":50,\"capacidadMaxima\":30}");
        Integer razaId = createEntity("/razas", "{\"nombre\":\"Hereford\"}");
        Integer catId = createEntity("/categorias", "{\"nombre\":\"Adulto\",\"descripcion\":\"Adulto\"}");

        Integer animalId = createEntity("/animales", String.format(
                "{\"identificadorArete\":\"EV-001\",\"nombre\":\"Animal Evt\",\"sexo\":\"Hembra\"," +
                "\"razaId\":%d,\"categoriaId\":%d,\"loteId\":%d,\"fincaId\":%d," +
                "\"fechaNacimiento\":\"2021-08-15\",\"pesoActual\":480.0,\"estado\":\"Activo\"}",
                razaId, catId, loteId, fincaId));

        // ── Create Evento ──
        ResponseEntity<Evento> createResponse = restClient.post()
                .uri("/eventos")
                .headers(withAuth())
                .body(String.format(
                        "{\"animalId\":%d,\"tipo\":\"Vacunacion\",\"descripcion\":\"Vacuna antiaftosa\"}",
                        animalId))
                .retrieve()
                .toEntity(Evento.class);

        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        Evento created = createResponse.getBody();
        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("Vacunacion", created.getTipo());
        assertEquals("Vacuna antiaftosa", created.getDescripcion());
        assertNotNull(created.getFecha());
        assertNotNull(created.getAnimal());

        // ── Find by animal ID ──
        ResponseEntity<List<Evento>> byAnimalResponse = restClient.get()
                .uri("/eventos/animal/" + animalId)
                .headers(withAuth())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.OK, byAnimalResponse.getStatusCode());
        List<Evento> byAnimal = byAnimalResponse.getBody();
        assertNotNull(byAnimal);
        assertFalse(byAnimal.isEmpty());
        assertTrue(byAnimal.stream().anyMatch(e -> created.getId().equals(e.getId())));

        // ── Get recent events ──
        ResponseEntity<List<EventoDTO>> recentResponse = restClient.get()
                .uri("/eventos/recent")
                .headers(withAuth())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.OK, recentResponse.getStatusCode());
        List<EventoDTO> recent = recentResponse.getBody();
        assertNotNull(recent);
        assertFalse(recent.isEmpty());
        assertTrue(recent.stream().anyMatch(e -> created.getId().equals(e.getId())));

        // ── Delete ──
        ResponseEntity<Void> deleteResponse = restClient.delete()
                .uri("/eventos/" + created.getId())
                .headers(withAuth())
                .retrieve()
                .toEntity(Void.class);

        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        // ── Verify deletion ──
        ResponseEntity<List<Evento>> afterDeleteResponse = restClient.get()
                .uri("/eventos/animal/" + animalId)
                .headers(withAuth())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.OK, afterDeleteResponse.getStatusCode());
        List<Evento> remaining = afterDeleteResponse.getBody();
        assertNotNull(remaining);
        assertTrue(remaining.stream().noneMatch(e -> created.getId().equals(e.getId())));
    }

    @Test
    void withoutAuth_returns401() {
        ResponseEntity<String> response = restClient.get()
                .uri("/eventos/recent")
                .headers(withJson())
                .retrieve()
                .toEntity(String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

}
