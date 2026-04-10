package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.dto.LoginRequest;
import com.gestionganadera.backend.dto.LoginResponse;
import com.gestionganadera.backend.model.Usuario;
import com.gestionganadera.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<Usuario> register(@Valid @RequestBody Usuario usuario) {
        return ResponseEntity.ok(authService.register(usuario));
    }
}
