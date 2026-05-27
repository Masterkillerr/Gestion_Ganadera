package com.gestionganadera.backend.model;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    @Test
    void noArgsConstructor_createsEmptyUsuario() {
        Usuario usuario = new Usuario();
        assertNull(usuario.getId());
        assertNull(usuario.getNombre());
        assertNull(usuario.getEmail());
        assertNull(usuario.getPassword());
        assertNull(usuario.getRole());
        assertNull(usuario.getCreadoEn());
    }

    @Test
    void allArgsConstructor_createsUsuario() {
        UUID id = UUID.randomUUID();
        Role role = new Role(1, "ADMIN");
        LocalDateTime now = LocalDateTime.now();

        Usuario usuario = new Usuario(id, "Juan Pérez", "juan@example.com", "password123", role, now);

        assertEquals(id, usuario.getId());
        assertEquals("Juan Pérez", usuario.getNombre());
        assertEquals("juan@example.com", usuario.getEmail());
        assertEquals("password123", usuario.getPassword());
        assertEquals(role, usuario.getRole());
        assertEquals(now, usuario.getCreadoEn());
    }

    @Test
    void settersAndGetters_workCorrectly() {
        Usuario usuario = new Usuario();
        UUID id = UUID.randomUUID();
        Role role = new Role(2, "USER");

        usuario.setId(id);
        usuario.setNombre("María García");
        usuario.setEmail("maria@example.com");
        usuario.setPassword("securePass");
        usuario.setRole(role);

        assertEquals(id, usuario.getId());
        assertEquals("María García", usuario.getNombre());
        assertEquals("maria@example.com", usuario.getEmail());
        assertEquals("securePass", usuario.getPassword());
        assertEquals(role, usuario.getRole());
    }

    @Test
    void getAuthorities_withRole_returnsROLE_Prefixed() {
        Role role = new Role(1, "ADMIN");
        Usuario usuario = new Usuario();
        usuario.setRole(role);

        Collection<? extends GrantedAuthority> authorities = usuario.getAuthorities();

        assertEquals(1, authorities.size());
        assertEquals("ROLE_ADMIN", authorities.iterator().next().getAuthority());
    }

    @Test
    void getAuthorities_withoutRole_returnsEmptyList() {
        Usuario usuario = new Usuario();

        Collection<? extends GrantedAuthority> authorities = usuario.getAuthorities();

        assertTrue(authorities.isEmpty());
    }

    @Test
    void getUsername_returnsEmail() {
        Usuario usuario = new Usuario();
        usuario.setEmail("test@example.com");

        assertEquals("test@example.com", usuario.getUsername());
    }

    @Test
    void userDetailsMethods_allReturnTrue() {
        Usuario usuario = new Usuario();

        assertTrue(usuario.isAccountNonExpired());
        assertTrue(usuario.isAccountNonLocked());
        assertTrue(usuario.isCredentialsNonExpired());
        assertTrue(usuario.isEnabled());
    }

    @Test
    void onCreate_setsCreadoEn() {
        Usuario usuario = new Usuario();
        assertNull(usuario.getCreadoEn());

        usuario.onCreate();

        assertNotNull(usuario.getCreadoEn());
        assertTrue(usuario.getCreadoEn().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void equalsAndHashCode_sameFields_areConsistent() {
        UUID id = UUID.randomUUID();
        Role role = new Role(1, "USER");
        LocalDateTime now = LocalDateTime.now();

        Usuario u1 = new Usuario(id, "Test", "test@test.com", "pass", role, now);
        Usuario u2 = new Usuario(id, "Test", "test@test.com", "pass", role, now);

        assertEquals(u1, u2);
        assertEquals(u1.hashCode(), u2.hashCode());
    }

    @Test
    void toString_doesNotThrow() {
        Usuario usuario = new Usuario();
        usuario.setNombre("Test User");

        assertNotNull(usuario.toString());
    }
}
