package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreatePartoRequest;
import com.gestionganadera.backend.dto.PartoDTO;
import com.gestionganadera.backend.dto.ReproduccionDTO;
import com.gestionganadera.backend.model.Animal;
import com.gestionganadera.backend.model.Parto;
import com.gestionganadera.backend.model.Reproduccion;
import com.gestionganadera.backend.model.Usuario;
import com.gestionganadera.backend.repository.PartoRepository;
import com.gestionganadera.backend.repository.ReproduccionRepository;
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
class PartoServiceTest {

    @Mock
    private PartoRepository partoRepository;
    @Mock
    private ReproduccionService reproduccionService;
    @Mock
    private ReproduccionRepository reproduccionRepository;

    @InjectMocks
    private PartoService partoService;

    private Usuario currentUser;
    private Animal vaca;
    private Reproduccion reproduccion;
    private Parto parto;

    @BeforeEach
    void setUp() {
        currentUser = createUser("user@example.com", "Test User");
        vaca = createAnimal(1, "Vaca Lechera");
        reproduccion = createReproduccion(10, vaca);
        parto = createParto(100, reproduccion, LocalDate.of(2026, 5, 28), 1, "Normal");

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

    private static Animal createAnimal(Integer id, String nombre) {
        Animal a = new Animal();
        a.setId(id);
        a.setNombre(nombre);
        a.setIdentificadorArete("AR-" + id);
        return a;
    }

    private static Reproduccion createReproduccion(Integer id, Animal vaca) {
        Reproduccion r = new Reproduccion();
        r.setId(id);
        r.setVaca(vaca);
        r.setFechaMonta(LocalDate.of(2026, 5, 1));
        r.setTipo("Natural");
        return r;
    }

    private static Parto createParto(Integer id, Reproduccion r, LocalDate fecha, Integer crias, String obs) {
        Parto p = new Parto();
        p.setId(id);
        p.setReproduccion(r);
        p.setFechaParto(fecha);
        p.setCantidadCrias(crias);
        p.setObservaciones(obs);
        return p;
    }

    private void authenticateAs(Usuario user) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
    }

    // --- findAll tests ---

    @Test
    void findAll_returnsFilteredList() {
        when(partoRepository.findAll()).thenReturn(List.of(parto));
        when(reproduccionService.findById(10)).thenReturn(new ReproduccionDTO());

        List<PartoDTO> result = partoService.findAll();

        assertEquals(1, result.size());
        assertEquals(100, result.get(0).getId());
        assertEquals("Vaca Lechera", result.get(0).getVacaNombre());
    }

    @Test
    void findAll_filtersOutUnauthorizedPartos() {
        Parto partoSinAcceso = createParto(101, reproduccion, LocalDate.of(2026, 6, 1), 2, "Otro");
        when(partoRepository.findAll()).thenReturn(List.of(parto, partoSinAcceso));
        // First call succeeds, second throws
        when(reproduccionService.findById(10))
                .thenReturn(new ReproduccionDTO())
                .thenThrow(new SecurityException("Acceso denegado"));

        List<PartoDTO> result = partoService.findAll();

        assertEquals(1, result.size());
        assertEquals(100, result.get(0).getId());
    }

    @Test
    void findAll_emptyList_whenNoPartos() {
        when(partoRepository.findAll()).thenReturn(List.of());

        List<PartoDTO> result = partoService.findAll();

        assertTrue(result.isEmpty());
    }

    // --- findById tests ---

    @Test
    void findById_returnsDTO() {
        when(partoRepository.findById(100)).thenReturn(Optional.of(parto));
        when(reproduccionService.findById(10)).thenReturn(new ReproduccionDTO());

        PartoDTO result = partoService.findById(100);

        assertEquals(100, result.getId());
        assertEquals("Vaca Lechera", result.getVacaNombre());
        assertEquals(LocalDate.of(2026, 5, 28), result.getFechaParto());
        assertEquals(1, result.getCantidadCrias());
    }

    @Test
    void findById_nonExistent_throws() {
        when(partoRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> partoService.findById(999));
    }

    @Test
    void findById_unauthorizedReproduccion_throws() {
        when(partoRepository.findById(100)).thenReturn(Optional.of(parto));
        when(reproduccionService.findById(10)).thenThrow(new SecurityException("Acceso denegado"));

        assertThrows(SecurityException.class, () -> partoService.findById(100));
    }

