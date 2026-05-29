package com.gestionganadera.backend.dto;

import com.gestionganadera.backend.model.Role;
import com.gestionganadera.backend.model.Usuario;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioDTOTest {

    @Test
    void fromEntity_withRole_mapsCorrectly() {
        Role role = new Role(1, "ADMIN");
        Usuario usuario = new Usuario();
        usuario.setId(1);
        usuario.setNombre("Admin User");
        usuario.setEmail("admin@test.com");
        usuario.setPassword("password");
        usuario.setRole(role);
        usuario.setCreadoEn(LocalDateTime.now());

        UsuarioDTO dto = UsuarioDTO.fromEntity(usuario);

        assertEquals(1, dto.getId());
        assertEquals("Admin User", dto.getNombre());
        assertEquals("admin@test.com", dto.getEmail());
        assertEquals("ADMIN", dto.getRol());
    }

    @Test
    void fromEntity_withoutRole_rolIsNull() {
        Usuario usuario = new Usuario();
        usuario.setId(1);
        usuario.setNombre("Test User");
        usuario.setEmail("test@test.com");

        UsuarioDTO dto = UsuarioDTO.fromEntity(usuario);

        assertEquals("Test User", dto.getNombre());
        assertNull(dto.getRol());
    }

    @Test
    void constructorAndSetters_work() {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(1);
        dto.setNombre("Juan");
        dto.setEmail("juan@test.com");
        dto.setRol("USER");

        assertEquals(1, dto.getId());
        assertEquals("Juan", dto.getNombre());
        assertEquals("juan@test.com", dto.getEmail());
        assertEquals("USER", dto.getRol());
    }

    @Test
    void allArgsConstructor_works() {
        UsuarioDTO dto = new UsuarioDTO(1, "María", "maria@test.com", "ADMIN");
        assertEquals(1, dto.getId());
        assertEquals("María", dto.getNombre());
        assertEquals("ADMIN", dto.getRol());
    }
}
