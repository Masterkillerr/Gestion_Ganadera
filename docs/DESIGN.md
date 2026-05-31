# Design System: GreenField — Gestión Ganadera

## 1. Overview & Creative North Star

**Creative North Star: "The Digital Agronomist"**

El sistema de diseño implementa un tema **oscuro por defecto** con acentos esmeralda y superficies de vidrio esmerilado (`glassmorphism`). El objetivo es transmitir la seriedad y robustez necesarias para la gestión ganadera, combinadas con la sofisticación de una plataforma SaaS moderna.

No existe soporte para modo claro — todo el diseño está concebido para oscuridad profunda, con fondos `#030712` (dark-950) y superficies elevadas con sutiles tonos carbón y bordes translúcidos.

---

## 2. Visual Identity

### 2.1 Color Palette

La paleta se define como variables CSS personalizadas dentro de la directiva `@theme` de Tailwind v4. No se utilizan colores por defecto de Tailwind para los colores principales.

| Token | Uso | Color |
|---|---|---|
| `brand-50` → `brand-950` | Esmeralda — acciones, enlaces, indicadores activos | `#ecfdf5` → `#022c22` |
| `brand-500` | Acento primario | `#10b981` |
| `brand-600` | Hover en botones, borde activo | `#059669` |
| `brand-700` | Fondo de botón primario | `#047857` |
| `surface-50` | Fondo de tarjetas claro | `#f8faf8` |
| `surface-100` | Fondo alternativo | `#eef7ee` |
| `earth-50` → `earth-950` | Ámbar/ocre — advertencias, alertas | `#fffbeb` → `#451a03` |
| `earth-500` | Advertencia | `#f59e0b` |
| `dark-950` | Fondo base de la aplicación | `#030712` |
| `dark-900` | Sidebar, fondos de elementos | `#0c0e12` |
| `dark-800` | Superficies elevadas (modales) | `#13161b` |
| `dark-700` | Bordes, hover backgrounds | `#1a1d24` |
| `dark-600` | Fondos de input inactivos | `#22262f` |
| `dark-500` | Hover en inputs | `#2a2f3a` |
| `dark-400` | Bordes translúcidos | `#363b48` |
| `dark-300` | Iconos secundarios | `#464c5c` |
| `dark-200` | Texto placeholder, etiquetas muted | `#6b7280` |
| `muted-contrast` | Texto de baja prioridad | `#9ca3af` |
| `gray-100` → `gray-500` | Texto (usando Tailwind gray por defecto) | Blanco sucio → gris medio |
| `red-*`, `rose-*` | Errores, estados críticos, botón de peligro | Tailwind defaults |

### 2.2 Typography

- **Fuente principal:** `Inter` (pesos 300–800) — importada desde Google Fonts.
- **Fuente del sistema de respaldo:** `system-ui, sans-serif`.

**Escala tipográfica:**

| Elemento | Clase / Tamaño | Peso | Tracking |
|---|---|---|---|
| KPI Value | `text-3xl` (30px / 36px line-height) | Bold | `tracking-tight` |
| Section Title | `text-base` (16px) | Bold | normal |
| Section Subtitle | `text-sm` (14px) | normal | normal |
| Body / Table cells | `text-sm` (14px) | Regular | normal |
| Labels / Badges | `text-xs` (12px) | Semibold | `tracking-wider` |
| Table headers | `text-xs` (12px) | Semibold | `uppercase tracking-wider` |
| Sidebar section labels | `text-[10px]` (10px) | Semibold | `uppercase tracking-widest` |
| Nav items | `text-sm` (14px) | Medium | normal |
| Button text | `text-sm` (14px) / `text-xs` | Semibold / Medium | normal |

### 2.3 Iconography

Se utilizan **Heroicons** (inline SVGs con `stroke="currentColor"`) para todos los iconos del sistema. No se usa Lucide React.

Iconos clave del sidebar:
- **Home** — Panel
- **Cow** (Cube icon) — Ganado
- **Truck** — Movimientos
- **Operations** (Cog) — Operaciones
- **Medkit** — Sanidad
- **Heart** — Reproducción
- **Infrastructure** (Building) — Infraestructura
- **Chart** — Producción
- **Admin** (User) — Administración
- **Settings** (Cog) — Configuración

---

## 3. Layout Structure

### 3.1 Global Layout

```
┌──────────┬──────────────────────────────────────┐
│          │            Navbar (sticky)            │
│ Sidebar  │──────────────────────────────────────┤
│  fixed   │                                      │
│  256px   │          Main Content Area            │
│  (w-64)  │     padding: 24px (p-6) md: 32px     │
│          │                                      │
└──────────┴──────────────────────────────────────┘
```

