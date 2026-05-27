package com.gestionganadera.backend.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EntityRelationshipsTest {

    private Usuario propietario;
    private Finca finca;
    private Lote lote;
    private Animal vaca;
    private Animal toro;
    private Animal cria;
    private Raza raza;
    private Categoria categoria;

    @BeforeEach
    void setUp() {
        propietario = new Usuario(UUID.randomUUID(), "Dueño", "dueno@finca.com", "pass", new Role(1, "USER"), LocalDateTime.now());
        finca = new Finca(1, "Finca El Porvenir", "Campo Hermoso", propietario);
        lote = new Lote(1, "Lote A", finca, BigDecimal.valueOf(50), 100, "Pasto estrella", "Activo");
        raza = new Raza(1, "Holstein");
        categoria = new Categoria(1, "Vaca Lechera", "Alta producción");
        vaca = new Animal(10, "AR-010", "Vaca Lechera", "H", raza, categoria, lote, LocalDate.of(2020, 1, 1), BigDecimal.valueOf(500), "Activo", null, null, null, null, finca);
        toro = new Animal(11, "AR-011", "Toro Bravo", "M", null, null, lote, LocalDate.of(2019, 5, 1), BigDecimal.valueOf(800), "Activo", null, null, null, null, finca);
        cria = new Animal(12, "AR-012", "Ternero", "M", null, null, lote, LocalDate.of(2024, 3, 1), BigDecimal.valueOf(80), "Activo", null, null, vaca, toro, finca);
    }

    // --- Finca + Propietario relationship ---

    @Test
    void finca_withPropietario_setsRelationship() {
        assertEquals(propietario, finca.getPropietario());
        assertEquals("Finca El Porvenir", finca.getNombre());
        assertEquals("Campo Hermoso", finca.getUbicacion());
    }

    @Test
    void finca_noArgsConstructor_thenSetters() {
        Finca f = new Finca();
        f.setId(2);
        f.setNombre("Nueva Finca");
        f.setPropietario(propietario);

        assertEquals(2, f.getId());
        assertEquals("Nueva Finca", f.getNombre());
        assertEquals(propietario, f.getPropietario());
    }

    // --- Lote + Finca relationship ---

    @Test
    void lote_withFinca_setsRelationship() {
        assertEquals(finca, lote.getFinca());
        assertEquals("Lote A", lote.getNombre());
        assertEquals(Integer.valueOf(100), lote.getCapacidadMaxima());
        assertEquals("Activo", lote.getEstado());
    }

    @Test
    void lote_noArgsConstructor_thenSetters() {
        Lote l = new Lote();
        l.setId(2);
        l.setNombre("Lote B");
        l.setFinca(finca);
        l.setEstado("Mantenimiento");

        assertEquals(2, l.getId());
        assertEquals("Lote B", l.getNombre());
        assertEquals(finca, l.getFinca());
        assertEquals("Mantenimiento", l.getEstado());
    }

    // --- Animal relationships (Raza, Categoria, Lote, Finca, Madre, Padre) ---

    @Test
    void animal_withAllRelationships_setsCorrectly() {
        assertEquals(raza, vaca.getRaza());
        assertEquals(categoria, vaca.getCategoria());
        assertEquals(lote, vaca.getLote());
        assertEquals(finca, vaca.getFinca());
        assertEquals("Holstein", vaca.getRaza().getNombre());
        assertEquals("Vaca Lechera", vaca.getCategoria().getNombre());
    }

    @Test
    void animal_withParents_linksMadreAndPadre() {
        assertEquals(vaca, cria.getMadre());
        assertEquals(toro, cria.getPadre());
    }

    @Test
    void animal_withoutOptionalRelationships_returnsNull() {
        Animal simple = new Animal();
        simple.setId(99);
        simple.setNombre("Simple");

        assertNull(simple.getRaza());
        assertNull(simple.getCategoria());
        assertNull(simple.getLote());
        assertNull(simple.getFinca());
        assertNull(simple.getMadre());
        assertNull(simple.getPadre());
    }

    // --- Reproduccion (vaca + toro) ---

    @Test
    void reproduccion_withParents_setsRelationships() {
        Reproduccion r = new Reproduccion();
        r.setId(1);
        r.setVaca(vaca);
        r.setToro(toro);
        r.setFechaMonta(LocalDate.of(2024, 6, 1));
        r.setTipo("Natural");

        assertEquals(vaca, r.getVaca());
        assertEquals(toro, r.getToro());
        assertEquals(LocalDate.of(2024, 6, 1), r.getFechaMonta());
        assertEquals("Natural", r.getTipo());
    }

    @Test
    void reproduccion_withResult_updatesFields() {
        Reproduccion r = new Reproduccion();
        r.setResultado("Exitosa");
        r.setFechaPartoEstimada(LocalDate.of(2025, 3, 1));
        r.setObservaciones("Sin complicaciones");

        assertEquals("Exitosa", r.getResultado());
        assertEquals(LocalDate.of(2025, 3, 1), r.getFechaPartoEstimada());
        assertEquals("Sin complicaciones", r.getObservaciones());
    }

    // --- Parto + Reproduccion relationship ---

    @Test
    void parto_withReproduccion_setsRelationship() {
        Reproduccion reproduccion = new Reproduccion();
        reproduccion.setId(1);
        reproduccion.setVaca(vaca);

        Parto parto = new Parto();
        parto.setId(1);
        parto.setReproduccion(reproduccion);
        parto.setFechaParto(LocalDate.of(2025, 3, 15));
        parto.setCantidadCrias(1);
        parto.setObservaciones("Parto normal");

        assertEquals(reproduccion, parto.getReproduccion());
        assertEquals(LocalDate.of(2025, 3, 15), parto.getFechaParto());
        assertEquals(1, parto.getCantidadCrias());
        assertEquals("Parto normal", parto.getObservaciones());
    }

    // --- RegistroTernero + Parto + Animal relationships ---

    @Test
    void registroTernero_withPartoAndAnimal_setsRelationships() {
        Reproduccion reproduccion = new Reproduccion();
        reproduccion.setId(1);

        Parto parto = new Parto();
        parto.setId(1);
        parto.setReproduccion(reproduccion);

        Animal ternero = new Animal();
        ternero.setId(20);
        ternero.setNombre("Ternero 001");

        RegistroTernero registro = new RegistroTernero();
        registro.setId(1);
        registro.setParto(parto);
        registro.setAnimal(ternero);
        registro.setPesoNacimiento(BigDecimal.valueOf(35.5));
        registro.setSexoNacimiento("M");
        registro.setCondicionNacimiento("Saludable");

        assertEquals(parto, registro.getParto());
        assertEquals(ternero, registro.getAnimal());
        assertEquals(BigDecimal.valueOf(35.5), registro.getPesoNacimiento());
        assertEquals("M", registro.getSexoNacimiento());
        assertEquals("Saludable", registro.getCondicionNacimiento());
    }

    // --- Movimiento with Lote relationships ---

    @Test
    void movimiento_withLotes_setsRelationships() {
        Lote loteOrigen = new Lote(1, "Lote A", finca, null, null, null, null);
        Lote loteDestino = new Lote(2, "Lote B", finca, null, null, null, null);

        Movimiento movimiento = new Movimiento();
        movimiento.setId(1);
        movimiento.setAnimal(vaca);
        movimiento.setLoteOrigen(loteOrigen);
        movimiento.setLoteDestino(loteDestino);
        movimiento.setFecha(LocalDate.of(2025, 1, 15));
        movimiento.setTipoMovimiento("Traslado");
        movimiento.setMotivo("Mejor pastura");

        assertEquals(vaca, movimiento.getAnimal());
        assertEquals(loteOrigen, movimiento.getLoteOrigen());
        assertEquals(loteDestino, movimiento.getLoteDestino());
        assertEquals(LocalDate.of(2025, 1, 15), movimiento.getFecha());
        assertEquals("Traslado", movimiento.getTipoMovimiento());
        assertEquals("Mejor pastura", movimiento.getMotivo());
    }
}
