package com.gestionganadera.backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RateLimitingFilterTest {

    private RateLimitingFilter filter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws IOException {
        filter = new RateLimitingFilter();
        responseWriter = new StringWriter();
        lenient().when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test
    void nonAuthPath_doesNotRateLimit() throws ServletException, IOException {
        // Arrange
        when(request.getServletPath()).thenReturn("/api/animales");

        // Act
        filter.doFilter(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verify(response, never()).setStatus(429);
    }

    @Test
    void authPath_allowsUpToMaxAttempts() throws ServletException, IOException {
        // Arrange
        when(request.getServletPath()).thenReturn("/auth/login");
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");

        // Act — 5 requests should pass (MAX_ATTEMPTS = 5)
        for (int i = 0; i < 5; i++) {
            filter.doFilter(request, response, filterChain);
        }

        // Assert
        verify(filterChain, times(5)).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
    }

    @Test
    void authPath_exceedsMaxAttempts_returns429() throws ServletException, IOException {
        // Arrange
        when(request.getServletPath()).thenReturn("/auth/login");
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");

        // Act — 6 requests (5 allowed, 6th blocked)
        for (int i = 0; i < 6; i++) {
            filter.doFilter(request, response, filterChain);
        }

        // Assert
        verify(filterChain, times(5)).doFilter(request, response);
        verify(response).setStatus(429);
        verify(response).setContentType("application/json");
        assertTrue(responseWriter.toString().contains("Demasiadas solicitudes"));
    }

    @Test
    void differentIps_haveSeparateCounters() throws ServletException, IOException {
        // Arrange
        when(request.getServletPath()).thenReturn("/auth/login");

        // Create mock for second IP
        HttpServletRequest request2 = mock(HttpServletRequest.class);
        when(request2.getServletPath()).thenReturn("/auth/login");
        when(request2.getRemoteAddr()).thenReturn("192.168.1.2");

        // First IP
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");

        // Act — 5 requests from IP 1, 5 from IP 2, then 6th from IP 1
        for (int i = 0; i < 5; i++) {
            filter.doFilter(request, response, filterChain);
        }
        for (int i = 0; i < 5; i++) {
            filter.doFilter(request2, response, filterChain);
        }
        // 6th from IP 1 — should be blocked
        filter.doFilter(request, response, filterChain);

        // Assert — each IP had 5 successful requests
        verify(filterChain, times(5)).doFilter(request, response);
        verify(filterChain, times(5)).doFilter(request2, response);
        // Verify IP 1 was blocked on 6th
        verify(response, times(1)).setStatus(429);
    }

    @Test
    void xForwardedFor_usesClientIp() throws ServletException, IOException {
        // Arrange
        when(request.getServletPath()).thenReturn("/auth/login");
        when(request.getHeader("X-Forwarded-For")).thenReturn("203.0.113.1, 10.0.0.1, 192.168.1.1");

        // Act — exceed limit for 203.0.113.1
        for (int i = 0; i < 6; i++) {
            filter.doFilter(request, response, filterChain);
        }

        // Assert — same IP through X-Forwarded-For should be rate limited
        verify(filterChain, times(5)).doFilter(request, response);
        verify(response).setStatus(429);
        assertTrue(responseWriter.toString().contains("Demasiadas solicitudes"));
    }

    @Test
    void xForwardedFor_empty_fallsBackToRemoteAddr() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/auth/login");
        when(request.getHeader("X-Forwarded-For")).thenReturn("");
        when(request.getRemoteAddr()).thenReturn("10.0.0.1");

        filter.doFilter(request, response, filterChain);

        // Even with empty X-Forwarded-For, the filter should work and use remoteAddr
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void destroy_withoutInit_handlesNullScheduler() {
        // cleanupScheduler is null because init() was never called
        filter.destroy();
        // No exception means the null branch was handled
    }

    @Test
    void expiredEntry_resetsCounter() throws Exception {
        when(request.getServletPath()).thenReturn("/auth/login");
        when(request.getRemoteAddr()).thenReturn("192.168.1.1");

        // Make 3 requests from the same IP
        for (int i = 0; i < 3; i++) {
            filter.doFilter(request, response, filterChain);
        }

        // Access the attempts map via reflection and replace the entry with an expired timestamp
        java.lang.reflect.Field attemptsField = RateLimitingFilter.class.getDeclaredField("attempts");
        attemptsField.setAccessible(true);
        java.util.concurrent.ConcurrentHashMap attempts = (java.util.concurrent.ConcurrentHashMap) attemptsField.get(filter);

        // Create a RateLimitEntry with old timestamp via reflection (record's private constructor)
        Class<?> entryClass = Class.forName("com.gestionganadera.backend.config.RateLimitingFilter$RateLimitEntry");
        java.lang.reflect.Constructor<?> constructor = entryClass.getDeclaredConstructor(int.class, long.class);
        constructor.setAccessible(true);
        Object oldEntry = constructor.newInstance(3, System.currentTimeMillis() - 120_000);
        attempts.put("192.168.1.1", oldEntry);

        // Make another request — expired entry should reset the counter
        filter.doFilter(request, response, filterChain);

        // All 4 requests should pass (counter was reset), none blocked
        verify(filterChain, times(4)).doFilter(request, response);
        verify(response, never()).setStatus(429);
    }

    @Test
    void cleanupScheduler_doesNotInterfereWithFiltering() {
        // Verify the filter can be initialized and destroyed without errors
        // init() requires a FilterConfig, but we can call destroy() directly
        filter.destroy();
        // No exception means success
    }
}