- **Sidebar:** Fija, 256px (`w-64`), fondo `dark-900` (#0c0e12), borde derecho translúcido.
- **Navbar:** Sticky, altura 64px, fondo `dark-950/80` con `backdrop-blur-xl`.
- **Contenido:** Fluido con `lg:ml-64`, padding `p-6` / `p-8` en desktop.
- **Mobile:** Sidebar se oculta, menú hamburguesa activa un drawer deslizante sobre overlay oscuro con `backdrop-blur-sm`.

### 3.2 Sidebar Detail

- Logo + nombre "GreenField" con subtítulo "Gestión Ganadera" en brand-400.
- Grupos de navegación: **Menú Principal**, **Gestión**, **Datos**.
- Acciones inferiores: Configuración, Cerrar Sesión (red), Eliminar Cuenta (red/gray).
- Indicador de elemento activo: barra vertical verde (`brand-400`) a la izquierda + fondo verde translúcido.
- Transiciones suaves (`cubic-bezier(0.2, 0.8, 0.2, 1)`).

### 3.3 Navbar Detail

- Izquierda: Menú hamburguesa (solo mobile) + breadcrumb "Sistema > Panel".
- Derecha: Campo de búsqueda (expandable on focus), campana de notificaciones con badge rojo, avatar con inicial del usuario + nombre/email.
- Efecto glass en el fondo (`bg-dark-950/80 backdrop-blur-xl`).

---

## 4. Component System

### 4.1 Cards

**.glass-card** — Componente base para contenedores:
```css
background: linear-gradient(165deg, rgba(26, 29, 36, 0.85) 0%, rgba(19, 22, 27, 0.95) 100%);
border: 1px solid rgba(54, 59, 72, 0.5);
border-radius: 1rem (rounded-2xl);
backdrop-filter: blur(8px);
box-shadow: 0 1px 2px rgba(0,0,0,0.3), inset 0 1px 0 rgba(255,255,255,0.03), inset 0 -1px 0 rgba(0,0,0,0.2);
```

**.stat-card** — Tarjeta de KPI con hover effect:
- Misma base que glass-card
- `transition` en transform, box-shadow y border-color
- Hover: `translateY(-2px)`, borde esmeralda translúcido, glow sutil

### 4.2 Buttons

| Clase | Estilo | Uso |
|---|---|---|
| `.btn-primary` | Gradient `#059669 → #047857`, shadow verde `rgba(5,150,105,0.4)`, rounded-xl | Acción principal |
| `.btn-primary:hover` | Gradient más claro, shadow más intenso, -1px translateY | Hover |
| `.btn-primary:disabled` | Opacidad 0.45, cursor not-allowed, grayscale | Deshabilitado |
| `.btn-secondary` | Fondo dark translúcido, borde sutil, texto gris | Acción secundaria |
| `.btn-secondary:hover` | Fondo más claro, borde más visible, texto blanco | Hover |

**Variante Danger** (usada en ConfirmModal): Sobrescribe btn-primary con fondo `red-600`, shadow `red-600/30`.

### 4.3 Badges

7 variantes de badge con borde redondeado completo (`rounded-full`):

| Variante | Fondo | Texto | Borde |
|---|---|---|---|
| `badge-green` | `rgba(2,44,34,0.7)` | `#6ee7b7` | `rgba(5,150,105,0.2)` |
| `badge-amber` | `rgba(69,26,3,0.6)` | `#fbbf24` | `rgba(217,119,6,0.2)` |
| `badge-red` | `rgba(127,29,29,0.5)` | `#fca5a5` | `rgba(220,38,38,0.2)` |
| `badge-blue` | `rgba(30,58,138,0.5)` | `#93c5fd` | `rgba(59,130,246,0.2)` |
| `badge-gray` | `rgba(42,47,58,0.7)` | `#d1d5db` | `rgba(54,59,72,0.3)` |
| `badge-pink` | `rgba(131,24,67,0.4)` | `#f9a8d4` | `rgba(190,24,93,0.2)` |
| `badge-purple` | `rgba(88,28,135,0.4)` | `#c4b5fd` | `rgba(147,51,234,0.2)` |

Tamaño: `font-size: 0.7rem`, `font-weight: 600`, `padding: 0.2rem 0.65rem`.

### 4.4 Input Fields

`.input-field`:
- Fondo: `rgba(34,38,47,0.8)`
- Borde: `rgba(54,59,72,0.5)`
- Border-radius: `rounded-xl` (0.75rem)
- Placeholder: `rgba(107,114,128,0.6)`
- Hover: borde más claro
- Focus: borde esmeralda + glow `rgba(16,185,129,0.08)` + `rgba(16,185,129,0.15)` outer ring
- `.error`: borde rojo + red glow

### 4.5 Data Tables

`.data-table`:
- Header: `text-xs font-semibold uppercase tracking-wider`, borde inferior translúcido, alineación izquierda
- Cells: `text-sm`, padding vertical 14px, borde inferior sutil
- Hover row: Fondo esmeralda ultra translúcido (`rgba(16,185,129,0.03)`)
- Última fila sin borde inferior

### 4.6 Modal System

Todos los modales comparten:
- Overlay: `bg-black/60 backdrop-blur-sm`, z-50
- Contenedor: `glass-card` con `p-6`, `max-w-lg/md/sm` según tipo
- Animación de entrada: `animate-fade-up`
- Header con título en bold + botón cerrar (hover con bg-dark-600)
- Footer con borde superior + botones de acción

Variantes:
- **InlineFormModal**: Contenido libre para formularios
- **ConfirmModal**: Icono + título + mensaje + dos botones. Soporta variants `danger` (rojo), `warning` (ámbar), `info` (brand)
- **DetailModal**: Lista de campos label/value con fondos dark-600/50
- **ErrorModal**: Icono warning + mensaje de error

### 4.7 Toast Notifications

Tres tipos con estilos de borde, fondo e icono diferenciados:

| Tipo | Fondo | Borde | Icono |
|---|---|---|---|
| `success` | `bg-emerald-950/70` | `border-emerald-700/60` | Checkmark |
| `error` | `bg-red-950/70` | `border-red-700/60` | X-circle |
| `info` | `bg-blue-950/70` | `border-blue-700/60` | Info-circle |

Efecto glass con `backdrop-blur-md`, `rounded-xl`, `shadow-xl`.

### 4.8 Loading States

- **LoadingSpinner**: Border spinner animado con colores dark-400/track + brand-500. Soporta tamaños `sm`, `md`, `lg`. Opcional: mensaje con `animate-pulse`, `fullPage` centrado.
- **Skeleton**: Barra de carga con shimmer effect (gradiente animado de izquierda a derecha). Usa `rounded-xl` y overflow hidden.

### 4.9 Icon Boxes

Contenedores de icono con color temático: `icon-box-green`, `icon-box-blue`, `icon-box-amber`, `icon-box-red`, `icon-box-purple`, `icon-box-brand`.

Cada uno con fondo, borde y color de texto en su respectivo tono. `rounded-lg`, `p-2.5`.

### 4.10 Empty States

`.empty-state`: Layout vertical centrado con icono mutado, título gris, descripción gris más claro, max-width 20rem.

---

## 5. Animation System

| Animación | Duración | Easing | Uso |
|---|---|---|---|
| `fadeInUp` | 0.4s | ease-out | Tarjetas, modales al abrir |
| `fadeIn` | 0.3s | ease-out | Overlays |
| `slideInRight` | 0.35s | ease-out | Paneles laterales |
| `slideInUp` | 0.3s | ease-out | Elementos inline |
| `scaleIn` | 0.25s | ease-out | Modales |
| `shimmer` | 1.5s | ease-in-out infinite | Skeleton loading |
| `pulseGlow` | 2.5s | ease-in-out infinite | Indicadores activos |

**Staggered delays:** Clases `.animate-fade-up-d1` a `.d5` (incrementos de 0.06s).

**Reduced motion:** Todas las animaciones y transiciones se desactivan cuando `prefers-reduced-motion: reduce` está activo.

---

## 6. Scrollbar

Scrollbar personalizado vía pseudo-elementos webkit:
- Width: 5px
- Track: `dark-900`
- Thumb: `dark-400`, `rounded-full`
- Thumb hover: `brand-600`

---

## 7. Focus & Selection

- `:focus-visible`: Sin outline nativo, box-shadow verde `rgba(16,185,129,0.25)` + `border-radius: 4px`
- `::selection`: Fondo verde translúcido `rgba(16,185,129,0.25)`, color heredado

---

## 8. Utility Classes

- `.divider`: Borde superior con `rgba(54,59,72,0.3)`
- `.text-gradient`: Gradient de brand-400 a brand-600 con `bg-clip-text` y `text-transparent`
- `.shimmer`: Efecto de barra de carga con gradient animado

---

## 9. Responsive Breakpoints

| Breakpoint | Comportamiento |
|---|---|
| `lg` (1024px+) | Sidebar visible, layout de dos columnas |
| `<lg` (mobile/tablet) | Sidebar oculta, drawer deslizante con overlay, navbar muestra hamburguesa |

---

## 10. Design Principles (Codebase)

1. **Dark-first**: Todo el diseño es oscuro. Sin modo claro.
2. **Glassmorphism estructural**: Tarjetas y modales usan fondos semitransparentes con blur para crear profundidad.
3. **Consistencia en bordes**: Todos los elementos interactivos usan `rounded-xl` o `rounded-2xl`. Sin variación brusca.
4. **Feedback visual**: Hover states en cards (translateY), botones (gradient change), inputs (border glow), filas de tabla (background tint).
5. **Jerarquía de datos**: KPIs en cards con números grandes (`text-3xl font-bold`), labels en uppercase 12px, tablas con headers tracking-wider.
6. **Sistema de badges rico**: 7 colores para clasificar estados sanitarios, de producción y administrativos.
