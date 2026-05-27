package com.gestionganadera.backend.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

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

    // --- Categoria ---

    @Test
    void categoria_constructorAndSetters_work() {
        Categoria categoria = new Categoria();
        categoria.setId(1);
        categoria.setNombre("Vaca Lechera");
        categoria.setDescripcion("Alta producción de leche");

        assertEquals(1, categoria.getId());
        assertEquals("Vaca Lechera", categoria.getNombre());
        assertEquals("Alta producción de leche", categoria.getDescripcion());
    }

    @Test
    void categoria_allArgsConstructor_works() {
        Categoria categoria = new Categoria(2, "Toro Reproductor", null);
        assertEquals(2, categoria.getId());
        assertEquals("Toro Reproductor", categoria.getNombre());
        assertNull(categoria.getDescripcion());
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
        Animal animal = new Animal();
        animal.setId(1);
        Medicamento medicamento = new Medicamento();
        medicamento.setId(1);

        Tratamiento tratamiento = new Tratamiento();
        tratamiento.setId(1);
        tratamiento.setAnimal(animal);
        tratamiento.setMedicamento(medicamento);
        tratamiento.setDosis("10ml");
        tratamiento.setFechaInicio(LocalDate.of(2025, 1, 1));
        tratamiento.setFechaFin(LocalDate.of(2025, 1, 15));
        tratamiento.setDiasRetiro(30);
        tratamiento.setObservaciones("Aplicación única");

        assertEquals(1, tratamiento.getId());
        assertEquals(animal, tratamiento.getAnimal());
        assertEquals(medicamento, tratamiento.getMedicamento());
        assertEquals("10ml", tratamiento.getDosis());
        assertEquals(LocalDate.of(2025, 1, 1), tratamiento.getFechaInicio());
        assertEquals(LocalDate.of(2025, 1, 15), tratamiento.getFechaFin());
        assertEquals(30, tratamiento.getDiasRetiro());
        assertEquals("Aplicación única", tratamiento.getObservaciones());
    }

    @Test
    void tratamiento_allArgsConstructor_works() {
        Tratamiento t = new Tratamiento(1, null, null, "5ml",
                LocalDate.of(2025, 2, 1), LocalDate.of(2025, 2, 10), 15, "Test");

        assertEquals(1, t.getId());
        assertEquals("5ml", t.getDosis());
        assertEquals(15, t.getDiasRetiro());
    }

    // --- Vacunacion ---

    @Test
    void vacunacion_constructorAndSetters_work() {
        Animal animal = new Animal();
        animal.setId(1);
        Vacuna vacuna = new Vacuna(1, "Rabia");

        Vacunacion vacunacion = new Vacunacion();
        vacunacion.setId(1);
        vacunacion.setAnimal(animal);
        vacunacion.setVacuna(vacuna);
        vacunacion.setFecha(LocalDate.of(2025, 1, 10));
        vacunacion.setProximaDosis(LocalDate.of(2025, 7, 10));
        vacunacion.setObservaciones("Primera dosis");

        assertEquals(1, vacunacion.getId());
        assertEquals(animal, vacunacion.getAnimal());
        assertEquals(vacuna, vacunacion.getVacuna());
        assertEquals(LocalDate.of(2025, 1, 10), vacunacion.getFecha());
        assertEquals(LocalDate.of(2025, 7, 10), vacunacion.getProximaDosis());
        assertEquals("Primera dosis", vacunacion.getObservaciones());
    }

    // --- Alimentacion ---

    @Test
    void alimentacion_constructorAndSetters_work() {
        Animal animal = new Animal();
        animal.setId(1);
        Alimento alimento = new Alimento(1, "Concentrado");

        Alimentacion alimentacion = new Alimentacion();
        alimentacion.setId(1);
        alimentacion.setAnimal(animal);
        alimentacion.setAlimento(alimento);
        alimentacion.setCantidad(BigDecimal.valueOf(5.5));
        alimentacion.setFecha(LocalDate.of(2025, 2, 1));
        alimentacion.setObservaciones("Ración matutina");

        assertEquals(1, alimentacion.getId());
        assertEquals(animal, alimentacion.getAnimal());
        assertEquals(alimento, alimentacion.getAlimento());
        assertEquals(BigDecimal.valueOf(5.5), alimentacion.getCantidad());
        assertEquals(LocalDate.of(2025, 2, 1), alimentacion.getFecha());
        assertEquals("Ración matutina", alimentacion.getObservaciones());
    }

    // --- Produccion ---

    @Test
    void produccion_constructorAndSetters_work() {
        Animal animal = new Animal();
        animal.setId(1);

        Produccion produccion = new Produccion();
        produccion.setId(1);
        produccion.setAnimal(animal);
        produccion.setLitros(BigDecimal.valueOf(25.5));
        produccion.setTurno("Mañana");
        produccion.setFecha(LocalDate.of(2025, 3, 1));

        assertEquals(1, produccion.getId());
        assertEquals(animal, produccion.getAnimal());
        assertEquals(BigDecimal.valueOf(25.5), produccion.getLitros());
        assertEquals("Mañana", produccion.getTurno());
        assertEquals(LocalDate.of(2025, 3, 1), produccion.getFecha());
    }
}
