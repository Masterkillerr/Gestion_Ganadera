package com.gestionganadera.backend.controller;

import com.gestionganadera.backend.config.TestAuthConfig;
import com.gestionganadera.backend.dto.LoginResponse;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for AuthController (register and login endpoints).
 *
 * Uses TestAuthConfig to override the real AuthService with one
 * that bypasses reCAPTCHA validation (which requires Google API calls).
 *
 * Tests the full register → login → JWT token flow that the frontend uses.
 */
@Import(TestAuthConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AuthIntegrationTest extends BaseIntegrationTest {

    @Test
    void register_ShouldCreateUserAndReturnUserResponse() {
        // Arrange
        String uniqueEmail = "newuser_" + System.currentTimeMillis() + "@test.com";
        String requestBody = String.format(
                "{\"nombre\":\"New User\",\"email\":\"%s\",\"password\":\"password123\"," +
                "\"recaptchaToken\":\"test-token\"}", uniqueEmail);

        // Act
        ResponseEntity<String> response = restClient.post()
                .uri("/auth/register")
                .headers(withJson())
                .body(requestBody)
                .retrieve()
                .toEntity(String.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        String body = response.getBody();
        assertNotNull(body);
        assertTrue(body.contains("\"email\":\"" + uniqueEmail + "\""),
                "Response should contain the registered email");
        assertTrue(body.contains("\"nombre\":\"New User\""),
                "Response should contain the user's name");
    }

    @Test
    void register_DuplicateEmail_ShouldReturnError() {
        // Arrange — use the email already created in BaseIntegrationTest.setUpBase()
        String requestBody = String.format(
                "{\"nombre\":\"Duplicate\",\"email\":\"%s\",\"password\":\"password123\"," +
                "\"recaptchaToken\":\"test-token\"}", TEST_USER_EMAIL);

        // Act
        ResponseEntity<String> response = restClient.post()
                .uri("/auth/register")
                .headers(withJson())
                .body(requestBody)
                .retrieve()
                .toEntity(String.class);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void register_WithInvalidEmail_ShouldReturn400() {
        // Act
        ResponseEntity<String> response = restClient.post()
                .uri("/auth/register")
                .headers(withJson())
                .body("{\"nombre\":\"Test\",\"email\":\"not-an-email\",\"password\":\"password123\"," +
                        "\"recaptchaToken\":\"test-token\"}")
                .retrieve()
                .toEntity(String.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void register_WithShortPassword_ShouldReturn400() {
        // Act — password must be at least 6 chars
        ResponseEntity<String> response = restClient.post()
                .uri("/auth/register")
                .headers(withJson())
                .body("{\"nombre\":\"Test\",\"email\":\"test@test.com\",\"password\":\"12345\"," +
                        "\"recaptchaToken\":\"test-token\"}")
                .retrieve()
                .toEntity(String.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void login_WithValidCredentials_ShouldReturnToken() {
        // Arrange — test user was created in BaseIntegrationTest.setUpBase()
        String requestBody = String.format(
                "{\"email\":\"%s\",\"password\":\"%s\",\"recaptchaToken\":\"test-token\"}",
                TEST_USER_EMAIL, TEST_USER_PASSWORD);

        // Act
        ResponseEntity<LoginResponse> response = restClient.post()
                .uri("/auth/login")
                .headers(withJson())
                .body(requestBody)
                .retrieve()
                .toEntity(LoginResponse.class);

        // Assert — verify the JWT token response structure
        assertEquals(HttpStatus.OK, response.getStatusCode());
        LoginResponse loginResponse = response.getBody();
        assertNotNull(loginResponse);
        assertNotNull(loginResponse.getToken(), "JWT token should be returned");
        assertTrue(loginResponse.getToken().length() > 20, "JWT token should be a long string");
        assertEquals(TEST_USER_EMAIL, loginResponse.getEmail());
        assertEquals("USER", loginResponse.getRol());
        assertEquals("Test User", loginResponse.getNombre());
    }

    @Test
    void login_WithWrongPassword_ShouldReturn401() {
        // Arrange
        String requestBody = String.format(
                "{\"email\":\"%s\",\"password\":\"wrongpassword\",\"recaptchaToken\":\"test-token\"}",
                TEST_USER_EMAIL);

        // Act
        ResponseEntity<String> response = restClient.post()
                .uri("/auth/login")
                .headers(withJson())
                .body(requestBody)
                .retrieve()
                .toEntity(String.class);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void login_WithNonExistentEmail_ShouldReturn401() {
        // Arrange
        String requestBody = "{\"email\":\"nonexistent@test.com\",\"password\":\"password123\"," +
                "\"recaptchaToken\":\"test-token\"}";

        // Act
        ResponseEntity<String> response = restClient.post()
                .uri("/auth/login")
                .headers(withJson())
                .body(requestBody)
                .retrieve()
                .toEntity(String.class);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    /**
     * Full auth lifecycle: Register → Login → Use JWT to access protected endpoint
     */
    @Test
    void authFullLifecycle_RegisterThenLoginThenAccessProtectedEndpoint() {
        // 1. Register a brand new user
        String uniqueEmail = "lifecycle_" + System.currentTimeMillis() + "@test.com";
        String registerBody = String.format(
                "{\"nombre\":\"Lifecycle User\",\"email\":\"%s\",\"password\":\"password123\"," +
                "\"recaptchaToken\":\"test-token\"}", uniqueEmail);

        ResponseEntity<String> registerResponse = restClient.post()
                .uri("/auth/register")
                .headers(withJson())
                .body(registerBody)
                .retrieve()
                .toEntity(String.class);
        assertEquals(HttpStatus.OK, registerResponse.getStatusCode());

        // 2. Login with the new user's credentials
        String loginBody = String.format(
                "{\"email\":\"%s\",\"password\":\"password123\",\"recaptchaToken\":\"test-token\"}",
                uniqueEmail);

        ResponseEntity<LoginResponse> loginResponse = restClient.post()
                .uri("/auth/login")
                .headers(withJson())
                .body(loginBody)
                .retrieve()
                .toEntity(LoginResponse.class);
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        assertNotNull(loginResponse.getBody());
        String jwtToken = loginResponse.getBody().getToken();

        // 3. Use the JWT to access a protected endpoint (GET /razas)
        ResponseEntity<String> protectedResponse = restClient.get()
                .uri("/razas")
                .headers(headers -> {
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.setBearerAuth(jwtToken);
                })
                .retrieve()
                .toEntity(String.class);
        assertEquals(HttpStatus.OK, protectedResponse.getStatusCode(),
                "JWT token from login should grant access to protected endpoints");
    }
}
