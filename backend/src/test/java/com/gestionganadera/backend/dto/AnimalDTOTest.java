package com.gestionganadera.backend.dto;

import com.gestionganadera.backend.model.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AnimalDTOTest {

    @Test
    void fromEntity_withAllFields_mapsCorrectly() {
        Raza raza = new Raza(1, "Holstein");
        Sexo sexo = new Sexo(1, "Hembra");
        EstadoAnimal estado = new EstadoAnimal(1, "Saludable");

        Animal madre = new Animal();
        madre.setId(10);
        Animal padre = new Animal();
        padre.setId(11);

        Animal animal = new Animal();
        animal.setId(1);
        animal.setIdentificadorArete("AR-001");
        animal.setNombre("Vaca Lola");
        animal.setSexo(sexo);
        animal.setEstadoAnimal(estado);
        animal.setRaza(raza);
        animal.setFechaNacimiento(LocalDate.of(2023, 1, 15));
        animal.setPesoActualKg(BigDecimal.valueOf(500));
        animal.setFotoUrl("foto.jpg");
        animal.setCreadoEn(LocalDateTime.now());
        animal.setMadre(madre);
        animal.setPadre(padre);

        AnimalDTO dto = AnimalDTO.fromEntity(animal);

        assertEquals(1, dto.getId());
        assertEquals("AR-001", dto.getIdentificadorArete());
        assertEquals("Vaca Lola", dto.getNombre());
        assertEquals("Hembra", dto.getSexo());
        assertEquals("Holstein", dto.getRazaNombre());
        assertEquals("Saludable", dto.getEstadoAnimal());
        assertEquals(BigDecimal.valueOf(500), dto.getPesoActualKg());
        assertEquals("foto.jpg", dto.getFotoUrl());
        assertEquals(10, dto.getMadreId());
        assertEquals(11, dto.getPadreId());
    }

    @Test
    void fromEntity_withNullRelationships_returnsNullFields() {
        Animal animal = new Animal();
        animal.setId(1);
        animal.setNombre("Simple");
        animal.setIdentificadorArete("AR-000");

        AnimalDTO dto = AnimalDTO.fromEntity(animal);

        assertEquals(1, dto.getId());
        assertEquals("Simple", dto.getNombre());
        assertNull(dto.getRazaNombre());
        assertNull(dto.getSexo());
        assertNull(dto.getEstadoAnimal());
        assertNull(dto.getFotoUrl());
        assertNull(dto.getMadreId());
        assertNull(dto.getPadreId());
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
            1, "AR-001", "Vaca", "Hembra",
            "Saludable", "Holstein",
            BigDecimal.valueOf(500), "foto.jpg",
            10, 11, "2023-01-15"
        );

        assertEquals(1, dto.getId());
        assertEquals("Vaca", dto.getNombre());
        assertEquals("Holstein", dto.getRazaNombre());
        assertEquals("Hembra", dto.getSexo());
        assertEquals(10, dto.getMadreId());
    }
}
