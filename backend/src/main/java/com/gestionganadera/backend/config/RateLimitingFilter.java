package com.gestionganadera.backend.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Component
@Order(1)
public class RateLimitingFilter implements Filter {

    private final Map<String, RateLimitEntry> attempts = new ConcurrentHashMap<>();
    private ScheduledExecutorService cleanupScheduler;

    // Max 5 attempts per IP per minute for auth endpoints
    private static final int MAX_ATTEMPTS = 5;
    private static final long WINDOW_MILLIS = TimeUnit.MINUTES.toMillis(1);

    @PostConstruct
    public void init() {
        cleanupScheduler = Executors.newSingleThreadScheduledExecutor();
        // Clean up stale entries every 5 minutes to prevent memory leaks
        cleanupScheduler.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            attempts.entrySet().removeIf(entry ->
                now - entry.getValue().timestamp() > WINDOW_MILLIS);
        }, 5, 5, TimeUnit.MINUTES);
    }

    @PreDestroy
    public void destroy() {
        if (cleanupScheduler != null) {
            cleanupScheduler.shutdown();
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String path = request.getRequestURI();
        // Only rate limit auth endpoints
        if (!path.contains("/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = getClientIp(request);
        long now = System.currentTimeMillis();

        RateLimitEntry entry = attempts.compute(clientIp, (key, existing) -> {
            if (existing == null || now - existing.timestamp() > WINDOW_MILLIS) {
                return new RateLimitEntry(1, now);
            }
            return new RateLimitEntry(existing.count() + 1, existing.timestamp());
        });

        if (entry.count() > MAX_ATTEMPTS) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("{\"status\":429,\"message\":\"Demasiadas solicitudes. Intenta de nuevo en 1 minuto.\",\"timestamp\":" + now + "}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private record RateLimitEntry(int count, long timestamp) {}
}
