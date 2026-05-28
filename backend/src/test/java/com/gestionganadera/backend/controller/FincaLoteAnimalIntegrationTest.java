package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.AnimalDTO;
import com.gestionganadera.backend.dto.FincaStatsDTO;
import com.gestionganadera.backend.model.Finca;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Full-stack integration test for the most critical entity flow:
 * Finca → Lote → Animal.
 *
 * These tests simulate what the frontend does:
 * 1. Create a Finca
 * 2. Create Lotes under that Finca
 * 3. Create Animals referencing the Finca, Lote, Raza, and Categoria
 * 4. Query animals by lote and by finca
 * 5. Update and delete animals
 * 6. Test error cases
 */
class FincaLoteAnimalIntegrationTest extends BaseIntegrationTest {

    @Test
    void createFinca_ShouldReturnCreatedFinca() {
        // Act
        ResponseEntity<Finca> response = restClient.post()
                .uri("/fincas")
                .headers(withAuth())
                .body("{\"nombre\":\"Finca Test\",\"ubicacion\":\"Campo 1\"}")
                .retrieve()
                .toEntity(Finca.class);

        // Assert — response structure matches what frontend expects
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Finca finca = response.getBody();
        assertNotNull(finca);
        assertNotNull(finca.getId(), "Finca should have an auto-generated ID");
        assertEquals("Finca Test", finca.getNombre());
        assertEquals("Campo 1", finca.getUbicacion());
        // Propietario should be the authenticated user
        assertNotNull(finca.getPropietario());
        assertEquals(TEST_USER_EMAIL, finca.getPropietario().getEmail());
    }

