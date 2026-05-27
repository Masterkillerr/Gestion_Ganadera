package com.gestionganadera.backend.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EntityLifecycleTest {

    // --- Alerta @PrePersist tests ---

    @Test
    void alerta_onCreate_setsFechaAndDefaultsLeida() {
        Alerta alerta = new Alerta();
        assertNull(alerta.getFecha());
        assertFalse(alerta.getLeida()); // field default

        alerta.onCreate();

        assertNotNull(alerta.getFecha());
        assertTrue(alerta.getFecha().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertFalse(alerta.getLeida());
    }

    @Test
    void alerta_onCreate_doesNotOverrideExistingFecha() {
        LocalDateTime customFecha = LocalDateTime.of(2024, 1, 1, 10, 0);
        Alerta alerta = new Alerta();
        alerta.setFecha(customFecha);

        alerta.onCreate();

        assertEquals(customFecha, alerta.getFecha());
    }

    @Test
    void alerta_constructorAndSetters_work() {
        Alerta alerta = new Alerta();
        alerta.setId(1);
        alerta.setTipo("Salud");
        alerta.setMensaje("Revisión necesaria");
        alerta.setLeida(true);

        assertEquals(1, alerta.getId());
        assertEquals("Salud", alerta.getTipo());
        assertEquals("Revisión necesaria", alerta.getMensaje());
        assertTrue(alerta.getLeida());
    }

    @Test
    void alerta_allArgsConstructor_works() {
        Alerta alerta = new Alerta(1, "Salud", "Mensaje", null, LocalDateTime.now(), false);

        assertEquals(1, alerta.getId());
        assertEquals("Salud", alerta.getTipo());
        assertEquals("Mensaje", alerta.getMensaje());
        assertNotNull(alerta.getFecha());
    }

    // --- Evento @PrePersist tests ---

    @Test
    void evento_onCreate_setsFecha() {
        Evento evento = new Evento();
        assertNull(evento.getFecha());

        evento.onCreate();

        assertNotNull(evento.getFecha());
        assertTrue(evento.getFecha().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void evento_onCreate_doesNotOverrideExistingFecha() {
        LocalDateTime customFecha = LocalDateTime.of(2024, 6, 15, 8, 30);
        Evento evento = new Evento();
        evento.setFecha(customFecha);

        evento.onCreate();

        assertEquals(customFecha, evento.getFecha());
    }

    @Test
    void evento_constructorAndSetters_work() {
        Evento evento = new Evento();
        evento.setId(1);
        evento.setTipo("Reproduccion");
        evento.setDescripcion("Inseminación exitosa");

        assertEquals(1, evento.getId());
        assertEquals("Reproduccion", evento.getTipo());
        assertEquals("Inseminación exitosa", evento.getDescripcion());
    }

    @Test
    void evento_allArgsConstructor_works() {
        LocalDateTime now = LocalDateTime.now();
        Evento evento = new Evento(1, null, "Salud", "Revisión", now);

        assertEquals(1, evento.getId());
        assertEquals("Salud", evento.getTipo());
        assertEquals("Revisión", evento.getDescripcion());
        assertEquals(now, evento.getFecha());
    }

    // --- Animal @PrePersist tests ---

    @Test
    void animal_onCreate_setsCreadoEn() {
        Animal animal = new Animal();
        assertNull(animal.getCreadoEn());

        animal.onCreate();

        assertNotNull(animal.getCreadoEn());
        assertTrue(animal.getCreadoEn().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void animal_onCreate_doesNotOverrideExistingCreadoEn() {
        LocalDateTime customDate = LocalDateTime.of(2023, 12, 1, 0, 0);
        Animal animal = new Animal();
        animal.setCreadoEn(customDate);

        animal.onCreate();

        assertEquals(customDate, animal.getCreadoEn());
    }

    @Test
    void animal_constructorAndSetters_work() {
        Animal animal = new Animal();
        animal.setId(1);
        animal.setNombre("Vaca Lola");
        animal.setIdentificadorArete("AR-001");
        animal.setSexo("H");
        animal.setEstado("Activo");

        assertEquals(1, animal.getId());
        assertEquals("Vaca Lola", animal.getNombre());
        assertEquals("AR-001", animal.getIdentificadorArete());
        assertEquals("H", animal.getSexo());
        assertEquals("Activo", animal.getEstado());
    }

    @Test
    void animal_allArgsConstructor_works() {
        Animal animal = new Animal(
            1, "AR-001", "Vaca Lola", "H",
            null, null, null,
            LocalDate.of(2023, 1, 15),
            BigDecimal.valueOf(450), "Activo", null, null,
            null, null, null
        );

        assertEquals(1, animal.getId());
        assertEquals("AR-001", animal.getIdentificadorArete());
        assertEquals("Vaca Lola", animal.getNombre());
        assertEquals("H", animal.getSexo());
        assertEquals(LocalDate.of(2023, 1, 15), animal.getFechaNacimiento());
        assertEquals(BigDecimal.valueOf(450), animal.getPesoActual());
        assertEquals("Activo", animal.getEstado());
    }
}
