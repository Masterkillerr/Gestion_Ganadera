package com.gestionganadera.backend.config;

import com.gestionganadera.backend.dto.LoginRequest;
import com.gestionganadera.backend.dto.LoginResponse;
import com.gestionganadera.backend.dto.RegisterRequest;
import com.gestionganadera.backend.dto.UsuarioResponse;
import com.gestionganadera.backend.model.Role;
import com.gestionganadera.backend.model.Usuario;
import com.gestionganadera.backend.repository.RoleRepository;
import com.gestionganadera.backend.repository.UsuarioRepository;
import com.gestionganadera.backend.service.AuthService;
import com.gestionganadera.backend.service.EmailService;
import com.gestionganadera.backend.util.JwtUtil;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Test configuration that overrides the real AuthService with one
 * that bypasses reCAPTCHA validation (which requires external API calls).
 *
 * In tests, we create a user directly via the repository and generate
 * a JWT token programmatically instead of going through the login flow.
 */
@TestConfiguration
public class TestAuthConfig {

    /**
     * Override the AuthService with one that skips recaptcha validation.
     * This allows integration tests to use the real auth flow without
     * needing a real reCAPTCHA token.
     */
    @Bean
    @Primary
    public AuthService testAuthService(
            AuthenticationManager authenticationManager,
            UsuarioRepository usuarioRepository,
            RoleRepository roleRepository,
            JwtUtil jwtUtil,
            PasswordEncoder passwordEncoder,
            EmailService emailService) {
        return new AuthService(authenticationManager, usuarioRepository, roleRepository, jwtUtil, passwordEncoder, emailService) {
            @Override
            public LoginResponse login(LoginRequest request) {
                // Bypass recaptcha — authenticate directly
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                Usuario usuario = (Usuario) authentication.getPrincipal();
                String token = jwtUtil.generateToken(usuario);
                String roleName = usuario.getRole() != null ? usuario.getRole().getNombre() : "OPERARIO";
                return new LoginResponse(token, usuario.getEmail(), roleName, usuario.getNombre());
            }

            @Override
            public UsuarioResponse register(RegisterRequest request) {
                // Bypass recaptcha — register directly via repository
                if (usuarioRepository.existsByEmail(request.getEmail())) {
                    throw new RuntimeException("El email ya está registrado");
                }

                Usuario usuario = new Usuario();
                usuario.setNombre(request.getNombre());
                usuario.setEmail(request.getEmail());
                usuario.setPassword(passwordEncoder.encode(request.getPassword()));

                Role userRole = roleRepository.findByNombre("OPERARIO")
                        .orElseGet(() -> {
                            Role role = new Role();
                            role.setNombre("OPERARIO");
                            return roleRepository.save(role);
                        });
                usuario.setRole(userRole);

                Usuario saved = usuarioRepository.save(usuario);
                return UsuarioResponse.fromEntity(saved);
            }
        };
    }
}
