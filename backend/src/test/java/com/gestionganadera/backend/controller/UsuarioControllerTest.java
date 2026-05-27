package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.UsuarioDTO;
import com.gestionganadera.backend.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController controller;

    @Test
    void getAllUsuarios_returnsList() {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setNombre("User");
        dto.setEmail("user@example.com");
        when(usuarioService.findAll()).thenReturn(List.of(dto));

        ResponseEntity<List<UsuarioDTO>> response = controller.getAllUsuarios();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        assertEquals("User", response.getBody().get(0).getNombre());
    }
}
