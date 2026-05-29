package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.LoginRequest;
import com.gestionganadera.backend.dto.LoginResponse;
import com.gestionganadera.backend.dto.RegisterRequest;
import com.gestionganadera.backend.dto.UsuarioResponse;
import com.gestionganadera.backend.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void login_returnsLoginResponse() {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("pass123");
        request.setRecaptchaToken("token");

        LoginResponse expected = new LoginResponse("jwt-token", "user@example.com", "USER", "User");
        when(authService.login(request)).thenReturn(expected);

        ResponseEntity<LoginResponse> response = authController.login(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("jwt-token", response.getBody().getToken());
        verify(authService).login(request);
    }

    @Test
    void register_returnsUsuarioResponse() {
        RegisterRequest request = new RegisterRequest();
        request.setNombre("New User");
        request.setEmail("new@example.com");
        request.setPassword("pass123");
        request.setRecaptchaToken("token");

        UsuarioResponse expected = new UsuarioResponse(1, "New User", "new@example.com", "USER", LocalDateTime.now());
        when(authService.register(request)).thenReturn(expected);

        ResponseEntity<UsuarioResponse> response = authController.register(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("New User", response.getBody().getNombre());
        verify(authService).register(request);
    }
}
