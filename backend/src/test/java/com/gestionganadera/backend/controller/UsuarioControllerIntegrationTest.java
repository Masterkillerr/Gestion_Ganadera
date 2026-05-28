package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.UsuarioDTO;
import com.gestionganadera.backend.model.Role;
import com.gestionganadera.backend.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioControllerIntegrationTest extends BaseIntegrationTest {

    private String adminToken;

    @BeforeEach
    void setUpAdmin() {
        // Create an ADMIN user for testing admin-only endpoints
        if (!usuarioRepository.existsByEmail("admin@example.com")) {
            Role adminRole = roleRepository.findByNombre("ADMIN").orElseGet(() -> {
                Role role = new Role();
                role.setNombre("ADMIN");
                return roleRepository.save(role);
            });
            Usuario admin = new Usuario();
            admin.setNombre("Admin User");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("adminpass123"));
            admin.setRole(adminRole);
            usuarioRepository.save(admin);
        }
        Usuario adminUser = usuarioRepository.findByEmail("admin@example.com").orElseThrow();
        adminToken = jwtUtil.generateToken(adminUser);
    }

    // ── ADMIN endpoints (require hasRole('ADMIN')) ──

    @Test
    void getAllUsuarios_withAdmin_returnsList() {
        ResponseEntity<List<UsuarioDTO>> response = restClient.get()
                .uri("/usuarios")
                .headers(h -> {
                    h.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
                    h.setBearerAuth(adminToken);
                })
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<UsuarioDTO> list = response.getBody();
        assertNotNull(list);
        assertFalse(list.isEmpty());
        assertTrue(list.stream().anyMatch(u -> u.getEmail().equals("admin@example.com")));
    }

    @Test
    void getUsuarioById_withAdmin_returnsUser() {
        // Get the admin user's ID by listing all users
        ResponseEntity<List<UsuarioDTO>> listResponse = restClient.get()
                .uri("/usuarios")
                .headers(h -> {
                    h.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
                    h.setBearerAuth(adminToken);
                })
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        String adminId = listResponse.getBody().stream()
                .filter(u -> u.getEmail().equals("admin@example.com"))
                .findFirst().orElseThrow().getId();

        ResponseEntity<UsuarioDTO> response = restClient.get()
                .uri("/usuarios/" + adminId)
                .headers(h -> {
                    h.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
                    h.setBearerAuth(adminToken);
                })
                .retrieve()
                .toEntity(UsuarioDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        UsuarioDTO user = response.getBody();
        assertNotNull(user);
        assertEquals("admin@example.com", user.getEmail());
        assertEquals("ADMIN", user.getRol());
    }

    @Test
    void getUsuarioById_nonExistent_returns404() {
        ResponseEntity<String> response = restClient.get()
                .uri("/usuarios/00000000-0000-0000-0000-000000000000")
                .headers(h -> {
                    h.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
                    h.setBearerAuth(adminToken);
                })
                .retrieve()
                .toEntity(String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void createUsuario_withAdmin_returnsCreated() {
        ResponseEntity<UsuarioDTO> response = restClient.post()
                .uri("/usuarios")
                .headers(h -> {
                    h.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
                    h.setBearerAuth(adminToken);
                })
                .body("{\"nombre\":\"Nuevo Usuario\",\"email\":\"nuevo@example.com\",\"password\":\"pass123\",\"rol\":\"USER\"}")
                .retrieve()
                .toEntity(UsuarioDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        UsuarioDTO created = response.getBody();
        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("nuevo@example.com", created.getEmail());
        assertEquals("USER", created.getRol());
    }

    @Test
    void updateUsuario_withAdmin_returnsUpdated() {
        // First create a user
        ResponseEntity<UsuarioDTO> createResponse = restClient.post()
                .uri("/usuarios")
                .headers(h -> {
                    h.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
                    h.setBearerAuth(adminToken);
                })
                .body("{\"nombre\":\"Original\",\"email\":\"original@example.com\",\"password\":\"pass123\",\"rol\":\"USER\"}")
                .retrieve()
                .toEntity(UsuarioDTO.class);

        String userId = createResponse.getBody().getId();

        // Update
        ResponseEntity<UsuarioDTO> updateResponse = restClient.put()
                .uri("/usuarios/" + userId)
                .headers(h -> {
                    h.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
                    h.setBearerAuth(adminToken);
                })
                .body("{\"nombre\":\"Actualizado\",\"email\":\"original@example.com\",\"password\":\"newpass123\",\"rol\":\"USER\"}")
                .retrieve()
                .toEntity(UsuarioDTO.class);

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        UsuarioDTO updated = updateResponse.getBody();
        assertNotNull(updated);
        assertEquals("Actualizado", updated.getNombre());
    }

    @Test
    void deleteUsuario_withAdmin_returns204() {
        // First create a user to delete
        ResponseEntity<UsuarioDTO> createResponse = restClient.post()
                .uri("/usuarios")
                .headers(h -> {
                    h.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
                    h.setBearerAuth(adminToken);
                })
                .body("{\"nombre\":\"Para Eliminar\",\"email\":\"delete@example.com\",\"password\":\"pass123\",\"rol\":\"USER\"}")
                .retrieve()
                .toEntity(UsuarioDTO.class);

        String userId = createResponse.getBody().getId();

        // Delete
        ResponseEntity<Void> deleteResponse = restClient.delete()
                .uri("/usuarios/" + userId)
                .headers(h -> {
                    h.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
                    h.setBearerAuth(adminToken);
                })
                .retrieve()
                .toEntity(Void.class);

        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        // Verify deletion
        ResponseEntity<String> afterDeleteResponse = restClient.get()
                .uri("/usuarios/" + userId)
                .headers(h -> {
                    h.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
                    h.setBearerAuth(adminToken);
                })
                .retrieve()
                .toEntity(String.class);

        assertEquals(HttpStatus.NOT_FOUND, afterDeleteResponse.getStatusCode());
    }

    @Test
    void adminEndpoints_withUserRole_returns403() {
        // Attempt to access ADMIN endpoints with regular USER token
        ResponseEntity<String> response = restClient.get()
                .uri("/usuarios")
                .headers(withAuth()) // USER token
                .retrieve()
                .toEntity(String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    // ── Profile endpoints (any authenticated) ──

    @Test
    void getProfile_withUser_returnsProfile() {
        ResponseEntity<UsuarioDTO> response = restClient.get()
                .uri("/usuarios/profile")
                .headers(withAuth())
                .retrieve()
                .toEntity(UsuarioDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        UsuarioDTO profile = response.getBody();
        assertNotNull(profile);
        assertEquals(TEST_USER_EMAIL, profile.getEmail());
        assertEquals("Test User", profile.getNombre());
    }

    @Test
    void updateProfile_withUser_returnsUpdated() {
        ResponseEntity<UsuarioDTO> response = restClient.put()
                .uri("/usuarios/profile")
                .headers(withAuth())
                .body("{\"nombre\":\"Updated Test User\",\"email\":\"testuser@example.com\"}")
                .retrieve()
                .toEntity(UsuarioDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        UsuarioDTO profile = response.getBody();
        assertNotNull(profile);
        assertEquals("Updated Test User", profile.getNombre());

        // Restore original name for subsequent tests
        restClient.put()
                .uri("/usuarios/profile")
                .headers(withAuth())
                .body("{\"nombre\":\"Test User\",\"email\":\"testuser@example.com\"}")
                .retrieve()
                .toEntity(UsuarioDTO.class);
    }

    @Test
    void profileEndpoints_withoutAuth_returns401() {
        ResponseEntity<String> response = restClient.get()
                .uri("/usuarios/profile")
                .headers(withJson())
                .retrieve()
                .toEntity(String.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}
