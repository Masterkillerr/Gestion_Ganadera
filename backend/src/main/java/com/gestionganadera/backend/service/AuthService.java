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
import org.springframework.web.client.RestTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import com.gestionganadera.backend.dto.RecaptchaResponse;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        // Validación de ReCAPTCHA
        if (request.getRecaptchaToken() == null || request.getRecaptchaToken().isEmpty()) {
            throw new BadCredentialsException("Por favor completa el ReCAPTCHA");
        }
        
        RestTemplate restTemplate = new RestTemplate();
        String recaptchaSecret = "6LetSccsAAAAAFtJRg1IinYNKnBIZSGuKyftPI6h";
        String url = "https://www.google.com/recaptcha/api/siteverify?secret=" + recaptchaSecret + "&response=" + request.getRecaptchaToken();
        
        RecaptchaResponse recaptchaResponse = restTemplate.postForObject(url, null, RecaptchaResponse.class);
        if (recaptchaResponse == null || !recaptchaResponse.isSuccess()) {
            throw new BadCredentialsException("ReCAPTCHA inválido");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        Usuario usuario = (Usuario) authentication.getPrincipal();
        String token = jwtUtil.generateToken(usuario);

        String roleName = usuario.getRole() != null ? usuario.getRole().getNombre() : "USER";
        return new LoginResponse(token, usuario.getEmail(), roleName, usuario.getNombre());
    }

    public Usuario register(Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }
}
