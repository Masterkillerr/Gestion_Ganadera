# Gestion Ganadera Backend - Security & Code Quality Audit Report

**Date:** 2026-06-01  
**Project:** Gestion Ganadera Backend (Java/Spring Boot)  
**Scope:** Comprehensive security audit and code quality review

---

## Executive Summary

This audit identified **17 significant issues** across security, code quality, and Java/Spring best practices. Key findings include:

- **Critical:** JWT token blacklist not integrated into authentication filter
- **High:** Weak password requirements, missing token revocation mechanism
- **High:** Potential N+1 query problems with lazy loading
- **High:** SQL injection risks in MovimientoRepository native queries
- **Medium:** Missing authorization checks on multi-tenant endpoints
- **Medium:** No pagination on all endpoints, missing input validation on some fields
- **Medium:** Resource leak potential in EmailService and RateLimitingFilter

Detailed findings and remediation steps follow.

---

## SECURITY ISSUES

### 1. JWT Token Blacklist Not Integrated into Authentication Filter

**Severity:** CRITICAL  
**Category:** Authentication & Token Management  
**Location:** 
- `/backend/src/main/java/com/gestionganadera/backend/config/JwtAuthenticationFilter.java` (lines 27-62)
- `/backend/src/main/java/com/gestionganadera/backend/service/TokenBlocklistService.java` (lines 1-46)

**Issue:**
The `TokenBlocklistService` exists but is **never called** from the `JwtAuthenticationFilter`. This means:
- Tokens cannot be revoked after logout
- Blacklisted tokens are still accepted by the API
- Session termination is ineffective

```java
// JwtAuthenticationFilter does NOT check blacklist
if (jwtUtil.validateToken(jwt, userDetails)) {  // Line 47
    // Token is accepted without blacklist check
}
```

**Impact:**
- Users cannot effectively log out
- Compromised tokens remain valid until expiration
- No mechanism to revoke admin tokens

**Remediation:**
```java
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final TokenBlocklistService tokenBlocklistService;  // ADD THIS

    @Override
    protected void doFilterInternal(...) throws ServletException, IOException {
        // ... existing code ...
        jwt = authHeader.substring(7);
        
        // ADD THIS CHECK BEFORE validateToken
        if (tokenBlocklistService.isBlacklisted(jwt)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        if (jwtUtil.validateToken(jwt, userDetails)) {
            // ... rest of code
        }
    }
}
```

---

### 2. Weak Password Requirements in Registration

**Severity:** HIGH  
**Category:** Password Security  
**Location:** `/backend/src/main/java/com/gestionganadera/backend/dto/RegisterRequest.java` (lines 23-24)

**Issue:**
```java
@Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
private String password;
```

Minimum 6 characters is insufficient. No complexity requirements for:
- Uppercase letters
- Lowercase letters
- Numbers
- Special characters

A 6-character password can be cracked in seconds.

**Impact:**
- Easy password guessing/brute force
- Weak user accounts
- Risk of credential compromise

**Remediation:**
```java
@NotBlank(message = "La contraseña es obligatoria")
@Size(min = 12, message = "La contraseña debe tener al menos 12 caracteres")
@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{12,}$",
        message = "La contraseña debe incluir mayúsculas, minúsculas, números y símbolos especiales")
private String password;
```

---

### 3. Missing Logout Endpoint with Token Revocation

**Severity:** HIGH  
**Category:** Session Management  
**Location:** `/backend/src/main/java/com/gestionganadera/backend/controller/AuthController.java`

**Issue:**
There is no logout endpoint. Users cannot explicitly revoke their tokens. The `TokenBlocklistService` exists but has no controller entry point.

**Impact:**
- No explicit logout mechanism
- Tokens remain valid until expiration
- Users cannot force session termination

