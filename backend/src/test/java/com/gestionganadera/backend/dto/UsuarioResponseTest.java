package com.gestionganadera.backend.dto;

import com.gestionganadera.backend.model.Role;
import com.gestionganadera.backend.model.Usuario;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioResponseTest {

    @Test
    void fromEntity_withRole_mapsCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        Role role = new Role(1, "ADMIN");
        UUID id = UUID.randomUUID();
        Usuario usuario = new Usuario(id, "Admin User", "admin@test.com", "pass", role, now);

        UsuarioResponse response = UsuarioResponse.fromEntity(usuario);

        assertEquals(id, response.getId());
        assertEquals("Admin User", response.getNombre());
        assertEquals("admin@test.com", response.getEmail());
        assertEquals("ADMIN", response.getRole());
        assertEquals(now, response.getCreadoEn());
    }

    @Test
    void fromEntity_withoutRole_defaultsToUSER() {
        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setNombre("Test");
        usuario.setEmail("test@test.com");
        usuario.setCreadoEn(LocalDateTime.now());

        UsuarioResponse response = UsuarioResponse.fromEntity(usuario);

        assertEquals("Test", response.getNombre());
        assertEquals("USER", response.getRole());
    }

    @Test
    void constructorAndSetters_work() {
        UUID id = UUID.randomUUID();
        UsuarioResponse response = new UsuarioResponse();
        response.setId(id);
        response.setNombre("Juan");
        response.setEmail("juan@test.com");
        response.setRole("USER");

        assertEquals(id, response.getId());
        assertEquals("Juan", response.getNombre());
        assertEquals("juan@test.com", response.getEmail());
        assertEquals("USER", response.getRole());
    }

    @Test
    void allArgsConstructor_works() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        UsuarioResponse response = new UsuarioResponse(id, "María", "maria@test.com", "ADMIN", now);

        assertEquals(id, response.getId());
        assertEquals("María", response.getNombre());
        assertEquals("ADMIN", response.getRole());
        assertEquals(now, response.getCreadoEn());
    }
}
