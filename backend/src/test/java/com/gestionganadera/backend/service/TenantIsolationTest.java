package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.*;
import com.gestionganadera.backend.model.*;
import com.gestionganadera.backend.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Integration tests for tenant isolation.
 * Verifies that User A cannot access User B's resources (fincas, lotes, animals, child records).
 *
 * Uses mocked repositories with SecurityContextHolder.setAuthentication() to simulate different users.
 */
@ExtendWith(MockitoExtension.class)
class TenantIsolationTest {

    // ── Shared test fixtures ──────────────────────────────────────────────

    Usuario userA;
    Usuario userB;
    Finca fincaA;
    Finca fincaB;
    Lote loteA;
    Lote loteB;
    Animal animalA;
    Animal animalB;

    @Mock
    FincaRepository fincaRepository;
    @Mock
    LoteRepository loteRepository;
    @Mock
    AnimalRepository animalRepository;
    @Mock
    ProduccionRepository produccionRepository;
    @Mock
    VacunacionRepository vacunacionRepository;
    @Mock
    VacunaRepository vacunaRepository;
    @Mock
    TratamientoRepository tratamientoRepository;
    @Mock
    MedicamentoRepository medicamentoRepository;
    @Mock
    EventoRepository eventoRepository;
    @Mock
    AlimentacionRepository alimentacionRepository;
    @Mock
    AlimentoRepository alimentoRepository;
    @Mock
    RazaRepository razaRepository;
    @Mock
    CategoriaRepository categoriaRepository;

    @InjectMocks
    FincaService fincaService;
    @InjectMocks
    LoteService loteService;
    @InjectMocks
    AnimalService animalService;
    @InjectMocks
    ProduccionService produccionService;
    @InjectMocks
    VacunacionService vacunacionService;
    @InjectMocks
    TratamientoService tratamientoService;
    @InjectMocks
    EventoService eventoService;
    @InjectMocks
    AlimentacionService alimentacionService;

