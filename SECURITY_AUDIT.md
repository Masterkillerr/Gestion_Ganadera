# 🔒 Auditoría de Seguridad - Gestion Ganadera

**Fecha:** 2026-06-01  
**Estado:** Múltiples vulnerabilidades detectadas

---

## 📋 Resumen Ejecutivo

Se identificaron **4 críticas**, **4 altas** y **4 medias** vulnerabilidades de seguridad. La mayoría pueden remediarse con cambios en configuración y código.

---

## 🔴 CRÍTICAS

### 1. JWT_SECRET Expuesto en .env.example
**Archivo:** `backend/.env.example:21`  
**Riesgo:** La clave secreta para firmar JWTs está expuesta en el repositorio público.

```
JWT_SECRET=tOBKV78IsdH5cmmF/E2MO7F+4HEKkfmWOz9O7WYpWsJAcY9Bg7RzotfPQh1K9aFyWRhz02Yea9xVi0h+5MAiwA==
```

**Impacto:** Cualquiera con acceso al repositorio puede falsificar tokens JWT y hacerse pasar por cualquier usuario.

**Remediación:**
- Generar una nueva clave JWT secreta: `openssl rand -base64 32`
- NO incluir secretos reales en `.env.example` — usar placeholders
- Usar solo `your_jwt_secret_here` en el template

---

### 2. Credenciales de Base de Datos Expuestas
**Archivo:** `backend/.env.example:10-13`  
**Riesgo:** URL de RDS, usuario y contraseña de desarrollo están visibles.

```
DB_URL=jdbc:postgresql://ganaderia.cbm6w28ialq0.us-east-2.rds.amazonaws.com:5432/ganaderia
DB_PASSWORD=your_db_password_here
```

**Impacto:** Si se compromete el repositorio, los atacantes obtienen acceso a la base de datos de desarrollo.

**Remediación:**
- No incluir credenciales reales en el repo — solo placeholders
- Usar AWS Secrets Manager en producción
- Rotar todas las credenciales de la RDS

---

### 3. JWT Almacenado en localStorage (XSS Vulnerable)
**Archivo:** `frontend/src/services/authService.js:19`  
**Riesgo:** Los tokens JWT están almacenados en `localStorage`, vulnerable a ataques XSS.

```javascript
localStorage.setItem('token', response.data.token);
```

**Impacto:** Si hay una vulnerabilidad XSS en el frontend, el atacante puede robar el token con JavaScript `localStorage.getItem('token')`.

**Remediación:**
- Migrar a **httpOnly + Secure cookies** (no accesibles desde JavaScript)
- Configurar en AuthService:
  ```javascript
  // Backend debe retornar cookie en vez de token
  // Frontend debe confiar en la cookie automáticamente
  ```
- Backend debe establecer cookie:
  ```java
  HttpHeaders headers = new HttpHeaders();
  headers.add(HttpHeaders.SET_COOKIE, 
    "auth-token=" + token + 
    "; HttpOnly; Secure; SameSite=Strict; Max-Age=" + expiration);
  ```

---

### 4. AWS Account ID Expuesto en GitHub Actions
**Archivo:** `.github/workflows/deploy.yml:17`  
**Riesgo:** El ID de la cuenta AWS (`392362834988`) está visible en el workflow público.

```yaml
EB_S3_BUCKET: elasticbeanstalk-us-east-2-392362834988
```

**Impacto:** Los atacantes pueden identificar los recursos AWS de la organización.

**Remediación:**
- Mover variables sensibles a GitHub Secrets
- Usar `${{ secrets.AWS_ACCOUNT_ID }}` en el workflow
- Configurar en GitHub > Settings > Secrets and variables

---

## 🟠 ALTAS

### 5. Swagger UI Habilitado en Producción
**Archivo:** `backend/src/main/resources/application.properties:9-11`  
**Riesgo:** Swagger permite enumerar todos los endpoints de la API sin autenticación.

```properties
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.tryItOutEnabled=true
```

**Impacto:** Information disclosure — los atacantes ven la estructura completa de la API.

