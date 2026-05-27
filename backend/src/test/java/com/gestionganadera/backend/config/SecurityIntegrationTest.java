package com.gestionganadera.backend.config;

import com.gestionganadera.backend.dto.LoginRequest;
import com.gestionganadera.backend.dto.LoginResponse;
import com.gestionganadera.backend.model.Role;
import com.gestionganadera.backend.model.Usuario;
import com.gestionganadera.backend.repository.FincaRepository;
import com.gestionganadera.backend.repository.LoteRepository;
import com.gestionganadera.backend.repository.RoleRepository;
import com.gestionganadera.backend.repository.UsuarioRepository;
import com.gestionganadera.backend.service.AuthService;
import com.gestionganadera.backend.util.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureTestRestTemplate
class SecurityIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FincaRepository fincaRepository;

    @Autowired
    private LoteRepository loteRepository;

    @MockitoBean
    private AuthService authService;

    private String validToken;

    @BeforeEach
    void setUp() {
        // Create test role
        Role role = roleRepository.findByNombre("USER").orElseGet(() -> {
            Role newRole = new Role();
            newRole.setNombre("USER");
            return roleRepository.save(newRole);
        });

        // Create test user in H2
        Usuario testUser = new Usuario();
        testUser.setNombre("Test User");
        testUser.setEmail("test-security@" + System.nanoTime() + ".com");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser.setRole(role);
        testUser.setCreadoEn(LocalDateTime.now());
        testUser = usuarioRepository.save(testUser);

        // Generate a valid JWT token
        validToken = jwtUtil.generateToken(testUser);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        loteRepository.deleteAll();
        fincaRepository.deleteAll();
        usuarioRepository.deleteAll();
        roleRepository.deleteAll();
    }

    // ===== Public Endpoints =====

    @Test
    void loginEndpoint_allowsPublicAccess() {
        LoginResponse mockResponse = new LoginResponse("mock-token", "test@example.com", "USER", "Test User");
        when(authService.login(any(LoginRequest.class))).thenReturn(mockResponse);

        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setRecaptchaToken("test-token");

        ResponseEntity<LoginResponse> response = restTemplate.postForEntity(
            "/auth/login", request, LoginResponse.class);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(Objects.requireNonNull(response.getBody()).getToken());
        assertEquals("test@example.com", response.getBody().getEmail());
    }

    @Test
    void loginEndpoint_withInvalidBody_returns400() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>("{\"email\":\"bad\"}", headers);

        ResponseEntity<String> response = restTemplate.exchange(
            "/auth/login", HttpMethod.POST, entity, String.class);

        assertEquals(400, response.getStatusCode().value());
    }

    // ===== Secured Endpoints =====

    @Test
    void securedEndpoint_returns401WithoutAuth() {
        // No auth header → JwtAuthenticationFilter doesn't set authentication
        // → ExceptionTranslationFilter sends 401 (AuthenticationException)
        ResponseEntity<String> response = restTemplate.getForEntity("/fincas", String.class);

        assertEquals(401, response.getStatusCode().value(),
            "Without authentication, secured endpoints should return 401");
    }

    @Test
    void securedEndpoint_returns401WithInvalidToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("invalid-jwt-token");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
            "/fincas", HttpMethod.GET, entity, String.class);

        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    void securedEndpoint_returns200WithValidToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(validToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
            "/fincas", HttpMethod.GET, entity, String.class);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void securedEndpoint_returns200WithDifferentValidEndpoint() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(validToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
            "/lotes", HttpMethod.GET, entity, String.class);

        assertEquals(200, response.getStatusCode().value());
    }

    // ===== Security Headers =====

    @Test
    void responseIncludesSecurityHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(validToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
            "/fincas", HttpMethod.GET, entity, String.class);

        HttpHeaders responseHeaders = response.getHeaders();

        assertEquals("nosniff", responseHeaders.getFirst("X-Content-Type-Options"),
            "X-Content-Type-Options should be nosniff");

        assertEquals("DENY", responseHeaders.getFirst("X-Frame-Options"),
            "X-Frame-Options should be DENY");

        String csp = responseHeaders.getFirst("Content-Security-Policy");
        assertNotNull(csp, "Content-Security-Policy should be present");
    }

    // ===== CORS =====

    @Test
    void corsHeaders_allowedOrigin_allowsRequest() {
        HttpHeaders headers = new HttpHeaders();
        headers.setOrigin("http://localhost:5173");
        headers.setBearerAuth(validToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
            "/fincas", HttpMethod.GET, entity, String.class);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void corsHeaders_rejectsDisallowedOrigin() {
        HttpHeaders headers = new HttpHeaders();
        headers.setOrigin("http://evil-site.com");
        headers.setBearerAuth(validToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
            "/fincas", HttpMethod.GET, entity, String.class);

        // Spring's DefaultCorsProcessor returns 403 for disallowed origins
        assertEquals(403, response.getStatusCode().value(),
            "CORS should reject requests from non-whitelisted origins");
    }

    // ===== Admin Endpoints =====

    @Test
    void adminEndpoint_returns403ForUserRole() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(validToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
            "/usuarios", HttpMethod.GET, entity, String.class);

        // @PreAuthorize("hasRole('ADMIN')") should reject USER role → 403 (AccessDeniedException)
        assertEquals(403, response.getStatusCode().value(),
            "User with USER role should be denied access to admin endpoint");
    }

    @Test
    void accessDeniedHandler_viaFilterChain_returns403WithMessage() {
        // Hit /admin/* path directly (filter-chain-level hasRole check, NOT @PreAuthorize)
        // This exercises the accessDeniedHandler lambda in SecurityConfig
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(validToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
            "/admin/test", HttpMethod.GET, entity, String.class);

        assertEquals(403, response.getStatusCode().value());

        String body = Objects.requireNonNull(response.getBody());
        assertTrue(body.contains("403"), "Body should contain status 403: " + body);
        assertTrue(body.contains("Acceso denegado"),
            "Body should contain 'Acceso denegado' message: " + body);
    }
}
