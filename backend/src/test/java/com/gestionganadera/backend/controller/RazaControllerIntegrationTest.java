package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.model.Raza;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Full-stack integration test for RazaController.
 * Tests the complete flow: HTTP → Controller → Service → Repository → DB
 * using RestClient with real JWT auth and H2 database.
 */
class RazaControllerIntegrationTest extends BaseIntegrationTest {

    @Test
    void createRaza_ShouldReturnCreatedRaza() {
        // Act
        ResponseEntity<Raza> response = restClient.post()
                .uri("/razas")
                .headers(withAuth())
                .body("{\"nombre\":\"Holstein\"}")
                .retrieve()
                .toEntity(Raza.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Raza raza = response.getBody();
        assertNotNull(raza);
        assertNotNull(raza.getId());
        assertEquals("Holstein", raza.getNombre());
    }

    @Test
    void createRaza_WithoutAuth_ShouldReturn401() {
        // Act
        ResponseEntity<String> response = restClient.post()
                .uri("/razas")
                .headers(withJson())
                .body("{\"nombre\":\"Holstein\"}")
                .retrieve()
                .toEntity(String.class);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void getAllRazas_WhenEmpty_ShouldReturnEmptyList() {
        // Act
        ResponseEntity<List<Raza>> response = restClient.get()
                .uri("/razas")
                .headers(withAuth())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Raza> razas = response.getBody();
        assertNotNull(razas);
        assertTrue(razas.isEmpty());
    }

    @Test
    void getAllRazas_AfterCreatingMultiple_ShouldReturnAll() {
        // Arrange
        createRaza("Holstein");
        createRaza("Angus");
        createRaza("Brahman");

        // Act
        ResponseEntity<List<Raza>> response = restClient.get()
                .uri("/razas")
                .headers(withAuth())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Raza> razas = response.getBody();
        assertNotNull(razas);
        assertEquals(3, razas.size());
        assertTrue(razas.stream().anyMatch(r -> "Holstein".equals(r.getNombre())));
        assertTrue(razas.stream().anyMatch(r -> "Angus".equals(r.getNombre())));
        assertTrue(razas.stream().anyMatch(r -> "Brahman".equals(r.getNombre())));
    }

    @Test
    void createRaza_WithBlankNombre_ShouldReturn400() {
        // Act
        ResponseEntity<String> response = restClient.post()
                .uri("/razas")
                .headers(withAuth())
                .body("{\"nombre\":\"\"}")
                .retrieve()
                .toEntity(String.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getAllRazas_WithoutAuth_ShouldReturn401() {
        // Act
        ResponseEntity<String> response = restClient.get()
                .uri("/razas")
                .headers(withJson())
                .retrieve()
                .toEntity(String.class);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    /**
     * Full lifecycle test: Create → List → Verify response structure matches DTO.
     */
    @Test
    void razaFullLifecycle_ShouldBeConsistent() {
        // 1. Create
        Raza created = createRaza("Hereford");
        assertNotNull(created.getId());
        assertEquals("Hereford", created.getNombre());

        // 2. List
        ResponseEntity<List<Raza>> listResponse = restClient.get()
                .uri("/razas")
                .headers(withAuth())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});
        List<Raza> all = listResponse.getBody();
        assertNotNull(all);

        // 3. Verify the created raza is in the list with consistent data
        Raza found = all.stream()
                .filter(r -> created.getId().equals(r.getId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Created raza not found in list"));

        assertEquals(created.getId(), found.getId());
        assertEquals("Hereford", found.getNombre());
    }

    // ── Helper ──

    private Raza createRaza(String nombre) {
        ResponseEntity<Raza> response = restClient.post()
                .uri("/razas")
                .headers(withAuth())
                .body("{\"nombre\":\"" + nombre + "\"}")
                .retrieve()
                .toEntity(Raza.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        return response.getBody();
    }
}