**Remediación:**
- Deshabilitar Swagger en producción:
  ```properties
  springdoc.swagger-ui.enabled=${SWAGGER_ENABLED:false}
  ```
- Configurar en `application.properties`:
  ```properties
  springdoc.swagger-ui.enabled=false
  ```
- Habilitarlo solo en desarrollo en `application-dev.properties`

---

### 6. Contraseña Mínimo 6 Caracteres
**Archivo:** `backend/src/main/java/com/gestionganadera/backend/dto/RegisterRequest.java:23`  
**Riesgo:** La validación permite contraseñas muy débiles.

```java
@Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
```

**Impacto:** Los usuarios pueden crear contraseñas fáciles de romper (6 caracteres = ~2.5 bits de entropía por carácter).

**Remediación:**
- Aumentar mínimo a 12 caracteres
- Requerir complejidad (mayúsculas, minúsculas, números, símbolos):
  ```java
  @Pattern(
    regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{12,}$",
    message = "Mínimo 12 caracteres: mayúsculas, minúsculas, números y símbolos"
  )
  private String password;
  ```

---

### 7. CSP con unsafe-inline en style-src
**Archivo:** `backend/src/main/java/com/gestionganadera/backend/config/SecurityHeadersFilter.java:34`  
**Riesgo:** CSP permite estilos inline, debilitando la protección contra XSS.

```java
"style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; "
```

**Impacto:** Los atacantes pueden inyectar estilos CSS maliciosos.

**Remediación:**
- Usar nonces o hashes para estilos inline necesarios
- Si no es necesario unsafe-inline, removerlo:
  ```java
  "style-src 'self' https://fonts.googleapis.com; "
  ```
- Mover estilos inline a archivos `.css` externos

---

### 8. reCAPTCHA Secret Puede Estar Vacío
**Archivo:** `backend/src/main/java/com/gestionganadera/backend/service/AuthService.java:44-46`  
**Riesgo:** En desarrollo, si `RECAPTCHA_SECRET` está vacío, la validación se salta.

```java
if (recaptchaSecret == null || recaptchaSecret.isEmpty()) {
  return;  // Saltea validación reCAPTCHA
}
```

**Impacto:** En desarrollo/testing accidental en producción, se puede hacer brute force sin CAPTCHA.

**Remediación:**
- En producción, siempre validar reCAPTCHA
- Usar perfiles de Spring para separar dev/prod:
  ```yaml
  # application-dev.properties
  app.recaptcha.enabled=false
  
  # application.properties (producción)
  app.recaptcha.enabled=true
  ```

---

## 🟡 MEDIAS

### 9. CORS Allowlist Incluye localhost Múltiples
**Archivo:** `backend/src/main/resources/application.properties:48`  
**Riesgo:** Se permiten múltiples puertos localhost; si la aplicación se expone a internet, localhost será inútil pero confuso.

```properties
app.cors.allowed-origins=http://localhost:5173,http://localhost:5174,...,https://d3gw8tv95pui9q.cloudfront.net
```

**Impacto:** Menor — localhost no es alcanzable desde internet, pero indica falta de configuración por entorno.

**Remediación:**
- Separar CORS por perfil:
  - `application-dev.properties`: localhost
  - `application.properties`: solo CloudFront

---

### 10. Hardcoded CloudFront URL
**Archivo:** `frontend/src/services/api.js:5`  
**Riesgo:** La URL del backend está hardcodeada en el frontend.

```javascript
baseURL: import.meta.env.VITE_API_URL || 'https://d3gw8tv95pui9q.cloudfront.net'
```

**Impacto:** Si se cambia el endpoint de backend, hay que rebuildar el frontend.

**Remediación:**
- Configurar vía variable de entorno en build:
  ```yaml
  # .github/workflows/deploy.yml
  env:
    VITE_API_URL: ${{ secrets.API_URL }}
  ```

---

### 11. File Upload: REPLACE_EXISTING
**Archivo:** `backend/src/main/java/com/gestionganadera/backend/util/FileUploadUtil.java:69`  
**Riesgo:** `StandardCopyOption.REPLACE_EXISTING` puede sobreescribir archivos de otros usuarios.

