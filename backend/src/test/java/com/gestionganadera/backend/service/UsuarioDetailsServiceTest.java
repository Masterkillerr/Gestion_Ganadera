package com.gestionganadera.backend.service;

import com.gestionganadera.backend.model.Usuario;
import com.gestionganadera.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioDetailsServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioDetailsService usuarioDetailsService;

    @Test
    void loadUserByUsername_existingUser_returnsUserDetails() {
        Usuario usuario = new Usuario();
        usuario.setEmail("test@example.com");
        usuario.setPassword("encoded-pass");
        when(usuarioRepository.findByEmail("test@example.com")).thenReturn(Optional.of(usuario));

        UserDetails result = usuarioDetailsService.loadUserByUsername("test@example.com");

        assertEquals("test@example.com", result.getUsername());
        assertEquals("encoded-pass", result.getPassword());
        verify(usuarioRepository).findByEmail("test@example.com");
    }

    @Test
    void loadUserByUsername_nonExistentUser_throwsUsernameNotFoundException() {
        when(usuarioRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> usuarioDetailsService.loadUserByUsername("unknown@example.com"));

        assertTrue(exception.getMessage().contains("unknown@example.com"));
        verify(usuarioRepository).findByEmail("unknown@example.com");
    }
}
