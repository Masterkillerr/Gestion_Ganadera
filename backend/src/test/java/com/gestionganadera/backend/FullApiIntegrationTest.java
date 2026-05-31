package com.gestionganadera.backend;

import com.gestionganadera.backend.dto.CreateFincaRequest;
import com.gestionganadera.backend.dto.CreateLoteRequest;
import com.gestionganadera.backend.dto.LoginRequest;
import com.gestionganadera.backend.dto.LoginResponse;
import com.gestionganadera.backend.model.Finca;
import com.gestionganadera.backend.model.Lote;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureTestRestTemplate
class FullApiIntegrationTest {

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
    private HttpHeaders authHeaders;

    @BeforeEach
    void setUp() {
        // Create test role
        Role role = roleRepository.findByNombre("OPERARIO").orElseGet(() -> {
            Role newRole = new Role();
            newRole.setNombre("OPERARIO");
            return roleRepository.save(newRole);
        });

        // Create test user with unique email (H2 persists across tests unless dropped)
        Usuario testUser = new Usuario();
        testUser.setNombre("Test User");
        testUser.setEmail("test-crud-" + System.nanoTime() + "@example.com");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser.setRole(role);
        testUser.setCreadoEn(LocalDateTime.now());
        testUser = usuarioRepository.save(testUser);

        // Generate a valid JWT token
        validToken = jwtUtil.generateToken(testUser);

        // Prepare auth headers for all requests
        authHeaders = new HttpHeaders();
        authHeaders.setBearerAuth(validToken);
        authHeaders.setContentType(MediaType.APPLICATION_JSON);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        loteRepository.deleteAll();
        fincaRepository.deleteAll();
        usuarioRepository.deleteAll();
        roleRepository.deleteAll();
    }

    // ===== Auth Flow =====

    @Test
    void registerAndLogin_returnsCredentials() {
        LoginResponse mockLogin = new LoginResponse("mock-jwt-token", "newuser@example.com", "OPERARIO", "New User");
        when(authService.login(any(LoginRequest.class))).thenReturn(mockLogin);

        LoginRequest loginReq = new LoginRequest();
        loginReq.setEmail("newuser@example.com");
        loginReq.setPassword("password123");
        loginReq.setRecaptchaToken("test-token");

        ResponseEntity<LoginResponse> loginResp = restTemplate.postForEntity(
            "/auth/login", loginReq, LoginResponse.class);

        assertEquals(200, loginResp.getStatusCode().value());
        assertNotNull(Objects.requireNonNull(loginResp.getBody()).getToken());
        assertEquals("newuser@example.com", loginResp.getBody().getEmail());
        assertEquals("OPERARIO", loginResp.getBody().getRol(),
            "Role field should be 'OPERARIO' in the response");
    }

    // ===== Finca CRUD =====

    @Test
    void createAndGetFinca_returnsSavedFinca() {
        HttpEntity<CreateFincaRequest> entity = new HttpEntity<>(
            new CreateFincaRequest("Mi Finca", "Campo Verde", null), authHeaders);

        ResponseEntity<Finca> createResp = restTemplate.exchange(
            "/finca", HttpMethod.POST, entity, Finca.class);

        // Controller uses ResponseEntity.ok() → 200
        assertEquals(200, createResp.getStatusCode().value());
        assertNotNull(Objects.requireNonNull(createResp.getBody()).getId());
        assertEquals("Mi Finca", createResp.getBody().getNombre());
        assertEquals("Campo Verde", createResp.getBody().getUbicacion());

        // Verify it's accessible via GET (list)
        HttpEntity<Void> getEntity = new HttpEntity<>(authHeaders);
        ResponseEntity<List<Finca>> getResp = restTemplate.exchange(
            "/finca", HttpMethod.GET, getEntity,
            new ParameterizedTypeReference<List<Finca>>() {});

        assertEquals(200, getResp.getStatusCode().value());
        List<Finca> fincas = getResp.getBody();
        assertNotNull(fincas);
        assertTrue(fincas.stream().anyMatch(f -> "Mi Finca".equals(f.getNombre())));
    }

    @Test
    void createFinca_withoutAuth_returns401() {
        HttpEntity<CreateFincaRequest> entity = new HttpEntity<>(
            new CreateFincaRequest("No Auth", "Nowhere", null));

        ResponseEntity<String> response = restTemplate.exchange(
            "/finca", HttpMethod.POST, entity, String.class);

        assertEquals(401, response.getStatusCode().value(),
            "Unauthenticated requests should return 401");
    }