```java
Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
```

**Impacto:** Un usuario podría sobrescribir fotos de otros animales si adivinan el nombre.

**Remediación:**
- Usar nombres únicos con UUID + salts:
  ```java
  String filename = UUID.randomUUID() + "-" + sanitizedFilename;
  Path targetPath = Paths.get(uploadDir, filename).normalize();
  Files.copy(file.getInputStream(), targetPath); // sin REPLACE_EXISTING
  ```

---

### 12. Information Disclosure en Error Responses
**Archivo:** `backend/src/main/java/com/gestionganadera/backend/exception/GlobalExceptionHandler.java:139-147`  
**Riesgo:** Los mensajes de error pueden exponer detalles de la base de datos.

```java
if (rootMsg != null) {
  if (rootMsg.contains("duplicate key value")) {
    message = "Ya existe un registro con ese identificador único";
  }
}
```

**Impacto:** Bajo — los mensajes están generalizados, pero en algunos casos podrían exponer información.

**Remediación:**
- En producción, usar mensajes genéricos:
  ```java
  @Value("${app.environment:production}")
  private String environment;
  
  if ("development".equals(environment)) {
    message = rootMsg; // mostrar detalles en dev
  } else {
    message = "Error de base de datos";  // genérico en prod
  }
  ```

---

## ✅ PUNTOS POSITIVOS

1. ✅ **CSRF deshabilitado apropiadamente** — JWT es stateless, CSRF no aplica
2. ✅ **Session Stateless** — `SessionCreationPolicy.STATELESS`
3. ✅ **JWT con algoritmo fuerte** — HMAC-SHA (jjwt 0.13.0)
4. ✅ **Spring Security habilitado** — `@EnableWebSecurity`
5. ✅ **Validación de entrada** — `@Valid` en DTOs
6. ✅ **File upload protegido** — Whitelist de extensiones, validación de path traversal
7. ✅ **Flyway para migraciones** — Schema versionado
8. ✅ **Docker multi-stage** — Reduce tamaño de imagen
9. ✅ **Global exception handler** — No expone stack traces en producción
10. ✅ **HSTS, X-Frame-Options, X-Content-Type-Options** — Headers de seguridad implementados
11. ✅ **JaCoCo coverage** — Tests con cobertura monitoreada
12. ✅ **OIDC para AWS** — Mejor que access keys en GitHub Actions

---

## 🛠️ Pasos de Remediación Prioritarios

### Semana 1 (CRÍTICAS):
1. Generar nueva JWT_SECRET y rotarla en producción
2. Mover archivo `.env.example` a placeholder
3. Cambiar credenciales de RDS y actualizar secrets
4. Retirar AWS Account ID del workflow; mover a secrets

### Semana 2 (ALTAS):
5. Migrar JWT de localStorage a httpOnly cookies
6. Deshabilitar Swagger UI en producción
7. Aumentar validación de contraseña a 12+ caracteres con complejidad
8. Remover `unsafe-inline` de CSP

### Semana 3 (MEDIAS):
9. Separar CORS por perfil (dev vs prod)
10. Usar UUID para nombres de archivo upload
11. Configurar RECAPTCHA_ENABLED por perfil

---

## 📖 Referencias OWASP

- [OWASP A01:2021 — Broken Access Control](https://owasp.org/Top10/A01_2021-Broken_Access_Control/)
- [OWASP A02:2021 — Cryptographic Failures](https://owasp.org/Top10/A02_2021-Cryptographic_Failures/)
- [OWASP A03:2021 — Injection](https://owasp.org/Top10/A03_2021-Injection/)
- [OWASP A05:2021 — Cross-Site Scripting (XSS)](https://owasp.org/Top10/A05_2021-Cross-Site_Scripting_(XSS)/)
- [OWASP A07:2021 — Identification and Authentication Failures](https://owasp.org/Top10/A07_2021-Identification_and_Authentication_Failures/)

---

**Próxima revisión recomendada:** 2026-07-01