    @BeforeEach
    void setUp() {
        userA = createUser("userA@example.com", "User A");
        userB = createUser("userB@example.com", "User B");

        fincaA = createFinca(1, "Finca A", userA);
        fincaB = createFinca(2, "Finca B", userB);

        loteA = createLote(10, "Lote A", fincaA);
        loteB = createLote(20, "Lote B", fincaB);

        animalA = createAnimal(100, "Animal A", fincaA);
        animalB = createAnimal(200, "Animal B", fincaB);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ── Helper methods ────────────────────────────────────────────────────

    private static Usuario createUser(String email, String nombre) {
        Usuario u = new Usuario();
        u.setId(UUID.randomUUID());
        u.setNombre(nombre);
        u.setEmail(email);
        u.setPassword("encoded");
        Role role = new Role(1, "USER");
        u.setRole(role);
        return u;
    }

    private static Finca createFinca(Integer id, String nombre, Usuario propietario) {
        Finca f = new Finca();
        f.setId(id);
        f.setNombre(nombre);
        f.setPropietario(propietario);
        return f;
    }

    private static Lote createLote(Integer id, String nombre, Finca finca) {
        Lote l = new Lote();
        l.setId(id);
        l.setNombre(nombre);
        l.setFinca(finca);
        return l;
    }

    private static Animal createAnimal(Integer id, String nombre, Finca finca) {
        Animal a = new Animal();
        a.setId(id);
        a.setNombre(nombre);
        a.setFinca(finca);
        a.setSexo("M");
        a.setIdentificadorArete("AR-" + id);
        a.setEstado("Activo");
        return a;
    }

    /** Authenticate as the given user by setting SecurityContextHolder. */
    private void authenticateAs(Usuario user) {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    // ══════════════════════════════════════════════════════════════════════
    //  FINCA SERVICE — tenant isolation tests
    // ══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("FincaService tenant isolation")
    class FincaServiceIsolation {

        @Test
        @DisplayName("findAll returns only current user's fincas")
        void findAll_returnsOnlyOwnFincas() {
            authenticateAs(userA);
            when(fincaRepository.findByPropietario(userA)).thenReturn(List.of(fincaA));

            List<Finca> result = fincaService.findAll();

            assertEquals(1, result.size());
            assertEquals("Finca A", result.get(0).getNombre());
            verify(fincaRepository).findByPropietario(userA);
        }

        @Test
        @DisplayName("findById returns empty for another user's finca")
        void findById_otherUserFinca_returnsEmpty() {
            authenticateAs(userA);
            when(fincaRepository.findByIdAndPropietario(fincaB.getId(), userA)).thenReturn(Optional.empty());

            Optional<Finca> result = fincaService.findById(fincaB.getId());

            assertTrue(result.isEmpty());
            verify(fincaRepository).findByIdAndPropietario(fincaB.getId(), userA);
        }

        @Test
        @DisplayName("findById returns current user's own finca")
        void findById_ownFinca_returnsFinca() {
            authenticateAs(userA);
            when(fincaRepository.findByIdAndPropietario(fincaA.getId(), userA)).thenReturn(Optional.of(fincaA));

            Optional<Finca> result = fincaService.findById(fincaA.getId());

            assertTrue(result.isPresent());
            assertEquals("Finca A", result.get().getNombre());
        }

        @Test
        @DisplayName("update throws for another user's finca")
        void update_otherUserFinca_throws() {
            authenticateAs(userA);
            when(fincaRepository.findByIdAndPropietario(fincaB.getId(), userA)).thenReturn(Optional.empty());

            CreateFincaRequest req = new CreateFincaRequest();
            req.setNombre("Hacked Finca");

            assertThrows(RuntimeException.class, () -> fincaService.update(fincaB.getId(), req));
        }

        @Test
        @DisplayName("delete throws for another user's finca")
        void delete_otherUserFinca_throws() {
            authenticateAs(userA);
            when(fincaRepository.findByIdAndPropietario(fincaB.getId(), userA)).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> fincaService.delete(fincaB.getId()));
            verify(fincaRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("save sets current user as propietario")
        void save_setsAuthenticatedUserAsOwner() {
            authenticateAs(userA);
            CreateFincaRequest req = new CreateFincaRequest();
            req.setNombre("Nueva Finca");
            req.setUbicacion("Campo");

            Finca saved = createFinca(3, "Nueva Finca", userA);
            when(fincaRepository.save(any())).thenReturn(saved);

            Finca result = fincaService.save(req);

            assertEquals("Nueva Finca", result.getNombre());
            verify(fincaRepository).save(argThat(f -> f.getPropietario().equals(userA)));
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    //  LOTE SERVICE — tenant isolation tests
    // ══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("LoteService tenant isolation")
    class LoteServiceIsolation {

        @Test
        @DisplayName("findAll returns only lotes in current user's fincas")
        void findAll_returnsOnlyOwnLotes() {
            authenticateAs(userA);
            when(fincaRepository.findByPropietario(userA)).thenReturn(List.of(fincaA));
            when(loteRepository.findByFincaIdIn(List.of(fincaA.getId()))).thenReturn(List.of(loteA));

            List<Lote> result = loteService.findAll();

            assertEquals(1, result.size());
            assertEquals("Lote A", result.get(0).getNombre());
        }

        @Test
        @DisplayName("findById returns empty for lote in another user's finca")
        void findById_otherUserLote_returnsEmpty() {
            authenticateAs(userA);
            when(fincaRepository.findByPropietario(userA)).thenReturn(List.of(fincaA));
            when(loteRepository.findByIdAndFincaIdIn(loteB.getId(), List.of(fincaA.getId()))).thenReturn(Optional.empty());

            Optional<Lote> result = loteService.findById(loteB.getId());

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("update throws for lote in another user's finca")
        void update_otherUserLote_throws() {
            authenticateAs(userA);
            when(fincaRepository.findByPropietario(userA)).thenReturn(List.of(fincaA));
            when(loteRepository.findByIdAndFincaIdIn(loteB.getId(), List.of(fincaA.getId()))).thenReturn(Optional.empty());

            CreateLoteRequest req = new CreateLoteRequest();
            req.setNombre("Hacked Lote");

            assertThrows(RuntimeException.class, () -> loteService.update(loteB.getId(), req));
        }

        @Test
        @DisplayName("delete throws for lote in another user's finca")
        void delete_otherUserLote_throws() {
            authenticateAs(userA);
            when(fincaRepository.findByPropietario(userA)).thenReturn(List.of(fincaA));
            when(loteRepository.findByIdAndFincaIdIn(loteB.getId(), List.of(fincaA.getId()))).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> loteService.delete(loteB.getId()));
            verify(loteRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("save with another user's fincaId throws")
        void save_otherUserFincaId_throws() {
            authenticateAs(userA);
            // save() -> fromRequest() only calls findByIdAndPropietario, not findByPropietario
            when(fincaRepository.findByIdAndPropietario(fincaB.getId(), userA)).thenReturn(Optional.empty());

            CreateLoteRequest req = new CreateLoteRequest();
            req.setNombre("Lote Malicioso");
            req.setFincaId(fincaB.getId());

            assertThrows(RuntimeException.class, () -> loteService.save(req));
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    //  ANIMAL SERVICE — tenant isolation tests
    // ══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("AnimalService tenant isolation")
    class AnimalServiceIsolation {

        @Test
        @DisplayName("findAll returns only animals in current user's fincas")
        void findAll_returnsOnlyOwnAnimals() {
            authenticateAs(userA);
            when(fincaRepository.findByPropietario(userA)).thenReturn(List.of(fincaA));
            when(animalRepository.findByFincaIdIn(List.of(fincaA.getId()))).thenReturn(List.of(animalA));

            List<Animal> result = animalService.findAll();

            assertEquals(1, result.size());
            assertEquals("Animal A", result.get(0).getNombre());
        }

        @Test
        @DisplayName("findById returns empty for animal in another user's finca")
        void findById_otherUserAnimal_returnsEmpty() {
            authenticateAs(userA);
            when(fincaRepository.findByPropietario(userA)).thenReturn(List.of(fincaA));
            when(animalRepository.findByIdAndFincaIdIn(animalB.getId(), List.of(fincaA.getId()))).thenReturn(Optional.empty());

            Optional<Animal> result = animalService.findById(animalB.getId());

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("update throws for animal in another user's finca")
        void update_otherUserAnimal_throws() {
            authenticateAs(userA);
            when(fincaRepository.findByPropietario(userA)).thenReturn(List.of(fincaA));
            when(animalRepository.findByIdAndFincaIdIn(animalB.getId(), List.of(fincaA.getId()))).thenReturn(Optional.empty());

            CreateAnimalRequest req = new CreateAnimalRequest();
            req.setNombre("Hacked Animal");
            req.setSexo("M");
            req.setIdentificadorArete("XXX");
            req.setEstado("Activo");

            assertThrows(RuntimeException.class, () -> animalService.update(animalB.getId(), req));
        }

        @Test
        @DisplayName("delete throws for animal in another user's finca")
        void delete_otherUserAnimal_throws() {
            authenticateAs(userA);
            when(fincaRepository.findByPropietario(userA)).thenReturn(List.of(fincaA));
            when(animalRepository.findByIdAndFincaIdIn(animalB.getId(), List.of(fincaA.getId()))).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> animalService.delete(animalB.getId()));
            verify(animalRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("save with another user's fincaId throws")
        void save_otherUserFincaId_throws() {
            authenticateAs(userA);
            // save() -> fromRequest() calls findByIdAndPropietario for fincaId lookup
            // razaId/categoriaId/loteId are null in the request, so those repos are never called
            when(fincaRepository.findByIdAndPropietario(fincaB.getId(), userA)).thenReturn(Optional.empty());

            CreateAnimalRequest req = new CreateAnimalRequest();
            req.setNombre("Animal Malicioso");
            req.setSexo("M");
            req.setIdentificadorArete("XXX");
            req.setEstado("Activo");
            req.setFincaId(fincaB.getId());

            assertThrows(RuntimeException.class, () -> animalService.save(req));
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    //  PRODUCCION SERVICE — tenant isolation tests
    // ══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("ProduccionService tenant isolation")
    class ProduccionServiceIsolation {

        @Test
        @DisplayName("save with another user's animalId throws")
        void save_otherUserAnimal_throws() {
            authenticateAs(userA);
            when(fincaRepository.findByPropietario(userA)).thenReturn(List.of(fincaA));
            when(animalRepository.findByIdAndFincaIdIn(animalB.getId(), List.of(fincaA.getId()))).thenReturn(Optional.empty());

            CreateProduccionRequest req = new CreateProduccionRequest();
            req.setAnimalId(animalB.getId());
            req.setLitros(BigDecimal.valueOf(10.5));
            req.setTurno("Mañana");
            req.setFecha(LocalDate.now());

            assertThrows(EntityNotFoundException.class, () -> produccionService.create(req));
        }

        @Test
        @DisplayName("findByAnimalId for another user's animal throws")
        void findByAnimalId_otherUserAnimal_throws() {
            authenticateAs(userA);
            when(fincaRepository.findByPropietario(userA)).thenReturn(List.of(fincaA));
            when(animalRepository.findByIdAndFincaIdIn(animalB.getId(), List.of(fincaA.getId()))).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> produccionService.findByAnimalId(animalB.getId()));
        }

        @Test
        @DisplayName("delete for record belonging to another user's animal throws")
        void delete_otherUserRecord_throws() {
            authenticateAs(userA);
            Produccion prodB = new Produccion();
            prodB.setId(999);
            prodB.setAnimal(animalB); // animalB belongs to User B

            when(produccionRepository.findById(999)).thenReturn(Optional.of(prodB));
            when(fincaRepository.findByPropietario(userA)).thenReturn(List.of(fincaA));
            when(animalRepository.findByIdAndFincaIdIn(animalB.getId(), List.of(fincaA.getId()))).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> produccionService.delete(999));
            verify(produccionRepository, never()).deleteById(any());
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    //  VACUNACION SERVICE — tenant isolation tests
    // ══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("VacunacionService tenant isolation")
    class VacunacionServiceIsolation {

        @Test
        @DisplayName("save with another user's animalId throws")
        void save_otherUserAnimal_throws() {
            authenticateAs(userA);
            when(fincaRepository.findByPropietario(userA)).thenReturn(List.of(fincaA));
            // getAuthorizedAnimal() is called FIRST - ownership check happens before vacuna lookup
            when(animalRepository.findByIdAndFincaIdIn(animalB.getId(), List.of(fincaA.getId()))).thenReturn(Optional.empty());

            CreateVacunacionRequest req = new CreateVacunacionRequest();
            req.setAnimalId(animalB.getId());
            req.setVacunaId(1);
            req.setFecha(LocalDate.now());

            assertThrows(EntityNotFoundException.class, () -> vacunacionService.save(req));
        }

        @Test
        @DisplayName("delete for record belonging to another user's animal throws")
        void delete_otherUserRecord_throws() {
            authenticateAs(userA);
            Vacunacion vacB = new Vacunacion();
            vacB.setId(888);
            vacB.setAnimal(animalB);

            when(vacunacionRepository.findById(888)).thenReturn(Optional.of(vacB));
            when(fincaRepository.findByPropietario(userA)).thenReturn(List.of(fincaA));
            when(animalRepository.findByIdAndFincaIdIn(animalB.getId(), List.of(fincaA.getId()))).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> vacunacionService.delete(888));
            verify(vacunacionRepository, never()).deleteById(any());
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    //  TRATAMIENTO SERVICE — tenant isolation tests
    // ══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("TratamientoService tenant isolation")
    class TratamientoServiceIsolation {

        @Test
        @DisplayName("save with another user's animalId throws")
        void save_otherUserAnimal_throws() {
            authenticateAs(userA);
            when(fincaRepository.findByPropietario(userA)).thenReturn(List.of(fincaA));
            // getAuthorizedAnimal() is called FIRST - ownership check happens before medicamento lookup
            when(animalRepository.findByIdAndFincaIdIn(animalB.getId(), List.of(fincaA.getId()))).thenReturn(Optional.empty());

            CreateTratamientoRequest req = new CreateTratamientoRequest();
            req.setAnimalId(animalB.getId());
            req.setMedicamentoId(1);
            req.setDosis("10ml");
            req.setFechaInicio(LocalDate.now());

            assertThrows(EntityNotFoundException.class, () -> tratamientoService.save(req));
        }

        @Test
        @DisplayName("delete for record belonging to another user's animal throws")
        void delete_otherUserRecord_throws() {
            authenticateAs(userA);
            Tratamiento trB = new Tratamiento();
            trB.setId(777);
            trB.setAnimal(animalB);

            when(tratamientoRepository.findById(777)).thenReturn(Optional.of(trB));
            when(fincaRepository.findByPropietario(userA)).thenReturn(List.of(fincaA));
            when(animalRepository.findByIdAndFincaIdIn(animalB.getId(), List.of(fincaA.getId()))).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> tratamientoService.delete(777));
            verify(tratamientoRepository, never()).deleteById(any());
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    //  EVENTO SERVICE — tenant isolation tests
    // ══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("EventoService tenant isolation")
    class EventoServiceIsolation {

        @Test
        @DisplayName("save with another user's animalId throws")
        void save_otherUserAnimal_throws() {
            authenticateAs(userA);
            when(fincaRepository.findByPropietario(userA)).thenReturn(List.of(fincaA));
            when(animalRepository.findByIdAndFincaIdIn(animalB.getId(), List.of(fincaA.getId()))).thenReturn(Optional.empty());

            CreateEventoRequest req = new CreateEventoRequest();
            req.setAnimalId(animalB.getId());
            req.setTipo("Salud");

            assertThrows(EntityNotFoundException.class, () -> eventoService.save(req));
        }

        @Test
        @DisplayName("delete for record belonging to another user's animal throws")
        void delete_otherUserRecord_throws() {
            authenticateAs(userA);
            Evento evB = new Evento();
            evB.setId(666);
            evB.setAnimal(animalB);

            when(eventoRepository.findById(666)).thenReturn(Optional.of(evB));
            when(fincaRepository.findByPropietario(userA)).thenReturn(List.of(fincaA));
            when(animalRepository.findByIdAndFincaIdIn(animalB.getId(), List.of(fincaA.getId()))).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> eventoService.delete(666));
            verify(eventoRepository, never()).deleteById(any());
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    //  ALIMENTACION SERVICE — tenant isolation tests
    // ══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("AlimentacionService tenant isolation")
    class AlimentacionServiceIsolation {

        @Test
        @DisplayName("save with another user's animalId throws")
        void save_otherUserAnimal_throws() {
            authenticateAs(userA);
            when(fincaRepository.findByPropietario(userA)).thenReturn(List.of(fincaA));
            // getAuthorizedAnimal() is called FIRST - ownership check happens before alimento lookup
            when(animalRepository.findByIdAndFincaIdIn(animalB.getId(), List.of(fincaA.getId()))).thenReturn(Optional.empty());

            CreateAlimentacionRequest req = new CreateAlimentacionRequest();
            req.setAnimalId(animalB.getId());
            req.setAlimentoId(1);
            req.setCantidad(BigDecimal.valueOf(5.0));
            req.setFecha(LocalDate.now());

            assertThrows(EntityNotFoundException.class, () -> alimentacionService.save(req));
        }

        @Test
        @DisplayName("delete for record belonging to another user's animal throws")
        void delete_otherUserRecord_throws() {
            authenticateAs(userA);
            Alimentacion aliB = new Alimentacion();
            aliB.setId(555);
            aliB.setAnimal(animalB);

            when(alimentacionRepository.findById(555)).thenReturn(Optional.of(aliB));
            when(fincaRepository.findByPropietario(userA)).thenReturn(List.of(fincaA));
            when(animalRepository.findByIdAndFincaIdIn(animalB.getId(), List.of(fincaA.getId()))).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> alimentacionService.delete(555));
            verify(alimentacionRepository, never()).deleteById(any());
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    //  POSITIVE TESTS — User A CAN access their own resources
    // ══════════════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Positive: own resource access (User A can access User A's resources)")
    class PositiveAccess {

        @Test
        @DisplayName("FincaService - own finca full CRUD works")
        void fincaService_ownResource_works() {
            authenticateAs(userA);
            when(fincaRepository.findByPropietario(userA)).thenReturn(List.of(fincaA));
            when(fincaRepository.findByIdAndPropietario(fincaA.getId(), userA)).thenReturn(Optional.of(fincaA));

            // findAll
            assertEquals(1, fincaService.findAll().size());
            // findById
            assertTrue(fincaService.findById(fincaA.getId()).isPresent());
        }

        @Test
        @DisplayName("LoteService - own lote full CRUD works")
        void loteService_ownResource_works() {
            authenticateAs(userA);
            when(fincaRepository.findByPropietario(userA)).thenReturn(List.of(fincaA));
            when(loteRepository.findByFincaIdIn(List.of(fincaA.getId()))).thenReturn(List.of(loteA));
            when(loteRepository.findByIdAndFincaIdIn(loteA.getId(), List.of(fincaA.getId()))).thenReturn(Optional.of(loteA));

            // findAll
            assertEquals(1, loteService.findAll().size());
            // findById
            assertTrue(loteService.findById(loteA.getId()).isPresent());
        }

        @Test
        @DisplayName("AnimalService - own animal full CRUD works")
        void animalService_ownResource_works() {
            authenticateAs(userA);
            when(fincaRepository.findByPropietario(userA)).thenReturn(List.of(fincaA));
            when(animalRepository.findByFincaIdIn(List.of(fincaA.getId()))).thenReturn(List.of(animalA));
            when(animalRepository.findByIdAndFincaIdIn(animalA.getId(), List.of(fincaA.getId()))).thenReturn(Optional.of(animalA));

            // findAll
            assertEquals(1, animalService.findAll().size());
            // findById
            assertTrue(animalService.findById(animalA.getId()).isPresent());
        }

        @Test
        @DisplayName("ProduccionService - own animal findByAnimalId works")
        void produccionService_ownAnimal_works() {
            authenticateAs(userA);
            when(fincaRepository.findByPropietario(userA)).thenReturn(List.of(fincaA));
            when(animalRepository.findByIdAndFincaIdIn(animalA.getId(), List.of(fincaA.getId()))).thenReturn(Optional.of(animalA));
            when(produccionRepository.findByAnimalId(animalA.getId())).thenReturn(List.of());

            List<Produccion> result = produccionService.findByAnimalId(animalA.getId());
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }
}
