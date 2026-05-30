package com.gestionganadera.backend.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(0)
public class SecurityHeadersFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        jakarta.servlet.http.HttpServletRequest httpRequest = (jakarta.servlet.http.HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String path = httpRequest.getRequestURI();

        // Evitar que las directivas estrictas de CSP e iframe bloqueen Swagger UI
        if (path != null && (path.contains("/swagger-ui") || path.contains("/v3/api-docs"))) {
            chain.doFilter(request, response);
            return;
        }

        // HSTS: force HTTPS for 1 year
        httpResponse.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");

        // CSP: mitigate XSS — only allow resources from same origin and trusted CDNs
        httpResponse.setHeader("Content-Security-Policy",
            "default-src 'self'; " +
            "script-src 'self' https://www.google.com https://www.gstatic.com; " +
            "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; " +
            "font-src 'self' https://fonts.gstatic.com; " +
            "img-src 'self' data:; " +
            "frame-src https://www.google.com; " +
            "connect-src 'self'");

        // Prevent MIME-type sniffing
        httpResponse.setHeader("X-Content-Type-Options", "nosniff");

        // Prevent clickjacking
        httpResponse.setHeader("X-Frame-Options", "DENY");

        // Referrer policy
        httpResponse.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

        // Disable caching for sensitive API responses
        httpResponse.setHeader("Cache-Control", "no-store, max-age=0");

        chain.doFilter(request, response);
    }
}
