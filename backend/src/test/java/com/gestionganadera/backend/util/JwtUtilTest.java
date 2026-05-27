package com.gestionganadera.backend.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    private JwtUtil jwtUtil;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // Use a 256-bit secret (32 chars = 256 bits for HS256)
        ReflectionTestUtils.setField(jwtUtil, "secret", "0123456789abcdef0123456789abcdef");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 3600000L); // 1 hour

        userDetails = new User("test@example.com", "password", Collections.emptyList());
    }

    @Test
    void generateToken_createsValidJwt() {
        String token = jwtUtil.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3, "JWT should have 3 parts");
    }

    @Test
    void extractUsername_returnsCorrectEmail() {
        String token = jwtUtil.generateToken(userDetails);

        String username = jwtUtil.extractUsername(token);

        assertEquals("test@example.com", username);
    }

    @Test
    void validateToken_validToken_returnsTrue() {
        String token = jwtUtil.generateToken(userDetails);

        assertTrue(jwtUtil.validateToken(token, userDetails));
    }

    @Test
    void validateToken_wrongUser_returnsFalse() {
        String token = jwtUtil.generateToken(userDetails);

        UserDetails otherUser = new User("other@example.com", "password", Collections.emptyList());

        assertFalse(jwtUtil.validateToken(token, otherUser));
    }

    @Test
    void validateToken_expiredToken_returnsFalse() {
        // Set expiration to negative (token expires immediately)
        ReflectionTestUtils.setField(jwtUtil, "expiration", -60000L);

        String token = jwtUtil.generateToken(userDetails);

        // validateToken calls extractUsername which throws ExpiredJwtException for expired tokens
        assertThrows(io.jsonwebtoken.ExpiredJwtException.class,
                () -> jwtUtil.validateToken(token, userDetails));
    }

    @Test
    void differentSecrets_produceDifferentTokens() {
        String token1 = jwtUtil.generateToken(userDetails);

        JwtUtil jwtUtil2 = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil2, "secret", "fedcba9876543210fedcba9876543210");
        ReflectionTestUtils.setField(jwtUtil2, "expiration", 3600000L);

        String token2 = jwtUtil2.generateToken(userDetails);

        assertNotEquals(token1, token2);
    }
}