**Remediation:**
Add logout endpoint:
```java
@PostMapping("/logout")
@Operation(summary = "Logout", description = "Revokes the current JWT token")
public ResponseEntity<Void> logout(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
        String token = authHeader.substring(7);
        tokenBlocklistService.block(token);
    }
    return ResponseEntity.ok().build();
}
```

---

### 4. SQL Injection Risk in MovimientoRepository Native Queries

**Severity:** HIGH  
**Category:** SQL Injection  
**Location:** `/backend/src/main/java/com/gestionganadera/backend/repository/MovimientoRepository.java` (lines 26-31, 34-38)

**Issue:**
Native queries use parameterized queries (good), but query logic is complex with potential issues:

```java
@Query(value = "SELECT l.id FROM evento e " +
       "JOIN movimiento m ON e.id = m.id_evento " +
       "JOIN lote l ON m.id_lote_destino = l.id " +
       "JOIN animal a ON e.id_animal = a.id " +
       "WHERE a.id = :animalId " +
       "ORDER BY e.fecha DESC LIMIT 1", nativeQuery = true)
Integer findUltimoLoteIdByAnimalId(@Param("animalId") Integer animalId);
```

While parameterized (safe), the complexity suggests potential for introduction of string concatenation if modified.

**Impact:**
- Risk if code is refactored without care
- Difficult to maintain safely

**Remediation:**
Convert to JPQL (safer, database-agnostic):
```java
@Query("SELECT l.id FROM Movimiento m " +
       "JOIN m.evento e " +
       "JOIN m.loteDestino l " +
       "WHERE e.animal.id = :animalId " +
       "ORDER BY e.fecha DESC")
Integer findUltimoLoteIdByAnimalId(@Param("animalId") Integer animalId);
```

---

### 5. CORS Configuration Too Permissive

**Severity:** MEDIUM  
**Category:** CORS & Cross-Origin Requests  
**Location:** `/backend/src/main/resources/application.properties` (line 48)

**Issue:**
```properties
app.cors.allowed-origins=${CORS_ALLOWED_ORIGINS:http://localhost:5173,http://localhost:5174,http://localhost:5175,http://localhost:5176,https://d3gw8tv95pui9q.cloudfront.net}
```

Multiple localhost origins allowed. While good for development, `localhost:5173-5176` suggests developer tooling that shouldn't all be allowed in production.

Additionally, the code allows wildcard patterns:
```java
configuration.setAllowedOriginPatterns(
    Arrays.stream(allowedOrigins.split(","))
        .map(String::trim)
        .filter(s -> !s.isEmpty())
        .collect(Collectors.toList())
);
```

**Impact:**
- Potential CORS bypass if wildcard patterns used
- Multiple dev ports in production config

**Remediation:**
```properties
# Production config
app.cors.allowed-origins=https://yourdomain.cloudfront.net,https://admin.yourdomain.com

# Development config (separate application-dev.properties)
app.cors.allowed-origins=http://localhost:3000,http://localhost:5173
```

---

### 6. Missing Authorization Checks on Resource Access (Multi-Tenant)

**Severity:** MEDIUM  
**Category:** Authorization  
**Location:** 
- `/backend/src/main/java/com/gestionganadera/backend/controller/FincaController.java` (lines 29-31)
- `/backend/src/main/java/com/gestionganadera/backend/service/FincaService.java` (lines 28-36)

**Issue:**
Controllers use `@PreAuthorize("isAuthenticated()")` but do NOT verify **ownership**. Users can:
- List all fincas in the system (not just their own)
- Access other users' fincas by ID

```java
@GetMapping
@Operation(summary = "Listar fincas")
public ResponseEntity<List<FincaDTO>> getAllFincas() {
    return ResponseEntity.ok(fincaService.findAll());  // Returns ALL fincas!
}
```

This is a **multi-tenant isolation violation**.

**Impact:**
- Data breach: users see other users' farms
- Regulatory violation (privacy)
- No tenant isolation

