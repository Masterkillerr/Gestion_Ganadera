package com.gestionganadera.backend;

import com.gestionganadera.backend.config.ApplicationConfig;
import com.gestionganadera.backend.config.JwtAuthenticationFilter;
import com.gestionganadera.backend.config.RateLimitingFilter;
import com.gestionganadera.backend.config.SecurityConfig;
import com.gestionganadera.backend.config.SecurityHeadersFilter;
import com.gestionganadera.backend.exception.GlobalExceptionHandler;
import com.gestionganadera.backend.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class BackendApplicationIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @LocalServerPort
    private int port;

    @Test
    void contextLoads() {
        assertNotNull(applicationContext);
    }

    @Test
    void serverIsRunning() {
        assertTrue(port > 0, "Server should be assigned a random port");
    }

    @Test
    void securityConfigBeanIsLoaded() {
        assertNotNull(applicationContext.getBean(SecurityConfig.class));
    }

    @Test
    void applicationConfigBeanIsLoaded() {
        assertNotNull(applicationContext.getBean(ApplicationConfig.class));
    }

    @Test
    void jwtUtilBeanIsLoaded() {
        assertNotNull(applicationContext.getBean(JwtUtil.class));
    }

    @Test
    void jwtAuthFilterBeanIsLoaded() {
        assertNotNull(applicationContext.getBean(JwtAuthenticationFilter.class));
    }

    @Test
    void rateLimitingFilterBeanIsLoaded() {
        assertNotNull(applicationContext.getBean(RateLimitingFilter.class));
    }

    @Test
    void securityHeadersFilterBeanIsLoaded() {
        assertNotNull(applicationContext.getBean(SecurityHeadersFilter.class));
    }

    @Test
    void globalExceptionHandlerBeanIsLoaded() {
        assertNotNull(applicationContext.getBean(GlobalExceptionHandler.class));
    }

    @Test
    void restTemplateCanBeCreated() {
        // Verify that basic HTTP infrastructure works
        RestTemplate restTemplate = new RestTemplate();
        assertNotNull(restTemplate);
    }
}
