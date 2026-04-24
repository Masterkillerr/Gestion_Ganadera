package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.LoginRequest;
import com.gestionganadera.backend.dto.LoginResponse;
import com.gestionganadera.backend.model.Usuario;
import com.gestionganadera.backend.repository.UsuarioRepository;
import com.gestionganadera.backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        Usuario usuario = (Usuario) authentication.getPrincipal();
        String token = jwtUtil.generateToken(usuario);

        return new LoginResponse(token, usuario.getEmail(), usuario.getRole().getNombre(), usuario.getNombre());
    }

    public Usuario register(Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }
}