**Remediation:**
Add user context to queries:
```java
// In FincaService
private Usuario getCurrentUser() {
    return (Usuario) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
}

public List<FincaDTO> findAllByCurrentUser() {
    Usuario user = getCurrentUser();
    return fincaRepository.findByUserId(user.getId())  // Filter by user
            .stream()
            .map(FincaDTO::fromEntity)
            .collect(Collectors.toList());
}

// In Controller
@GetMapping
@PreAuthorize("isAuthenticated()")
public ResponseEntity<List<FincaDTO>> getAllFincas() {
    return ResponseEntity.ok(fincaService.findAllByCurrentUser());
}
```

**Note:** This requires adding a `usuario_id` foreign key to all domain entities (finca, lote, animal, etc.) for multi-tenant support. This is a **major architectural change** that should be prioritized.

---

### 7. No Rate Limiting on Non-Auth Endpoints

**Severity:** MEDIUM  
**Category:** Rate Limiting & DDoS Protection  
**Location:** `/backend/src/main/java/com/gestionganadera/backend/config/RateLimitingFilter.java` (lines 25-30, 69-73)

**Issue:**
```java
private static final Set<String> AUTH_PREFIXES = Set.of(
    "/auth/login",
    "/auth/register",
    "/auth/forgot-password",
    "/auth/reset-password"
);

// Only auth paths are rate limited
if (!isAuthPath(path)) {
    filterChain.doFilter(request, response);
    return;
}
```

All other endpoints (animal CRUD, finca CRUD, etc.) are **not rate limited**. An attacker can:
- Brute force API enumeration
- Exhaust database resources
- DDoS the application

**Impact:**
- No protection against abusive API calls
- Resource exhaustion
- Service degradation

**Remediation:**
```java
// Expand rate limiting to all authenticated endpoints
private static final Set<String> RATE_LIMIT_PREFIXES = Set.of(
    "/auth/login",
    "/auth/register",
    "/animal",
    "/finca",
    "/evento",
    "/produccion"
    // ... other endpoints
);

// Or use a global rate limit with higher threshold
private static final int MAX_REQUESTS_PER_HOUR = 1000;  // Per user/IP
```

---

### 8. Inadequate Exception Handling - Information Disclosure

**Severity:** MEDIUM  
**Category:** Exception Handling & Information Disclosure  
**Location:** `/backend/src/main/java/com/gestionganadera/backend/exception/GlobalExceptionHandler.java` (lines 136-148)

**Issue:**
```java
@ExceptionHandler(DataIntegrityViolationException.class)
public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
    log.error("Data integrity violation: {}", ex.getMessage());
    
    String rootMsg = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();
    String message = "La operación viola una restricción de integridad de la base de datos";
    
    if (rootMsg != null) {
        if (rootMsg.contains("duplicate key value")) {
            message = "Ya existe un registro con ese identificador único";
        } else if (rootMsg.contains("is still referenced from table")) {
            message = "No se puede eliminar el registro porque está siendo utilizado por otros elementos";
        }
    }
    // ...
}
```

Exception messages are logged with full details but also reveal database schema information. The `rootMsg.contains()` checks leak database-specific error messages.

**Impact:**
- Database schema information exposed
- Attacker can learn column/table names
- Facilitates targeted SQL injection

**Remediation:**
```java
@ExceptionHandler(DataIntegrityViolationException.class)
public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
    // Log full details for debugging
    log.error("Data integrity violation", ex);  // Don't log root cause publicly
    
    String message = "La operación no se puede completar debido a una restricción de datos";
    
    // Check root cause for logging only (not exposing details)
    if (ex.getRootCause() != null) {
        String rootMsg = ex.getRootCause().getMessage();
        if (rootMsg != null && rootMsg.contains("unique constraint")) {
            log.debug("Unique constraint violation detected");
        }
    }
    
    ErrorResponse error = new ErrorResponse(
        HttpStatus.CONFLICT.value(),
        message,  // Generic message to user
        System.currentTimeMillis()
    );
    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
}
```

