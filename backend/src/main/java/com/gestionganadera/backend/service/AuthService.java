package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.LoginRequest;
import com.gestionganadera.backend.dto.LoginResponse;
import com.gestionganadera.backend.dto.RecaptchaResponse;
import com.gestionganadera.backend.dto.RegisterRequest;
import com.gestionganadera.backend.dto.UsuarioResponse;
import com.gestionganadera.backend.exception.DuplicateResourceException;
import com.gestionganadera.backend.model.Role;
import com.gestionganadera.backend.model.Usuario;
import com.gestionganadera.backend.repository.RoleRepository;
import com.gestionganadera.backend.repository.UsuarioRepository;
import com.gestionganadera.backend.util.JwtUtil;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AuthService {

 private final AuthenticationManager authenticationManager;
 private final UsuarioRepository usuarioRepository;
 private final RoleRepository roleRepository;
 private final JwtUtil jwtUtil;
 private final PasswordEncoder passwordEncoder;
 private final EmailService emailService;
 private final AuditService auditService;

 @Value("${app.recaptcha.secret}")
 private String recaptchaSecret;

 private void validateRecaptcha(String recaptchaToken) {
  // En desarrollo (sin secret configurado), saltar validación reCAPTCHA
  if (recaptchaSecret == null || recaptchaSecret.isEmpty()) {
   return;
  }

  if (recaptchaToken == null || recaptchaToken.isEmpty()) {
   throw new BadCredentialsException("Por favor completa el ReCAPTCHA");
  }

  RestTemplate restTemplate = new RestTemplate();
  String url = "https://www.google.com/recaptcha/api/siteverify?secret=" + URLEncoder.encode(recaptchaSecret, StandardCharsets.UTF_8) + "&response=" + URLEncoder.encode(recaptchaToken, StandardCharsets.UTF_8);

  RecaptchaResponse recaptchaResponse = restTemplate.postForObject(url, null, RecaptchaResponse.class);
  if (recaptchaResponse == null || !recaptchaResponse.isSuccess() || (recaptchaResponse.getScore() != null && recaptchaResponse.getScore() < 0.5)) {
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

  // Log successful login
  auditService.logLogin(usuario.getEmail());

  String roleName = usuario.getRole() != null ? usuario.getRole().getNombre() : "OPERARIO";
  return new LoginResponse(token, usuario.getEmail(), roleName, usuario.getNombre());
 }

 @Transactional
 public UsuarioResponse register(RegisterRequest request) {
  // Validar reCAPTCHA en registro también
  validateRecaptcha(request.getRecaptchaToken());

  if (usuarioRepository.existsByEmail(request.getEmail())) {
   throw new DuplicateResourceException("El email ya está registrado");
  }

  Usuario usuario = new Usuario();
  usuario.setNombre(request.getNombre());
  usuario.setEmail(request.getEmail());
  usuario.setPassword(passwordEncoder.encode(request.getPassword()));

  // Asignar rol OPERARIO por defecto
  Role userRole = roleRepository.findByNombre("OPERARIO")
   .orElseGet(() -> {
    Role role = new Role();
    role.setNombre("OPERARIO");
    return roleRepository.save(role);
   });
  usuario.setRole(userRole);

  Usuario saved = usuarioRepository.save(usuario);

  try {
   emailService.sendWelcomeEmail(saved.getEmail(), saved.getNombre());
  } catch (Exception ex) {
   throw new IllegalStateException("No se pudo enviar el correo de bienvenida", ex);
  }

  return UsuarioResponse.fromEntity(saved);
 }
}
