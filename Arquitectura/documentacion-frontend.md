# Documentación Técnica Frontend — Sistema de Ventas

## Objetivo
Documentación técnica completa orientada al desarrollo del frontend Angular, abarcando UI/UX, stack tecnológico, arquitectura y contratos de integración con el backend Spring Boot existente.

---

## 1. Especificaciones de Interfaz (UI/UX)

### 1.1 Diseño Usuarios Finales (SHEIN-inspired)

**Paleta de colores:**
- Fondo predominante: blanco (#FFFFFF) y grises muy claros (#F5F5F5, #FAFAFA)
- Acento fuerte: negro (#000000) para textos y bordes
- Color CTA/urgente: rojo (#E8344E) para botones de acción, ofertas y alertas
- Grises intermedios: #666666 (textos secundarios), #E0E0E0 (bordes)

**Tipografía:**
- Fuente principal: Inter o SF Pro Display
- Jerarquía tipográfica:
  - Títulos H1-H3: 24-32px, font-weight 700
  - Títulos H4-H6: 18-22px, font-weight 600
  - Cuerpo de texto: 14-16px, font-weight 400
  - Precios: 18-20px, font-weight 700
  - Captions: 12px, font-weight 400, color #999999

**Layout general:**
- Enfoque Mobile-first
- Cards con sombra sutil (box-shadow: 0 2px 8px rgba(0,0,0,0.08))
- Bordes redondeados: 8-12px
- Espaciado base: múltiplos de 8px

**Componentes clave:**

| Componente | Especificación |
|---|---|
| Catálogo | Grid 2 columnas (móvil) → 4 columnas (desktop). Cada item: imagen + nombre + precio destacado + badge "OFERTA" si aplica |
| Carrito | Bottom sheet deslizable en móvil (altura 60% viewport), sidebar fija en desktop (ancho 400px) |
| Checkout | Flujo de pasos: 1. Domicilio → 2. Pago → 3. Confirmar. Progress bar sticky superior |
| Búsqueda | Barra sticky inferior (móvil) o superior (desktop) con autocomplete instantáneo (300ms debounce) |
| Filtros | Drawer lateral en móvil, panel colapsible en desktop |
| Producto detalle | Galería swipeable, sticky "Agregar al carrito" en móvil |

**Microinteracciones:**
- Skeleton loaders durante carga de datos (animación shimmer)
- Transiciones de 200ms para hover, focus y apertura/cierre de modales
- Pull-to-refresh en listas móviles
- Feedback táctil: scale(0.97) en botones al presionar
- Toast notifications para acciones (agregar carrito, éxito de compra)

**Estados vacíos:**
- Ilustraciones minimalistas SVG inline
- Mensaje empático (ej: "Tu carrito está vacío, explora nuestro catálogo")
- CTA claro hacia la acción principal

### 1.2 Diseño Administradores (Interfaz Innovadora)

**Layout base:**
- Sidebar collapsible oscuro (#1A1A2E) con logo, navegación principal y footer con usuario
- Contenido en fondo blanco con padding 24-32px
- Header sticky con breadcrumbs, acciones contextuales y botón de modo oscuro

**Dashboard principal:**
- KPI cards (4 columnas en desktop, 2 en tablet, 1 en móvil):
  - Ventas del día, Pedidos pendientes, Productos bajos en stock, Clientes nuevos
  - Cada card incluye sparkline (mini gráfico SVG inline) con tendencia últimos 7 días
  - Indicador de variación porcentual con flecha ↑↓
- Tabla de ventas recientes:
  - Columnas: N° Venta, Cliente, Total, Estado, Fecha
  - Filtros inline: rango de fechas, estado, vendedor, método de pago
  - Acciones: ver detalle, anular, generar comprobante
- Gráfico de barras por vendedor (Chart.js o ngx-charts):
  - Top 10 vendedores por volumen de ventas
  - Tooltip con detalle de transacciones
  - Toggle entre vista mensual/semanal

**Gestión de productos:**
- Tabla editable inline: click en celda para editar nombre, precio, stock
- Drag-and-drop para reordenar categorías (Angular CDK DragDrop)
- Filtros avanzados: categoría, marca, rango de precio, stock disponible
- Vista grid/cards toggle para exploración visual
- Modal de creación/edición con formulario en 2 columnas

**Gestión de inventario:**
- Panel kanban por almacén: columnas = almacenes, tarjetas = productos
- Alertas en rojo pulsante (animación) para productos bajo stock mínimo
- Transferencia entre almacenes vía drag-and-drop
- Historial de movimientos con filtro por fecha, tipo, usuario

**Panel de auditoría:**
- Timeline interactivo vertical con iconos por módulo (venta, producto, usuario)
- Filtros combinados: módulo, usuario, rango de fechas, acción
- Detalle expandible por evento: datos anteriores vs nuevos
- Exportación a PDF/CSV

**Temas:**
- Modo oscuro nativo: variables CSS custom properties
- Modo "alto contraste": bordes más gruesos, colores saturados, sin sombras
- Persistencia en localStorage

---

## 2. Stack Tecnológico y Herramientas

| Herramienta | Versión | Justificación |
|---|---|---|
| Angular | 19+ | Framework enterprise, compilador Ivy, signals, standalone components, SSR opcional con Angular Universal |
| Angular Material | latest | Componentes pre-built, accesibilidad (a11y) WCAG 2.1, theming consistente |
| Angular Router | integrado | Navegación lazy-loaded, guards, resolvers para precarga de datos |
| Angular HttpClient + RxJS | integrado | Peticiones HTTP tipadas, operators (catchError, map, tap, switchMap) |
| Standalone Signals (NgRx opcional) | - | Estado global reactivo para cart, auth, checkout. NgRx solo si la complejidad lo requiere |
| Angular Reactive Forms | integrado | Validación síncrona/asíncrona, controles tipados, facilita pruebas |
| Angular CDK | integrado | Drag-drop, overlays, virtual scroll para tablas grandes, portales |
| Chart.js (ng-charts) | latest | Dashboard admin: ventas, KPIs, ranking productos |
| Angular PWA | integrado | Service worker, offline para catálogo básico y caché de assets |
| Jest + Angular Testing Library | latest | Pruebas unitarias y de integración, 80%+ cobertura objetivo |
| ESLint + Prettier | latest | Linting y formateo automático, configuración estricta |

---

## 3. Arquitectura de Software

### 3.1 Estructura de carpetas

```
sistema-ventas-frontend/
├── src/
│   ├── app/
│   │   ├── core/                          # Código singleton (una sola instancia)
│   │   │   ├── guards/
│   │   │   │   ├── auth.guard.ts
│   │   │   │   └── role.guard.ts
│   │   │   ├── interceptors/
│   │   │   │   ├── auth.interceptor.ts
│   │   │   │   └── error.interceptor.ts
│   │   │   ├── services/
│   │   │   │   ├── api.service.ts
│   │   │   │   ├── auth.service.ts
│   │   │   │   └── storage.service.ts
│   │   │   └── models/
│   │   │       └── api-response.model.ts
│   │   ├── shared/                        # Componentes, pipes, directives reutilizables
│   │   │   ├── components/
│   │   │   │   ├── loading-spinner/
│   │   │   │   ├── empty-state/
│   │   │   │   ├── confirm-dialog/
│   │   │   │   └── price-display/
│   │   │   ├── pipes/
│   │   │   │   ├── currency.pipe.ts
│   │   │   │   ├── date-format.pipe.ts
│   │   │   │   └── truncate.pipe.ts
│   │   │   └── directives/
│   │   │       ├── loading.directive.ts
│   │   │       └── permission.directive.ts
│   │   ├── features/                      # Módulos de funcionalidad (Feature-Sliced Design)
│   │   │   ├── auth/
│   │   │   │   ├── login/
│   │   │   │   ├── register/
│   │   │   │   └── components/
│   │   │   ├── catalog/
│   │   │   │   ├── product-list/
│   │   │   │   ├── product-detail/
│   │   │   │   ├── category-filter/
│   │   │   │   └── search/
│   │   │   ├── cart/
│   │   │   │   ├── cart-page/
│   │   │   │   ├── cart-summary/
│   │   │   │   └── checkout/
│   │   │   │       ├── address/
│   │   │   │       ├── payment/
│   │   │   │       └── confirmation/
│   │   │   ├── sales/
│   │   │   │   ├── sale-list/
│   │   │   │   ├── sale-detail/
│   │   │   │   └── receipt/
│   │   │   ├── inventory/
│   │   │   │   ├── stock-panel/
│   │   │   │   ├── transfer-modal/
│   │   │   │   └── movement-history/
│   │   │   └── admin/
│   │   │       ├── dashboard/
│   │   │       ├── product-management/
│   │   │       ├── user-management/
│   │   │       ├── audit-panel/
│   │   │       └── reports/
│   │   └── app.routes.ts
│   ├── environments/
│   │   ├── environment.ts
│   │   └── environment.prod.ts
│   └── index.html
├── angular.json
├── package.json
├── tsconfig.json
├── eslint.config.js
└── README.md
```

### 3.2 Patrones arquitectónicos

**Feature-Sliced Design (FSD):**
- Cada feature es autocontenido: contiene sus propios componentes, servicios, modelos y rutas
- Las features no importan directamente entre sí; la comunicación se realiza a través de `core` o eventos
- Capas en orden de dependencia: `widgets` → `features` → `shared` → `core`

**Smart vs Dumb components:**
- **Smart (páginas/containers)**: Contienen lógica de negocio, se suscriben a servicios/estado, manejan datos
- **Dumb (presentacionales)**: Reciben datos via @Input, emiten eventos via @Output, sin lógica de negocio
- Ejemplo: `ProductListPage` (smart) → `ProductCardComponent` (dumb)

**Comunicación entre componentes:**
- Estado global: Signals para estado compartido (cart, auth session)
- Eventos: RxJS Subjects en servicios para comunicación entre features
- Routing: Navegación declarativa con parámetros y query params

**Lazy loading:**
- Cada feature se carga bajo demanda mediante `loadComponent` en el router
- Módulos shared y core se cargan eager (singleton)

---

## 4. Documentación de Integración

### 4.1 Configuración Base

**URL base por ambiente:**
- Desarrollo: `http://localhost:8080/api`
- Producción: configurable via variable de entorno

**Headers estándar:**
```
Content-Type: application/json
Accept: application/json
Authorization: Bearer {accessToken}
```

**Formato de respuesta estándar (ApiResponse<T>):**
```typescript
interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}
```

**Manejo de errores:**
- 4xx: Error del cliente, mostrar mensaje al usuario
- 401: Token expirado → interceptor dispara refresh automático
- 403: Sin permisos → redirigir a acceso denegado
- 5xx: Error del servidor → mensaje genérico + retry automático (máximo 3 intentos)

### 4.2 Interfaces TypeScript completas

```typescript
// ========== AUTH ==========
interface LoginRequest {
  email: string;
  password: string;
}

interface RegisterRequest {
  nombre: string;
  apellido: string;
  email: string;
  password: string;
  dni?: string;
  telefono?: string;
}

interface RefreshTokenRequest {
  refreshToken: string;
}

interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  usuario: {
    id: number;
    uuid: string;
    nombre: string;
    apellido: string;
    email: string;
    roles: string[];
    permisos: string[];
  };
}

// ========== CLIENTE ==========
interface TipoDocumento {
  id: number;
  codigo: string;
  descripcion: string;
}

interface Cliente {
  id: number;
  uuid: string;
  nombre: string;
  apellido: string;
  razonSocial?: string;
  email?: string;
  telefono?: string;
  direccion?: string;
  distrito?: string;
  provincia?: string;
  departamento?: string;
  tipoDocumento?: TipoDocumento;
  numeroDocumento?: string;
  activo: boolean;
}

interface ClienteRequest {
  tipoDocumentoId: number;
  numeroDocumento: string;
  nombre: string;
  apellido?: string;
  razonSocial?: string;
  email?: string;
  telefono?: string;
  direccion?: string;
  distrito?: string;
  provincia?: string;
  departamento?: string;
}

// ========== PRODUCTO ==========
interface Categoria {
  id: number;
  nombre: string;
  categoriaPadreId?: number;
}

interface Marca {
  id: number;
  nombre: string;
  logoUrl?: string;
}

interface UnidadMedida {
  id: number;
  codigo: string;
  descripcion: string;
  codigoSunat?: string;
}

interface Producto {
  id: number;
  uuid: string;
  codigo?: string;
  codigoBarras?: string;
  nombre: string;
  descripcion?: string;
  imagenUrl?: string;
  precioCosto: number;
  precioVenta: number;
  precioMayoreo?: number;
  igvIncluido: boolean;
  afectoIgv: boolean;
  activo: boolean;
  destacado: boolean;
  categoria?: Categoria;
  marca?: Marca;
  unidadMedida?: UnidadMedida;
  stockActual?: number;
}

interface ProductoRequest {
  codigo?: string;
  codigoBarras?: string;
  nombre: string;
  descripcion?: string;
  categoriaId: number;
  marcaId: number;
  unidadMedidaId: number;
  precioCosto: number;
  precioVenta: number;
  precioMayoreo?: number;
  igvIncluido?: boolean;
  afectoIgv?: boolean;
  destacado?: boolean;
}

// ========== DETALLE VENTA ==========
interface DetalleVentaRequest {
  productoId: number;
  cantidad: number;
  descuentoPorcentaje?: number;
}

interface DetalleVentaResponse {
  id: number;
  productoNombre: string;
  productoCodigo?: string;
  cantidad: number;
  precioUnitario: number;
  descuentoPorcentaje?: number;
  descuentoMonto?: number;
  precioFinal: number;
  subtotal: number;
  igv?: number;
  total: number;
}

// ========== VENTA ==========
interface VentaRequest {
  clienteId?: number;
  almacenId: number;
  moneda?: string;
  observaciones?: string;
  detalles: DetalleVentaRequest[];
}

interface Venta {
  id: number;
  uuid: string;
  numeroVenta: string;
  cliente?: Cliente;
  vendedor: {
    id: number;
    nombre: string;
    apellido: string;
  };
  estadoVenta: {
    id: number;
    codigo: string;
    descripcion: string;
  };
  subtotal: number;
  descuentoTotal?: number;
  igv: number;
  total: number;
  moneda: string;
  fechaVenta: string;
  observaciones?: string;
  detalles: DetalleVentaResponse[];
  pago?: Pago;
}

// ========== PAGO ==========
interface MetodoPago {
  id: number;
  codigo: string;
  descripcion: string;
  requierePasarela: boolean;
}

interface EstadoPago {
  id: number;
  codigo: string;
  descripcion: string;
}

interface Pago {
  id: number;
  uuid: string;
  monto: number;
  moneda: string;
  metodoPagoCodigo: string;
  metodoPagoDescripcion: string;
  estadoPagoCodigo: string;
  estadoPagoDescripcion: string;
  codigoAutorizacion?: string;
  ultimos4Digitos?: string;
  marcaTarjeta?: string;
  numeroOperacion?: string;
  telefonoOrigen?: string;
  fechaPago: string;
}

interface PagoRequest {
  ventaId: number;
  metodoPagoId: number;
  monto: number;
  montoRecibido?: number;
  referenciaExterna?: string;
  telefonoOrigen?: string;
  numeroOperacion?: string;
}

// ========== INVENTARIO ==========
interface Almacen {
  id: number;
  nombre: string;
  direccion?: string;
  principal: boolean;
}

interface Inventario {
  id: number;
  productoId: number;
  almacenId: number;
  stockActual: number;
  stockMinimo?: number;
  stockMaximo?: number;
  ubicacion?: string;
}

interface TipoMovimientoStock {
  id: number;
  codigo: string;
  descripcion: string;
}

// ========== COMPROBANTE ==========
interface TipoComprobante {
  id: number;
  codigo: string;
  descripcion: string;
  serieDefault: string;
}

interface Comprobante {
  id: number;
  ventaId: number;
  tipoComprobante: TipoComprobante;
  serie: string;
  correlativo: string;
  numeroCompleto: string;
  clienteTipoDoc?: string;
  clienteNumeroDoc?: string;
  clienteNombre?: string;
  subtotal: number;
  igv: number;
  total: number;
  estadoSunat: string;
  pdfUrl?: string;
}

// ========== PAGINACIÓN ==========
interface PageResponse<T> {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}
```

### 4.3 API Endpoints completos (verificado contra código fuente)

| Módulo | Endpoint | Método | Auth | Request | Response |
|---|---|---|---|---|---|
| Auth | `/auth/login` | POST | No | LoginRequest | ApiResponse<AuthResponse> |
| Auth | `/auth/register` | POST | No | RegisterRequest | ApiResponse<AuthResponse> |
| Auth | `/auth/refresh` | POST | No | RefreshTokenRequest | ApiResponse<AuthResponse> |
| Auth | `/auth/logout` | POST | Sí | RefreshTokenRequest | ApiResponse<void> |
| Usuarios | `/usuarios` | GET | Sí | Query params | ApiResponse<PageResponse<Usuario>> |
| Clientes | `/clientes` | GET | Sí | Query | ApiResponse<PageResponse<Cliente>> |
| Clientes | `/clientes/buscar` | GET | Sí | Query: termino | ApiResponse<PageResponse<Cliente>> |
| Clientes | `/clientes/documento` | GET | Sí | Query: tipoId, numero | ApiResponse<Cliente> |
| Productos | `/productos` | GET | Sí | Query: page, size | ApiResponse<PageResponse<Producto>> |
| Inventario | `/inventario/alertas` | GET | Sí | - | ApiResponse<List<Inventario>> |
| Ventas | `/ventas` | POST | Sí | VentaRequest | ApiResponse<Venta> |
| Ventas | `/ventas` | GET | Sí | Query: page, size | ApiResponse<PageResponse<Venta>> |
| Ventas | `/ventas/{id}` | GET | Sí | - | ApiResponse<Venta> |
| Ventas | `/ventas/{id}/anular` | POST | Sí | Query: motivo | ApiResponse<Venta> |
| Ventas | `/ventas/{id}/pago` | POST | Sí | PagoRequest | ApiResponse<Venta> |
| Pagos | `/pagos` | POST | Sí | PagoRequest | ApiResponse<Pago> |
| Pagos | `/pagos/webhook` | POST | No | body: string + X-Signature header | ApiResponse<void> |
| Pagos | `/pagos/venta/{ventaId}` | GET | Sí | - | ApiResponse<Pago> |

### 4.4 Flujos críticos

**Autenticación:**
1. Usuario ingresa credenciales → POST `/auth/login`
2. Backend retorna `accessToken`, `refreshToken`, datos de usuario
3. Frontend almacena tokens en localStorage (o IndexedDB para mayor seguridad)
4. Interceptor agrega `Authorization: Bearer {accessToken}` a cada petición
5. Si 401 Unauthorized → interceptor dispara refresh automático con `/auth/refresh`
6. Si refresh también falla → limpiar sesión y redirigir a login
7. Logout → POST `/auth/logout` + limpiar storage local

**Creación de venta:**
1. Usuario navega catálogo → agrega productos al carrito
2. Carrito valida stock disponible llamando a `/inventario/producto/{id}`
3. Al checkout, usuario selecciona domicilio y método de pago
4. Frontend construye `VentaRequest` con detalles y almacen seleccionado
5. POST `/ventas` con body completo
6. Backend retorna `Venta` con número generado, estado PENDIENTE
7. Si hay pago inmediato → flujo de pago

**Flujo de pago:**
1. POST `/pagos` con `PagoRequest` (ventaId, metodoPagoId, monto)
2. Si método requiere pasarela → redirigir/abrir modal de pasarela
3. Pasarela notifica via webhook a `/pagos/webhook`
4. Frontend consulta estado: GET `/pagos/venta/{ventaId}`
5. Al confirmar pago → mostrar comprobante (si existe) o número de venta

### 4.5 Catálogos (Enums)

| Catálogo | Valores |
|---|---|
| Roles | ADMIN, SUPERVISOR, VENDEDOR, ALMACENERO, CONTADOR |
| Estados Venta | PENDIENTE, PAGADO, ANULADO, DEVUELTO |
| Estados Pago | PENDIENTE, APROBADO, RECHAZADO, ANULADO, REEMBOLSADO |
| Métodos Pago | EFECTIVO, TARJETA_CREDITO, TARJETA_DEBITO, YAPE, PLIN, TRANSFERENCIA, DEPOSITO |
| Tipos Documento | DNI, RUC, CE, PASAPORTE |

### 4.6 Consideraciones adicionales

**Manejo de stock:**
- `stockActual` no se incluye en la respuesta de `/productos` (ignorado en ProductoMapper)
- El frontend debe consultar stock por producto via endpoint de inventario separado
- Cachear stock localmente y refrescar al agregar/quitar del carrito

**Tipos monetarios:**
- Todos los valores monetarios son `BigDecimal` en Java
- Mapear a `number` en TypeScript (doble precisión IEEE 754)
- Redondear a 2 decimales en presentación, nunca confiar en el backend para formateo

**Fechas:**
- Campos `LocalDateTime` se serializan como ISO 8601 strings
- Parsear con `new Date()` o librería date-fns/dayjs
- Mostrar en zona horaria local del usuario

**Paginación:**
- `PageResponse` usa `Pageable` con tamaño por defecto 20
- Implementar infinite scroll o paginación tradicional con controles
