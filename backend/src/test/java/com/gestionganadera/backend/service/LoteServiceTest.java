package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateLoteRequest;
import com.gestionganadera.backend.model.Finca;
import com.gestionganadera.backend.model.Lote;
import com.gestionganadera.backend.model.Role;
import com.gestionganadera.backend.model.Usuario;
import com.gestionganadera.backend.repository.FincaRepository;
import com.gestionganadera.backend.repository.LoteRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoteServiceTest {

    @Mock
    private LoteRepository loteRepository;

    @Mock
    private FincaRepository fincaRepository;

    @InjectMocks
    private LoteService loteService;

    private Usuario currentUser;
    private Finca finca;
    private Lote lote;

    @BeforeEach
    void setUp() {
        currentUser = createUser("user@example.com", "Test User");
        finca = createFinca(1, "Mi Finca", currentUser);
        lote = createLote(10, "Lote A", finca);
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
        u.setRole(new Role(1, "USER"));
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
        l.setHectareas(BigDecimal.TEN);
        l.setCapacidadMaxima(50);
        l.setEstado("Activo");
        return l;
    }

    private void authenticateAs(Usuario user) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
    }

    @Test
    void findAll_returnsLotesInUserFincas() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(loteRepository.findByFincaIdIn(List.of(1))).thenReturn(List.of(lote));

        List<Lote> result = loteService.findAll();

        assertEquals(1, result.size());
        assertEquals("Lote A", result.get(0).getNombre());
    }

    @Test
    void findById_ownLote_returnsLote() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(loteRepository.findByIdAndFincaIdIn(10, List.of(1))).thenReturn(Optional.of(lote));

        Optional<Lote> result = loteService.findById(10);

        assertTrue(result.isPresent());
        assertEquals("Lote A", result.get().getNombre());
    }

    @Test
    void findById_nonExistent_returnsEmpty() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(loteRepository.findByIdAndFincaIdIn(999, List.of(1))).thenReturn(Optional.empty());

        assertTrue(loteService.findById(999).isEmpty());
    }

    @Test
    void save_withFincaId_savesLote() {
        when(fincaRepository.findByIdAndPropietario(1, currentUser)).thenReturn(Optional.of(finca));

        CreateLoteRequest request = new CreateLoteRequest();
        request.setNombre("Nuevo Lote");
        request.setFincaId(1);
        request.setHectareas(BigDecimal.valueOf(15.5));
        request.setCapacidadMaxima(30);
        request.setEstado("Activo");
        request.setTipoPasto("Ryegrass");

        Lote saved = createLote(11, "Nuevo Lote", finca);
        saved.setHectareas(BigDecimal.valueOf(15.5));
        saved.setCapacidadMaxima(30);
        saved.setTipoPasto("Ryegrass");

        when(loteRepository.save(any(Lote.class))).thenReturn(saved);

        Lote result = loteService.save(request);

        assertEquals("Nuevo Lote", result.getNombre());
        assertEquals(15.5, result.getHectareas().doubleValue(), 0.01);
        assertEquals(30, result.getCapacidadMaxima());
        assertEquals("Ryegrass", result.getTipoPasto());
        verify(loteRepository).save(any(Lote.class));
    }

    @Test
    void save_withoutFincaId_savesLoteWithoutFinca() {
        CreateLoteRequest request = new CreateLoteRequest();
        request.setNombre("Lote Sin Finca");

        Lote saved = new Lote();
        saved.setId(12);
        saved.setNombre("Lote Sin Finca");

        when(loteRepository.save(any(Lote.class))).thenReturn(saved);

        Lote result = loteService.save(request);

        assertEquals("Lote Sin Finca", result.getNombre());
        assertNull(result.getFinca());
    }

    @Test
    void update_existingLote_updatesFields() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(loteRepository.findByIdAndFincaIdIn(10, List.of(1))).thenReturn(Optional.of(lote));

        CreateLoteRequest request = new CreateLoteRequest();
        request.setNombre("Lote Actualizado");
        request.setHectareas(BigDecimal.valueOf(20));
        request.setCapacidadMaxima(100);

        Lote updated = createLote(10, "Lote Actualizado", finca);
        updated.setHectareas(BigDecimal.valueOf(20));
        updated.setCapacidadMaxima(100);
        when(loteRepository.save(any(Lote.class))).thenReturn(updated);

        Lote result = loteService.update(10, request);

        assertEquals("Lote Actualizado", result.getNombre());
        assertEquals(20, result.getHectareas().doubleValue(), 0.01);
        assertEquals(100, result.getCapacidadMaxima());
    }

    @Test
    void update_nonExistent_throws() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(loteRepository.findByIdAndFincaIdIn(999, List.of(1))).thenReturn(Optional.empty());

        CreateLoteRequest request = new CreateLoteRequest();
        request.setNombre("Lote");

        assertThrows(RuntimeException.class, () -> loteService.update(999, request));
    }

    @Test
    void delete_existingLote_deletes() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(loteRepository.findByIdAndFincaIdIn(10, List.of(1))).thenReturn(Optional.of(lote));

        loteService.delete(10);

        verify(loteRepository).deleteById(10);
    }

    @Test
    void delete_nonExistent_throws() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(loteRepository.findByIdAndFincaIdIn(999, List.of(1))).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> loteService.delete(999));
        verify(loteRepository, never()).deleteById(any());
    }

    @Test
    void update_withFincaId_updatesFinca() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(loteRepository.findByIdAndFincaIdIn(10, List.of(1))).thenReturn(Optional.of(lote));
        when(fincaRepository.findByIdAndPropietario(1, currentUser)).thenReturn(Optional.of(finca));

        CreateLoteRequest request = new CreateLoteRequest();
        request.setNombre("Lote con Finca");
        request.setHectareas(BigDecimal.valueOf(25));
        request.setCapacidadMaxima(75);
        request.setTipoPasto("Bermuda");
        request.setEstado("Activo");
        request.setFincaId(1);

        Lote updated = createLote(10, "Lote con Finca", finca);
        updated.setHectareas(BigDecimal.valueOf(25));
        updated.setCapacidadMaxima(75);
        updated.setTipoPasto("Bermuda");

        when(loteRepository.save(any(Lote.class))).thenReturn(updated);

        Lote result = loteService.update(10, request);

        assertEquals("Lote con Finca", result.getNombre());
        assertNotNull(result.getFinca());
        assertEquals(25, result.getHectareas().doubleValue(), 0.01);
        assertEquals("Bermuda", result.getTipoPasto());
        verify(fincaRepository).findByIdAndPropietario(1, currentUser);
        verify(loteRepository).save(any(Lote.class));
    }

    @Test
    void update_nonExistentFincaInUpdate_throws() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));
        when(loteRepository.findByIdAndFincaIdIn(10, List.of(1))).thenReturn(Optional.of(lote));
        when(fincaRepository.findByIdAndPropietario(999, currentUser)).thenReturn(Optional.empty());

        CreateLoteRequest request = new CreateLoteRequest();
        request.setNombre("Fails");
        request.setFincaId(999);

        assertThrows(RuntimeException.class, () -> loteService.update(10, request));
        verify(loteRepository, never()).save(any());
    }

    @Test
    void save_nonExistentFinca_throws() {
        when(fincaRepository.findByIdAndPropietario(999, currentUser)).thenReturn(Optional.empty());

        CreateLoteRequest request = new CreateLoteRequest();
        request.setNombre("Bad Lote");
        request.setFincaId(999);

        assertThrows(RuntimeException.class, () -> loteService.save(request));
        verify(loteRepository, never()).save(any());
    }
}
