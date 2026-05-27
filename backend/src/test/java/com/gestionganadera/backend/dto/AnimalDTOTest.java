package com.gestionganadera.backend.dto;

import com.gestionganadera.backend.model.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AnimalDTOTest {

    @Test
    void fromEntity_withAllFields_mapsCorrectly() {
        Raza raza = new Raza(1, "Holstein");
        Categoria categoria = new Categoria(1, "Vaca Lechera", "Alta producción");
        Lote lote = new Lote(1, "Lote A", null, null, null, null, null);
        Usuario propietario = new Usuario(UUID.randomUUID(), "Dueño", "dueno@test.com", "pass", new Role(1, "USER"), LocalDateTime.now());
        Finca finca = new Finca(1, "Finca Test", "Ubicación", propietario);

        Animal madre = new Animal();
        madre.setId(10);
        Animal padre = new Animal();
        padre.setId(11);

        Animal animal = new Animal(
            1, "AR-001", "Vaca Lola", "H",
            raza, categoria, lote,
            LocalDate.of(2023, 1, 15),
            BigDecimal.valueOf(500), "Activo", "foto.jpg",
            LocalDateTime.now(), madre, padre, finca
        );

        AnimalDTO dto = AnimalDTO.fromEntity(animal);

        assertEquals(1, dto.getId());
        assertEquals("AR-001", dto.getIdentificadorArete());
        assertEquals("Vaca Lola", dto.getNombre());
        assertEquals("H", dto.getSexo());
        assertEquals("Holstein", dto.getRazaNombre());
        assertEquals("Vaca Lechera", dto.getCategoriaNombre());
        assertEquals("Activo", dto.getEstado());
        assertEquals(BigDecimal.valueOf(500), dto.getPesoActual());
        assertEquals("Lote A", dto.getLoteNombre());
        assertEquals(1, dto.getLoteId());
        assertEquals("foto.jpg", dto.getFotoUrl());
        assertEquals(10, dto.getMadreId());
        assertEquals(11, dto.getPadreId());
        assertEquals("Finca Test", dto.getFincaNombre());
    }

    @Test
    void fromEntity_withNullRelationships_returnsNullFields() {
        Animal animal = new Animal();
        animal.setId(1);
        animal.setNombre("Simple");
        animal.setIdentificadorArete("AR-000");
        animal.setSexo("M");
        animal.setEstado("Activo");

        AnimalDTO dto = AnimalDTO.fromEntity(animal);

        assertEquals(1, dto.getId());
        assertEquals("Simple", dto.getNombre());
        assertNull(dto.getRazaNombre());
        assertNull(dto.getCategoriaNombre());
        assertNull(dto.getLoteNombre());
        assertNull(dto.getLoteId());
        assertNull(dto.getFotoUrl());
        assertNull(dto.getMadreId());
        assertNull(dto.getPadreId());
        assertNull(dto.getFincaNombre());
    }

    @Test
    void constructorAndSetters_work() {
        AnimalDTO dto = new AnimalDTO();
        dto.setId(1);
        dto.setNombre("Vaca");
        dto.setRazaNombre("Holstein");
        dto.setMadreId(10);

        assertEquals(1, dto.getId());
        assertEquals("Vaca", dto.getNombre());
        assertEquals("Holstein", dto.getRazaNombre());
        assertEquals(10, dto.getMadreId());
    }

    @Test
    void allArgsConstructor_works() {
        AnimalDTO dto = new AnimalDTO(
            1, "AR-001", "Vaca", "H",
            "Holstein", "Lechera", "Activo",
            BigDecimal.valueOf(500), "Lote A", null, null, null, "Finca X", null
        );

        assertEquals(1, dto.getId());
        assertEquals("Vaca", dto.getNombre());
        assertEquals("Holstein", dto.getRazaNombre());
        assertEquals("Activo", dto.getEstado());
        assertEquals("Finca X", dto.getFincaNombre());
    }
}
