package com.gestionganadera.backend.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EntityLifecycleTest {

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
        evento.setDescripcion("Inseminación exitosa");

        assertEquals(1, evento.getId());
        assertEquals("Inseminación exitosa", evento.getDescripcion());
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
        Sexo sexo = new Sexo(1, "Hembra");
        EstadoAnimal estado = new EstadoAnimal(1, "Saludable");

        Animal animal = new Animal();
        animal.setId(1);
        animal.setNombre("Vaca Lola");
        animal.setIdentificadorArete("AR-001");
        animal.setSexo(sexo);
        animal.setEstadoAnimal(estado);

        assertEquals(1, animal.getId());
        assertEquals("Vaca Lola", animal.getNombre());
        assertEquals("AR-001", animal.getIdentificadorArete());
        assertEquals("Hembra", animal.getSexo().getNombre());
        assertEquals("Saludable", animal.getEstadoAnimal().getNombre());
    }

    @Test
    void animal_creadoEn_autoSetOnCreate() {
        Animal animal = new Animal();
        animal.onCreate();

        assertNotNull(animal.getCreadoEn());
    }

    // --- Alimentacion @PrePersist tests ---

    @Test
    void alimentacion_onCreate_setsFecha() {
        Alimentacion a = new Alimentacion();
        assertNull(a.getFecha());

        a.onCreate();

        assertNotNull(a.getFecha());
    }

    @Test
    void alimentacion_onCreate_doesNotOverrideExistingFecha() {
        LocalDateTime custom = LocalDateTime.of(2024, 1, 1, 10, 0);
        Alimentacion a = new Alimentacion();
        a.setFecha(custom);

        a.onCreate();

        assertEquals(custom, a.getFecha());
    }

    // --- Usuario @PrePersist tests ---

    @Test
    void usuario_onCreate_setsCreadoEn() {
        Usuario usuario = new Usuario();
        assertNull(usuario.getCreadoEn());

        usuario.onCreate();

        assertNotNull(usuario.getCreadoEn());
    }
}