    // --- findByReproduccionId tests ---

    @Test
    void findByReproduccionId_returnsList() {
        when(reproduccionService.findById(10)).thenReturn(new ReproduccionDTO());
        when(partoRepository.findByReproduccionId(10)).thenReturn(List.of(parto));

        List<PartoDTO> result = partoService.findByReproduccionId(10);

        assertEquals(1, result.size());
        assertEquals(100, result.get(0).getId());
    }

    @Test
    void findByReproduccionId_unauthorized_throws() {
        when(reproduccionService.findById(99)).thenThrow(new SecurityException("Acceso denegado"));

        assertThrows(SecurityException.class, () -> partoService.findByReproduccionId(99));
        verify(partoRepository, never()).findByReproduccionId(anyInt());
    }

    // --- create tests ---

    @Test
    void create_createsParto() {
        CreatePartoRequest request = new CreatePartoRequest();
        request.setReproduccionId(10);
        request.setFechaParto(LocalDate.of(2026, 6, 15));
        request.setCantidadCrias(2);
        request.setObservaciones("Gemelos");

        when(reproduccionRepository.findById(10)).thenReturn(Optional.of(reproduccion));
        when(partoRepository.save(any(Parto.class))).thenAnswer(invocation -> {
            Parto saved = invocation.getArgument(0);
            saved.setId(200);
            return saved;
        });

        PartoDTO result = partoService.create(request);

        assertEquals("Vaca Lechera", result.getVacaNombre());
        assertEquals(LocalDate.of(2026, 6, 15), result.getFechaParto());
        assertEquals(2, result.getCantidadCrias());
        assertEquals("Gemelos", result.getObservaciones());
        verify(partoRepository).save(any(Parto.class));
    }

    @Test
    void create_reproduccionNotFound_throws() {
        CreatePartoRequest request = new CreatePartoRequest();
        request.setReproduccionId(999);
        request.setFechaParto(LocalDate.of(2026, 6, 15));
        request.setCantidadCrias(1);

        when(reproduccionRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> partoService.create(request));
        verify(partoRepository, never()).save(any());
    }

    // --- update tests ---

    @Test
    void update_updatesFields() {
        when(partoRepository.findById(100)).thenReturn(Optional.of(parto));
        when(reproduccionService.findById(10)).thenReturn(new ReproduccionDTO());

        CreatePartoRequest request = new CreatePartoRequest();
        request.setCantidadCrias(3);
        request.setObservaciones("Triples");

        Parto updatedParto = createParto(100, reproduccion, LocalDate.of(2026, 5, 28), 3, "Triples");
        when(partoRepository.save(any(Parto.class))).thenReturn(updatedParto);

        PartoDTO result = partoService.update(100, request);

        assertEquals(3, result.getCantidadCrias());
        assertEquals("Triples", result.getObservaciones());
        verify(partoRepository).save(any(Parto.class));
    }

    @Test
    void update_nonExistent_throws() {
        when(partoRepository.findById(999)).thenReturn(Optional.empty());

        CreatePartoRequest request = new CreatePartoRequest();
        request.setCantidadCrias(1);

        assertThrows(EntityNotFoundException.class, () -> partoService.update(999, request));
        verify(partoRepository, never()).save(any());
    }

    // --- delete tests ---

    @Test
    void delete_existingParto_deletes() {
        when(partoRepository.findById(100)).thenReturn(Optional.of(parto));
        when(reproduccionService.findById(10)).thenReturn(new ReproduccionDTO());

        partoService.delete(100);

        verify(partoRepository).delete(parto);
    }

    @Test
    void delete_nonExistent_throws() {
        when(partoRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> partoService.delete(999));
        verify(partoRepository, never()).delete(any());
    }

    @Test
    void delete_unauthorized_throws() {
        when(partoRepository.findById(100)).thenReturn(Optional.of(parto));
        when(reproduccionService.findById(10)).thenThrow(new SecurityException("Acceso denegado"));

        assertThrows(SecurityException.class, () -> partoService.delete(100));
        verify(partoRepository, never()).delete(any());
    }
}
