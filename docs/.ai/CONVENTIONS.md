# 🐄 Gestión Ganadera — AI Conventions

> Cosas que parecen normales pero no lo son, y patrones que DEBO seguir.
> Lo demás (endpoints, fields, entities) lo leo del código fuente.

---

## 🔴 Gotchas (cosas que me harían perder tiempo)

| # | Gotcha | Detalle |
|---|--------|---------|
| 1 | `"Manana"` sin tilde | Produccion entity enum value. Frontend también usa `"Manana"`. No existe `"Mañana"`. |
| 2 | FincaController devuelve entity `Finca`, no DTO | Único controller que no usa DTO. No refactorizar a DTO sin preguntar. |
| 3 | LoteController también devuelve `Lote` entity | Misma situación que Finca. |
| 4 | Alimento, Medicamento, Vacuna son JDBC-readonly | Son catálogos cargados vía JdbcTemplate al inicio. **No tienen CRUD endpoints.** |
| 5 | Frontend es submódulo Git | `git clone` requiere `git submodule init && git submodule update`. |
| 6 | HashRouter, no BrowserRouter | URLs tienen `#`. Si cambio a BrowserRouter, rompo deploy Cloudflare. |
| 7 | reCAPTCHA v3 en auth | Login y Register mandan `recaptchaToken`. Backend valida contra Google. |
| 8 | RateLimitingFilter existe | 10 requests/min por IP. Si un test falla con 429, es por esto. |
| 9 | SecurityHeadersFilter existe | Agrega headers CSP, HSTS, X-Frame-Options, etc. |
| 10 | `@JsonIgnoreProperties` en relaciones Lazy | Finca tiene `@JsonIgnoreProperties({"password", "fincas"})` en propietario. No serializar loops. |
| 11 | `"Manana"` aparece en tests también | Los tests de Produccion usan este valor exacto. |
| 12 | Swagger en `/api/swagger-ui.html`, no `/swagger-ui.html` | Configurado via `springdoc.swagger-ui.path` en application.properties. |
| 13 | OpenApiConfig.java tiene bean con security scheme JWT | Si toco config de Swagger, actualizar el security scheme ahí. |
| 14 | FileUploadUtil existe en util/ | Maneja subida de archivos. Revisar antes de agregar nueva lógica de archivos. |
| 15 | Tenant isolation method varía por repo | No todos los repos tienen `findByIdAndUsuarioId` — LoteRepository usa `findByIdAndFinca_FincaId`, etc. Revisar el Repository antes de asumir. |
| 16 | Todos los controllers retornan `ResponseEntity<T>` | Ninguno usa `@ResponseBody` directo ni anotación a nivel de clase. Nuevos endpoints deben mantener `ResponseEntity<T>`. |
| 17 | `@PreAuthorize` NO se usa en controllers | SecurityConfig maneja auth globalmente (todo requiere auth excepto `/auth/**`, `/swagger-ui/**`, etc.). Agregar `@PreAuthorize` en un controller es redundante. |
| 18 | application.properties tiene configs no obvias | `server.servlet.context-path=/api`, `springdoc.swagger-ui.path=/swagger-ui.html`, `spring.servlet.multipart.max-file-size=10MB`. Revisar antes de tocar uploads, Swagger o CORS. |

---

## 🟡 Patterns que DEBO seguir

### Tenant Isolation (SIEMPRE)

```java
// EN CADA Service method que accede a datos por ID:
Usuario currentUser = (Usuario) SecurityContextHolder.getContext()
    .getAuthentication().getPrincipal();

// NO hacer: repository.findById(id)
// SÍ hacer: El método exacto varía por repositorio
//   - findByIdAndUsuarioId(id, userId)     → Animal, Evento, etc.
//   - findByIdAndFinca_FincaId(id, fincaId) → Lote
//   - Ver el Repository antes de asumir el nombre
Entity entity = repository.findByIdAndXxxId(id, ownerId)
    .orElseThrow(() -> new EntityNotFoundException("..."));
```

