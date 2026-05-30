package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.CreateUsuarioRequest;
import com.gestionganadera.backend.dto.UpdateProfileRequest;
import com.gestionganadera.backend.dto.UsuarioDTO;
import com.gestionganadera.backend.exception.DuplicateResourceException;
import com.gestionganadera.backend.exception.ResourceNotFoundException;
import com.gestionganadera.backend.model.Role;
import com.gestionganadera.backend.model.Usuario;
import com.gestionganadera.backend.repository.RoleRepository;
import com.gestionganadera.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario currentUser;
    private Role roleUser;
    private Role roleAdmin;
    private Usuario otroUsuario;

    @BeforeEach
    void setUp() {
        roleUser = new Role(1, "USER");
        roleAdmin = new Role(2, "ADMIN");
        currentUser = createUser(1, "Test User", "test@example.com", roleUser);
        otroUsuario = createUser(2, "Otro User", "otro@example.com", roleUser);
        authenticateAs(currentUser);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private static Usuario createUser(Integer id, String nombre, String email, Role role) {
        Usuario u = new Usuario();
        u.setId(id);
        u.setNombre(nombre);
        u.setEmail(email);
        u.setPassword("encoded_password");
        u.setRole(role);
        return u;
    }

    private void authenticateAs(Usuario user) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
    }

    // --- findAll tests ---

    @Test
    void findAll_returnsAllUsers() {
        when(usuarioRepository.findAll()).thenReturn(List.of(currentUser, otroUsuario));

        List<UsuarioDTO> result = usuarioService.findAll();

        assertEquals(2, result.size());
        assertEquals("Test User", result.get(0).getNombre());
        assertEquals("Otro User", result.get(1).getNombre());
    }

    @Test
    void findAll_emptyList_whenNoUsers() {
        when(usuarioRepository.findAll()).thenReturn(List.of());

        assertTrue(usuarioService.findAll().isEmpty());
    }

    // --- findById tests ---

    @Test
    void findById_returnsDTO_whenFound() {
        when(usuarioRepository.findById(2)).thenReturn(Optional.of(otroUsuario));

        Optional<UsuarioDTO> result = usuarioService.findById(2);

        assertTrue(result.isPresent());
        assertEquals("Otro User", result.get().getNombre());
        assertEquals("otro@example.com", result.get().getEmail());
    }

    @Test
    void findById_returnsEmpty_whenNotFound() {
        when(usuarioRepository.findById(999)).thenReturn(Optional.empty());

        assertTrue(usuarioService.findById(999).isEmpty());
    }

    // --- create tests ---

    @Test
    void create_createsUser() {
        CreateUsuarioRequest request = new CreateUsuarioRequest();
        request.setNombre("Nuevo User");
        request.setEmail("nuevo@example.com");
        request.setPassword("password123");

        when(usuarioRepository.existsByEmail("nuevo@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded_new");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario saved = invocation.getArgument(0);
            saved.setId(10);
            return saved;
        });

        UsuarioDTO result = usuarioService.create(request);

        assertEquals("Nuevo User", result.getNombre());
        assertEquals("nuevo@example.com", result.getEmail());
        assertNotNull(result.getId());
        verify(usuarioRepository).save(any(Usuario.class));
        verify(passwordEncoder).encode("password123");
    }

    @Test
    void create_throws_whenEmailExists() {
        CreateUsuarioRequest request = new CreateUsuarioRequest();
        request.setNombre("Nuevo User");
        request.setEmail("existing@example.com");
        request.setPassword("password123");

        when(usuarioRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> usuarioService.create(request));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void create_withRole_setsRole() {
        CreateUsuarioRequest request = new CreateUsuarioRequest();
        request.setNombre("Admin User");
        request.setEmail("admin@example.com");
        request.setPassword("pass");
        request.setRol("ADMIN");

        when(usuarioRepository.existsByEmail("admin@example.com")).thenReturn(false);
        when(passwordEncoder.encode("pass")).thenReturn("encoded_pass");
        when(roleRepository.findByNombre("ADMIN")).thenReturn(Optional.of(roleAdmin));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario saved = invocation.getArgument(0);
            saved.setId(11);
            return saved;
        });

        UsuarioDTO result = usuarioService.create(request);

        assertEquals("ADMIN", result.getRol());
        verify(roleRepository).findByNombre("ADMIN");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void create_withRoleNotFound_throws() {
        CreateUsuarioRequest request = new CreateUsuarioRequest();
        request.setNombre("User");
        request.setEmail("user@example.com");
        request.setRol("SUPER");
        request.setPassword("pass");

        when(usuarioRepository.existsByEmail("user@example.com")).thenReturn(false);
        when(roleRepository.findByNombre("SUPER")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> usuarioService.create(request));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void create_withoutPassword_usesDefault() {
        CreateUsuarioRequest request = new CreateUsuarioRequest();
        request.setNombre("Default Pass");
        request.setEmail("default@example.com");

        when(usuarioRepository.existsByEmail("default@example.com")).thenReturn(false);
        when(passwordEncoder.encode("default123")).thenReturn("encoded_default");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario saved = invocation.getArgument(0);
            saved.setId(12);
            return saved;
        });

        UsuarioDTO result = usuarioService.create(request);

        assertEquals("Default Pass", result.getNombre());
        verify(passwordEncoder).encode("default123");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    // --- update tests ---

    @Test
    void update_updatesAllFields() {
        when(usuarioRepository.findById(2)).thenReturn(Optional.of(otroUsuario));
        when(passwordEncoder.encode("newpass")).thenReturn("encoded_newpass");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreateUsuarioRequest request = new CreateUsuarioRequest();
        request.setNombre("Nombre Modificado");
        request.setEmail("modificado@example.com");
        request.setPassword("newpass");
        request.setRol("ADMIN");

        when(roleRepository.findByNombre("ADMIN")).thenReturn(Optional.of(roleAdmin));

        UsuarioDTO result = usuarioService.update(2, request);

        assertEquals("Nombre Modificado", result.getNombre());
        assertEquals("modificado@example.com", result.getEmail());
        assertEquals("ADMIN", result.getRol());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void update_partialFields_onlyUpdatesProvided() {
        when(usuarioRepository.findById(2)).thenReturn(Optional.of(otroUsuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreateUsuarioRequest request = new CreateUsuarioRequest();
        request.setNombre("Solo Nombre");

        UsuarioDTO result = usuarioService.update(2, request);

        assertEquals("Solo Nombre", result.getNombre());
        verify(usuarioRepository).save(any(Usuario.class));
        verify(passwordEncoder, never()).encode(anyString());
        verify(roleRepository, never()).findByNombre(anyString());
    }

    @Test
    void update_throws_whenEmailAlreadyTaken() {
        when(usuarioRepository.findById(2)).thenReturn(Optional.of(otroUsuario));
        when(usuarioRepository.existsByEmail("ocupado@example.com")).thenReturn(true);

        CreateUsuarioRequest request = new CreateUsuarioRequest();
        request.setNombre("Otro User");
        request.setEmail("ocupado@example.com");

        assertThrows(DuplicateResourceException.class, () -> usuarioService.update(2, request));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void update_throws_whenUserNotFound() {
        when(usuarioRepository.findById(999)).thenReturn(Optional.empty());

        CreateUsuarioRequest request = new CreateUsuarioRequest();
        request.setNombre("Ghost");

        assertThrows(ResourceNotFoundException.class, () -> usuarioService.update(999, request));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void update_withRoleNotFound_throws() {
        when(usuarioRepository.findById(2)).thenReturn(Optional.of(otroUsuario));

        CreateUsuarioRequest request = new CreateUsuarioRequest();
        request.setNombre("Otro User");
        request.setRol("INEXISTENTE");

        when(roleRepository.findByNombre("INEXISTENTE")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> usuarioService.update(2, request));
        verify(usuarioRepository, never()).save(any());
    }

    // --- delete tests ---

    @Test
    void delete_existingUser_deletes() {
        when(usuarioRepository.findById(2)).thenReturn(Optional.of(otroUsuario));

        usuarioService.delete(2);

        verify(usuarioRepository).deleteById(2);
    }

    @Test
    void delete_nonExistent_throws() {
        when(usuarioRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> usuarioService.delete(999));
        verify(usuarioRepository, never()).deleteById(any());
    }

    // --- getProfile tests ---

    @Test
    void getProfile_returnsCurrentUser() {
        UsuarioDTO result = usuarioService.getProfile();

        assertEquals("Test User", result.getNombre());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("USER", result.getRol());
    }

    @Test
    void getProfile_returnsUserRole() {
        authenticateAs(createUser(3, "Admin", "admin@example.com", roleAdmin));

        UsuarioDTO result = usuarioService.getProfile();

        assertEquals("Admin", result.getNombre());
        assertEquals("ADMIN", result.getRol());
    }

    // --- updateProfile tests ---

    @Test
    void updateProfile_updatesAllFields() {
        when(passwordEncoder.encode("nueva_password")).thenReturn("encoded_nueva");

        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setNombre("Nombre Actualizado");
        request.setEmail("actualizado@example.com");
        request.setPassword("nueva_password");

        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioDTO result = usuarioService.updateProfile(request);

        assertEquals("Nombre Actualizado", result.getNombre());
        assertEquals("actualizado@example.com", result.getEmail());
        verify(usuarioRepository).save(any(Usuario.class));
        verify(passwordEncoder).encode("nueva_password");
    }

    @Test
    void updateProfile_partialFields_onlyUpdatesProvided() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setNombre("Solo Nombre");

        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioDTO result = usuarioService.updateProfile(request);

        assertEquals("Solo Nombre", result.getNombre());
        assertEquals("test@example.com", result.getEmail()); // unchanged
        verify(usuarioRepository).save(any(Usuario.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void updateProfile_throws_whenEmailAlreadyTaken() {
        when(usuarioRepository.existsByEmail("ocupado@example.com")).thenReturn(true);

        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setEmail("ocupado@example.com");

        assertThrows(DuplicateResourceException.class, () -> usuarioService.updateProfile(request));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void updateProfile_sameEmail_doesNotCheckDuplicate() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setEmail("test@example.com"); // mismo email del current user

        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioDTO result = usuarioService.updateProfile(request);

        assertEquals("test@example.com", result.getEmail());
        verify(usuarioRepository, never()).existsByEmail(anyString());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void updateProfile_emptyPassword_doesNotEncode() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setNombre("Test");
        request.setPassword("");

        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioDTO result = usuarioService.updateProfile(request);

        assertEquals("Test", result.getNombre());
        verify(passwordEncoder, never()).encode(anyString());
        verify(usuarioRepository).save(any(Usuario.class));
    }
}