    @Test
    void createFinca_WithBlankNombre_ShouldReturn400() {
        // Act
        ResponseEntity<String> response = restClient.post()
                .uri("/fincas")
                .headers(withAuth())
                .body("{\"nombre\":\"\",\"ubicacion\":\"Campo 1\"}")
                .retrieve()
                .toEntity(String.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void fullFlow_CreateFincaThenLoteThenAnimal_ShouldSucceed() {
        // ── Step 1: Create a Raza ──
        Integer razaId = createEntity("/razas", "{\"nombre\":\"Holstein\"}");

        // ── Step 2: Create a Categoria ──
        Integer categoriaId = createEntity("/categorias", "{\"nombre\":\"Vaquillona\",\"descripcion\":\"Joven 1-2 años\"}");

        // ── Step 3: Create a Finca ──
        Integer fincaId = createEntity("/fincas", "{\"nombre\":\"Finca Norte\",\"ubicacion\":\"Campo Grande\"}");

        // ── Step 4: Create a Lote under the Finca ──
        Integer loteId = createEntity("/lotes",
                "{\"nombre\":\"Lote A\",\"fincaId\":" + fincaId + ",\"hectareas\":100,\"capacidadMaxima\":50}");

        // ── Step 5: Create an Animal referencing the Raza, Lote, and Finca ──
        String animalJson = String.format(
                "{\"identificadorArete\":\"AR-001\",\"nombre\":\"Vaca 1\",\"sexo\":\"Hembra\"," +
                "\"razaId\":%d,\"categoriaId\":%d,\"loteId\":%d,\"fincaId\":%d," +
                "\"fechaNacimiento\":\"2024-06-15\",\"pesoActual\":450.5,\"estado\":\"Activo\"}",
                razaId, categoriaId, loteId, fincaId);

        ResponseEntity<AnimalDTO> createResponse = restClient.post()
                .uri("/animales")
                .headers(withAuth())
                .body(animalJson)
                .retrieve()
                .toEntity(AnimalDTO.class);
        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        AnimalDTO created = createResponse.getBody();
        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("AR-001", created.getIdentificadorArete());
        assertEquals("Vaca 1", created.getNombre());
        assertEquals("Hembra", created.getSexo());
        assertEquals("Holstein", created.getRazaNombre());
        assertEquals("Vaquillona", created.getCategoriaNombre());
        assertEquals("Lote A", created.getLoteNombre());
        assertEquals("Finca Norte", created.getFincaNombre());
        assertNotNull(created.getLoteId());
        assertEquals(450.5, created.getPesoActual().doubleValue(), 0.01);

        // ── Step 6: Get all animals ──
        ResponseEntity<List<AnimalDTO>> listResponse = restClient.get()
                .uri("/animales")
                .headers(withAuth())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});
        assertEquals(HttpStatus.OK, listResponse.getStatusCode());
        List<AnimalDTO> allAnimals = listResponse.getBody();
        assertNotNull(allAnimals);
        assertEquals(1, allAnimals.size());

        // ── Step 7: Get animal by ID ──
        ResponseEntity<AnimalDTO> getByIdResponse = restClient.get()
                .uri("/animales/" + created.getId())
                .headers(withAuth())
                .retrieve()
                .toEntity(AnimalDTO.class);
        assertEquals(HttpStatus.OK, getByIdResponse.getStatusCode());
        AnimalDTO fetched = getByIdResponse.getBody();
        assertNotNull(fetched);
        assertEquals(created.getId(), fetched.getId());
        assertEquals("Vaca 1", fetched.getNombre());

        // ── Step 8: Get animals by lote ──
        ResponseEntity<List<AnimalDTO>> byLoteResponse = restClient.get()
                .uri("/animales/lote/" + loteId)
                .headers(withAuth())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});
        assertEquals(HttpStatus.OK, byLoteResponse.getStatusCode());
        assertEquals(1, byLoteResponse.getBody().size());

        // ── Step 9: Get animals by finca ──
        ResponseEntity<List<AnimalDTO>> byFincaResponse = restClient.get()
                .uri("/animales/finca/" + fincaId)
                .headers(withAuth())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});
        assertEquals(HttpStatus.OK, byFincaResponse.getStatusCode());
        assertEquals(1, byFincaResponse.getBody().size());

        // ── Step 10: Update the animal ──
        String updateJson = String.format(
                "{\"identificadorArete\":\"AR-001\",\"nombre\":\"Vaca 1 Actualizada\",\"sexo\":\"Hembra\"," +
                "\"razaId\":%d,\"categoriaId\":%d,\"loteId\":%d,\"fincaId\":%d," +
                "\"fechaNacimiento\":\"2024-06-15\",\"pesoActual\":500.0,\"estado\":\"Activo\"}",
                razaId, categoriaId, loteId, fincaId);

        ResponseEntity<AnimalDTO> updateResponse = restClient.put()
                .uri("/animales/" + created.getId())
                .headers(withAuth())
                .body(updateJson)
                .retrieve()
                .toEntity(AnimalDTO.class);
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        AnimalDTO updated = updateResponse.getBody();
        assertNotNull(updated);
        assertEquals("Vaca 1 Actualizada", updated.getNombre());
        assertEquals(500.0, updated.getPesoActual().doubleValue(), 0.01);

        // ── Step 11: Delete the animal ──
        ResponseEntity<Void> deleteResponse = restClient.delete()
                .uri("/animales/" + created.getId())
                .headers(withAuth())
                .retrieve()
                .toEntity(Void.class);
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        // ── Step 12: Verify deletion ──
        ResponseEntity<AnimalDTO> afterDeleteResponse = restClient.get()
                .uri("/animales/" + created.getId())
                .headers(withAuth())
                .retrieve()
                .toEntity(AnimalDTO.class);
        assertEquals(HttpStatus.NOT_FOUND, afterDeleteResponse.getStatusCode());
    }

    @Test
    void getFincaStats_ShouldReturnCorrectCounts() {
        // Arrange — set up data with known counts
        Integer fincaId = createEntity("/fincas", "{\"nombre\":\"Finca Stats Test\",\"ubicacion\":\"Campo\"}");
        Integer loteId = createEntity("/lotes",
                "{\"nombre\":\"Lote Stats\",\"fincaId\":" + fincaId + "}");
        Integer razaId = createEntity("/razas", "{\"nombre\":\"Angus\"}");
        Integer catId = createEntity("/categorias", "{\"nombre\":\"Adulto\",\"descripcion\":\"Adulto\"}");

        // Create 2 male (Macho) and 1 female (Hembra) animal
        createAnimal(razaId, catId, loteId, fincaId, "Macho", "AR-M1");
        createAnimal(razaId, catId, loteId, fincaId, "Macho", "AR-M2");
        createAnimal(razaId, catId, loteId, fincaId, "Hembra", "AR-H1");

        // Act
        ResponseEntity<FincaStatsDTO> statsResponse = restClient.get()
                .uri("/fincas/" + fincaId + "/stats")
                .headers(withAuth())
                .retrieve()
                .toEntity(FincaStatsDTO.class);

        // Assert
        assertEquals(HttpStatus.OK, statsResponse.getStatusCode());
        FincaStatsDTO stats = statsResponse.getBody();
        assertNotNull(stats);
        assertEquals(3, stats.getTotalAnimales());
        assertEquals(2, stats.getMachos());
        assertEquals(1, stats.getHembras());
        assertEquals(1, stats.getTotalLotes());
    }

    @Test
    void getNonExistentAnimal_ShouldReturn404() {
        // Act
        ResponseEntity<AnimalDTO> response = restClient.get()
                .uri("/animales/99999")
                .headers(withAuth())
                .retrieve()
                .toEntity(AnimalDTO.class);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void createAnimal_WithNonExistentLote_ShouldReturn500() {
        // Arrange — first create a finca and raza to reference
        Integer razaId = createEntity("/razas", "{\"nombre\":\"Hereford\"}");
        Integer fincaId = createEntity("/fincas", "{\"nombre\":\"Finca Test\",\"ubicacion\":\"Campo\"}");

        // Act — reference a non-existent lote
        ResponseEntity<String> response = restClient.post()
                .uri("/animales")
                .headers(withAuth())
                .body(String.format(
                        "{\"nombre\":\"Test\",\"sexo\":\"Macho\",\"razaId\":%d,\"loteId\":99999,\"fincaId\":%d,\"fechaNacimiento\":\"2024-01-01\"}",
                        razaId, fincaId))
                .retrieve()
                .toEntity(String.class);

        // Assert — service throws RuntimeException("Lote no encontrado o no autorizado")
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void deleteAnimal_NonExistent_ShouldReturn500() {
        // Act
        ResponseEntity<String> response = restClient.delete()
                .uri("/animales/99999")
                .headers(withAuth())
                .retrieve()
                .toEntity(String.class);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    // ── Helpers ──

    private void createAnimal(Integer razaId, Integer categoriaId, Integer loteId,
                              Integer fincaId, String sexo, String arete) {
        String json = String.format(
                "{\"identificadorArete\":\"%s\",\"nombre\":\"Animal %s\",\"sexo\":\"%s\"," +
                "\"razaId\":%d,\"categoriaId\":%d,\"loteId\":%d,\"fincaId\":%d," +
                "\"fechaNacimiento\":\"2024-01-01\",\"pesoActual\":300.0,\"estado\":\"Activo\"}",
                arete, arete, sexo, razaId, categoriaId, loteId, fincaId);

        ResponseEntity<AnimalDTO> response = restClient.post()
                .uri("/animales")
                .headers(withAuth())
                .body(json)
                .retrieve()
                .toEntity(AnimalDTO.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