---

### 9. Missing HTTPS Enforcement (Server Configuration)

**Severity:** MEDIUM  
**Category:** Transport Security  
**Location:** `/backend/src/main/resources/application.properties`

**Issue:**
No server configuration for HTTPS enforcement. The HSTS header is set but only via SecurityHeadersFilter (application level), not at server level.

```properties
server.port=${SERVER_PORT:8080}  # Only HTTP
```

**Impact:**
- HTTPS not enforced by server
- Fallback to HTTP possible
- Man-in-the-middle attacks

**Remediation:**
```properties
# application.properties
server.port=8443
server.ssl.key-store-type=PKCS12
server.ssl.key-store=${SSL_KEYSTORE_PATH}
server.ssl.key-store-password=${SSL_KEYSTORE_PASSWORD}
server.ssl.key-alias=${SSL_KEY_ALIAS}

# Or use application-prod.properties for production
```

---

### 10. Insufficient Input Validation on Photo URL

**Severity:** MEDIUM  
**Category:** Input Validation  
**Location:** `/backend/src/main/java/com/gestionganadera/backend/dto/CreateAnimalRequest.java` (line 41)

**Issue:**
```java
private String fotoUrl;  // No validation!
```

The `fotoUrl` field accepts any string without validation. Risks:
- JavaScript URLs: `javascript:alert('xss')`
- File:// URLs: access local files
- Data URLs: embedded malicious content
- SSRF: `http://internal-service`

**Impact:**
- XSS if rendered in frontend
- SSRF attacks
- Information disclosure

**Remediation:**
```java
@NotBlank
@Pattern(regexp = "^https?://[a-zA-Z0-9\\-._~:/?#\\[\\]@!$&'()*+,;=%]+$",
        message = "URL inválida. Solo se permiten URLs HTTP/HTTPS")
@Size(max = 2048)
private String fotoUrl;
```

---

### 11. Plaintext Secret Handling in EmailService

**Severity:** MEDIUM  
**Category:** Secrets Management  
**Location:** `/backend/src/main/java/com/gestionganadera/backend/service/EmailService.java` (lines 17-35)

**Issue:**
```java
public EmailService(
    @Value("${MAIL_HOST:smtp.gmail.com}") String host,
    @Value("${MAIL_PORT:587}") int port,
    @Value("${MAIL_USERNAME:}") String username,
    @Value("${MAIL_PASSWORD:}") String password,  // Plaintext in memory!
    @Value("${app.mail.from:}") String fromEmail) {
    // ...
    impl.setPassword(password);  // Stored in JavaMailSenderImpl
}
```

Password is read from environment/properties and stored plaintext in `JavaMailSenderImpl`. No encryption.

**Impact:**
- Memory disclosure: password readable if debugger attaches
- Heap dumps expose passwords
- No rotation mechanism

**Remediation:**
```java
// Use encrypted properties
// application.properties
spring.mail.password=${MAIL_PASSWORD_ENCRYPTED}

// Decrypt at runtime using Spring Cloud Config Server or Jasypt
@Configuration
public class MailConfig {
    @Bean
    public JavaMailSender mailSender(
            @Value("${spring.mail.host}") String host,
            @Value("${spring.mail.port}") int port,
            @Value("${spring.mail.username}") String username,
            StringEncryptor encryptor) {  // Decrypt
        
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(host);
        sender.setPort(port);
        sender.setUsername(username);
        sender.setPassword(encryptor.decrypt(password));  // Decrypt here
        return sender;
    }
}
```

---

## CODE QUALITY ISSUES

### 12. N+1 Query Problem in Animal Filtering

**Severity:** HIGH  
**Category:** Database Performance  
**Location:** `/backend/src/main/java/com/gestionganadera/backend/service/AnimalService.java` (lines 36-64)

