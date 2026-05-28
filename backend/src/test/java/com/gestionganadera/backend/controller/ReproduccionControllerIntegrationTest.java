package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.PartosProximosDTO;
import com.gestionganadera.backend.dto.ReproduccionDTO;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Full-stack integration test for ReproduccionController.
 * Tests the complete flow: HTTP → Controller → Service → Repository → DB
 * using RestClient with real JWT auth and H2 database.
 */
class ReproduccionControllerIntegrationTest extends BaseIntegrationTest {

    @Test
    void findAll_whenEmpty_returnsEmptyList() {
        ResponseEntity<List<ReproduccionDTO>> response = restClient.get()
                .uri("/reproducciones")
                .headers(withAuth())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<ReproduccionDTO> list = response.getBody();
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    @Test
    void findById_nonExistent_returns404() {
        ResponseEntity<String> response = restClient.get()
                .uri("/reproducciones/99999")
                .headers(withAuth())
                .retrieve()
                .toEntity(String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void fullReproduccionLifecycle_shouldSucceed() {
        // ── Setup: create finca, lotes, razas, categorias, animales ──
        Integer fincaId = createEntity("/fincas", "{\"nombre\":\"Finca Repro\",\"ubicacion\":\"Campo\"}");
        Integer loteId = createEntity("/lotes",
                "{\"nombre\":\"Lote Repro\",\"fincaId\":" + fincaId + ",\"hectareas\":50,\"capacidadMaxima\":30}");
        Integer razaId = createEntity("/razas", "{\"nombre\":\"Angus\"}");
        Integer catId = createEntity("/categorias", "{\"nombre\":\"Adulto\",\"descripcion\":\"Adulto\"}");

        // Create a vaca (hembra) and toro (macho)
        Integer vacaId = createEntity("/animales", String.format(
                "{\"identificadorArete\":\"RV-001\",\"nombre\":\"Repro Vaca\",\"sexo\":\"Hembra\"," +
                "\"razaId\":%d,\"categoriaId\":%d,\"loteId\":%d,\"fincaId\":%d," +
                "\"fechaNacimiento\":\"2020-03-10\",\"pesoActual\":480.0,\"estado\":\"Activo\"}",
                razaId, catId, loteId, fincaId));

        Integer toroId = createEntity("/animales", String.format(
                "{\"identificadorArete\":\"RT-001\",\"nombre\":\"Repro Toro\",\"sexo\":\"Macho\"," +
                "\"razaId\":%d,\"categoriaId\":%d,\"loteId\":%d,\"fincaId\":%d," +
                "\"fechaNacimiento\":\"2019-08-20\",\"pesoActual\":750.0,\"estado\":\"Activo\"}",
                razaId, catId, loteId, fincaId));

        // ── Create Reproduccion ──
        String fechaMonta = LocalDate.of(2025, 2, 1).toString();
        String fechaPartoEst = LocalDate.of(2025, 11, 1).toString();
        ResponseEntity<ReproduccionDTO> createResponse = restClient.post()
                .uri("/reproducciones")
                .headers(withAuth())
                .body(String.format(
                        "{\"vacaId\":%d,\"toroId\":%d,\"fechaMonta\":\"%s\"," +
                        "\"tipo\":\"Natural\",\"resultado\":\"Gestante\"," +
                        "\"fechaPartoEstimada\":\"%s\",\"observaciones\":\"Test repro\"}",
                        vacaId, toroId, fechaMonta, fechaPartoEst))
                .retrieve()
                .toEntity(ReproduccionDTO.class);

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        ReproduccionDTO created = createResponse.getBody();
        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("Natural", created.getTipo());
        assertEquals("Gestante", created.getResultado());
        assertNotNull(created.getVacaNombre());
        assertNotNull(created.getToroNombre());

        // ── Find by ID ──
        ResponseEntity<ReproduccionDTO> byIdResponse = restClient.get()
                .uri("/reproducciones/" + created.getId())
                .headers(withAuth())
                .retrieve()
                .toEntity(ReproduccionDTO.class);

        assertEquals(HttpStatus.OK, byIdResponse.getStatusCode());
        ReproduccionDTO found = byIdResponse.getBody();
        assertNotNull(found);
        assertEquals(created.getId(), found.getId());

        // ── Find all ──
        ResponseEntity<List<ReproduccionDTO>> listResponse = restClient.get()
                .uri("/reproducciones")
                .headers(withAuth())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.OK, listResponse.getStatusCode());
        List<ReproduccionDTO> all = listResponse.getBody();
        assertNotNull(all);
        assertTrue(all.stream().anyMatch(r -> created.getId().equals(r.getId())));

        // ── Get proximos partos ──
        ResponseEntity<List<PartosProximosDTO>> proximosResponse = restClient.get()
                .uri("/reproducciones/proximos-partos")
                .headers(withAuth())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.OK, proximosResponse.getStatusCode());
        List<PartosProximosDTO> proximos = proximosResponse.getBody();
        assertNotNull(proximos);

        // ── Update Reproduccion ──
        String newFechaPartoEst = LocalDate.of(2025, 11, 15).toString();
        ResponseEntity<ReproduccionDTO> updateResponse = restClient.put()
                .uri("/reproducciones/" + created.getId())
                .headers(withAuth())
                .body(String.format(
                        "{\"vacaId\":%d,\"toroId\":%d,\"fechaMonta\":\"%s\"," +
                        "\"tipo\":\"Inseminacion\",\"resultado\":\"Gestante\"," +
                        "\"fechaPartoEstimada\":\"%s\",\"observaciones\":\"Actualizada\"}",
                        vacaId, toroId, fechaMonta, newFechaPartoEst))
                .retrieve()
                .toEntity(ReproduccionDTO.class);

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        ReproduccionDTO updated = updateResponse.getBody();
        assertNotNull(updated);
        assertEquals("Inseminacion", updated.getTipo());
        assertEquals("Actualizada", updated.getObservaciones());

        // ── Delete ──
        ResponseEntity<Void> deleteResponse = restClient.delete()
                .uri("/reproducciones/" + created.getId())
                .headers(withAuth())
                .retrieve()
                .toEntity(Void.class);

        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        // ── Verify deletion ──
        ResponseEntity<String> afterDeleteResponse = restClient.get()
                .uri("/reproducciones/" + created.getId())
                .headers(withAuth())
                .retrieve()
                .toEntity(String.class);

        assertEquals(HttpStatus.NOT_FOUND, afterDeleteResponse.getStatusCode());
    }

    @Test
    void withoutAuth_returns401() {
        ResponseEntity<String> response = restClient.get()
                .uri("/reproducciones")
                .headers(withJson())
                .retrieve()
                .toEntity(String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

}
