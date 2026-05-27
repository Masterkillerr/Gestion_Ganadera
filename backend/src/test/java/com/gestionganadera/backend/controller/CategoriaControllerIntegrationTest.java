package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.model.Categoria;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Full-stack integration test for CategoriaController.
 * Uses RestClient for Spring Boot 4.x compatibility.
 */
class CategoriaControllerIntegrationTest extends BaseIntegrationTest {

    @Test
    void createCategoria_ShouldReturnCreatedCategoria() {
        // Arrange
        String requestBody = "{\"nombre\":\"Vaquillona\",\"descripcion\":\"Joven de 1-2 años\"}";

        // Act
        ResponseEntity<Categoria> response = restClient.post()
                .uri("/categorias")
                .headers(withAuth())
                .body(requestBody)
                .retrieve()
                .toEntity(Categoria.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Categoria categoria = response.getBody();
        assertNotNull(categoria);
        assertNotNull(categoria.getId());
        assertEquals("Vaquillona", categoria.getNombre());
        assertEquals("Joven de 1-2 años", categoria.getDescripcion());
    }

    @Test
    void createCategoria_WithOnlyNombre_ShouldSucceed() {
        // Act — descripcion is optional per the DTO
        ResponseEntity<Categoria> response = restClient.post()
                .uri("/categorias")
                .headers(withAuth())
                .body("{\"nombre\":\"Toro\"}")
                .retrieve()
                .toEntity(Categoria.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Categoria categoria = response.getBody();
        assertNotNull(categoria);
        assertEquals("Toro", categoria.getNombre());
        assertNull(categoria.getDescripcion());
    }

    @Test
    void getAllCategorias_WhenEmpty_ShouldReturnEmptyList() {
        // Act
        ResponseEntity<List<Categoria>> response = restClient.get()
                .uri("/categorias")
                .headers(withAuth())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Categoria> categorias = response.getBody();
        assertNotNull(categorias);
        assertTrue(categorias.isEmpty());
    }

    @Test
    void getAllCategorias_AfterCreatingMultiple_ShouldReturnAll() {
        // Arrange
        createCategoria("Vaquillona", "Joven hembra 1-2 años");
        createCategoria("Toro", "Macho adulto");
        createCategoria("Novillo", "Joven macho 1-2 años");

        // Act
        ResponseEntity<List<Categoria>> response = restClient.get()
                .uri("/categorias")
                .headers(withAuth())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Categoria> categorias = response.getBody();
        assertNotNull(categorias);
        assertEquals(3, categorias.size());
    }

    @Test
    void createCategoria_WithBlankNombre_ShouldReturn400() {
        // Act
        ResponseEntity<String> response = restClient.post()
                .uri("/categorias")
                .headers(withAuth())
                .body("{\"nombre\":\"\"}")
                .retrieve()
                .toEntity(String.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getAllCategorias_WithoutAuth_ShouldReturn401() {
        // Act
        ResponseEntity<String> response = restClient.get()
                .uri("/categorias")
                .headers(withJson())
                .retrieve()
                .toEntity(String.class);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    /**
     * Full lifecycle test — validates the complete Create → List → Verify flow
     * that mirrors what the frontend does when loading catalog data.
     */
    @Test
    void categoriaFullLifecycle_ShouldBeConsistent() {
        // 1. Create
        Categoria created = createCategoria("Ternero", "0-12 meses");
        assertNotNull(created.getId());
        assertEquals("Ternero", created.getNombre());
        assertEquals("0-12 meses", created.getDescripcion());

        // 2. List all
        ResponseEntity<List<Categoria>> listResponse = restClient.get()
                .uri("/categorias")
                .headers(withAuth())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});
        List<Categoria> all = listResponse.getBody();
        assertNotNull(all);

        // 3. Verify created entity is in the list with correct data
        Categoria found = all.stream()
                .filter(c -> created.getId().equals(c.getId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Created categoria not found in list"));
        assertEquals("Ternero", found.getNombre());
        assertEquals("0-12 meses", found.getDescripcion());
    }

    // ── Helper ──

    private Categoria createCategoria(String nombre, String descripcion) {
        ResponseEntity<Categoria> response = restClient.post()
                .uri("/categorias")
                .headers(withAuth())
                .body("{\"nombre\":\"" + nombre + "\",\"descripcion\":\"" + descripcion + "\"}")
                .retrieve()
                .toEntity(Categoria.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        return response.getBody();
    }
}
