package com.gestionganadera.backend.config;

import com.gestionganadera.backend.dto.LoginRequest;
import com.gestionganadera.backend.dto.LoginResponse;
import com.gestionganadera.backend.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Isolated test for rate limiting. Uses @DirtiesContext to reset the
 * shared RateLimitingFilter singleton state (ConcurrentHashMap) after
 * the test completes.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureTestRestTemplate
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class RateLimitingIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockitoBean
    private AuthService authService;

    @Test
    void rateLimiting_blocksAfterMaxAttempts() {
        LoginResponse mockResponse = new LoginResponse("mock-token", "test@example.com", "USER", "Test User");
        when(authService.login(any(LoginRequest.class))).thenReturn(mockResponse);

        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setRecaptchaToken("test-token");

        // Send auth requests rapidly (MAX_ATTEMPTS=5, so 7th should be blocked)
        ResponseEntity<String> lastResponse = null;
        for (int i = 0; i < 7; i++) {
            lastResponse = restTemplate.postForEntity(
                "/auth/login", request, String.class);
        }

        // The 7th request should be rate-limited (429)
        assertNotNull(lastResponse);
        assertEquals(429, lastResponse.getStatusCode().value(),
            "Excessive requests should be rate-limited with 429");

        String body = Objects.requireNonNull(lastResponse.getBody());
        assertTrue(body.contains("Demasiadas solicitudes"),
            "Rate limit message should be in the response body: " + body);
    }
}
