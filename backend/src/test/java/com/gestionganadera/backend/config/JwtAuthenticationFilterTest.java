package com.gestionganadera.backend.config;

import com.gestionganadera.backend.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collections;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void noAuthHeader_passesThrough() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void invalidAuthHeader_passesThrough() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic token");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void validToken_authenticatesUser() throws Exception {
        String token = "Bearer valid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn(token);
        when(jwtUtil.extractUsername("valid.jwt.token")).thenReturn("user@example.com");

        UserDetails userDetails = new User("user@example.com", "pass", Collections.emptyList());
        when(userDetailsService.loadUserByUsername("user@example.com")).thenReturn(userDetails);
        when(jwtUtil.validateToken("valid.jwt.token", userDetails)).thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(jwtUtil).extractUsername("valid.jwt.token");
        verify(userDetailsService).loadUserByUsername("user@example.com");
        verify(jwtUtil).validateToken("valid.jwt.token", userDetails);
        // Verify authentication was set
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void invalidToken_doesNotAuthenticate() throws Exception {
        String token = "Bearer invalid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn(token);
        when(jwtUtil.extractUsername("invalid.jwt.token")).thenThrow(new RuntimeException("Invalid JWT"));

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        // No authentication should be set
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void expiredToken_doesNotAuthenticate() throws Exception {
        String token = "Bearer expired.jwt.token";
        when(request.getHeader("Authorization")).thenReturn(token);
        when(jwtUtil.extractUsername("expired.jwt.token")).thenReturn("user@example.com");

        UserDetails userDetails = new User("user@example.com", "pass", Collections.emptyList());
        when(userDetailsService.loadUserByUsername("user@example.com")).thenReturn(userDetails);
        when(jwtUtil.validateToken("expired.jwt.token", userDetails)).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void validTokenWithExistingAuth_doesNotReauthenticate() throws Exception {
        // Pre-set an authentication in the context
        SecurityContextHolder.getContext().setAuthentication(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        "existing", null, Collections.emptyList()));

        String token = "Bearer valid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn(token);
        when(jwtUtil.extractUsername("valid.jwt.token")).thenReturn("user@example.com");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        // Authentication should still be the pre-set one, not re-set
        assertEquals("existing",
                SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        verify(userDetailsService, never()).loadUserByUsername(any());
    }

    @Test
    void validTokenWithNullEmail_doesNotAuthenticate() throws Exception {
        String token = "Bearer token.with.null.email";
        when(request.getHeader("Authorization")).thenReturn(token);
        when(jwtUtil.extractUsername("token.with.null.email")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(userDetailsService, never()).loadUserByUsername(any());
    }
}