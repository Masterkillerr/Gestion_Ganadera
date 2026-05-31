package com.gestionganadera.backend.service;

import com.gestionganadera.backend.model.*;
import com.gestionganadera.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DemoDataSeeder implements CommandLineRunner {

    private final AnimalRepository animalRepository;
    private final UsuarioRepository usuarioRepository;
    private final FincaRepository fincaRepository;
    private final LoteRepository loteRepository;
    private final ProduccionRepository produccionRepository;
    private final EventoRepository eventoRepository;
    private final MovimientoRepository movimientoRepository;
    private final SexoRepository sexoRepository;
    private final RazaRepository razaRepository;
    private final EstadoAnimalRepository estadoAnimalRepository;
    private final TipoMovimientoRepository tipoMovimientoRepository;
    private final TipoEventoRepository tipoEventoRepository;
    private final TurnoProduccionRepository turnoProduccionRepository;
    private final RoleRepository roleRepository;
    private final ReproduccionRepository reproduccionRepository;
    private final PartoRepository partoRepository;
    private final VacunacionRepository vacunacionRepository;
    private final VacunaRepository vacunaRepository;
    private final TipoReproduccionRepository tipoReproduccionRepository;
    private final ResultadoReproduccionRepository resultadoReproduccionRepository;
    private final CondicionNacimientoRepository condicionNacimientoRepository;
    private final AlimentoRepository alimentoRepository;
    private final DietaRepository dietaRepository;
    private final AlimentacionRepository alimentacionRepository;
    private final DietaAlimentoRepository dietaAlimentoRepository;
    private final PasswordEncoder passwordEncoder;

    private final Random rnd = new Random(42L);

    @Override
    @Transactional
    public void run(String... args) {
        if (usuarioRepository.count() > 1) {
            log.info("Datos demo ya existen — saltando seeder.");
            return;
        }

        log.info("🌱 Sembrando datos demo para entorno local...");

        // ── 0. Catálogos (crear si no existen) ──────────────
        ensureRoles();
        Sexo macho   = ensureCatalog(sexoRepository, "Macho");
        Sexo hembra  = ensureCatalog(sexoRepository, "Hembra");

        Raza angus     = ensureRaza("Angus");
        Raza brahman   = ensureRaza("Brahman");
        Raza brangus   = ensureRaza("Brangus");
        Raza holstein  = ensureRaza("Holstein");
        Raza nelore    = ensureRaza("Nelore");
        Raza simmental = ensureRaza("Simmental");

        EstadoAnimal activo         = ensureEstado("Activo");
        EstadoAnimal vendido        = ensureEstado("Vendido");
        EstadoAnimal enfermo        = ensureEstado("Enfermo");
        EstadoAnimal enTratamiento  = ensureEstado("En Tratamiento");
        EstadoAnimal lactancia      = ensureEstado("Lactancia");

        TipoEvento tipoPesaje     = ensureTipoEvento("Pesaje");
        TipoMovimiento entrada    = ensureTipoMovimiento("Entrada");
        TurnoProduccion manana    = ensureTurno("Mañana");
        TurnoProduccion tarde     = ensureTurno("Tarde");

        log.info("  ✓ Catálogos listos");

        // ── 1. Roles & Usuarios ──────────────────────────
        Role adminRole = findByName(roleRepository.findAll(), Role::getNombre, "ADMIN");
        Role userRole  = findByName(roleRepository.findAll(), Role::getNombre, "USUARIO");

        Usuario admin = new Usuario();
        admin.setNombre("Admin Ganadero");
        admin.setEmail("admin@ganado.com");
        admin.setPassword(passwordEncoder.encode("Admin123!"));
        admin.setRole(adminRole != null ? adminRole : userRole);
        admin.setCreadoEn(LocalDateTime.now().minusMonths(6));
        usuarioRepository.save(admin);

        Usuario testUser = new Usuario();
        testUser.setNombre("Usuario Prueba");
        testUser.setEmail("test@ganado.com");
        testUser.setPassword(passwordEncoder.encode("Test123!"));
        testUser.setRole(userRole);
        testUser.setCreadoEn(LocalDateTime.now().minusMonths(3));
        usuarioRepository.save(testUser);
        log.info("  ✓ Usuarios: admin@ganado.com / test@ganado.com");

        // ── 2. Finca ─────────────────────────────────────
        Finca finca = new Finca();
        finca.setNombre("Finca El Porvenir");
        finca.setUbicacion("Municipio de San José, Departamento del Meta, Colombia");
        finca.setExtension(new BigDecimal("120.50"));
        finca = fincaRepository.save(finca);
        log.info("  ✓ Finca: {} ({} has)", finca.getNombre(), finca.getExtension());

        // ── 3. Lotes ─────────────────────────────────────
        Lote lote1 = createLote(finca, "Potrero Norte",     new BigDecimal("30.00"), 50, "Brachiaria decumbens");
        Lote lote2 = createLote(finca, "Potrero Sur",       new BigDecimal("25.00"), 40, "Panicum maximum");
        Lote lote3 = createLote(finca, "Potrero Oriental",  new BigDecimal("35.00"), 60, "Brachiaria brizantha");
        Lote lote4 = createLote(finca, "Corral de ordeño",  new BigDecimal("5.00"),  80, "Estabulado");
        Lote lote5 = createLote(finca, "Potrero de levante",new BigDecimal("25.50"), 45, "Brachiaria humidicola");
        List<Lote> lotes = List.of(lote1, lote2, lote3, lote4, lote5);
        log.info("  ✓ 5 lotes creados");

        // ── 4. Animales ──────────────────────────────────
        Animal toro1 = createAnimal("T-001", "Trueno",    macho, brahman,  activo, LocalDate.of(2020, 3, 15),  new BigDecimal("780.00"), null, null);
        Animal toro2 = createAnimal("T-002", "Relámpago", macho, brangus,  activo, LocalDate.of(2019, 8, 1),   new BigDecimal("810.00"), null, null);
        Animal toro3 = createAnimal("T-003", "Cimarrón",  macho, angus,    activo, LocalDate.of(2021, 1, 20),  new BigDecimal("720.00"), null, null);

        Animal vaca1  = createAnimal("V-001", "Luna",      hembra, holstein, activo, LocalDate.of(2021, 5, 10),  new BigDecimal("550.00"), null, null);
        Animal vaca2  = createAnimal("V-002", "Estrella",  hembra, holstein, activo, LocalDate.of(2020, 11, 5),  new BigDecimal("530.00"), null, null);
        Animal vaca3  = createAnimal("V-003", "Margarita", hembra, nelore,   activo, LocalDate.of(2021, 2, 18),  new BigDecimal("480.00"), null, null);
        Animal vaca4  = createAnimal("V-004", "Rosa",      hembra, brangus,  activo, LocalDate.of(2022, 4, 22),  new BigDecimal("460.00"), null, null);
        Animal vaca5  = createAnimal("V-005", "Blanca",    hembra, holstein, activo, LocalDate.of(2020, 7, 30),  new BigDecimal("570.00"), null, null);
        Animal vaca6  = createAnimal("V-006", "Clavel",    hembra, nelore,   activo, LocalDate.of(2022, 1, 12),  new BigDecimal("440.00"), null, null);
        Animal vaca7  = createAnimal("V-007", "Aurora",    hembra, holstein, activo, LocalDate.of(2021, 9, 8),   new BigDecimal("520.00"), null, null);
        Animal vaca8  = createAnimal("V-008", "Lucero",    hembra, brangus,  activo, LocalDate.of(2022, 6, 14),  new BigDecimal("450.00"), null, null);
        Animal vaca9  = createAnimal("V-009", "Nube",      hembra, holstein, activo, LocalDate.of(2019, 12, 3),  new BigDecimal("600.00"), null, null);
        Animal vaca10 = createAnimal("V-010", "Coral",     hembra, nelore,   activo, LocalDate.of(2023, 3, 20),  new BigDecimal("420.00"), null, null);
        Animal vaca11 = createAnimal("V-011", "Perla",     hembra, holstein, activo, LocalDate.of(2021, 7, 25),  new BigDecimal("510.00"), null, null);
        Animal vaca12 = createAnimal("V-012", "Dulce",     hembra, brangus,  activo, LocalDate.of(2020, 9, 14),  new BigDecimal("490.00"), null, null);

        Animal novilla1 = createAnimal("N-001", "Campanita", hembra, holstein, activo,  LocalDate.of(2023, 6, 1),   new BigDecimal("320.00"), null, null);
        Animal novilla2 = createAnimal("N-002", "Brisa",     hembra, brahman,  activo,  LocalDate.of(2023, 8, 15),  new BigDecimal("290.00"), null, null);
        Animal novilla3 = createAnimal("N-003", "Lluvia",    hembra, nelore,   activo,  LocalDate.of(2024, 1, 10),  new BigDecimal("250.00"), null, null);
        Animal novilla4 = createAnimal("N-004", "Arena",     hembra, brangus,  activo,  LocalDate.of(2023, 11, 5),  new BigDecimal("280.00"), null, null);
        Animal novilla5 = createAnimal("N-005", "Cielo",     hembra, holstein, activo,  LocalDate.of(2024, 3, 22),  new BigDecimal("220.00"), null, null);
        Animal novilla6 = createAnimal("N-006", "Zafiro",    hembra, simmental, activo, LocalDate.of(2023, 5, 30),  new BigDecimal("310.00"), null, null);

        Animal novillo1 = createAnimal("N-007", "Tormenta", macho, brahman,   activo, LocalDate.of(2023, 4, 12), new BigDecimal("340.00"), null, null);
        Animal novillo2 = createAnimal("N-008", "Rayo",     macho, brangus,   activo, LocalDate.of(2023, 9, 8),  new BigDecimal("300.00"), null, null);
        Animal novillo3 = createAnimal("N-009", "Fuego",    macho, angus,     activo, LocalDate.of(2024, 2, 14), new BigDecimal("260.00"), null, null);
        Animal novillo4 = createAnimal("N-010", "Viento",   macho, simmental, activo, LocalDate.of(2023, 7, 20), new BigDecimal("330.00"), null, null);

        Animal vendido1 = createAnimal("X-001", "Viejo Roble", macho,  angus,    vendido, LocalDate.of(2018, 3, 10), new BigDecimal("850.00"), null, null);
        Animal vendido2 = createAnimal("X-002", "Rosita",      hembra, holstein, vendido, LocalDate.of(2019, 6, 5),  new BigDecimal("580.00"), null, null);
        Animal enfermo1 = createAnimal("S-001", "Temblor",     macho,  brahman,  enfermo,  LocalDate.of(2022, 10, 3), new BigDecimal("420.00"), null, null);
        Animal ternero1 = createAnimal("C-001", "Pequitas",    hembra, holstein, activo,   LocalDate.of(2025, 1, 15), new BigDecimal("120.00"), vaca1, toro1);

        List<Animal> vacas = List.of(vaca1, vaca2, vaca3, vaca4, vaca5, vaca6, vaca7, vaca8, vaca9, vaca10, vaca11, vaca12);
        List<Animal> animales = List.of(
                toro1, toro2, toro3,
                vaca1, vaca2, vaca3, vaca4, vaca5, vaca6, vaca7, vaca8, vaca9, vaca10, vaca11, vaca12,
                novilla1, novilla2, novilla3, novilla4, novilla5, novilla6,
                novillo1, novillo2, novillo3, novillo4,
                vendido1, vendido2, enfermo1, ternero1);

        // Asignar "En Tratamiento" a algunos animales
        enfermo1.setEstadoAnimal(enTratamiento);
        animalRepository.save(enfermo1);

        // Asignar "Lactancia" a vacas que parieron (las exitosas de la lista de reproducción)
        List<Animal> vacasEnLactancia = List.of(vaca1, vaca2, vaca3, vaca5, vaca7, vaca9);
        for (Animal v : vacasEnLactancia) {
            v.setEstadoAnimal(lactancia);
            animalRepository.save(v);
        }
        log.info("  ✓ {} animales creados", animales.size());

        // ── 5. Movimientos ──────────────────────────────
        for (Animal a : animales) {
            int loteIdx = switch (a.getNombre()) {
                case "Trueno", "Relámpago", "Cimarrón" -> 0;
                case "Viejo Roble" -> 1;
                case "Temblor" -> 2;
                case "Pequitas" -> 1;
                default -> rnd.nextInt(lotes.size());
            };
            Lote lote = lotes.get(loteIdx);
            Evento evt = new Evento();
            evt.setAnimal(a);
            evt.setTipoEvento(tipoPesaje);
            evt.setFecha(LocalDateTime.now().minusDays(30));
            evt.setDescripcion("Ingreso a " + lote.getNombre());
            evt = eventoRepository.save(evt);

            Movimiento mov = new Movimiento();
            mov.setEvento(evt);
            mov.setTipoMovimiento(entrada);
            mov.setLoteDestino(lote);
            mov.setMotivo("Asignación inicial");
            movimientoRepository.save(mov);
        }
        log.info("  ✓ {} movimientos", animales.size());

        // ── 6. Producción (últimos 180 días) ────────────
        LocalDate today = LocalDate.now();
        int prodCount = 0;
        for (Animal vaca : vacas) {
            double productividad = 0.75 + rnd.nextDouble() * 0.25;
            LocalDate start = today.minusDays(180);
            for (LocalDate d = start; !d.isAfter(today); d = d.plusDays(1)) {
                if (rnd.nextDouble() > productividad) continue;
                double base = "Holstein".equals(vaca.getRaza().getNombre()) ? 18.0 : 12.0;
                double litros = Math.max(0, base + rnd.nextGaussian() * 3.0);
                if (litros > 0.5) {
                    produccionRepository.save(buildProduccion(vaca, manana, d, litros));
                    prodCount++;
                }
                if (rnd.nextDouble() < 0.6) {
                    double tardeLitros = litros * 0.6 + rnd.nextGaussian() * 1.5;
                    if (tardeLitros > 0.5) {
                        produccionRepository.save(buildProduccion(vaca, tarde, d, tardeLitros));
                        prodCount++;
                    }
                }
            }
        }
        log.info("  ✓ {} registros de producción (180 días)", prodCount);

        // ── 7. Reproducciones ────────────────────────────
        ensureTipoReproduccion("Monta Natural");
        ensureTipoReproduccion("Inseminación");
        ensureTipoReproduccion("Transf. Embrionaria");
        TipoReproduccion montaNatural = findByName(tipoReproduccionRepository.findAll(), TipoReproduccion::getNombre, "Monta Natural");
        TipoReproduccion inseminacion = findByName(tipoReproduccionRepository.findAll(), TipoReproduccion::getNombre, "Inseminación");

        ensureResultadoReproduccion("Exitosa");
        ensureResultadoReproduccion("No Exitosa");
        ensureResultadoReproduccion("Aborto");
        ensureResultadoReproduccion("Gemelar");
        ResultadoReproduccion exitosa   = findByName(resultadoReproduccionRepository.findAll(), ResultadoReproduccion::getNombre, "Exitosa");
        ResultadoReproduccion noExitosa = findByName(resultadoReproduccionRepository.findAll(), ResultadoReproduccion::getNombre, "No Exitosa");

        ensureCondicionNacimiento("Normal");
        ensureCondicionNacimiento("Distocia");
        ensureCondicionNacimiento("Gemelar");
        ensureCondicionNacimiento("Prematuro");

        TipoEvento tipoReproduccionEvt = ensureTipoEvento("Reproducción");
        TipoEvento tipoPartoEvt = ensureTipoEvento("Parto");
        TipoEvento tipoVacunacionEvt = ensureTipoEvento("Vacunación");

        // Vacunas catálogo
        ensureVacuna("Fiebre Aftosa");
        ensureVacuna("Brucelosis");
        ensureVacuna("Carbón Sintomático");
        ensureVacuna("Leptospirosis");
        ensureVacuna("Rabia");
        Vacuna fiebreAftosa = findByName(vacunaRepository.findAll(), Vacuna::getNombre, "Fiebre Aftosa");
        Vacuna brucelosis   = findByName(vacunaRepository.findAll(), Vacuna::getNombre, "Brucelosis");
        Vacuna carbon       = findByName(vacunaRepository.findAll(), Vacuna::getNombre, "Carbón Sintomático");
        Vacuna lepto        = findByName(vacunaRepository.findAll(), Vacuna::getNombre, "Leptospirosis");

        List<Animal> vacasRepro = List.of(vaca1, vaca2, vaca3, vaca5, vaca7, vaca9, vaca11, vaca12);
        List<Animal> toros = List.of(toro1, toro2);
        int reproCount = 0, partoCount = 0;

        for (int i = 0; i < vacasRepro.size(); i++) {
            Animal vaca = vacasRepro.get(i);
            Animal toro = toros.get(i % toros.size());
            boolean esInseminacion = i % 3 == 0;
            boolean esExitosa = i < 6; // primeras 6 exitosas, últimas 2 no

            // Evento de reproducción
            Evento evtRepro = new Evento();
            evtRepro.setAnimal(vaca);
            evtRepro.setTipoEvento(esInseminacion ? tipoReproduccionEvt : tipoReproduccionEvt);
            LocalDate fechaRepro = today.minusDays(90 + rnd.nextInt(120));
            evtRepro.setFecha(fechaRepro.atStartOfDay());
            evtRepro.setDescripcion((esInseminacion ? "Inseminación" : "Monta natural") + " con " + toro.getNombre());
            evtRepro = eventoRepository.save(evtRepro);

            Reproduccion repro = new Reproduccion();
            repro.setEvento(evtRepro);
            repro.setVaca(vaca);
            repro.setToro(toro);
            repro.setTipoReproduccion(esInseminacion ? inseminacion : montaNatural);
            repro.setResultadoReproduccion(esExitosa ? exitosa : noExitosa);
            if (esExitosa) {
                repro.setFechaPartoEstimada(fechaRepro.plusDays(285)); // ~9.5 meses
            }
            repro.setObservacion(esExitosa ? "Gestación en curso" : "No hubo gestación");
            reproduccionRepository.save(repro);
            reproCount++;

            if (esExitosa) {
                // Parto para las exitosas
                Evento evtParto = new Evento();
                evtParto.setAnimal(vaca);
                evtParto.setTipoEvento(tipoPartoEvt);
                LocalDate fechaParto = fechaRepro.plusDays(280 + rnd.nextInt(10));
                evtParto.setFecha(fechaParto.atStartOfDay());
                evtParto.setDescripcion("Parto de " + vaca.getNombre() + " — cría de " + toro.getNombre());
                evtParto = eventoRepository.save(evtParto);

                Parto parto = new Parto();
                parto.setEvento(evtParto);
                parto.setReproduccion(repro);
                parto.setCantidadCrias(i == 5 ? 2 : 1); // gemelar en la 6ª
                parto.setObservacion(i == 5 ? "Parto gemelar" : "Parto normal");
                partoRepository.save(parto);
                partoCount++;
            }
        }
        log.info("  ✓ {} reproducciones, {} partos", reproCount, partoCount);

        // ── 8. Vacunaciones ────────────────────────────
        List<Animal> aVacunar = List.of(vaca1, vaca2, vaca3, vaca4, vaca5, ternero1, novilla1, novillo1);
        int vacCount = 0;
        for (Animal a : aVacunar) {
            Vacuna vacuna = switch (a.getNombre()) {
                case "Pequitas" -> brucelosis;
                case "Campanita", "Brisa" -> lepto;
                default -> fiebreAftosa;
            };

            Evento evtVac = new Evento();
            evtVac.setAnimal(a);
            evtVac.setTipoEvento(tipoVacunacionEvt);
            LocalDate fechaVac = today.minusDays(30 + rnd.nextInt(60));
            evtVac.setFecha(fechaVac.atStartOfDay());
            evtVac.setDescripcion("Vacunación " + vacuna.getNombre());
            evtVac = eventoRepository.save(evtVac);

            Vacunacion vac = new Vacunacion();
            vac.setEvento(evtVac);
            vac.setVacuna(vacuna);
            vac.setProximaDosis(fechaVac.plusMonths(6));
            vac.setObservacion("Dosis aplicada por veterinario");
            vacunacionRepository.save(vac);
            vacCount++;
        }
        log.info("  ✓ {} vacunaciones", vacCount);

        // ── 9. Alimentos & Dietas ────────────────────────────
        Alimento pasto        = ensureAlimento("Pasto");
        Alimento concentrado  = ensureAlimento("Concentrado");
        Alimento salMin       = ensureAlimento("Sal Mineralizada");
        Alimento melaza       = ensureAlimento("Melaza");
        Alimento silaje       = ensureAlimento("Silaje");
        Alimento heno         = ensureAlimento("Henolaje");

        Dieta engorde      = ensureDieta("Dieta de Engorde", "Ración alta en energía para novillos en ceba");
        Dieta lactanciaD   = ensureDieta("Dieta de Lactancia", "Suplementación para vacas lactantes");
        Dieta mantto       = ensureDieta("Dieta de Mantenimiento", "Dieta base para mantenimiento del hato");

        createDietaAlimento(engorde, concentrado, new BigDecimal("4.00"), "kg/día");
        createDietaAlimento(engorde, silaje, new BigDecimal("15.00"), "kg/día");
        createDietaAlimento(engorde, salMin, new BigDecimal("0.10"), "kg/día");
        createDietaAlimento(lactanciaD, concentrado, new BigDecimal("6.00"), "kg/día");
        createDietaAlimento(lactanciaD, silaje, new BigDecimal("10.00"), "kg/día");
        createDietaAlimento(lactanciaD, salMin, new BigDecimal("0.15"), "kg/día");
        createDietaAlimento(lactanciaD, melaza, new BigDecimal("1.00"), "L/día");
        createDietaAlimento(mantto, pasto, new BigDecimal("30.00"), "kg/día");
        createDietaAlimento(mantto, salMin, new BigDecimal("0.08"), "kg/día");
        log.info("  ✓ 6 alimentos, 3 dietas, 9 composiciones");

        // ── 10. Alimentaciones ───────────────────────────────
        List<Animal> aAlimentar = List.of(vaca1, vaca2, vaca3, vaca5, vaca7, vaca9, novillo1, novillo2, toro1);
        int alimCount = 0;
        for (Animal a : aAlimentar) {
            Dieta d = a.getEstadoAnimal().getNombre().equals("Lactancia") ? lactanciaD : mantto;
            Alimentacion al = new Alimentacion();
            al.setAnimal(a);
            al.setDieta(d);
            al.setFecha(LocalDateTime.now().minusDays(rnd.nextInt(7)));
            al.setObservacion("Alimentación " + d.getNombre() + " — " + a.getNombre());
            alimentacionRepository.save(al);
            alimCount++;
        }
        log.info("  ✓ {} alimentaciones", alimCount);

        log.info("🌱✅ Datos demo listos! admin@ganado.com / test@ganado.com · {} animales · {} lotes · {} producciones · {} reproducciones · {} partos · {} vacunaciones",
                animales.size(), lotes.size(), prodCount, reproCount, partoCount, vacCount);
    }

    // ── helpers de catálogos ──────────────────────────────

    private void ensureRoles() {
        if (roleRepository.count() == 0) {
            for (String name : List.of("ADMIN", "USUARIO", "VETERINARIO")) {
                Role r = new Role();
                r.setNombre(name);
                roleRepository.save(r);
            }
        }
    }

    private Sexo ensureCatalog(SexoRepository repo, String nombre) {
        var list = repo.findAll();
        var found = findByName(list, Sexo::getNombre, nombre);
        if (found != null) return found;
        Sexo s = new Sexo();
        s.setNombre(nombre);
        return repo.save(s);
    }

    private Raza ensureRaza(String nombre) {
        var list = razaRepository.findAll();
        var found = findByName(list, Raza::getNombre, nombre);
        if (found != null) return found;
        Raza r = new Raza();
        r.setNombre(nombre);
        return razaRepository.save(r);
    }

    private EstadoAnimal ensureEstado(String nombre) {
        var list = estadoAnimalRepository.findAll();
        var found = findByName(list, EstadoAnimal::getNombre, nombre);
        if (found != null) return found;
        EstadoAnimal e = new EstadoAnimal();
        e.setNombre(nombre);
        return estadoAnimalRepository.save(e);
    }

    private TipoEvento ensureTipoEvento(String nombre) {
        var list = tipoEventoRepository.findAll();
        var found = findByName(list, TipoEvento::getNombre, nombre);
        if (found != null) return found;
        TipoEvento t = new TipoEvento();
        t.setNombre(nombre);
        return tipoEventoRepository.save(t);
    }

    private TipoMovimiento ensureTipoMovimiento(String nombre) {
        var list = tipoMovimientoRepository.findAll();
        var found = findByName(list, TipoMovimiento::getNombre, nombre);
        if (found != null) return found;
        TipoMovimiento t = new TipoMovimiento();
        t.setNombre(nombre);
        return tipoMovimientoRepository.save(t);
    }

    private Vacuna ensureVacuna(String nombre) {
        var list = vacunaRepository.findAll();
        var found = findByName(list, Vacuna::getNombre, nombre);
        if (found != null) return found;
        Vacuna v = new Vacuna();
        v.setNombre(nombre);
        return vacunaRepository.save(v);
    }

    private TipoReproduccion ensureTipoReproduccion(String nombre) {
        var list = tipoReproduccionRepository.findAll();
        var found = findByName(list, TipoReproduccion::getNombre, nombre);
        if (found != null) return found;
        TipoReproduccion t = new TipoReproduccion();
        t.setNombre(nombre);
        return tipoReproduccionRepository.save(t);
    }

    private ResultadoReproduccion ensureResultadoReproduccion(String nombre) {
        var list = resultadoReproduccionRepository.findAll();
        var found = findByName(list, ResultadoReproduccion::getNombre, nombre);
        if (found != null) return found;
        ResultadoReproduccion r = new ResultadoReproduccion();
        r.setNombre(nombre);
        return resultadoReproduccionRepository.save(r);
    }

    private CondicionNacimiento ensureCondicionNacimiento(String nombre) {
        var list = condicionNacimientoRepository.findAll();
        var found = findByName(list, CondicionNacimiento::getNombre, nombre);
        if (found != null) return found;
        CondicionNacimiento c = new CondicionNacimiento();
        c.setNombre(nombre);
        return condicionNacimientoRepository.save(c);
    }

    private TurnoProduccion ensureTurno(String nombre) {
        var list = turnoProduccionRepository.findAll();
        var found = findByName(list, TurnoProduccion::getNombre, nombre);
        if (found != null) return found;
        TurnoProduccion t = new TurnoProduccion();
        t.setNombre(nombre);
        return turnoProduccionRepository.save(t);
    }

    private Alimento ensureAlimento(String nombre) {
        var list = alimentoRepository.findAll();
        var found = findByName(list, Alimento::getNombre, nombre);
        if (found != null) return found;
        Alimento a = new Alimento();
        a.setNombre(nombre);
        return alimentoRepository.save(a);
    }

    private Dieta ensureDieta(String nombre, String descripcion) {
        var list = dietaRepository.findAll();
        var found = findByName(list, Dieta::getNombre, nombre);
        if (found != null) return found;
        Dieta d = new Dieta();
        d.setNombre(nombre);
        d.setDescripcion(descripcion);
        return dietaRepository.save(d);
    }

    private void createDietaAlimento(Dieta dieta, Alimento alimento, BigDecimal cantidad, String unidad) {
        DietaAlimento da = new DietaAlimento();
        da.setDieta(dieta);
        da.setAlimento(alimento);
        da.setCantidad(cantidad);
        da.setUnidad(unidad);
        dietaAlimentoRepository.save(da);
    }

    // ── helpers genéricos ─────────────────────────────────

    private <T> T findByName(List<T> items, Function<T, String> nameFn, String name) {
        return items.stream().filter(i -> name.equals(nameFn.apply(i))).findFirst().orElse(null);
    }

    private Lote createLote(Finca finca, String nombre, BigDecimal has, int cap, String pasto) {
        Lote l = new Lote();
        l.setFinca(finca);
        l.setNombre(nombre);
        l.setHectareas(has);
        l.setCapacidadMaxima(cap);
        l.setTipoPasto(pasto);
        l.setEstado("Activo");
        return loteRepository.save(l);
    }

    private Animal createAnimal(String arete, String nombre, Sexo sexo, Raza raza,
                                EstadoAnimal estado, LocalDate fnac, BigDecimal peso,
                                Animal madre, Animal padre) {
        Animal a = new Animal();
        a.setIdentificadorArete(arete);
        a.setNombre(nombre);
        a.setSexo(sexo);
        a.setRaza(raza);
        a.setEstadoAnimal(estado);
        a.setFechaNacimiento(fnac);
        a.setPesoActualKg(peso);
        a.setMadre(madre);
        a.setPadre(padre);
        return animalRepository.save(a);
    }

    private Produccion buildProduccion(Animal vaca, TurnoProduccion turno, LocalDate fecha, double litros) {
        Produccion p = new Produccion();
        p.setAnimal(vaca);
        p.setTurnoProduccion(turno);
        p.setLitros(BigDecimal.valueOf(Math.round(litros * 100.0) / 100.0));
        p.setFecha(fecha);
        return p;
    }
}
