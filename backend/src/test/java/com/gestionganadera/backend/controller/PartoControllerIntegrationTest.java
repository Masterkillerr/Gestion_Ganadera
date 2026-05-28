package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.PartoDTO;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Full-stack integration test for PartoController.
 * Tests the complete flow: HTTP → Controller → Service → Repository → DB
 * using RestClient with real JWT auth and H2 database.
 */
class PartoControllerIntegrationTest extends BaseIntegrationTest {

    @Test
    void findAll_whenEmpty_returnsEmptyList() {
        ResponseEntity<List<PartoDTO>> response = restClient.get()
                .uri("/partos")
                .headers(withAuth())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<PartoDTO> partos = response.getBody();
        assertNotNull(partos);
        assertTrue(partos.isEmpty());
    }

    @Test
    void findById_nonExistent_returns404() {
        ResponseEntity<String> response = restClient.get()
                .uri("/partos/99999")
                .headers(withAuth())
                .retrieve()
                .toEntity(String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void fullPartoLifecycle_shouldSucceed() {
        // ── 1. Setup: create finca, razas, categorias, lotes, animales ──
        Integer fincaId = createEntity("/fincas", "{\"nombre\":\"Finca Partos\",\"ubicacion\":\"Campo\"}");
        Integer loteId = createEntity("/lotes",
                "{\"nombre\":\"Lote Partos\",\"fincaId\":" + fincaId + ",\"hectareas\":50,\"capacidadMaxima\":30}");
        Integer razaId = createEntity("/razas", "{\"nombre\":\"Holstein\"}");
        Integer catId = createEntity("/categorias", "{\"nombre\":\"Adulto\",\"descripcion\":\"Adulto\"}");

        // Create a vaca (hembra) and a toro (macho)
        Integer vacaId = createEntity("/animales", String.format(
                "{\"identificadorArete\":\"V-001\",\"nombre\":\"Vaca 1\",\"sexo\":\"Hembra\"," +
                "\"razaId\":%d,\"categoriaId\":%d,\"loteId\":%d,\"fincaId\":%d," +
                "\"fechaNacimiento\":\"2020-01-15\",\"pesoActual\":500.0,\"estado\":\"Activo\"}",
                razaId, catId, loteId, fincaId));

        Integer toroId = createEntity("/animales", String.format(
                "{\"identificadorArete\":\"T-001\",\"nombre\":\"Toro 1\",\"sexo\":\"Macho\"," +
                "\"razaId\":%d,\"categoriaId\":%d,\"loteId\":%d,\"fincaId\":%d," +
                "\"fechaNacimiento\":\"2019-05-10\",\"pesoActual\":700.0,\"estado\":\"Activo\"}",
                razaId, catId, loteId, fincaId));

        // ── 2. Create a Reproduccion first (required by Parto FK) ──
        // Note: createEntity expects 200 OK but ReproduccionController returns 201 CREATED,
        // so we create it inline here.
        String fechaMonta = LocalDate.of(2025, 1, 15).toString();
        String fechaPartoEst = LocalDate.of(2025, 10, 15).toString();
        ResponseEntity<String> reproResponse = restClient.post()
                .uri("/reproducciones")
                .headers(withAuth())
                .body(String.format(
                        "{\"vacaId\":%d,\"toroId\":%d,\"fechaMonta\":\"%s\"," +
                        "\"tipo\":\"Natural\",\"resultado\":\"Gestante\"," +
                        "\"fechaPartoEstimada\":\"%s\",\"observaciones\":\"Monta exitosa\"}",
                        vacaId, toroId, fechaMonta, fechaPartoEst))
                .retrieve()
                .toEntity(String.class);
        assertEquals(HttpStatus.CREATED, reproResponse.getStatusCode());
        Integer reproId;
        try {
            reproId = objectMapper.readTree(reproResponse.getBody()).get("id").asInt();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse ID from reproduccion response", e);
        }
        assertNotNull(reproId);

        // ── 3. Create Parto ──
        String fechaParto = LocalDate.of(2025, 10, 10).toString();
        ResponseEntity<PartoDTO> createResponse = restClient.post()
                .uri("/partos")
                .headers(withAuth())
                .body(String.format(
                        "{\"reproduccionId\":%d,\"fechaParto\":\"%s\"," +
                        "\"cantidadCrias\":2,\"observaciones\":\"Parto normal\"}",
                        reproId, fechaParto))
                .retrieve()
                .toEntity(PartoDTO.class);

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        PartoDTO created = createResponse.getBody();
        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals(2, created.getCantidadCrias());
        assertEquals("Parto normal", created.getObservaciones());

        // ── 4. Find by ID ──
        ResponseEntity<PartoDTO> byIdResponse = restClient.get()
                .uri("/partos/" + created.getId())
                .headers(withAuth())
                .retrieve()
                .toEntity(PartoDTO.class);

        assertEquals(HttpStatus.OK, byIdResponse.getStatusCode());
        PartoDTO found = byIdResponse.getBody();
        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
        assertEquals(2, found.getCantidadCrias());

        // ── 5. Find by Reproduccion ID ──
        ResponseEntity<List<PartoDTO>> byReproResponse = restClient.get()
                .uri("/partos/por-reproduccion/" + reproId)
                .headers(withAuth())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.OK, byReproResponse.getStatusCode());
        List<PartoDTO> byRepro = byReproResponse.getBody();
        assertNotNull(byRepro);
        assertEquals(1, byRepro.size());
        assertEquals(created.getId(), byRepro.get(0).getId());

        // ── 6. Update Parto ──
        ResponseEntity<PartoDTO> updateResponse = restClient.put()
                .uri("/partos/" + created.getId())
                .headers(withAuth())
                .body(String.format(
                        "{\"reproduccionId\":%d,\"fechaParto\":\"%s\"," +
                        "\"cantidadCrias\":3,\"observaciones\":\"Parto gemelar\"}",
                        reproId, fechaParto))
                .retrieve()
                .toEntity(PartoDTO.class);

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        PartoDTO updated = updateResponse.getBody();
        assertNotNull(updated);
        assertEquals(3, updated.getCantidadCrias());
        assertEquals("Parto gemelar", updated.getObservaciones());

        // ── 7. Delete Parto ──
        ResponseEntity<Void> deleteResponse = restClient.delete()
                .uri("/partos/" + created.getId())
                .headers(withAuth())
                .retrieve()
                .toEntity(Void.class);

        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        // ── 8. Verify deletion ──
        ResponseEntity<String> afterDeleteResponse = restClient.get()
                .uri("/partos/" + created.getId())
                .headers(withAuth())
                .retrieve()
                .toEntity(String.class);

        assertEquals(HttpStatus.NOT_FOUND, afterDeleteResponse.getStatusCode());
    }

    @Test
    void withoutAuth_returns401() {
        ResponseEntity<String> response = restClient.get()
                .uri("/partos")
                .headers(withJson())
                .retrieve()
                .toEntity(String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

}