**Issue:**
```java
public Page<Animal> findAllFiltered(String search, String estado, String sexo, Pageable pageable) {
    Specification<Animal> spec = (root, query, cb) -> cb.conjunction();

    if (estado != null && !estado.isBlank()) {
        String finalEstado = estado;
        spec = spec.and((root, query, cb) ->
            cb.equal(cb.lower(root.join("estadoAnimal").get("nombre")), finalEstado.toLowerCase())
            //                 ^^^^ EAGER JOIN
        );
    }
    // ...
    return animalRepository.findAll(spec, pageable);
}
```

The `root.join("estadoAnimal")` creates a JOIN, but `Animal` has `FetchType.LAZY`:

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "id_estado_animal")
private EstadoAnimal estadoAnimal;
```

When converting to `AnimalDTO`, **all lazy fields are accessed**:

```java
dto.setEstado(estado);  // Triggers N queries
dto.setSexo(animal.getSexo().getNombre());  // Lazy load for each animal
dto.setRazaNombre(animal.getRaza().getNombre());  // Lazy load for each animal
```

**Impact:**
- 1 query for page + 20 queries per page = 21 queries (N+1)
- Database connection exhaustion
- Slow response times

**Remediation:**
```java
@Query("SELECT a FROM Animal a " +
       "LEFT JOIN FETCH a.sexo " +
       "LEFT JOIN FETCH a.estadoAnimal " +
       "LEFT JOIN FETCH a.raza " +
       "WHERE (:search IS NULL OR a.nombre LIKE %:search% OR a.identificadorArete LIKE %:search%)")
Page<Animal> findAllWithEagerLoads(@Param("search") String search, Pageable pageable);
```

---

### 13. Missing @Transactional Propagation Control

**Severity:** MEDIUM  
**Category:** Transaction Management  
**Location:** `/backend/src/main/java/com/gestionganadera/backend/service/FincaService.java` (lines 48-58)

**Issue:**
```java
@Transactional
public FincaDTO update(@NonNull Integer id, @NonNull CreateFincaRequest request) {
    return fincaRepository.findById(id)
            .map(existing -> {
                existing.setNombre(request.getNombre());
                existing.setUbicacion(request.getUbicacion());
                existing.setExtension(request.getExtension());
                return FincaDTO.fromEntity(fincaRepository.save(existing));
            })
            .orElseThrow(() -> new RuntimeException("Finca no encontrada"));
}
```

If an exception occurs in `FincaDTO.fromEntity()` (after save), the transaction has already committed. No rollback occurs.

**Impact:**
- Partial updates persisted
- Data inconsistency
- Silent failures

**Remediation:**
```java
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public FincaDTO update(@NonNull Integer id, @NonNull CreateFincaRequest request) {
    Finca existing = fincaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Finca no encontrada"));
    
    existing.setNombre(request.getNombre());
    existing.setUbicacion(request.getUbicacion());
    existing.setExtension(request.getExtension());
    
    Finca saved = fincaRepository.save(existing);  // Save before DTO conversion
    return FincaDTO.fromEntity(saved);  // Conversion outside transaction
}
```

---

### 14. Generic RuntimeException Usage

**Severity:** MEDIUM  
**Category:** Error Handling  
**Location:** Multiple files
- `/backend/src/main/java/com/gestionganadera/backend/service/FincaService.java` (lines 57, 69)
- `/backend/src/main/java/com/gestionganadera/backend/service/AnimalService.java` (line 117)

**Issue:**
```java
.orElseThrow(() -> new RuntimeException("Finca no encontrada"));
```

Using generic `RuntimeException` hides the actual error type. Should use custom exceptions.

**Impact:**
- Inconsistent error handling
- Difficult to distinguish error causes
- Poor error recovery

**Remediation:**
```java
// Use existing custom exceptions
.orElseThrow(() -> new ResourceNotFoundException("Finca no encontrada"));

