package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.model.Tratamiento;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TratamientoControllerIntegrationTest extends BaseIntegrationTest {

    @Test
    void findByAnimalId_nonExistent_returns404() {
        ResponseEntity<String> response = restClient.get()
                .uri("/tratamientos/animal/99999")
                .headers(withAuth())
                .retrieve()
                .toEntity(String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void fullTratamientoLifecycle_shouldSucceed() {
        // ── Setup: create prerequisite entities ──
        Integer fincaId = createEntity("/fincas", "{\"nombre\":\"Finca Trat\",\"ubicacion\":\"Campo\"}");
        Integer loteId = createEntity("/lotes",
                "{\"nombre\":\"Lote Trat\",\"fincaId\":" + fincaId + ",\"hectareas\":50,\"capacidadMaxima\":30}");
        Integer razaId = createEntity("/razas", "{\"nombre\":\"Brahman\"}");
        Integer catId = createEntity("/categorias", "{\"nombre\":\"Adulto\",\"descripcion\":\"Adulto\"}");

        Integer animalId = createEntity("/animales", String.format(
                "{\"identificadorArete\":\"TR-001\",\"nombre\":\"Animal Trat\",\"sexo\":\"Macho\"," +
                "\"razaId\":%d,\"categoriaId\":%d,\"loteId\":%d,\"fincaId\":%d," +
                "\"fechaNacimiento\":\"2020-11-20\",\"pesoActual\":600.0,\"estado\":\"Activo\"}",
                razaId, catId, loteId, fincaId));

        // Create a Medicamento reference via JDBC
        jdbcTemplate.execute("INSERT INTO medicamentos (nombre, descripcion) " +
                "VALUES ('Ivermectina', 'Antiparasitario')");

        // ── Create Tratamiento ──
        ResponseEntity<Tratamiento> createResponse = restClient.post()
                .uri("/tratamientos")
                .headers(withAuth())
                .body(String.format(
                        "{\"animalId\":%d,\"medicamentoId\":1,\"dosis\":\"5ml\",\"observaciones\":\"Tratamiento antiparasitario\"}",
                        animalId))
                .retrieve()
                .toEntity(Tratamiento.class);

        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        Tratamiento created = createResponse.getBody();
        assertNotNull(created);
        assertNotNull(created.getId());
        assertNotNull(created.getAnimal());
        assertNotNull(created.getMedicamento());
        assertEquals("5ml", created.getDosis());
        assertEquals("Tratamiento antiparasitario", created.getObservaciones());

        // ── Find by animal ID ──
        ResponseEntity<List<Tratamiento>> byAnimalResponse = restClient.get()
                .uri("/tratamientos/animal/" + animalId)
                .headers(withAuth())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.OK, byAnimalResponse.getStatusCode());
        List<Tratamiento> byAnimal = byAnimalResponse.getBody();
        assertNotNull(byAnimal);
        assertFalse(byAnimal.isEmpty());
        assertTrue(byAnimal.stream().anyMatch(t -> created.getId().equals(t.getId())));

        // ── Delete ──
        ResponseEntity<Void> deleteResponse = restClient.delete()
                .uri("/tratamientos/" + created.getId())
                .headers(withAuth())
                .retrieve()
                .toEntity(Void.class);

        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        // ── Verify deletion ──
        ResponseEntity<List<Tratamiento>> afterDeleteResponse = restClient.get()
                .uri("/tratamientos/animal/" + animalId)
                .headers(withAuth())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.OK, afterDeleteResponse.getStatusCode());
        List<Tratamiento> remaining = afterDeleteResponse.getBody();
        assertNotNull(remaining);
        assertTrue(remaining.stream().noneMatch(t -> created.getId().equals(t.getId())));
    }

    @Test
    void withoutAuth_returns401() {
        ResponseEntity<String> response = restClient.get()
                .uri("/tratamientos/animal/1")
                .headers(withJson())
                .retrieve()
                .toEntity(String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

}