    @Test
    void updateFinca_modifiesExistingFinca() {
        // Create a finca first
        HttpEntity<CreateFincaRequest> createEntity = new HttpEntity<>(
            new CreateFincaRequest("Original", "Original Location", null), authHeaders);

        ResponseEntity<Finca> createResp = restTemplate.exchange(
            "/finca", HttpMethod.POST, createEntity, Finca.class);

        Integer fincaId = Objects.requireNonNull(createResp.getBody()).getId();

        // Update it
        HttpEntity<CreateFincaRequest> updateEntity = new HttpEntity<>(
            new CreateFincaRequest("Updated", "Updated Location", null), authHeaders);

        ResponseEntity<Finca> updateResp = restTemplate.exchange(
            "/finca/" + fincaId, HttpMethod.PUT, updateEntity, Finca.class);

        assertEquals(200, updateResp.getStatusCode().value());
        assertEquals("Updated", Objects.requireNonNull(updateResp.getBody()).getNombre());
        assertEquals("Updated Location", updateResp.getBody().getUbicacion());
    }

    @Test
    void deleteFinca_removesFinca() {
        // Create a finca first
        HttpEntity<CreateFincaRequest> createEntity = new HttpEntity<>(
            new CreateFincaRequest("To Delete", "Location", null), authHeaders);

        ResponseEntity<Finca> createResp = restTemplate.exchange(
            "/finca", HttpMethod.POST, createEntity, Finca.class);

        Integer fincaId = Objects.requireNonNull(createResp.getBody()).getId();

        // Delete it
        HttpEntity<Void> deleteEntity = new HttpEntity<>(authHeaders);
        ResponseEntity<Void> deleteResp = restTemplate.exchange(
            "/finca/" + fincaId, HttpMethod.DELETE, deleteEntity, Void.class);

        assertEquals(204, deleteResp.getStatusCode().value());

        // Verify it's gone
        HttpEntity<Void> getEntity = new HttpEntity<>(authHeaders);
        ResponseEntity<String> getResp = restTemplate.exchange(
            "/finca/" + fincaId, HttpMethod.GET, getEntity, String.class);

        assertEquals(404, getResp.getStatusCode().value());
    }

    // ===== Lote CRUD (requires finca) =====

    @Test
    void createLoteUnderFinca_returnsLote() {
        // Create a finca first
        HttpEntity<CreateFincaRequest> fincaEntity = new HttpEntity<>(
            new CreateFincaRequest("Finca con Lotes", "Ubicación", null), authHeaders);

        ResponseEntity<Finca> fincaResp = restTemplate.exchange(
            "/finca", HttpMethod.POST, fincaEntity, Finca.class);

        Integer fincaId = Objects.requireNonNull(fincaResp.getBody()).getId();

        // Create a lote under that finca
        CreateLoteRequest loteReq = new CreateLoteRequest();
        loteReq.setNombre("Lote 1");
        loteReq.setHectareas(BigDecimal.valueOf(50.5));
        loteReq.setCapacidadMaxima(100);
        loteReq.setTipoPasto("Ryegrass");
        loteReq.setEstado("Activo");
        loteReq.setFincaId(fincaId);

        HttpEntity<CreateLoteRequest> loteEntity = new HttpEntity<>(loteReq, authHeaders);

        ResponseEntity<Lote> loteResp = restTemplate.exchange(
            "/lote", HttpMethod.POST, loteEntity, Lote.class);

        // Controller uses ResponseEntity.ok() → 200
        assertEquals(200, loteResp.getStatusCode().value());
        assertNotNull(Objects.requireNonNull(loteResp.getBody()).getId());
        assertEquals("Lote 1", loteResp.getBody().getNombre());
    }

    // ===== CRUD Sequence =====

    @Test
    void fullCrudSequence_createMultipleAndQuery() {
        // Create 2 fincas
        HttpEntity<CreateFincaRequest> finca1 = new HttpEntity<>(
            new CreateFincaRequest("Finca A", "Norte", null), authHeaders);
        HttpEntity<CreateFincaRequest> finca2 = new HttpEntity<>(
            new CreateFincaRequest("Finca B", "Sur", null), authHeaders);

        restTemplate.exchange("/finca", HttpMethod.POST, finca1, Finca.class);
        restTemplate.exchange("/finca", HttpMethod.POST, finca2, Finca.class);

        // Query all fincas
        HttpEntity<Void> getEntity = new HttpEntity<>(authHeaders);
        ResponseEntity<List<Finca>> getResp = restTemplate.exchange(
            "/finca", HttpMethod.GET, getEntity,
            new ParameterizedTypeReference<List<Finca>>() {});

        assertEquals(200, getResp.getStatusCode().value());
        assertNotNull(getResp.getBody());
        assertEquals(2, getResp.getBody().size(),
            "Should return exactly 2 fincas created by the test user");
    }
}