**Excepción:** `UsuarioController` (solo ADMIN bypass).

### Status Codes no estándar

| Método | Código | Cuándo |
|--------|--------|--------|
| POST | **201** | Solo Reproducciones y Partos |
| POST | **200** | Todo lo demás |
| DELETE | **204** | Siempre |
| GET/PUT | **200** | Siempre |

### Controller → Service → Repository

```java
@RestController
@RequestMapping("/path")
@RequiredArgsConstructor
@Tag(name = "X", description = "Y")
public class XxxController {
    private final XxxService service;

    @GetMapping
    @Operation(summary = "...")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    public ResponseEntity<List<XxxDTO>> getAll() { ... }
}
```

### Servicio standard

```
1. Validar entrada
2. Extraer currentUser de SecurityContext
3. Verificar ownership (getAuthorizedXxx)
4. Lógica de negocio
5. Save/return DTO
```

### Frontend: API calls

```js
import { getAnimales, createAnimal } from '../../api/ganado';
// NO importar de services/ directamente
```

### Frontend: Nueva página

```
1. App.jsx → const XxxPage = React.lazy(() => import('./pages/Xxx/XxxPage'));
2. <Route path="xxx" element={<XxxPage />} /> en dashboard layout
3. Sidebar.jsx → agregar entry en menuItems
```

### Swagger docs (PENDIENTE: agregar @ApiResponse a todos los endpoints)

Actualmente los controllers tienen `@Tag` y `@Operation`, pero **falta `@ApiResponse`** en la mayoría. Al crear endpoints nuevos, seguir este patrón:

```java
@GetMapping
@Operation(summary = "Listar todos")
@ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
@ApiResponse(responseCode = "401", description = "Token JWT inválido o ausente")
@ApiResponse(responseCode = "404", description = "Recurso no encontrado")
public ResponseEntity<List<XxxDTO>> getAll() { ... }
```

---

## 🟢 Tests: patrones clave

| Patrón | Archivo ejemplo |
|--------|----------------|
| Unit test controller | `AnimalControllerTest.java` — MockMvc |
| Unit test service | `AnimalServiceTest.java` — Mockito |
| Integration test | `AnimalControllerTest.java` — BaseIntegrationTest + RestClient |
| Auth integration | `AuthIntegrationTest.java` |
| Tenant isolation | `TenantIsolationTest.java` |

**Integration test base:**
```java
class XxxControllerTest extends BaseIntegrationTest {
    // Helpers disponibles:
    //   createEntity(path, body) → ResponseEntity con entity creada
    //   Usar RestClient para HTTP calls
    // Cubrir: CRUD lifecycle, 404 test, 401 test
}
```

---

## 📁 Archivos que siempre leer ANTES de editar

| Si edito... | Leer primero... |
|-------------|-----------------|
| Un Controller | Su Service + DTOs relacionados |
| Un Service | Controller + Repository de la entidad |
| Un Entity | DTOs relacionados + Controller endpoints |
| Frontend page | El hook que usa + api/ganado.js funciones |
| SecurityConfig | JwtAuthenticationFilter + SecurityHeadersFilter + RateLimitingFilter |
| Config de Swagger | OpenApiConfig.java + application.properties (springdoc props) |
| Tests | BaseIntegrationTest (para entender helpers disponibles) |

---

## 🚫 Cosas que NUNCA hacer

- ❌ Llamar `Repository` desde `Controller` — siempre pasar por `Service`
- ❌ Devolver `Entity` desde Controller (excepto FincaController, LoteController)
- ❌ Agregar `@PreAuthorize` en controllers — SecurityConfig global ya maneja auth
- ❌ Importar de `services/` directamente en frontend — ir por `api/ganado.js`
- ❌ Asumir que POST es 201 — es 200 excepto Reproducciones y Partos
- ❌ Cambiar de HashRouter a BrowserRouter sin verificar deploy
- ❌ Poner lógica de negocio en Controller — va en Service
- ❌ Asumir `findByIdAndUsuarioId` existe en todos los repos — cada repo tiene su método de ownership
