package com.gestionganadera.backend.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EntityRelationshipsTest {

    private Usuario propietario;
    private Finca finca;
    private Lote lote;
    private Animal vaca;
    private Animal toro;
    private Animal cria;
    private Raza raza;
    private Sexo sexoHembra;
    private Sexo sexoMacho;
    private EstadoAnimal estado;
    private TipoEvento tipoEvento;
    private Evento evento;
    private Reproduccion reproduccion;
    private Parto parto;

    @BeforeEach
    void setUp() {
        propietario = new Usuario();
        propietario.setId(1);
        propietario.setNombre("Dueño");
        propietario.setEmail("dueno@finca.com");
        propietario.setPassword("pass");
        Role role = new Role(1, "USER");
        propietario.setRole(role);
        propietario.setCreadoEn(LocalDateTime.now());

        finca = new Finca(1, "Finca El Porvenir", "Campo Hermoso", null);
        lote = new Lote(1, "Lote A", finca, BigDecimal.valueOf(50), 100, "Pasto estrella", "Activo");
        raza = new Raza(1, "Holstein");
        sexoHembra = new Sexo(1, "Hembra");
        sexoMacho = new Sexo(2, "Macho");
        estado = new EstadoAnimal(1, "Saludable");

        vaca = new Animal();
        vaca.setId(10);
        vaca.setIdentificadorArete("AR-010");
        vaca.setNombre("Vaca Lechera");
        vaca.setSexo(sexoHembra);
        vaca.setEstadoAnimal(estado);
        vaca.setRaza(raza);
        vaca.setFechaNacimiento(LocalDate.of(2020, 1, 1));
        vaca.setPesoActualKg(BigDecimal.valueOf(500));

        toro = new Animal();
        toro.setId(11);
        toro.setIdentificadorArete("AR-011");
        toro.setNombre("Toro Bravo");
        toro.setSexo(sexoMacho);
        toro.setFechaNacimiento(LocalDate.of(2019, 5, 1));
        toro.setPesoActualKg(BigDecimal.valueOf(800));

        cria = new Animal();
        cria.setId(12);
        cria.setIdentificadorArete("AR-012");
        cria.setNombre("Ternero");
        cria.setSexo(sexoMacho);
        cria.setFechaNacimiento(LocalDate.of(2024, 3, 1));
        cria.setPesoActualKg(BigDecimal.valueOf(80));
        cria.setMadre(vaca);
        cria.setPadre(toro);

        tipoEvento = new TipoEvento(1, "Reproducción");
        evento = new Evento();
        evento.setId(1);
        evento.setAnimal(vaca);
        evento.setTipoEvento(tipoEvento);
        evento.setDescripcion("Monta natural");
    }

    // --- Finca ---

    @Test
    void finca_setsFields() {
        assertEquals("Finca El Porvenir", finca.getNombre());
        assertEquals("Campo Hermoso", finca.getUbicacion());
    }

    // --- Lote + Finca relationship ---

    @Test
    void lote_withFinca_setsRelationship() {
        assertEquals(finca, lote.getFinca());
        assertEquals("Lote A", lote.getNombre());
        assertEquals(Integer.valueOf(100), lote.getCapacidadMaxima());
        assertEquals("Activo", lote.getEstado());
    }

    // --- Animal relationships (Raza, Sexo, EstadoAnimal, Madre, Padre) ---

    @Test
    void animal_withAllRelationships_setsCorrectly() {
        assertEquals(raza, vaca.getRaza());
        assertEquals(sexoHembra, vaca.getSexo());
        assertEquals(estado, vaca.getEstadoAnimal());
        assertEquals("Holstein", vaca.getRaza().getNombre());
        assertEquals("Hembra", vaca.getSexo().getNombre());
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
        assertNull(simple.getSexo());
        assertNull(simple.getEstadoAnimal());
        assertNull(simple.getMadre());
        assertNull(simple.getPadre());
    }

    // --- Evento relationships ---

    @Test
    void evento_withAnimalAndTipo_setsRelationships() {
        assertEquals(vaca, evento.getAnimal());
        assertEquals(tipoEvento, evento.getTipoEvento());
    }

    // --- Reproduccion (with Evento, TipoReproduccion, ResultadoReproduccion) ---

    @Test
    void reproduccion_withAllRelationships_setsCorrectly() {
        Reproduccion r = new Reproduccion();
        r.setId(1);
        r.setEvento(evento);
        r.setVaca(vaca);
        r.setToro(toro);
        r.setFechaPartoEstimada(LocalDate.of(2025, 3, 1));
        r.setObservacion("Sin complicaciones");

        assertEquals(evento, r.getEvento());
        assertEquals(vaca, r.getVaca());
        assertEquals(toro, r.getToro());
        assertEquals(LocalDate.of(2025, 3, 1), r.getFechaPartoEstimada());
        assertEquals("Sin complicaciones", r.getObservacion());
    }

    // --- Parto + Reproduccion relationship ---

    @Test
    void parto_withReproduccion_setsRelationship() {
        Reproduccion reproduccion = new Reproduccion();
        reproduccion.setId(1);
        reproduccion.setVaca(vaca);

        Evento partoEvento = new Evento();
        partoEvento.setId(2);
        partoEvento.setAnimal(vaca);

        Parto parto = new Parto();
        parto.setId(1);
        parto.setEvento(partoEvento);
        parto.setReproduccion(reproduccion);
        parto.setCantidadCrias(1);
        parto.setObservacion("Parto normal");

        assertEquals(reproduccion, parto.getReproduccion());
        assertEquals(partoEvento, parto.getEvento());
        assertEquals(1, parto.getCantidadCrias());
        assertEquals("Parto normal", parto.getObservacion());
    }

    // --- Movimiento with Lote relationships ---

    @Test
    void movimiento_withLotes_setsRelationships() {
        Evento movEvento = new Evento();
        movEvento.setId(3);
        movEvento.setAnimal(vaca);

        Lote loteOrigen = new Lote(1, "Lote A", finca, null, null, null, null);
        Lote loteDestino = new Lote(2, "Lote B", finca, null, null, null, null);
        TipoMovimiento tipoMov = new TipoMovimiento(1, "Traslado");

        Movimiento movimiento = new Movimiento();
        movimiento.setId(1);
        movimiento.setEvento(movEvento);
        movimiento.setTipoMovimiento(tipoMov);
        movimiento.setLoteOrigen(loteOrigen);
        movimiento.setLoteDestino(loteDestino);
        movimiento.setMotivo("Mejor pastura");

        assertEquals(movEvento, movimiento.getEvento());
        assertEquals(loteOrigen, movimiento.getLoteOrigen());
        assertEquals(loteDestino, movimiento.getLoteDestino());
        assertEquals("Traslado", movimiento.getTipoMovimiento().getNombre());
        assertEquals("Mejor pastura", movimiento.getMotivo());
    }
}
