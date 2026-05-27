package com.gestionganadera.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestionganadera.backend.model.Role;
import com.gestionganadera.backend.model.Usuario;
import com.gestionganadera.backend.repository.RoleRepository;
import com.gestionganadera.backend.repository.UsuarioRepository;
import com.gestionganadera.backend.util.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

import java.util.function.Consumer;

/**
 * Base class for Spring Boot integration tests.
 * Sets up:
 * - A REST client pointed at the local test server
 * - A test user in the database
 * - A valid JWT token for authenticated requests
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @LocalServerPort
    private int port;

    protected final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    protected UsuarioRepository usuarioRepository;

    @Autowired
    protected RoleRepository roleRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected JwtUtil jwtUtil;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    /** REST client with no-op error handler (4xx/5xx don't throw). */
    protected RestClient restClient;

    protected String userToken;
    protected Usuario testUser;

    protected static final String TEST_USER_EMAIL = "testuser@example.com";
    protected static final String TEST_USER_PASSWORD = "testpass123";

    private static final JdkClientHttpRequestFactory REQUEST_FACTORY = new JdkClientHttpRequestFactory();

    @BeforeEach
    void setUpBase() {
        // Build RestClient that doesn't throw on error status codes
        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port + "/api")
                .requestFactory(REQUEST_FACTORY)
                .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
                    // No-op — swallow errors so we can assert status codes manually
                })
                .build();

        // Ensure roles exist
        Role userRole = roleRepository.findByNombre("USER").orElseGet(() -> {
            Role role = new Role();
            role.setNombre("USER");
            return roleRepository.save(role);
        });
        roleRepository.findByNombre("ADMIN").orElseGet(() -> {
            Role role = new Role();
            role.setNombre("ADMIN");
            return roleRepository.save(role);
        });

        // Create test USER
        if (!usuarioRepository.existsByEmail(TEST_USER_EMAIL)) {
            testUser = new Usuario();
            testUser.setNombre("Test User");
            testUser.setEmail(TEST_USER_EMAIL);
            testUser.setPassword(passwordEncoder.encode(TEST_USER_PASSWORD));
            testUser.setRole(userRole);
            testUser = usuarioRepository.save(testUser);
        } else {
            testUser = usuarioRepository.findByEmail(TEST_USER_EMAIL).orElseThrow();
        }

        // Generate JWT token
        userToken = jwtUtil.generateToken(testUser);
    }

    @AfterEach
    void tearDownBase() {
        // Clean up domain tables between tests to prevent data leakage.
        // Order matters: delete child tables before parent tables to respect FK constraints.
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");
        for (String table : TABLES_TO_CLEAR) {
            jdbcTemplate.execute("DELETE FROM " + table);
        }
        jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");
    }

    /** Tables to clear between test methods, in safe deletion order. */
    private static final String[] TABLES_TO_CLEAR = {
        "alimentaciones", "vacunaciones", "tratamientos", "reproducciones",
        "registro_terneros", "partos", "producciones", "movimientos",
        "alertas", "eventos", "animales", "lotes", "fincas",
        "razas", "categorias", "alimentos", "medicamentos", "vacunas",
        "usuarios", "roles"
    };

    // ── Header helpers ──

    /** Consumer that adds Bearer auth + JSON content type. */
    protected Consumer<HttpHeaders> withAuth() {
        return headers -> {
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(userToken);
        };
    }

    /** Consumer that adds just JSON content type (no auth). */
    protected Consumer<HttpHeaders> withJson() {
        return headers -> headers.setContentType(MediaType.APPLICATION_JSON);
    }
}
