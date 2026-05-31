package com.gestionganadera.backend.service;

import com.gestionganadera.backend.dto.LoginRequest;
import com.gestionganadera.backend.dto.LoginResponse;
import com.gestionganadera.backend.dto.RecaptchaResponse;
import com.gestionganadera.backend.dto.RegisterRequest;
import com.gestionganadera.backend.dto.UsuarioResponse;
import com.gestionganadera.backend.model.Role;
import com.gestionganadera.backend.model.Usuario;
import com.gestionganadera.backend.repository.RoleRepository;
import com.gestionganadera.backend.repository.UsuarioRepository;
import com.gestionganadera.backend.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest validRequest;
    private Role userRole;
    private Usuario savedUser;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "recaptchaSecret", "test-secret");

        validRequest = new RegisterRequest();
        validRequest.setNombre("Juan Pérez");
        validRequest.setEmail("juan@example.com");
        validRequest.setPassword("password123");
        validRequest.setRecaptchaToken("valid-token");

        userRole = new Role(1, "OPERARIO");

        savedUser = new Usuario();
        savedUser.setId(1);
        savedUser.setNombre("Juan Pérez");
        savedUser.setEmail("juan@example.com");
        savedUser.setPassword("encoded-password");
        savedUser.setRole(userRole);
        savedUser.setCreadoEn(LocalDateTime.now());
    }

    @Test
    void register_nullRecaptchaToken_throwsBadCredentials() {
        // with secret set, validation runs
        RegisterRequest request = new RegisterRequest();
        request.setNombre("Juan");
        request.setEmail("juan@example.com");
        request.setPassword("password123");
        request.setRecaptchaToken(null);

        BadCredentialsException exception = assertThrows(BadCredentialsException.class,
                () -> authService.register(request));
        assertEquals("Por favor completa el ReCAPTCHA", exception.getMessage());

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void register_emptyRecaptchaToken_throwsBadCredentials() {
        RegisterRequest request = new RegisterRequest();
        request.setNombre("Juan");
        request.setEmail("juan@example.com");
        request.setPassword("password123");
        request.setRecaptchaToken("");

        BadCredentialsException exception = assertThrows(BadCredentialsException.class,
                () -> authService.register(request));
        assertEquals("Por favor completa el ReCAPTCHA", exception.getMessage());

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void register_duplicateEmail_throwsRuntimeException() {
        when(usuarioRepository.existsByEmail(validRequest.getEmail())).thenReturn(true);

        RecaptchaResponse recaptchaResponse = new RecaptchaResponse();
        recaptchaResponse.setSuccess(true);

        try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
                (mock, context) -> {
                    when(mock.postForObject(anyString(), isNull(), eq(RecaptchaResponse.class)))
                            .thenReturn(recaptchaResponse);
                })) {

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> authService.register(validRequest));
            assertEquals("El email ya está registrado", exception.getMessage());
        }

        verify(usuarioRepository).existsByEmail("juan@example.com");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void register_success_returnsUsuarioResponse() {
        when(usuarioRepository.existsByEmail(validRequest.getEmail())).thenReturn(false);
        when(roleRepository.findByNombre("OPERARIO")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode(validRequest.getPassword())).thenReturn("encoded-password");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(savedUser);

        RecaptchaResponse recaptchaResponse = new RecaptchaResponse();
        recaptchaResponse.setSuccess(true);

        try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
                (mock, context) -> {
                    when(mock.postForObject(anyString(), isNull(), eq(RecaptchaResponse.class)))
                            .thenReturn(recaptchaResponse);
                })) {

            UsuarioResponse response = authService.register(validRequest);

            assertNotNull(response);
            assertEquals(savedUser.getId(), response.getId());
            assertEquals("Juan Pérez", response.getNombre());
            assertEquals("juan@example.com", response.getEmail());
            assertEquals("OPERARIO", response.getRole());
            assertNotNull(response.getCreadoEn());
        }

        verify(usuarioRepository).existsByEmail("juan@example.com");
        verify(roleRepository).findByNombre("OPERARIO");
        verify(passwordEncoder).encode("password123");
        verify(usuarioRepository).save(argThat(usuario ->
                usuario.getNombre().equals("Juan Pérez") &&
                usuario.getEmail().equals("juan@example.com") &&
                usuario.getPassword().equals("encoded-password") &&
                usuario.getRole().equals(userRole)
        ));
    }

    @Test
    void login_nullRecaptchaToken_throws() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("pass123");
        request.setRecaptchaToken(null);

        BadCredentialsException exception = assertThrows(BadCredentialsException.class,
                () -> authService.login(request));
        assertEquals("Por favor completa el ReCAPTCHA", exception.getMessage());
        verifyNoInteractions(authenticationManager);
    }

    @Test
    void login_invalidRecaptcha_throws() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("pass123");
        request.setRecaptchaToken("invalid-token");

        RecaptchaResponse recaptchaResponse = new RecaptchaResponse();
        recaptchaResponse.setSuccess(false);

        try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
                (mock, context) -> {
                    when(mock.postForObject(anyString(), isNull(), eq(RecaptchaResponse.class)))
                            .thenReturn(recaptchaResponse);
                })) {

            BadCredentialsException exception = assertThrows(BadCredentialsException.class,
                    () -> authService.login(request));
            assertEquals("ReCAPTCHA inválido", exception.getMessage());
        }

        verifyNoInteractions(authenticationManager);
    }

    @Test
    void login_success_returnsLoginResponse() {
        LoginRequest request = new LoginRequest();
        request.setEmail("juan@example.com");
        request.setPassword("password123");
        request.setRecaptchaToken("valid-token");

        RecaptchaResponse recaptchaResponse = new RecaptchaResponse();
        recaptchaResponse.setSuccess(true);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(savedUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtil.generateToken(savedUser)).thenReturn("test-jwt-token");

        try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
                (mock, context) -> {
                    when(mock.postForObject(anyString(), isNull(), eq(RecaptchaResponse.class)))
                            .thenReturn(recaptchaResponse);
                })) {

            LoginResponse response = authService.login(request);

            assertNotNull(response);
            assertEquals("test-jwt-token", response.getToken());
            assertEquals("juan@example.com", response.getEmail());
            assertEquals("OPERARIO", response.getRol());
            assertEquals("Juan Pérez", response.getNombre());
        }

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil).generateToken(savedUser);
    }

    @Test
    void login_withoutRole_usesDefaultRole() {
        savedUser.setRole(null);

        LoginRequest request = new LoginRequest();
        request.setEmail("juan@example.com");
        request.setPassword("password123");
        request.setRecaptchaToken("valid-token");

        RecaptchaResponse recaptchaResponse = new RecaptchaResponse();
        recaptchaResponse.setSuccess(true);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(savedUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtil.generateToken(savedUser)).thenReturn("test-jwt-token");

        try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
                (mock, context) -> {
                    when(mock.postForObject(anyString(), isNull(), eq(RecaptchaResponse.class)))
                            .thenReturn(recaptchaResponse);
                })) {

            LoginResponse response = authService.login(request);

            assertEquals("OPERARIO", response.getRol());
        }
    }

    @Test
    void register_invalidRecaptcha_throws() {
        RegisterRequest request = new RegisterRequest();
        request.setNombre("Juan");
        request.setEmail("juan@example.com");
        request.setPassword("password123");
        request.setRecaptchaToken("invalid-token");

        RecaptchaResponse recaptchaResponse = new RecaptchaResponse();
        recaptchaResponse.setSuccess(false);

        try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
                (mock, context) -> {
                    when(mock.postForObject(anyString(), isNull(), eq(RecaptchaResponse.class)))
                            .thenReturn(recaptchaResponse);
                })) {

            BadCredentialsException exception = assertThrows(BadCredentialsException.class,
                    () -> authService.register(request));
            assertEquals("ReCAPTCHA inválido", exception.getMessage());
        }

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void register_recaptchaResponseNull_throws() {
        RegisterRequest request = new RegisterRequest();
        request.setNombre("Juan");
        request.setEmail("juan@example.com");
        request.setPassword("password123");
        request.setRecaptchaToken("some-token");

        try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
                (mock, context) -> {
                    when(mock.postForObject(anyString(), isNull(), eq(RecaptchaResponse.class)))
                            .thenReturn(null);
                })) {

            BadCredentialsException exception = assertThrows(BadCredentialsException.class,
                    () -> authService.register(request));
            assertEquals("ReCAPTCHA inválido", exception.getMessage());
        }

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void register_success_createsRoleIfNotFound() {
        when(usuarioRepository.existsByEmail(validRequest.getEmail())).thenReturn(false);
        when(roleRepository.findByNombre("OPERARIO")).thenReturn(Optional.empty());
        when(roleRepository.save(any(Role.class))).thenReturn(userRole);
        when(passwordEncoder.encode(validRequest.getPassword())).thenReturn("encoded-password");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(savedUser);

        RecaptchaResponse recaptchaResponse = new RecaptchaResponse();
        recaptchaResponse.setSuccess(true);

        try (MockedConstruction<RestTemplate> mocked = mockConstruction(RestTemplate.class,
                (mock, context) -> {
                    when(mock.postForObject(anyString(), isNull(), eq(RecaptchaResponse.class)))
                            .thenReturn(recaptchaResponse);
                })) {

            UsuarioResponse response = authService.register(validRequest);

            assertNotNull(response);
            assertEquals("Juan Pérez", response.getNombre());

            verify(roleRepository).save(argThat(role -> role.getNombre().equals("OPERARIO")));
        }
    }
}
