# 📚 Guía de Implementación de Swagger/OpenAPI - Gestión Ganadera

Este documento describe la arquitectura y los pasos técnicos para implementar y asegurar **Swagger/OpenAPI** en el backend de **Gestión Ganadera** utilizando **SpringDoc OpenAPI v3** sobre **Spring Boot 4.0.x** y **Spring Security**.

---

## 🛠️ 1. Dependencia Maven (`pom.xml`)
Para integrar Swagger con Spring Boot y generar la interfaz gráfica interactiva, se añadió la dependencia oficial de **SpringDoc OpenAPI**:

```xml
<!-- Swagger / OpenAPI v3 -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>3.0.0</version>
</dependency>
```

Esta biblioteca automatiza la generación de la especificación OpenAPI (en formato JSON/YAML) a partir de la inspección de los `@RestController` y los endpoints del sistema.

---

## ⚙️ 2. Parámetros de Configuración (`application.properties`)
Se definieron propiedades para mapear las rutas de acceso y optimizar la visualización de la documentación:

```properties
# Server Context Path (Afecta a todas las rutas)
server.servlet.context-path=/api

# Ruta de acceso a la interfaz web de Swagger
springdoc.swagger-ui.path=/swagger-ui.html

# Habilitar opción "Try it out" por defecto para probar peticiones directamente
springdoc.swagger-ui.tryItOutEnabled=true

# Ocultar la sección inferior de Schemas/Modelos para una vista más limpia (-1 lo deshabilita)
springdoc.swagger-ui.defaultModelsExpandDepth=-1
```

> [!NOTE]
> Dado que el context path es `/api`, las rutas reales generadas por el servidor son:
> *   **UI de Swagger:** `/api/swagger-ui.html` (que redirige internamente a `/api/swagger-ui/index.html`)
> *   **Descriptor OpenAPI JSON:** `/api/v3/api-docs` y `/api/v3/api-docs/swagger-config`

---

