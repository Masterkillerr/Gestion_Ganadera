# Design System Document: Campo Moderno

## 1. Overview & Creative North Star
**Creative North Star: "The Digital Agronomist"**

Este sistema de diseño busca equilibrar la robustez necesaria para el trabajo de campo con la sofisticación de una plataforma SaaS moderna. El objetivo es proporcionar una herramienta que se sienta profesional, confiable y extremadamente eficiente.

## 2. Visual Identity

### 2.1 Color Palette
- **Primario (Esmeralda):** `#047857` (Emerald-700) - Utilizado para acciones principales, navegación activa y branding.
- **Secundario (Superficies):** `#F8FAFC` (Slate-50) y `#F1F5F9` (Slate-100) - Para fondos y áreas de contenido.
- **Oscuros (Sidebar):** `#0F172A` (Slate-900) - Proporciona un contraste fuerte para la navegación lateral.
- **Estados:**
    - **Éxito/Acento:** `#84CC16` (Lime-500) - Para indicadores positivos.
    - **Advertencia:** `#F59E0B` (Amber-500) - Alertas de partos o preventivas.
    - **Error/Crítico:** `#EF4444` (Red-500) - Alertas sanitarias urgentes.

### 2.2 Typography
- **Fuente Principal:** Inter (Sans-serif).
- **Escala:**
    - H1: 30px, Bold, Tracking tight.
    - H2/Subtitles: 18px, Medium.
    - Body: 14px, Regular.
    - Labels: 12px, Semibold, Uppercase.

### 2.3 UI Components (Shadcn/ui Style)
- **Border Radius:** `0.5rem` (rounded-lg) para tarjetas, botones e inputs.
- **Shadows:** Sombras sutiles (`shadow-sm`) para elevar tarjetas sobre el fondo gris claro.
- **Inputs:** Bordes suaves, foco en color esmeralda, fondo ligeramente tintado.

## 3. Layout Structure
- **Global Layout:** Sidebar fija de 256px (w-64) con fondo oscuro y área de contenido fluida con fondo claro.
- **Navegación:** Uso de iconos de Lucide React para una identificación visual rápida de los módulos.

## 4. Design Principles
1. **Claridad de Datos:** Priorizar la legibilidad en tablas y gráficos.
2. **Jerarquía Visual:** Uso de KPI Cards para resaltar la información más importante en el Dashboard.
3. **Consistencia:** Mantener el mismo radio de borde y espaciado en todos los módulos.
