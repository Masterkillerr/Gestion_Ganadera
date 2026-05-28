package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.ProduccionDTO;
import com.gestionganadera.backend.dto.ProduccionResumenDTO;
import com.gestionganadera.backend.model.Produccion;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Full-stack integration test for ProduccionController.
 * Tests the complete flow: HTTP → Controller → Service → Repository → DB
 * using RestClient with real JWT auth and H2 database.
 */
class ProduccionControllerIntegrationTest extends BaseIntegrationTest {

    @Test
    void findAll_whenEmpty_returnsEmptyList() {
        ResponseEntity<List<ProduccionDTO>> response = restClient.get()
                .uri("/producciones")
                .headers(withAuth())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<ProduccionDTO> list = response.getBody();
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    @Test
    void findByAnimalId_nonExistent_returns404() {
        ResponseEntity<String> response = restClient.get()
                .uri("/producciones/animal/99999")
                .headers(withAuth())
                .retrieve()
                .toEntity(String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getResumen_returnsResumen() {
        // Setup — create data so the resumen has something to show
        setupProduccionData();

        ResponseEntity<List<ProduccionResumenDTO>> response = restClient.get()
                .uri("/producciones/resumen?year=" + LocalDate.now().getYear())
                .headers(withAuth())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<ProduccionResumenDTO> resumen = response.getBody();
        assertNotNull(resumen);
    }

    @Test
    void fullProduccionLifecycle_shouldSucceed() {
        // ── Setup entities ──
        Integer fincaId = createEntity("/fincas", "{\"nombre\":\"Finca Prod\",\"ubicacion\":\"Campo\"}");
        Integer loteId = createEntity("/lotes",
                "{\"nombre\":\"Lote Prod\",\"fincaId\":" + fincaId + ",\"hectareas\":50,\"capacidadMaxima\":30}");
        Integer razaId = createEntity("/razas", "{\"nombre\":\"Jersey\"}");
        Integer catId = createEntity("/categorias", "{\"nombre\":\"Adulto\",\"descripcion\":\"Vaca lechera\"}");

        // Create a cow (needed for milk production)
        Integer animalId = createEntity("/animales", String.format(
                "{\"identificadorArete\":\"PR-001\",\"nombre\":\"Vaca Lechera\",\"sexo\":\"Hembra\"," +
                "\"razaId\":%d,\"categoriaId\":%d,\"loteId\":%d,\"fincaId\":%d," +
                "\"fechaNacimiento\":\"2020-05-10\",\"pesoActual\":520.0,\"estado\":\"Activo\"}",
                razaId, catId, loteId, fincaId));

        // ── Create Produccion ──
        String fecha = LocalDate.now().toString();
        ResponseEntity<ProduccionDTO> createResponse = restClient.post()
                .uri("/producciones")
                .headers(withAuth())
                .body(String.format(
                        "{\"animalId\":%d,\"litros\":25.5,\"turno\":\"Manana\",\"fecha\":\"%s\"}",
                        animalId, fecha))
                .retrieve()
                .toEntity(ProduccionDTO.class);

        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        ProduccionDTO created = createResponse.getBody();
        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals(25.5, created.getLitros().doubleValue(), 0.01);
        assertEquals("Manana", created.getTurno());
        assertNotNull(created.getAnimalNombre());
        assertNotNull(created.getAnimalArete());

        // ── Find by ID via findAll ──
        ResponseEntity<List<ProduccionDTO>> listResponse = restClient.get()
                .uri("/producciones")
                .headers(withAuth())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.OK, listResponse.getStatusCode());
        List<ProduccionDTO> all = listResponse.getBody();
        assertNotNull(all);
        assertTrue(all.stream().anyMatch(p -> created.getId().equals(p.getId())));

        // ── Find by animal ID ──
        ResponseEntity<List<Produccion>> byAnimalResponse = restClient.get()
                .uri("/producciones/animal/" + animalId)
                .headers(withAuth())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.OK, byAnimalResponse.getStatusCode());
        List<Produccion> byAnimal = byAnimalResponse.getBody();
        assertNotNull(byAnimal);
        assertTrue(byAnimal.stream().anyMatch(p -> created.getId().equals(p.getId())));

        // ── Get resumen ──
        ResponseEntity<List<ProduccionResumenDTO>> resumenResponse = restClient.get()
                .uri("/producciones/resumen?year=" + LocalDate.now().getYear())
                .headers(withAuth())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.OK, resumenResponse.getStatusCode());
        List<ProduccionResumenDTO> resumen = resumenResponse.getBody();
        assertNotNull(resumen);
        assertFalse(resumen.isEmpty());

        // ── Update Produccion ──
        ResponseEntity<ProduccionDTO> updateResponse = restClient.put()
                .uri("/producciones/" + created.getId())
                .headers(withAuth())
                .body(String.format(
                        "{\"animalId\":%d,\"litros\":30.0,\"turno\":\"Tarde\",\"fecha\":\"%s\"}",
                        animalId, fecha))
                .retrieve()
                .toEntity(ProduccionDTO.class);

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        ProduccionDTO updated = updateResponse.getBody();
        assertNotNull(updated);
        assertEquals(30.0, updated.getLitros().doubleValue(), 0.01);
        assertEquals("Tarde", updated.getTurno());

        // ── Delete ──
        ResponseEntity<Void> deleteResponse = restClient.delete()
                .uri("/producciones/" + created.getId())
                .headers(withAuth())
                .retrieve()
                .toEntity(Void.class);

        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        // ── Verify deletion ──
        ResponseEntity<List<ProduccionDTO>> afterDeleteResponse = restClient.get()
                .uri("/producciones")
                .headers(withAuth())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.OK, afterDeleteResponse.getStatusCode());
        List<ProduccionDTO> remaining = afterDeleteResponse.getBody();
        assertNotNull(remaining);
        assertTrue(remaining.stream().noneMatch(p -> created.getId().equals(p.getId())));
    }

    @Test
    void withoutAuth_returns401() {
        ResponseEntity<String> response = restClient.get()
                .uri("/producciones")
                .headers(withJson())
                .retrieve()
                .toEntity(String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    // ── Helpers ──

    private void setupProduccionData() {
        Integer fincaId = createEntity("/fincas", "{\"nombre\":\"Finca Resumen\",\"ubicacion\":\"Campo\"}");
        Integer loteId = createEntity("/lotes",
                "{\"nombre\":\"Lote Resumen\",\"fincaId\":" + fincaId + ",\"hectareas\":50,\"capacidadMaxima\":30}");
        Integer razaId = createEntity("/razas", "{\"nombre\":\"Jersey\"}");
        Integer catId = createEntity("/categorias", "{\"nombre\":\"Adulto\",\"descripcion\":\"Vaca lechera\"}");

        Integer animalId = createEntity("/animales", String.format(
                "{\"identificadorArete\":\"RS-001\",\"nombre\":\"Vaca Resumen\",\"sexo\":\"Hembra\"," +
                "\"razaId\":%d,\"categoriaId\":%d,\"loteId\":%d,\"fincaId\":%d," +
                "\"fechaNacimiento\":\"2020-05-10\",\"pesoActual\":520.0,\"estado\":\"Activo\"}",
                razaId, catId, loteId, fincaId));

        String fecha = LocalDate.now().toString();
        restClient.post()
                .uri("/producciones")
                .headers(withAuth())
                .body(String.format(
                        "{\"animalId\":%d,\"litros\":20.0,\"turno\":\"Manana\",\"fecha\":\"%s\"}",
                        animalId, fecha))
                .retrieve()
                .toEntity(ProduccionDTO.class);
    }

}
