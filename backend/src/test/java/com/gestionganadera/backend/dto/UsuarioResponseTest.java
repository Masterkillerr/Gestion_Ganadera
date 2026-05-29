package com.gestionganadera.backend.dto;

import com.gestionganadera.backend.model.Role;
import com.gestionganadera.backend.model.Usuario;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioResponseTest {

    @Test
    void fromEntity_withRole_mapsCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        Role role = new Role(1, "ADMIN");
        Usuario usuario = new Usuario();
        usuario.setId(1);
        usuario.setNombre("Admin User");
        usuario.setEmail("admin@test.com");
        usuario.setPassword("pass");
        usuario.setRole(role);
        usuario.setCreadoEn(now);

        UsuarioResponse response = UsuarioResponse.fromEntity(usuario);

        assertEquals(1, response.getId());
        assertEquals("Admin User", response.getNombre());
        assertEquals("admin@test.com", response.getEmail());
        assertEquals("ADMIN", response.getRole());
        assertEquals(now, response.getCreadoEn());
    }

    @Test
    void fromEntity_withoutRole_defaultsToUSER() {
        Usuario usuario = new Usuario();
        usuario.setId(1);
        usuario.setNombre("Test");
        usuario.setEmail("test@test.com");
        usuario.setCreadoEn(LocalDateTime.now());

        UsuarioResponse response = UsuarioResponse.fromEntity(usuario);

        assertEquals("Test", response.getNombre());
        assertEquals("USER", response.getRole());
    }

    @Test
    void constructorAndSetters_work() {
        UsuarioResponse response = new UsuarioResponse();
        response.setId(1);
        response.setNombre("Juan");
        response.setEmail("juan@test.com");
        response.setRole("USER");

        assertEquals(1, response.getId());
        assertEquals("Juan", response.getNombre());
        assertEquals("juan@test.com", response.getEmail());
        assertEquals("USER", response.getRole());
    }

    @Test
    void allArgsConstructor_works() {
        LocalDateTime now = LocalDateTime.now();
        UsuarioResponse response = new UsuarioResponse(1, "María", "maria@test.com", "ADMIN", now);

        assertEquals(1, response.getId());
        assertEquals("María", response.getNombre());
        assertEquals("ADMIN", response.getRole());
        assertEquals(now, response.getCreadoEn());
    }
}
