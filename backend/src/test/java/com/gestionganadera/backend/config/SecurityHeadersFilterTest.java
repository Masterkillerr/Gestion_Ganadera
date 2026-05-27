package com.gestionganadera.backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityHeadersFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private SecurityHeadersFilter filter;

    @Test
    void setsAllSecurityHeaders() throws ServletException, IOException {
        filter.doFilter(request, response, filterChain);

        verify(response).setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        verify(response).setHeader(eq("Content-Security-Policy"), anyString());
        verify(response).setHeader("X-Content-Type-Options", "nosniff");
        verify(response).setHeader("X-Frame-Options", "DENY");
        verify(response).setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        verify(response).setHeader("Cache-Control", "no-store, max-age=0");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void cspContainsGoogleScripts() throws ServletException, IOException {
        filter.doFilter(request, response, filterChain);

        verify(response).setHeader(eq("Content-Security-Policy"), argThat(csp ->
                csp.contains("https://www.google.com") &&
                csp.contains("https://www.gstatic.com") &&
                csp.contains("'self'")
        ));
    }
}
