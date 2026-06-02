package com.gestionganadera.backend.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class RateLimitingFilter implements Filter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String path = httpRequest.getRequestURI();
        String clientIp = getClientIp(httpRequest);
        
        // Rate limit auth endpoints
        if (isAuthEndpoint(path)) {
            Bucket bucket = buckets.computeIfAbsent(clientIp + ":" + path, k -> createNewBucket());
            
            if (bucket.tryConsume(1)) {
                chain.doFilter(request, response);
            } else {
                log.warn("Rate limit exceeded for {} from {}", path, clientIp);
                httpResponse.setStatus(429); // Too Many Requests
                httpResponse.getWriter().write("{\"error\": \"Too many requests. Try again in 1 minute.\"}");
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(1)));
        return Bucket4j.builder()
            .addLimit(limit)
            .build();
    }

    private boolean isAuthEndpoint(String path) {
        return path.contains("/auth/login") || 
               path.contains("/auth/register") || 
               path.contains("/auth/forgot-password") ||
               path.contains("/auth/reset-password");
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0];
        }
        return request.getRemoteAddr();
    }
}
