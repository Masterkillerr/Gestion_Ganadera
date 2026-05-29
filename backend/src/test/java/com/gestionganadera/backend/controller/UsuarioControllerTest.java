package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.CreateUsuarioRequest;
import com.gestionganadera.backend.dto.UpdateProfileRequest;
import com.gestionganadera.backend.dto.UsuarioDTO;
import com.gestionganadera.backend.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController controller;

    private UsuarioDTO createDTO(Integer id, String nombre, String email, String rol) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(id);
        dto.setNombre(nombre);
        dto.setEmail(email);
        dto.setRol(rol);
        return dto;
    }

    @Test
    void getAllUsuarios_returnsList() {
        UsuarioDTO dto1 = createDTO(1, "User One", "one@example.com", "USER");
        UsuarioDTO dto2 = createDTO(2, "User Two", "two@example.com", "ADMIN");
        when(usuarioService.findAll()).thenReturn(List.of(dto1, dto2));

        ResponseEntity<List<UsuarioDTO>> response = controller.getAllUsuarios();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(2, response.getBody().size());
        assertEquals("User One", response.getBody().get(0).getNombre());
    }

    @Test
    void getUsuarioById_found_returns200() {
        UsuarioDTO dto = createDTO(1, "Found User", "found@example.com", "USER");
        when(usuarioService.findById(1)).thenReturn(Optional.of(dto));

        ResponseEntity<UsuarioDTO> response = controller.getUsuarioById(1);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Found User", response.getBody().getNombre());
    }

    @Test
    void getUsuarioById_notFound_returns404() {
        when(usuarioService.findById(999)).thenReturn(Optional.empty());

        ResponseEntity<UsuarioDTO> response = controller.getUsuarioById(999);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void createUsuario_returns200() {
        CreateUsuarioRequest request = new CreateUsuarioRequest();
        request.setNombre("New User");
        request.setEmail("new@example.com");
        request.setPassword("pass");

        UsuarioDTO created = createDTO(1, "New User", "new@example.com", "USER");
        when(usuarioService.create(request)).thenReturn(created);

        ResponseEntity<UsuarioDTO> response = controller.createUsuario(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("New User", response.getBody().getNombre());
    }

    @Test
    void updateUsuario_returns200() {
        CreateUsuarioRequest request = new CreateUsuarioRequest();
        request.setNombre("Updated User");
        request.setEmail("updated@example.com");

        UsuarioDTO updated = createDTO(1, "Updated User", "updated@example.com", "ADMIN");
        when(usuarioService.update(1, request)).thenReturn(updated);

        ResponseEntity<UsuarioDTO> response = controller.updateUsuario(1, request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Updated User", response.getBody().getNombre());
        assertEquals("ADMIN", response.getBody().getRol());
    }

    @Test
    void deleteUsuario_returns204() {
        doNothing().when(usuarioService).delete(1);

        ResponseEntity<Void> response = controller.deleteUsuario(1);

        assertEquals(204, response.getStatusCode().value());
        verify(usuarioService).delete(1);
    }

    @Test
    void getProfile_returns200() {
        UsuarioDTO dto = createDTO(1, "Profile User", "profile@example.com", "USER");
        when(usuarioService.getProfile()).thenReturn(dto);

        ResponseEntity<UsuarioDTO> response = controller.getProfile();

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Profile User", response.getBody().getNombre());
    }

    @Test
    void updateProfile_returns200() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setNombre("Updated Profile");
        request.setEmail("profile@example.com");

        UsuarioDTO updated = createDTO(1, "Updated Profile", "profile@example.com", "USER");
        when(usuarioService.updateProfile(request)).thenReturn(updated);

        ResponseEntity<UsuarioDTO> response = controller.updateProfile(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Updated Profile", response.getBody().getNombre());
    }
}
