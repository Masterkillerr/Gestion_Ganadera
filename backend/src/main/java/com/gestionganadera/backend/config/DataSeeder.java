package com.gestionganadera.backend.config;

import com.gestionganadera.backend.model.*;
import com.gestionganadera.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final SexoRepository sexoRepository;
    private final EstadoAnimalRepository estadoAnimalRepository;
    private final CondicionNacimientoRepository condicionNacimientoRepository;
    private final TipoEventoRepository tipoEventoRepository;
    private final TipoMovimientoRepository tipoMovimientoRepository;
    private final TipoReproduccionRepository tipoReproduccionRepository;
    private final ResultadoReproduccionRepository resultadoReproduccionRepository;
    private final TurnoProduccionRepository turnoProduccionRepository;
    private final RoleRepository roleRepository;
    private final RazaRepository razaRepository;
    private final AlimentoRepository alimentoRepository;
    private final MedicamentoRepository medicamentoRepository;
    private final VacunaRepository vacunaRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (sexoRepository.count() > 0) {
            log.info("Catalog data already exists, skipping seeder.");
            return;
        }

        log.info("Seeding catalog data for dev profile...");

        // Sexo
        sexoRepository.saveAll(List.of(
                new Sexo(null, "Macho"),
                new Sexo(null, "Hembra")
        ));

        // Estado Animal
        estadoAnimalRepository.saveAll(List.of(
                new EstadoAnimal(null, "Activo"),
                new EstadoAnimal(null, "Vendido"),
                new EstadoAnimal(null, "Muerto"),
                new EstadoAnimal(null, "Enfermo"),
                new EstadoAnimal(null, "En Tratamiento")
        ));

        // Condición Nacimiento
        condicionNacimientoRepository.saveAll(List.of(
                new CondicionNacimiento(null, "Normal"),
                new CondicionNacimiento(null, "Distocia"),
                new CondicionNacimiento(null, "Gemelar"),
                new CondicionNacimiento(null, "Prematuro")
        ));

        // Tipo Evento
        tipoEventoRepository.saveAll(List.of(
                new TipoEvento(null, "Vacunación"),
                new TipoEvento(null, "Tratamiento"),
                new TipoEvento(null, "Parto"),
                new TipoEvento(null, "Movimiento"),
                new TipoEvento(null, "Reproducción"),
                new TipoEvento(null, "Alimentación"),
                new TipoEvento(null, "Pesaje"),
                new TipoEvento(null, "Desparasitación")
        ));

        // Tipo Movimiento
        tipoMovimientoRepository.saveAll(List.of(
                new TipoMovimiento(null, "Entrada"),
                new TipoMovimiento(null, "Salida"),
                new TipoMovimiento(null, "Traslado Interno")
        ));

        // Tipo Reproducción
        tipoReproduccionRepository.saveAll(List.of(
                new TipoReproduccion(null, "Monta Natural"),
                new TipoReproduccion(null, "Inseminación"),
                new TipoReproduccion(null, "Transf. Embrionaria")
        ));

        // Resultado Reproducción
        resultadoReproduccionRepository.saveAll(List.of(
                new ResultadoReproduccion(null, "Exitosa"),
                new ResultadoReproduccion(null, "No Exitosa"),
                new ResultadoReproduccion(null, "Aborto"),
                new ResultadoReproduccion(null, "Gemelar")
        ));

        // Turno Producción
        turnoProduccionRepository.saveAll(List.of(
                new TurnoProduccion(null, "Mañana"),
                new TurnoProduccion(null, "Tarde"),
                new TurnoProduccion(null, "Noche")
        ));

        // Roles
        roleRepository.saveAll(List.of(
                new Role(null, "ADMIN"),
                new Role(null, "USUARIO"),
                new Role(null, "VETERINARIO")
        ));

        // Razas
        razaRepository.saveAll(List.of(
                new Raza(null, "Angus"),
                new Raza(null, "Brahman"),
                new Raza(null, "Brangus"),
                new Raza(null, "Charolais"),
                new Raza(null, "Hereford"),
                new Raza(null, "Holstein"),
                new Raza(null, "Jersey"),
                new Raza(null, "Limousin"),
                new Raza(null, "Nelore"),
                new Raza(null, "Simmental")
        ));

        // Alimentos
        alimentoRepository.saveAll(List.of(
                new Alimento(null, "Concentrado"),
                new Alimento(null, "Heno"),
                new Alimento(null, "Silo"),
                new Alimento(null, "Pasto"),
                new Alimento(null, "Sal Mineral"),
                new Alimento(null, "Melaza")
        ));

        // Medicamentos
        medicamentoRepository.saveAll(List.of(
                new Medicamento(null, "Ivermectina", "Antiparasitario interno y externo"),
                new Medicamento(null, "Oxitetraciclina", "Antibiótico de amplio espectro"),
                new Medicamento(null, "Vitaminas ADE", "Complejo vitamínico A, D, E"),
                new Medicamento(null, "Desparasitante Oral", "Desparasitante vía oral"),
                new Medicamento(null, "Antiinflamatorio", "Antiinflamatorio no esteroideo")
        ));

        // Vacunas
        vacunaRepository.saveAll(List.of(
                new Vacuna(null, "Fiebre Aftosa"),
                new Vacuna(null, "Brucelosis"),
                new Vacuna(null, "Carbón Sintomático"),
                new Vacuna(null, "Leptospirosis"),
                new Vacuna(null, "Rabia")
        ));

        log.info("Catalog data seeded successfully ({} tables).", 13);
    }
}
