package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.MovimientoDTO;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Full-stack integration test for MovimientoController.
 * Tests the complete flow: HTTP → Controller → Service → Repository → DB
 * using RestClient with real JWT auth and H2 database.
 */
class MovimientoControllerIntegrationTest extends BaseIntegrationTest {

    @Test
    void findAll_whenEmpty_returnsEmptyList() {
        ResponseEntity<List<MovimientoDTO>> response = restClient.get()
                .uri("/movimientos")
                .headers(withAuth())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<MovimientoDTO> list = response.getBody();
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    @Test
    void findById_nonExistent_returns404() {
        ResponseEntity<String> response = restClient.get()
                .uri("/movimientos/99999")
                .headers(withAuth())
                .retrieve()
                .toEntity(String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getRecent_returnsRecentMovimientos() {
        // Setup
        setupMovimientoData();

        ResponseEntity<List<MovimientoDTO>> response = restClient.get()
                .uri("/movimientos/recent")
                .headers(withAuth())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<MovimientoDTO> recent = response.getBody();
        assertNotNull(recent);
        assertFalse(recent.isEmpty());
    }

    @Test
    void fullMovimientoLifecycle_shouldSucceed() {
        // ── Setup entities ──
        Integer fincaId = createEntity("/fincas", "{\"nombre\":\"Finca Mov\",\"ubicacion\":\"Campo\"}");
        // Create two lotes for the movement
        Integer loteOrigenId = createEntity("/lotes",
                "{\"nombre\":\"Lote Origen\",\"fincaId\":" + fincaId + ",\"hectareas\":30,\"capacidadMaxima\":20}");
        Integer loteDestinoId = createEntity("/lotes",
                "{\"nombre\":\"Lote Destino\",\"fincaId\":" + fincaId + ",\"hectareas\":40,\"capacidadMaxima\":25}");
        Integer razaId = createEntity("/razas", "{\"nombre\":\"Brahman\"}");
        Integer catId = createEntity("/categorias", "{\"nombre\":\"Adulto\",\"descripcion\":\"Adulto\"}");

        // Create animal in origen lote
        Integer animalId = createEntity("/animales", String.format(
                "{\"identificadorArete\":\"MV-001\",\"nombre\":\"Mov Animal\",\"sexo\":\"Macho\"," +
                "\"razaId\":%d,\"categoriaId\":%d,\"loteId\":%d,\"fincaId\":%d," +
                "\"fechaNacimiento\":\"2021-06-01\",\"pesoActual\":400.0,\"estado\":\"Activo\"}",
                razaId, catId, loteOrigenId, fincaId));

        // ── Create Movimiento ──
        String fechaMov = LocalDate.of(2025, 3, 15).toString();
        ResponseEntity<MovimientoDTO> createResponse = restClient.post()
                .uri("/movimientos")
                .headers(withAuth())
                .body(String.format(
                        "{\"animalId\":%d,\"loteOrigenId\":%d,\"loteDestinoId\":%d," +
                        "\"fecha\":\"%s\",\"tipoMovimiento\":\"Traslado\",\"motivo\":\"Cambio de lote por peso\"}",
                        animalId, loteOrigenId, loteDestinoId, fechaMov))
                .retrieve()
                .toEntity(MovimientoDTO.class);

        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        MovimientoDTO created = createResponse.getBody();
        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("Traslado", created.getTipoMovimiento());

        // ── Find by ID ──
        ResponseEntity<MovimientoDTO> byIdResponse = restClient.get()
                .uri("/movimientos/" + created.getId())
                .headers(withAuth())
                .retrieve()
                .toEntity(MovimientoDTO.class);

        assertEquals(HttpStatus.OK, byIdResponse.getStatusCode());
        MovimientoDTO found = byIdResponse.getBody();
        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
        assertNotNull(found.getOrigen());
        assertNotNull(found.getDestino());

        // ── Find all ──
        ResponseEntity<List<MovimientoDTO>> listResponse = restClient.get()
                .uri("/movimientos")
                .headers(withAuth())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.OK, listResponse.getStatusCode());
        List<MovimientoDTO> all = listResponse.getBody();
        assertNotNull(all);
        assertTrue(all.stream().anyMatch(m -> created.getId().equals(m.getId())));

        // ── Update Movimiento ──
        String newFecha = LocalDate.of(2025, 3, 20).toString();
        ResponseEntity<MovimientoDTO> updateResponse = restClient.put()
                .uri("/movimientos/" + created.getId())
                .headers(withAuth())
                .body(String.format(
                        "{\"animalId\":%d,\"loteOrigenId\":%d,\"loteDestinoId\":%d," +
                        "\"fecha\":\"%s\",\"tipoMovimiento\":\"Traslado\",\"motivo\":\"Cambio de lote actualizado\"}",
                        animalId, loteOrigenId, loteDestinoId, newFecha))
                .retrieve()
                .toEntity(MovimientoDTO.class);

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertNotNull(updateResponse.getBody());

        // ── Delete ──
        ResponseEntity<Void> deleteResponse = restClient.delete()
                .uri("/movimientos/" + created.getId())
                .headers(withAuth())
                .retrieve()
                .toEntity(Void.class);

        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        // ── Verify deletion ──
        ResponseEntity<String> afterDeleteResponse = restClient.get()
                .uri("/movimientos/" + created.getId())
                .headers(withAuth())
                .retrieve()
                .toEntity(String.class);

        assertEquals(HttpStatus.NOT_FOUND, afterDeleteResponse.getStatusCode());
    }

    @Test
    void withoutAuth_returns401() {
        ResponseEntity<String> response = restClient.get()
                .uri("/movimientos")
                .headers(withJson())
                .retrieve()
                .toEntity(String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    // ── Helpers ──

    private void setupMovimientoData() {
        Integer fincaId = createEntity("/fincas", "{\"nombre\":\"Finca Mov Recent\",\"ubicacion\":\"Campo\"}");
        Integer loteOrgId = createEntity("/lotes",
                "{\"nombre\":\"Lote Origen\",\"fincaId\":" + fincaId + ",\"hectareas\":30,\"capacidadMaxima\":20}");
        Integer loteDstId = createEntity("/lotes",
                "{\"nombre\":\"Lote Destino\",\"fincaId\":" + fincaId + ",\"hectareas\":40,\"capacidadMaxima\":25}");
        Integer razaId = createEntity("/razas", "{\"nombre\":\"Hereford\"}");
        Integer catId = createEntity("/categorias", "{\"nombre\":\"Adulto\",\"descripcion\":\"Adulto\"}");

        Integer animalId = createEntity("/animales", String.format(
                "{\"identificadorArete\":\"MR-001\",\"nombre\":\"Mov Recent\",\"sexo\":\"Macho\"," +
                "\"razaId\":%d,\"categoriaId\":%d,\"loteId\":%d,\"fincaId\":%d," +
                "\"fechaNacimiento\":\"2021-06-01\",\"pesoActual\":400.0,\"estado\":\"Activo\"}",
                razaId, catId, loteOrgId, fincaId));

        String fecha = LocalDate.now().toString();
        restClient.post()
                .uri("/movimientos")
                .headers(withAuth())
                .body(String.format(
                        "{\"animalId\":%d,\"loteOrigenId\":%d,\"loteDestinoId\":%d," +
                        "\"fecha\":\"%s\",\"tipoMovimiento\":\"Traslado\",\"motivo\":\"Test\"}",
                        animalId, loteOrgId, loteDstId, fecha))
                .retrieve()
                .toEntity(MovimientoDTO.class);
    }

}
