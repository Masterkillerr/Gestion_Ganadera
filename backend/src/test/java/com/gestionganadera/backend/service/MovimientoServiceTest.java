package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateMovimientoRequest;
import com.gestionganadera.backend.dto.MovimientoDTO;
import com.gestionganadera.backend.model.Animal;
import com.gestionganadera.backend.model.Finca;
import com.gestionganadera.backend.model.Lote;
import com.gestionganadera.backend.model.Movimiento;
import com.gestionganadera.backend.model.Usuario;
import com.gestionganadera.backend.repository.AnimalRepository;
import com.gestionganadera.backend.repository.FincaRepository;
import com.gestionganadera.backend.repository.LoteRepository;
import com.gestionganadera.backend.repository.MovimientoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovimientoServiceTest {

    @Mock
    private MovimientoRepository movimientoRepository;
    @Mock
    private FincaRepository fincaRepository;
    @Mock
    private AnimalRepository animalRepository;
    @Mock
    private LoteRepository loteRepository;

    @InjectMocks
    private MovimientoService movimientoService;

    private Usuario currentUser;
    private Finca finca;
    private Lote loteOrigen;
    private Lote loteDestino;
    private Animal animal;
    private Movimiento movimiento;

    @BeforeEach
    void setUp() {
        currentUser = createUser("user@example.com", "Test User");
        finca = createFinca(1, "Mi Finca", currentUser);
        loteOrigen = createLote(10, "Lote Origen", finca);
        loteDestino = createLote(20, "Lote Destino", finca);
        animal = createAnimal(100, "Vaca Test", finca);
        movimiento = createMovimiento(1, animal, loteOrigen, loteDestino);
        authenticateAs(currentUser);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private static Usuario createUser(String email, String nombre) {
        Usuario u = new Usuario();
        u.setId(UUID.randomUUID());
        u.setNombre(nombre);
        u.setEmail(email);
        u.setPassword("encoded");
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
        a.setIdentificadorArete("AR-" + id);
        a.setFinca(finca);
        a.setSexo("H");
        a.setEstado("Activo");
        return a;
    }

    private static Movimiento createMovimiento(Integer id, Animal animal, Lote origen, Lote destino) {
        Movimiento m = new Movimiento();
        m.setId(id);
        m.setAnimal(animal);
        m.setLoteOrigen(origen);
        m.setLoteDestino(destino);
        m.setFecha(LocalDate.of(2026, 5, 28));
        m.setTipoMovimiento("Traslado");
        m.setMotivo("Cambio de alimentacion");
        return m;
    }

    private void authenticateAs(Usuario user) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
    }

    // --- getRecent tests ---

    @Test
    void getRecent_returnsList() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(movimientoRepository.findTop10ByAnimalFincaIdInOrderByFechaDesc(List.of(1)))
                .thenReturn(List.of(movimiento));

        List<MovimientoDTO> result = movimientoService.getRecent();

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals("Vaca Test", result.get(0).getAnimalNombre());
        assertEquals("Traslado", result.get(0).getTipoMovimiento());
    }

    @Test
    void getRecent_empty_whenNoFincas() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of());

        List<MovimientoDTO> result = movimientoService.getRecent();

        assertTrue(result.isEmpty());
    }

    @Test
    void getRecent_empty_whenNoMovimientos() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(movimientoRepository.findTop10ByAnimalFincaIdInOrderByFechaDesc(List.of(1)))
                .thenReturn(List.of());

        List<MovimientoDTO> result = movimientoService.getRecent();

        assertTrue(result.isEmpty());
    }

    // --- findAll tests ---

    @Test
    void findAll_returnsList() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(movimientoRepository.findByAnimalFincaIdInOrderByFechaDesc(List.of(1)))
                .thenReturn(List.of(movimiento));

        List<MovimientoDTO> result = movimientoService.findAll();

        assertEquals(1, result.size());
        assertEquals("Vaca Test", result.get(0).getAnimalNombre());
        assertEquals("Lote Origen", result.get(0).getOrigen());
        assertEquals("Lote Destino", result.get(0).getDestino());
    }

    @Test
    void findAll_empty_whenNoFincas() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of());

        List<MovimientoDTO> result = movimientoService.findAll();

        assertTrue(result.isEmpty());
    }

    // --- findById tests ---

    @Test
    void findById_returnsDTO() {
        when(movimientoRepository.findById(1)).thenReturn(Optional.of(movimiento));
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));

        MovimientoDTO result = movimientoService.findById(1);

        assertEquals(1, result.getId());
        assertEquals("Vaca Test", result.getAnimalNombre());
        assertEquals("Lote Origen", result.getOrigen());
        assertEquals("Lote Destino", result.getDestino());
    }

    @Test
    void findById_nonExistent_throws() {
        when(movimientoRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> movimientoService.findById(999));
    }

    @Test
    void findById_unauthorized_throws() {
        Finca otraFinca = createFinca(99, "Otra Finca", createUser("other@example.com", "Other"));
        Animal otroAnimal = createAnimal(200, "Otro", otraFinca);
        Movimiento otroMov = createMovimiento(2, otroAnimal, loteOrigen, loteDestino);

        when(movimientoRepository.findById(2)).thenReturn(Optional.of(otroMov));
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));

        assertThrows(EntityNotFoundException.class, () -> movimientoService.findById(2));
    }

    // --- create tests ---

    @Test
    void create_createsMovimiento() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(100, List.of(1))).thenReturn(Optional.of(animal));
        when(loteRepository.findById(anyInt())).thenAnswer(invocation -> {
            Integer id = invocation.getArgument(0);
            if (id.equals(20)) return Optional.of(loteDestino);
            if (id.equals(10)) return Optional.of(loteOrigen);
            return Optional.empty();
        });

        CreateMovimientoRequest request = new CreateMovimientoRequest();
        request.setAnimalId(100);
        request.setLoteDestinoId(20);
        request.setLoteOrigenId(10);
        request.setFecha(LocalDate.of(2026, 6, 1));
        request.setTipoMovimiento("Venta");
        request.setMotivo("Venta a otro productor");

        when(movimientoRepository.save(any(Movimiento.class))).thenAnswer(invocation -> {
            Movimiento saved = invocation.getArgument(0);
            saved.setId(50);
            return saved;
        });

        MovimientoDTO result = movimientoService.create(request);

        assertEquals("Vaca Test", result.getAnimalNombre());
        assertEquals("Lote Destino", result.getDestino());
        assertEquals("Lote Origen", result.getOrigen());
        verify(movimientoRepository).save(any(Movimiento.class));
    }

    @Test
    void create_withoutLoteOrigen_creates() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(100, List.of(1))).thenReturn(Optional.of(animal));
        when(loteRepository.findById(20)).thenReturn(Optional.of(loteDestino));

        CreateMovimientoRequest request = new CreateMovimientoRequest();
        request.setAnimalId(100);
        request.setLoteDestinoId(20);
        request.setFecha(LocalDate.of(2026, 6, 1));
        request.setTipoMovimiento("Ingreso");

        Movimiento saved = createMovimiento(50, animal, null, loteDestino);
        when(movimientoRepository.save(any(Movimiento.class))).thenReturn(saved);

        MovimientoDTO result = movimientoService.create(request);

        assertNull(result.getOrigen());
        assertEquals("Lote Destino", result.getDestino());
        verify(movimientoRepository).save(any(Movimiento.class));
    }

    @Test
    void create_unauthorizedAnimal_throws() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(999, List.of(1))).thenReturn(Optional.empty());

        CreateMovimientoRequest request = new CreateMovimientoRequest();
        request.setAnimalId(999);
        request.setLoteDestinoId(20);
        request.setFecha(LocalDate.of(2026, 6, 1));

        assertThrows(EntityNotFoundException.class, () -> movimientoService.create(request));
        verify(movimientoRepository, never()).save(any());
    }

    @Test
    void create_unauthorizedLote_throws() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(100, List.of(1))).thenReturn(Optional.of(animal));
        when(loteRepository.findById(99)).thenReturn(Optional.of(createLote(99, "Ajeno", createFinca(99, "Ajeno", null))));

        CreateMovimientoRequest request = new CreateMovimientoRequest();
        request.setAnimalId(100);
        request.setLoteDestinoId(99);
        request.setFecha(LocalDate.of(2026, 6, 1));

        assertThrows(EntityNotFoundException.class, () -> movimientoService.create(request));
        verify(movimientoRepository, never()).save(any());
    }

    // --- update tests ---

    @Test
    void update_updatesFields() {
        when(movimientoRepository.findById(1)).thenReturn(Optional.of(movimiento));
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(100, List.of(1))).thenReturn(Optional.of(animal));
        when(animalRepository.findByIdAndFincaIdIn(100, List.of(1))).thenReturn(Optional.of(animal));
        when(loteRepository.findById(20)).thenReturn(Optional.of(loteDestino));

        CreateMovimientoRequest request = new CreateMovimientoRequest();
        request.setAnimalId(100);
        request.setLoteDestinoId(20);
        request.setFecha(LocalDate.of(2026, 6, 15));
        request.setTipoMovimiento("Traslado");
        request.setMotivo("Nuevo motivo");

        Movimiento updated = createMovimiento(1, animal, loteOrigen, loteDestino);
        updated.setFecha(LocalDate.of(2026, 6, 15));
        updated.setMotivo("Nuevo motivo");
        when(movimientoRepository.save(any(Movimiento.class))).thenReturn(updated);

        MovimientoDTO result = movimientoService.update(1, request);

        assertEquals("Traslado", result.getTipoMovimiento());
        verify(movimientoRepository).save(any(Movimiento.class));
    }

    @Test
    void update_nonExistent_throws() {
        when(movimientoRepository.findById(999)).thenReturn(Optional.empty());

        CreateMovimientoRequest request = new CreateMovimientoRequest();
        request.setAnimalId(100);
        request.setLoteDestinoId(20);
        request.setFecha(LocalDate.now());

        assertThrows(EntityNotFoundException.class, () -> movimientoService.update(999, request));
        verify(movimientoRepository, never()).save(any());
    }

    // --- delete tests ---

    @Test
    void delete_existing_deletes() {
        when(movimientoRepository.findById(1)).thenReturn(Optional.of(movimiento));
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(100, List.of(1))).thenReturn(Optional.of(animal));

        movimientoService.delete(1);

        verify(movimientoRepository).deleteById(1);
    }

    @Test
    void delete_nonExistent_throws() {
        when(movimientoRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> movimientoService.delete(999));
        verify(movimientoRepository, never()).deleteById(anyInt());
    }

    @Test
    void delete_unauthorized_throws() {
        Finca otraFinca = createFinca(99, "Otra Finca", createUser("other@example.com", "Other"));
        Animal otroAnimal = createAnimal(200, "Otro", otraFinca);
        Movimiento otroMov = createMovimiento(2, otroAnimal, loteOrigen, loteDestino);

        when(movimientoRepository.findById(2)).thenReturn(Optional.of(otroMov));
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(animalRepository.findByIdAndFincaIdIn(200, List.of(1))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> movimientoService.delete(2));
        verify(movimientoRepository, never()).deleteById(anyInt());
    }
}
