package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.model.Vacunacion;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VacunacionControllerIntegrationTest extends BaseIntegrationTest {

    @Test
    void findByAnimalId_nonExistent_returns404() {
        ResponseEntity<String> response = restClient.get()
                .uri("/vacunaciones/animal/99999")
                .headers(withAuth())
                .retrieve()
                .toEntity(String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void fullVacunacionLifecycle_shouldSucceed() {
        // ── Setup: create prerequisite entities ──
        Integer fincaId = createEntity("/fincas", "{\"nombre\":\"Finca Vac\",\"ubicacion\":\"Campo\"}");
        Integer loteId = createEntity("/lotes",
                "{\"nombre\":\"Lote Vac\",\"fincaId\":" + fincaId + ",\"hectareas\":50,\"capacidadMaxima\":30}");
        Integer razaId = createEntity("/razas", "{\"nombre\":\"Angus\"}");
        Integer catId = createEntity("/categorias", "{\"nombre\":\"Adulto\",\"descripcion\":\"Adulto\"}");

        Integer animalId = createEntity("/animales", String.format(
                "{\"identificadorArete\":\"VC-001\",\"nombre\":\"Animal Vac\",\"sexo\":\"Hembra\"," +
                "\"razaId\":%d,\"categoriaId\":%d,\"loteId\":%d,\"fincaId\":%d," +
                "\"fechaNacimiento\":\"2022-05-10\",\"pesoActual\":450.0,\"estado\":\"Activo\"}",
                razaId, catId, loteId, fincaId));

        // Create a Vacuna reference via JDBC
        jdbcTemplate.execute("INSERT INTO vacunas (nombre) VALUES ('Aftosa')");

        // ── Create Vacunacion ──
        String fecha = LocalDate.now().toString();
        String proxDosis = LocalDate.now().plusMonths(6).toString();
        ResponseEntity<Vacunacion> createResponse = restClient.post()
                .uri("/vacunaciones")
                .headers(withAuth())
                .body(String.format(
                        "{\"animalId\":%d,\"vacunaId\":1,\"fecha\":\"%s\",\"proximaDosis\":\"%s\",\"observaciones\":\"Dosis anual\"}",
                        animalId, fecha, proxDosis))
                .retrieve()
                .toEntity(Vacunacion.class);

        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        Vacunacion created = createResponse.getBody();
        assertNotNull(created);
        assertNotNull(created.getId());
        assertNotNull(created.getAnimal());
        assertNotNull(created.getVacuna());
        assertEquals(LocalDate.parse(fecha), created.getFecha());
        assertEquals(LocalDate.parse(proxDosis), created.getProximaDosis());

        // ── Find by animal ID ──
        ResponseEntity<List<Vacunacion>> byAnimalResponse = restClient.get()
                .uri("/vacunaciones/animal/" + animalId)
                .headers(withAuth())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.OK, byAnimalResponse.getStatusCode());
        List<Vacunacion> byAnimal = byAnimalResponse.getBody();
        assertNotNull(byAnimal);
        assertFalse(byAnimal.isEmpty());
        assertTrue(byAnimal.stream().anyMatch(v -> created.getId().equals(v.getId())));

        // ── Delete ──
        ResponseEntity<Void> deleteResponse = restClient.delete()
                .uri("/vacunaciones/" + created.getId())
                .headers(withAuth())
                .retrieve()
                .toEntity(Void.class);

        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        // ── Verify deletion ──
        ResponseEntity<List<Vacunacion>> afterDeleteResponse = restClient.get()
                .uri("/vacunaciones/animal/" + animalId)
                .headers(withAuth())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.OK, afterDeleteResponse.getStatusCode());
        List<Vacunacion> remaining = afterDeleteResponse.getBody();
        assertNotNull(remaining);
        assertTrue(remaining.stream().noneMatch(v -> created.getId().equals(v.getId())));
    }

    @Test
    void withoutAuth_returns401() {
        ResponseEntity<String> response = restClient.get()
                .uri("/vacunaciones/animal/1")
                .headers(withJson())
                .retrieve()
                .toEntity(String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

}