## 🏗️ 3. Clase de Configuración de OpenAPI (`OpenApiConfig.java`)
Para personalizar los metadatos generales de la API y habilitar las pruebas con tokens de autenticación en la interfaz, se creó la clase [OpenApiConfig.java](file:///c:/Users/Kevin/OneDrive/Software/SEMESTRE%20V/WEB/GESTIONGANADERAAWS/Gestion_Ganadera-281be6a75f142ad910d97d6490a8cffad8efbeb3/backend/src/main/java/com/gestionganadera/backend/config/OpenApiConfig.java):

```java
package com.gestionganadera.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
            .info(new Info()
                .title("API Gestión Ganadera")
                .description("API REST para el Sistema de Gestión Ganadera. " +
                    "Permite administrar fincas, lotes, animales, producción, " +
                    "reproducción, alimentación, tratamientos y vacunaciones.")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Desarrollador")
                    .email("dev@gestionganadera.com"))
                .license(new License()
                    .name("MIT")
                    .url("https://opensource.org/licenses/MIT")))
            // Activar botón de "Authorize" global
            .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
            .components(new Components()
                // Definir esquema de seguridad JWT Bearer
                .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                    .name(securitySchemeName)
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("Token JWT obtenido de /auth/login o /auth/register")));
    }
}
```

---

## 🔐 4. Configuración en Spring Security (`SecurityConfig.java`)
Para evitar que Spring Security bloquee el acceso a la documentación, se agregaron las rutas de Swagger y OpenAPI en la lista de URLs públicas permitidas en [SecurityConfig.java](file:///c:/Users/Kevin/OneDrive/Software/SEMESTRE%20V/WEB/GESTIONGANADERAAWS/Gestion_Ganadera-281be6a75f142ad910d97d6490a8cffad8efbeb3/backend/src/main/java/com/gestionganadera/backend/config/SecurityConfig.java):

```java
.authorizeHttpRequests(auth -> auth
    // Permitir acceso sin autenticación a Swagger UI y esquemas OpenAPI JSON
    .requestMatchers(
        "/auth/**", 
        "/health", 
        "/error", 
        "/swagger-ui.html", 
        "/swagger-ui/**", 
        "/v3/api-docs", 
        "/v3/api-docs/**"
    ).permitAll()
    .requestMatchers("/admin/**").hasRole("ADMIN")
    .anyRequest().authenticated()
)
```

---

## 🛡️ 5. Omisión en Filtro de Seguridad HTTP (`SecurityHeadersFilter.java`)
El proyecto cuenta con un filtro personalizado (`SecurityHeadersFilter`) para establecer políticas como **HSTS**, **X-Frame-Options** y **Content Security Policy (CSP)**. 

Dado que Swagger UI requiere cargar estilos en línea (`style-src 'unsafe-inline'`), ejecutar scripts embebidos y descargar iconos, la política CSP por defecto lo rompía. Se configuró una omisión en el método `doFilter` de [SecurityHeadersFilter.java](file:///c:/Users/Kevin/OneDrive/Software/SEMESTRE%20V/WEB/GESTIONGANADERAAWS/Gestion_Ganadera-281be6a75f142ad910d97d6490a8cffad8efbeb3/backend/src/main/java/com/gestionganadera/backend/config/SecurityHeadersFilter.java):

```java
@Override
public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {

    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;
    String path = httpRequest.getRequestURI();

    // Evitar que las directivas estrictas de CSP e iframe bloqueen Swagger UI
    if (path != null && (path.contains("/swagger-ui") || path.contains("/v3/api-docs"))) {
        chain.doFilter(request, response);
        return;
    }

    // Cabeceras estrictas HSTS, CSP, etc. para la API
    // ...
    chain.doFilter(request, response);
}
```

---

## 📝 6. Documentación de Controladores y DTOs
Para enriquecer visualmente la documentación interactiva, se utilizaron anotaciones de OpenAPI en el código del servidor.

### Ejemplo en Controlador (`AnimalController.java`):
Mediante `@Tag` y `@Operation`, se agrupan los endpoints y se define su propósito:

```java
@RestController
@RequestMapping("/animal")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Animales", description = "CRUD de animales del inventario ganadero") // Nombre del módulo en Swagger
public class AnimalController {

    private final AnimalService animalService;

    @GetMapping
    @Operation(summary = "Listar animales", description = "Obtiene todos los animales en el inventario")
    public ResponseEntity<List<AnimalDTO>> getAllAnimales() {
        // ...
    }

    @PostMapping
    @Operation(summary = "Crear animal", description = "Registra un nuevo animal y calcula su categoría por edad")
    public ResponseEntity<AnimalDTO> createAnimal(@Valid @RequestBody CreateAnimalRequest request) {
        // ...
    }
}
```

### Inferencia automática de Schemas desde DTOs:
SpringDoc lee automáticamente las restricciones de **Jakarta Validation** (como `@NotBlank`, `@NotNull`, `@Positive`, `@Size`) de los DTOs (ej: `CreateAnimalRequest`) y las renderiza en la sección de esquema, mostrando a los desarrolladores qué campos son obligatorios y sus límites sin necesidad de escribir código repetitivo.

---

## 🚀 7. Modos de Acceso

1.  **Entorno Local:**
    *   Iniciar backend localmente: `mvn spring-boot:run` en la carpeta `backend/`.
    *   Ingresar a: [http://localhost:8080/api/swagger-ui.html](http://localhost:8080/api/swagger-ui.html)
2.  **Entorno de Producción (AWS Elastic Beanstalk):**
    *   Ingresar a: [https://Gestionganaderabackend-env.eba-kmujbtjg.us-east-2.elasticbeanstalk.com/api/swagger-ui.html](https://Gestionganaderabackend-env.eba-kmujbtjg.us-east-2.elasticbeanstalk.com/api/swagger-ui.html)
