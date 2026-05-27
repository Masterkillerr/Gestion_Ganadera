package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.LoginRequest;
import com.gestionganadera.backend.dto.LoginResponse;
import com.gestionganadera.backend.dto.RegisterRequest;
import com.gestionganadera.backend.dto.UsuarioResponse;
import com.gestionganadera.backend.model.Role;
import com.gestionganadera.backend.model.Usuario;
import com.gestionganadera.backend.repository.RoleRepository;
import com.gestionganadera.backend.repository.UsuarioRepository;
import com.gestionganadera.backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import com.gestionganadera.backend.dto.RecaptchaResponse;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.recaptcha.secret}")
    private String recaptchaSecret;

    private void validateRecaptcha(String recaptchaToken) {
        if (recaptchaToken == null || recaptchaToken.isEmpty()) {
            throw new BadCredentialsException("Por favor completa el ReCAPTCHA");
        }

        RestTemplate restTemplate = new RestTemplate();
        String url = "https://www.google.com/recaptcha/api/siteverify?secret=" + recaptchaSecret + "&response=" + recaptchaToken;

        RecaptchaResponse recaptchaResponse = restTemplate.postForObject(url, null, RecaptchaResponse.class);
        if (recaptchaResponse == null || !recaptchaResponse.isSuccess()) {
            throw new BadCredentialsException("ReCAPTCHA inválido");
        }
    }

    public LoginResponse login(LoginRequest request) {
        validateRecaptcha(request.getRecaptchaToken());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        Usuario usuario = (Usuario) authentication.getPrincipal();
        String token = jwtUtil.generateToken(usuario);

        String roleName = usuario.getRole() != null ? usuario.getRole().getNombre() : "USER";
        return new LoginResponse(token, usuario.getEmail(), roleName, usuario.getNombre());
    }

    public UsuarioResponse register(RegisterRequest request) {
        // Validar reCAPTCHA en registro también
        validateRecaptcha(request.getRecaptchaToken());

        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));

        // Asignar rol USER por defecto
        Role userRole = roleRepository.findByNombre("USER")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setNombre("USER");
                    return roleRepository.save(role);
                });
        usuario.setRole(userRole);

        Usuario saved = usuarioRepository.save(usuario);
        return UsuarioResponse.fromEntity(saved);
    }
}
