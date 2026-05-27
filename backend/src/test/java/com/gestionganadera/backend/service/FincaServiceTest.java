package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateFincaRequest;
import com.gestionganadera.backend.model.Finca;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FincaServiceTest {

    @Mock
    private FincaRepository fincaRepository;

    @InjectMocks
    private FincaService fincaService;

    private Usuario currentUser;
    private Finca finca;

    @BeforeEach
    void setUp() {
        currentUser = createUser("user@example.com", "Test User");
        finca = createFinca(1, "Mi Finca", currentUser);
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

    private void authenticateAs(Usuario user) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
    }

    @Test
    void findAll_returnsOnlyOwnFincas() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of(finca));

        List<Finca> result = fincaService.findAll();

        assertEquals(1, result.size());
        assertEquals("Mi Finca", result.get(0).getNombre());
        verify(fincaRepository).findByPropietario(currentUser);
    }

    @Test
    void findAll_returnsEmptyList_whenNoFincas() {
        when(fincaRepository.findByPropietario(currentUser)).thenReturn(List.of());

        assertTrue(fincaService.findAll().isEmpty());
    }

    @Test
    void findById_ownFinca_returnsFinca() {
        when(fincaRepository.findByIdAndPropietario(1, currentUser)).thenReturn(Optional.of(finca));

        Optional<Finca> result = fincaService.findById(1);

        assertTrue(result.isPresent());
        assertEquals("Mi Finca", result.get().getNombre());
    }

    @Test
    void findById_nonExistent_returnsEmpty() {
        when(fincaRepository.findByIdAndPropietario(999, currentUser)).thenReturn(Optional.empty());

        assertTrue(fincaService.findById(999).isEmpty());
    }

    @Test
    void save_createsFincaWithCurrentUserAsOwner() {
        CreateFincaRequest request = new CreateFincaRequest();
        request.setNombre("Nueva Finca");
        request.setUbicacion("Campo Verde");

        Finca saved = createFinca(2, "Nueva Finca", currentUser);
        saved.setUbicacion("Campo Verde");

        when(fincaRepository.save(any(Finca.class))).thenReturn(saved);

        Finca result = fincaService.save(request);

        assertEquals("Nueva Finca", result.getNombre());
        assertEquals("Campo Verde", result.getUbicacion());
        verify(fincaRepository).save(argThat(f ->
                f.getNombre().equals("Nueva Finca") &&
                f.getUbicacion().equals("Campo Verde") &&
                f.getPropietario().equals(currentUser)));
    }

    @Test
    void save_withoutUbicacion_savesSuccessfully() {
        CreateFincaRequest request = new CreateFincaRequest();
        request.setNombre("Minimal");

        Finca saved = createFinca(3, "Minimal", currentUser);
        when(fincaRepository.save(any(Finca.class))).thenReturn(saved);

        Finca result = fincaService.save(request);

        assertEquals("Minimal", result.getNombre());
    }

    @Test
    void update_existingFinca_updatesFields() {
        when(fincaRepository.findByIdAndPropietario(1, currentUser)).thenReturn(Optional.of(finca));

        CreateFincaRequest request = new CreateFincaRequest();
        request.setNombre("Finca Actualizada");
        request.setUbicacion("Nueva Ubicación");

        Finca updated = createFinca(1, "Finca Actualizada", currentUser);
        updated.setUbicacion("Nueva Ubicación");
        when(fincaRepository.save(any(Finca.class))).thenReturn(updated);

        Finca result = fincaService.update(1, request);

        assertEquals("Finca Actualizada", result.getNombre());
        assertEquals("Nueva Ubicación", result.getUbicacion());
    }

    @Test
    void update_nonExistent_throws() {
        when(fincaRepository.findByIdAndPropietario(999, currentUser)).thenReturn(Optional.empty());

        CreateFincaRequest request = new CreateFincaRequest();
        request.setNombre("Finca");

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> fincaService.update(999, request));
        assertEquals("Finca no encontrada", exception.getMessage());
    }

    @Test
    void delete_existingFinca_deletes() {
        when(fincaRepository.findByIdAndPropietario(1, currentUser)).thenReturn(Optional.of(finca));

        fincaService.delete(1);

        verify(fincaRepository).deleteById(1);
    }

    @Test
    void delete_nonExistent_throws() {
        when(fincaRepository.findByIdAndPropietario(999, currentUser)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> fincaService.delete(999));
        verify(fincaRepository, never()).deleteById(any());
    }
}