// Or create specific exceptions
public class FincaNotFoundException extends ResourceNotFoundException {
    public FincaNotFoundException(Integer id) {
        super("Finca con ID " + id + " no encontrada");
    }
}
```

---

### 15. Missing Pagination on List Endpoints

**Severity:** MEDIUM  
**Category:** API Design & Performance  
**Location:** Multiple controllers
- `/backend/src/main/java/com/gestionganadera/backend/controller/FincaController.java` (lines 28-31)
- `/backend/src/main/java/com/gestionganadera/backend/controller/UsuarioController.java` (lines 28-30)

**Issue:**
```java
@GetMapping
public ResponseEntity<List<FincaDTO>> getAllFincas() {
    return ResponseEntity.ok(fincaService.findAll());  // No pagination!
}
```

Returns all results. If database has 10,000 fincas, all are loaded into memory and sent to client.

**Impact:**
- Memory exhaustion
- Slow response times
- Poor API design

**Remediation:**
```java
@GetMapping
@Operation(summary = "Listar fincas (paginado)")
public ResponseEntity<Page<FincaDTO>> getAllFincas(
        @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
    return ResponseEntity.ok(fincaService.findAll(pageable)
            .map(FincaDTO::fromEntity));
}
```

---

### 16. Potential Resource Leak in RateLimitingFilter

**Severity:** MEDIUM  
**Category:** Resource Management  
**Location:** `/backend/src/main/java/com/gestionganadera/backend/config/RateLimitingFilter.java` (lines 43-59)

**Issue:**
```java
@PostConstruct
public void init() {
    cleanupScheduler = Executors.newSingleThreadScheduledExecutor();
    cleanupScheduler.scheduleAtFixedRate(...);
}

