package com.gestionganadera.backend.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class SimpleEntitiesTest {

    // --- Role ---

    @Test
    void role_constructorAndSetters_work() {
        Role role = new Role();
        role.setId(1);
        role.setNombre("ADMIN");

        assertEquals(1, role.getId());
        assertEquals("ADMIN", role.getNombre());
    }

    @Test
    void role_allArgsConstructor_works() {
        Role role = new Role(2, "USER");
        assertEquals(2, role.getId());
        assertEquals("USER", role.getNombre());
    }

    // --- Raza ---

    @Test
    void raza_constructorAndSetters_work() {
        Raza raza = new Raza();
        raza.setId(1);
        raza.setNombre("Holstein");

        assertEquals(1, raza.getId());
        assertEquals("Holstein", raza.getNombre());
    }

    @Test
    void raza_allArgsConstructor_works() {
        Raza raza = new Raza(2, "Brahmán");
        assertEquals(2, raza.getId());
        assertEquals("Brahmán", raza.getNombre());
    }

    // --- Medicamento ---

    @Test
    void medicamento_constructorAndSetters_work() {
        Medicamento medicamento = new Medicamento();
        medicamento.setId(1);
        medicamento.setNombre("Ivermectina");
        medicamento.setDescripcion("Antiparasitario");

        assertEquals(1, medicamento.getId());
        assertEquals("Ivermectina", medicamento.getNombre());
        assertEquals("Antiparasitario", medicamento.getDescripcion());
    }

    // --- Vacuna ---

    @Test
    void vacuna_constructorAndSetters_work() {
        Vacuna vacuna = new Vacuna();
        vacuna.setId(1);
        vacuna.setNombre("Aftosa");

        assertEquals(1, vacuna.getId());
        assertEquals("Aftosa", vacuna.getNombre());
    }

    // --- Alimento ---

    @Test
    void alimento_constructorAndSetters_work() {
        Alimento alimento = new Alimento();
        alimento.setId(1);
        alimento.setNombre("Pasto");

        assertEquals(1, alimento.getId());
        assertEquals("Pasto", alimento.getNombre());
    }

    // --- Tratamiento ---

    @Test
    void tratamiento_constructorAndSetters_work() {
        Evento evento = new Evento();
        evento.setId(1);
        Medicamento medicamento = new Medicamento();
        medicamento.setId(1);

        Tratamiento tratamiento = new Tratamiento();
        tratamiento.setId(1);
        tratamiento.setEvento(evento);
        tratamiento.setMedicamento(medicamento);
        tratamiento.setDosisMl("10ml");
        tratamiento.setFechaInicio(LocalDate.of(2025, 1, 1));
        tratamiento.setFechaFin(LocalDate.of(2025, 1, 15));
        tratamiento.setObservacion("Aplicación única");

        assertEquals(1, tratamiento.getId());
        assertEquals(evento, tratamiento.getEvento());
        assertEquals(medicamento, tratamiento.getMedicamento());
        assertEquals("10ml", tratamiento.getDosisMl());
        assertEquals(LocalDate.of(2025, 1, 1), tratamiento.getFechaInicio());
        assertEquals(LocalDate.of(2025, 1, 15), tratamiento.getFechaFin());
        assertEquals("Aplicación única", tratamiento.getObservacion());
    }

    @Test
    void tratamiento_allArgsConstructor_works() {
        Evento evento = new Evento();
        evento.setId(1);
        Medicamento medicamento = new Medicamento();
        medicamento.setId(1);
        Tratamiento t = new Tratamiento(1, evento, medicamento,
                LocalDate.of(2025, 2, 1), LocalDate.of(2025, 2, 10), "5ml", "Test");

        assertEquals(1, t.getId());
        assertEquals("5ml", t.getDosisMl());
        assertEquals("Test", t.getObservacion());
    }

    // --- Vacunacion ---

    @Test
    void vacunacion_constructorAndSetters_work() {
        Evento evento = new Evento();
        evento.setId(1);
        Vacuna vacuna = new Vacuna(1, "Rabia");

        Vacunacion vacunacion = new Vacunacion();
        vacunacion.setId(1);
        vacunacion.setEvento(evento);
        vacunacion.setVacuna(vacuna);
        vacunacion.setProximaDosis(LocalDate.of(2025, 7, 10));
        vacunacion.setObservacion("Primera dosis");

        assertEquals(1, vacunacion.getId());
        assertEquals(evento, vacunacion.getEvento());
        assertEquals(vacuna, vacunacion.getVacuna());
        assertEquals(LocalDate.of(2025, 7, 10), vacunacion.getProximaDosis());
        assertEquals("Primera dosis", vacunacion.getObservacion());
    }

    // --- Alimentacion ---

    @Test
    void alimentacion_constructorAndSetters_work() {
        Animal animal = new Animal();
        animal.setId(1);
        Dieta dieta = new Dieta();
        dieta.setId(1);

        Alimentacion alimentacion = new Alimentacion();
        alimentacion.setId(1);
        alimentacion.setAnimal(animal);
        alimentacion.setDieta(dieta);
        alimentacion.setObservacion("Ración matutina");

        assertEquals(1, alimentacion.getId());
        assertEquals(animal, alimentacion.getAnimal());
        assertEquals(dieta, alimentacion.getDieta());
        assertEquals("Ración matutina", alimentacion.getObservacion());
    }

    // --- Produccion ---

    @Test
    void produccion_constructorAndSetters_work() {
        Animal animal = new Animal();
        animal.setId(1);
        TurnoProduccion turno = new TurnoProduccion(1, "Mañana");

        Produccion produccion = new Produccion();
        produccion.setId(1);
        produccion.setAnimal(animal);
        produccion.setTurnoProduccion(turno);
        produccion.setFecha(LocalDate.of(2025, 3, 1));

        assertEquals(1, produccion.getId());
        assertEquals(animal, produccion.getAnimal());
        assertEquals(turno, produccion.getTurnoProduccion());
        assertEquals(LocalDate.of(2025, 3, 1), produccion.getFecha());
    }
}
