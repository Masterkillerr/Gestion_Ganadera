package com.gestionganadera.backend.dto;

import com.gestionganadera.backend.model.Role;
import com.gestionganadera.backend.model.Usuario;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioDTOTest {

    @Test
    void fromEntity_withRole_mapsCorrectly() {
        Role role = new Role(1, "ADMIN");
        Usuario usuario = new Usuario(
            UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
            "Admin User", "admin@test.com", "password",
            role, LocalDateTime.now()
        );

        UsuarioDTO dto = UsuarioDTO.fromEntity(usuario);

        assertEquals("123e4567-e89b-12d3-a456-426614174000", dto.getId());
        assertEquals("Admin User", dto.getNombre());
        assertEquals("admin@test.com", dto.getEmail());
        assertEquals("ADMIN", dto.getRol());
    }

    @Test
    void fromEntity_withoutRole_rolIsNull() {
        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setNombre("Test User");
        usuario.setEmail("test@test.com");

        UsuarioDTO dto = UsuarioDTO.fromEntity(usuario);

        assertEquals("Test User", dto.getNombre());
        assertNull(dto.getRol());
    }

    @Test
    void constructorAndSetters_work() {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId("uuid-123");
        dto.setNombre("Juan");
        dto.setEmail("juan@test.com");
        dto.setRol("USER");

        assertEquals("uuid-123", dto.getId());
        assertEquals("Juan", dto.getNombre());
        assertEquals("juan@test.com", dto.getEmail());
        assertEquals("USER", dto.getRol());
    }

    @Test
    void allArgsConstructor_works() {
        UsuarioDTO dto = new UsuarioDTO("uuid-1", "María", "maria@test.com", "ADMIN");
        assertEquals("uuid-1", dto.getId());
        assertEquals("María", dto.getNombre());
        assertEquals("ADMIN", dto.getRol());
    }
}
