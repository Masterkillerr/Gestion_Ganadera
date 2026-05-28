package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.model.Alimentacion;
import com.gestionganadera.backend.repository.AlimentoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AlimentacionControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private AlimentoRepository alimentoRepository;

    @Test
    void findByAnimalId_nonExistent_returns404() {
        ResponseEntity<String> response = restClient.get()
                .uri("/alimentaciones/animal/99999")
                .headers(withAuth())
                .retrieve()
                .toEntity(String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void fullAlimentacionLifecycle_shouldSucceed() {
        // ── Setup: create prerequisite entities ──
        Integer fincaId = createEntity("/fincas", "{\"nombre\":\"Finca Ali\",\"ubicacion\":\"Campo\"}");
        Integer loteId = createEntity("/lotes",
                "{\"nombre\":\"Lote Ali\",\"fincaId\":" + fincaId + ",\"hectareas\":50,\"capacidadMaxima\":30}");
        Integer razaId = createEntity("/razas", "{\"nombre\":\"Brahman\"}");
        Integer catId = createEntity("/categorias", "{\"nombre\":\"Adulto\",\"descripcion\":\"Adulto\"}");

        // Create an animal
        Integer animalId = createEntity("/animales", String.format(
                "{\"identificadorArete\":\"AL-001\",\"nombre\":\"Animal Alim\",\"sexo\":\"Macho\"," +
                "\"razaId\":%d,\"categoriaId\":%d,\"loteId\":%d,\"fincaId\":%d," +
                "\"fechaNacimiento\":\"2022-03-10\",\"pesoActual\":500.0,\"estado\":\"Activo\"}",
                razaId, catId, loteId, fincaId));

        // Create an Alimento reference via repository
        jdbcTemplate.execute("INSERT INTO alimentos (nombre) VALUES ('Pasto')");

        // ── Create Alimentacion ──
        String fecha = LocalDate.now().toString();
        ResponseEntity<Alimentacion> createResponse = restClient.post()
                .uri("/alimentaciones")
                .headers(withAuth())
                .body(String.format(
                        "{\"animalId\":%d,\"alimentoId\":1,\"cantidad\":10.5,\"fecha\":\"%s\",\"observaciones\":\"Suplemento diario\"}",
                        animalId, fecha))
                .retrieve()
                .toEntity(Alimentacion.class);

        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        Alimentacion created = createResponse.getBody();
        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals(10.5, created.getCantidad().doubleValue(), 0.01);
        assertNotNull(created.getAnimal());
        assertNotNull(created.getAlimento());

        // ── Find by animal ID ──
        ResponseEntity<List<Alimentacion>> byAnimalResponse = restClient.get()
                .uri("/alimentaciones/animal/" + animalId)
                .headers(withAuth())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.OK, byAnimalResponse.getStatusCode());
        List<Alimentacion> byAnimal = byAnimalResponse.getBody();
        assertNotNull(byAnimal);
        assertFalse(byAnimal.isEmpty());
        assertTrue(byAnimal.stream().anyMatch(a -> created.getId().equals(a.getId())));

        // ── Delete ──
        ResponseEntity<Void> deleteResponse = restClient.delete()
                .uri("/alimentaciones/" + created.getId())
                .headers(withAuth())
                .retrieve()
                .toEntity(Void.class);

        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        // ── Verify deletion ──
        ResponseEntity<List<Alimentacion>> afterDeleteResponse = restClient.get()
                .uri("/alimentaciones/animal/" + animalId)
                .headers(withAuth())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.OK, afterDeleteResponse.getStatusCode());
        List<Alimentacion> remaining = afterDeleteResponse.getBody();
        assertNotNull(remaining);
        assertTrue(remaining.stream().noneMatch(a -> created.getId().equals(a.getId())));
    }

    @Test
    void withoutAuth_returns401() {
        ResponseEntity<String> response = restClient.get()
                .uri("/alimentaciones/animal/1")
                .headers(withJson())
                .retrieve()
                .toEntity(String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

}