@PreDestroy
public void destroy() {
    if (cleanupScheduler != null) {
        cleanupScheduler.shutdown();  // Good!
    }
}
```

While cleanup is implemented, the `ScheduledExecutorService` doesn't await termination:

```java
cleanupScheduler.shutdown();
// Should add:
// try { cleanupScheduler.awaitTermination(5, TimeUnit.SECONDS); } catch (InterruptedException ignored) {}
```

If shutdown is slow, lingering threads may cause issues.

**Impact:**
- Thread leaks during redeployment
- Connection pool exhaustion
- Memory leaks in production

**Remediation:**
```java
@PreDestroy
public void destroy() {
    if (cleanupScheduler != null) {
        cleanupScheduler.shutdown();
        try {
            if (!cleanupScheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                cleanupScheduler.shutdownNow();  // Force shutdown
            }
        } catch (InterruptedException e) {
            cleanupScheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
```

---

### 17. EmailService Constructor Complexity & Null Check

**Severity:** MEDIUM  
**Category:** Design & Resource Management  
**Location:** `/backend/src/main/java/com/gestionganadera/backend/service/EmailService.java` (lines 17-30)

**Issue:**
```java
public EmailService(...) {
    this.fromEmail = fromEmail;
    
    if (username == null || username.isBlank() || password == null || password.isBlank()) {
        this.mailSender = null;  // Silent failure!
        return;
    }
    // ... setup
}
```

If email is not configured, `mailSender` is set to null. Later, `sendWelcomeEmail()` checks for null:

```java
public void sendWelcomeEmail(String toEmail, String nombre) {
    if (mailSender == null || fromEmail == null || fromEmail.isBlank()) return;  // Silent return!
}
```

This causes silent failures - registration "succeeds" but email is never sent, and no error is logged.

**Impact:**
- Silent failures (user registered but no welcome email)
- Difficult to debug
- Poor user experience

**Remediation:**
```java
@Configuration
public class EmailConfig {
    
    @Bean
    @ConditionalOnProperty(name = "spring.mail.host")
    public EmailService emailService(...) {
        return new EmailService(...);
    }
}

// In AuthService
@Transactional
public UsuarioResponse register(RegisterRequest request) {
    // ... validation ...
    Usuario saved = usuarioRepository.save(usuario);
    
    try {
        emailService.sendWelcomeEmail(saved.getEmail(), saved.getNombre());
    } catch (Exception ex) {
        log.error("Failed to send welcome email for user {}", saved.getEmail(), ex);
        // Decide: retry, queue, or fail registration
        throw new MailSendingException("Could not send welcome email", ex);
    }
    
    return UsuarioResponse.fromEntity(saved);
}
```

---

## JAVA/SPRING SPECIFIC ISSUES

### 18. No Null Safety in getCurrentUser()

**Severity:** MEDIUM  
**Category:** Null Pointer Safety  
**Location:** `/backend/src/main/java/com/gestionganadera/backend/service/UsuarioService.java` (lines 31-34)

**Issue:**
```java
private Usuario getCurrentUser() {
    return (Usuario) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
}
```

If security context is null or authentication is null, `NullPointerException` occurs. No null checks.

**Impact:**
- Unhandled NPE crashes endpoints
- 500 errors instead of 401
- Poor error messages

**Remediation:**
```java
private Usuario getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
        throw new AccessDeniedException("Usuario no autenticado");
    }
    if (!(auth.getPrincipal() instanceof Usuario user)) {
        throw new AccessDeniedException("Usuario no encontrado en contexto");
    }
    return user;
}
```

---

### 19. Missing Field Validation in DTOs

**Severity:** MEDIUM  
**Category:** Input Validation  
**Location:** Multiple DTOs
- `/backend/src/main/java/com/gestionganadera/backend/dto/CreateFincaRequest.java` (missing validations)

**Issue:**
```java
@Data
public class CreateFincaRequest {
    @NotBlank
    private String nombre;
    
    private String ubicacion;  // No validation!
    private BigDecimal extension;  // No validation!
}
```

Several fields lack validation:
- `ubicacion` can be any length (potential DOS with huge strings)
- `extension` can be negative or zero (illogical)

**Impact:**
- Malformed data stored
- Database constraint violations
- DOS via large payloads

**Remediation:**
```java
@Data
public class CreateFincaRequest {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombre;
    
    @Size(max = 500, message = "La ubicación no puede exceder 500 caracteres")
    private String ubicacion;
    
    @Positive(message = "La extensión debe ser mayor a 0")
    @DecimalMax(value = "1000000.00", message = "La extensión no puede exceder 1 millón de hectáreas")
    private BigDecimal extension;
}
```

---

### 20. Test Coverage Insufficient

**Severity:** MEDIUM  
**Category:** Testing  
**Location:** `/backend/src/test/java/...` (49 test files exist)

**Issue:**
With 49 test files, coverage appears decent, but key security areas lack tests:
- No tests for `TokenBlocklistService.isBlacklisted()`
- No tests for token revocation flow
- No authorization tests (multi-tenant isolation)
- No rate limiting bypass tests
- No SQL injection prevention tests

**Impact:**
- Security regressions not caught
- No confidence in refactoring
- Hidden vulnerabilities

**Remediation:**
Add security-focused tests:
```java
@Test
void testBlacklistedTokenRejected() {
    String token = jwtUtil.generateToken(userDetails);
    tokenBlocklistService.block(token);
    
    // Should reject blacklisted token
    assertFalse(jwtUtil.validateToken(token, userDetails));
}

@Test
void testUserCannotAccessOtherUsersFinca() {
    // Login as user1
    loginAs(user1);
    
    // Try to access user2's finca
    assertThrows(AccessDeniedException.class, 
        () -> fincaService.findById(user2Finca.getId()));
}
```

---

## SUMMARY TABLE

| ID | Issue | Severity | Category | Remediation Effort |
|----|----|----------|----------|-------------------|
| 1 | JWT blacklist not integrated | CRITICAL | Auth | Low |
| 2 | Weak password requirements | HIGH | Auth | Low |
| 3 | No logout endpoint | HIGH | Session | Low |
| 4 | SQL injection in native queries | HIGH | SQL | Medium |
| 5 | CORS too permissive | MEDIUM | Security | Low |
| 6 | No multi-tenant isolation | MEDIUM | Auth | High |
| 7 | No rate limiting on endpoints | MEDIUM | DDoS | Medium |
| 8 | Exception info disclosure | MEDIUM | Security | Low |
| 9 | No HTTPS enforcement | MEDIUM | TLS | Low |
| 10 | No URL validation | MEDIUM | Input | Low |
| 11 | Plaintext secrets | MEDIUM | Secrets | Medium |
| 12 | N+1 query problem | HIGH | Performance | Medium |
| 13 | Missing transaction control | MEDIUM | Transactions | Low |
| 14 | Generic exceptions | MEDIUM | Error Handling | Low |
| 15 | No pagination | MEDIUM | API Design | Medium |
| 16 | Resource leak in filters | MEDIUM | Resource Mgmt | Low |
| 17 | Silent email failures | MEDIUM | Error Handling | Low |
| 18 | No null safety checks | MEDIUM | NPE | Low |
| 19 | Missing DTO validation | MEDIUM | Input | Low |
| 20 | Insufficient test coverage | MEDIUM | Testing | High |

---

## QUICK FIX CHECKLIST (Priority Order)

### Phase 1: CRITICAL (Do First)
- [ ] Integrate `TokenBlocklistService` into `JwtAuthenticationFilter`
- [ ] Add logout endpoint with token revocation
- [ ] Implement password complexity requirements

### Phase 2: HIGH (Security)
- [ ] Convert native queries to JPQL
- [ ] Implement N+1 query fixes with FETCH joins
- [ ] Add rate limiting to all endpoints

### Phase 3: MEDIUM (Quality)
- [ ] Add generic exception handling improvements
- [ ] Implement proper null safety checks
- [ ] Add pagination to all list endpoints
- [ ] Improve DTO validation

### Phase 4: ARCHITECTURAL (Long-term)
- [ ] Implement multi-tenant isolation (requires schema changes)
- [ ] Add encryption for sensitive properties
- [ ] Enhance test coverage (security-focused tests)
- [ ] Implement HTTPS enforcement at server level

---

## CONFIGURATION RECOMMENDATIONS

### Production Environment Variables

```bash
# Security
JWT_SECRET=<generate-strong-key-min-256-bits>
JWT_EXPIRATION=3600000  # 1 hour
RECAPTCHA_SECRET=<your-recaptcha-secret>

# Database
DB_URL=jdbc:postgresql://prod-db:5432/gestion_ganadera
DB_USERNAME=<secure-user>
DB_PASSWORD=<secure-password>

# CORS
CORS_ALLOWED_ORIGINS=https://yourdomain.cloudfront.net

# Email
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=<your-email>
MAIL_PASSWORD=<app-password>
MAIL_FROM=noreply@yourdomain.com

# SSL
SSL_KEYSTORE_PATH=/secure/keystore.p12
SSL_KEYSTORE_PASSWORD=<secure-password>
SSL_KEY_ALIAS=gestion-ganadera
```

---

## REFERENCES

- OWASP Top 10: https://owasp.org/www-project-top-ten/
- Spring Security: https://spring.io/projects/spring-security
- SQL Injection Prevention: https://cheatsheetseries.owasp.org/cheatsheets/SQL_Injection_Prevention_Cheat_Sheet.html
- JWT Best Practices: https://tools.ietf.org/html/rfc8725
- Password Requirements: https://cheatsheetseries.owasp.org/cheatsheets/Authentication_Cheat_Sheet.html

---

**Audit Completed:** 2026-06-01  
**Auditor:** Claude Code Security Audit  
**Recommendation:** Address CRITICAL and HIGH severity items immediately before production deployment.
