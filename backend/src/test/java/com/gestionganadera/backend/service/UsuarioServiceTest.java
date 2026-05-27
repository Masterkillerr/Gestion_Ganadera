package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.UsuarioDTO;
import com.gestionganadera.backend.model.Role;
import com.gestionganadera.backend.model.Usuario;
import com.gestionganadera.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void findAll_returnsAllUsersAsDTOs() {
        Role role = new Role(1, "USER");
        Usuario user = new Usuario();
        user.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        user.setNombre("Juan Pérez");
        user.setEmail("juan@example.com");
        user.setRole(role);

        when(usuarioRepository.findAll()).thenReturn(List.of(user));

        List<UsuarioDTO> result = usuarioService.findAll();

        assertEquals(1, result.size());
        assertEquals("Juan Pérez", result.get(0).getNombre());
        assertEquals("juan@example.com", result.get(0).getEmail());
        assertEquals("USER", result.get(0).getRol());
    }

    @Test
    void findAll_returnsEmptyList_whenNoUsers() {
        when(usuarioRepository.findAll()).thenReturn(List.of());

        assertTrue(usuarioService.findAll().isEmpty());
    }

    @Test
    void findAll_mapsRoleToNull_whenRoleIsNull() {
        Usuario user = new Usuario();
        user.setId(UUID.randomUUID());
        user.setNombre("Sin Rol");
        user.setEmail("sinrol@example.com");
        user.setRole(null);

        when(usuarioRepository.findAll()).thenReturn(List.of(user));

        List<UsuarioDTO> result = usuarioService.findAll();

        assertNull(result.get(0).getRol());
    }
}
