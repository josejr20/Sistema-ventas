# Arquitectura Backend — Sistema de Ventas
> **Stack:** Spring Boot 3 · Spring Security 6 · JWT · JPA/Hibernate · PostgreSQL · Swagger

---

## 1. Patrón de arquitectura: Capas (Layered Architecture)

El backend sigue una arquitectura en **7 capas estrictas**. Cada capa solo se comunica con la capa inmediatamente inferior. Ninguna capa "salta" a otra.

```
┌─────────────────────────────────────────────┐
│  CLIENTE  (React / Postman / App móvil)     │
└─────────────────────┬───────────────────────┘
                      │  HTTP Request
┌─────────────────────▼───────────────────────┐
│  SECURITY   JwtAuthenticationFilter         │  ← Valida el token JWT
│             UserDetailsServiceImpl          │     antes de llegar al controller
└─────────────────────┬───────────────────────┘
                      │
┌─────────────────────▼───────────────────────┐
│  CONTROLLER  @RestController                │  ← Recibe la request HTTP
│              Valida formato del JSON        │     Devuelve ResponseEntity
│              Llama al Service               │
└─────────────────────┬───────────────────────┘
                      │
┌─────────────────────▼───────────────────────┐
│  SERVICE  @Service  @Transactional          │  ← Toda la lógica de negocio
│           Valida reglas de negocio          │     Aquí vive el "cómo funciona"
│           Coordina repositorios             │     el sistema
└─────────────────────┬───────────────────────┘
                      │
┌─────────────────────▼───────────────────────┐
│  REPOSITORY  JpaRepository                  │  ← Consultas a la base de datos
│              Queries JPQL / @Query          │     Solo maneja SQL/JPA
└─────────────────────┬───────────────────────┘
                      │
┌─────────────────────▼───────────────────────┐
│  ENTITY  @Entity  @Table                    │  ← Mapea cada tabla de PostgreSQL
│          Relaciones JPA                     │     a un objeto Java
└─────────────────────┬───────────────────────┘
                      │
┌─────────────────────▼───────────────────────┐
│  BASE DE DATOS  PostgreSQL                  │  ← Las 26 tablas del schema
└─────────────────────────────────────────────┘
```

**Regla de oro:** Un `Controller` nunca accede a un `Repository` directamente. Siempre pasa por el `Service`.

---

## 2. Stack tecnológico (`pom.xml`)

| Dependencia | Versión | Para qué sirve |
|---|---|---|
| `spring-boot-starter-web` | 3.x | Servidor HTTP, REST endpoints |
| `spring-boot-starter-data-jpa` | 3.x | JPA/Hibernate para PostgreSQL |
| `spring-boot-starter-security` | 3.x | Autenticación y autorización |
| `spring-boot-starter-validation` | 3.x | Validar DTOs (`@NotBlank`, `@Email`) |
| `postgresql` | 42.x | Driver JDBC de PostgreSQL |
| `jjwt-api` + `jjwt-impl` + `jjwt-jackson` | 0.11.5 | Generar y validar tokens JWT |
| Elimina boilerplate (`@Data`, `@Builder`) |
| `springdoc-openapi-starter-webmvc-ui` | 2.x | Swagger UI en `/swagger-ui.html` |
| `mapstruct` | 1.5.x | Convierte Entity ↔ DTO automáticamente |

---

## 3. Flujo completo de una petición de venta

Ejemplo: **POST /api/ventas** (crear una venta)

```
1. React envía:  POST /api/ventas  +  Header: Authorization: Bearer <token>

2. JwtAuthenticationFilter intercepta la request
   → Extrae el token del header
   → Llama a JwtTokenProvider.validateToken()
   → Si es válido: carga el usuario al SecurityContext
   → Si inválido: devuelve 401 Unauthorized

3. VentaController.crearVenta(@RequestBody VentaRequest)
   → Valida que el JSON tenga los campos requeridos (@Valid)
   → Llama a ventaService.crearVenta(request)

4. VentaService.crearVenta(request)
   → Verifica que el cliente existe (clienteRepository.findById)
   → Verifica stock de cada producto (inventarioRepository)
   → Crea la entidad Venta con estado PENDIENTE
   → Guarda con ventaRepository.save(venta)
   → Crea los DetalleVenta
   → Registra en auditoriaService.registrar(...)
   → Devuelve VentaResponse

5. VentaController devuelve:
   ResponseEntity<ApiResponse<VentaResponse>> con 201 Created
```

---

## 4. Árbol completo de archivos

```
Backend-sistema-ventas/
│
├── pom.xml                                          ← Dependencias Maven
│
└── src/
    ├── main/
    │   ├── java/com/empresa/ventas/sistema_ventas/
    │   │   │
    │   │   ├── SistemaVentasApplication.java        ← Punto de entrada (@SpringBootApplication)
    │   │   │
    │   │   ├── config/                              ── CAPA: CONFIGURACIÓN ──
    │   │   │   ├── SecurityConfig.java              ← Cadena de filtros Spring Security
    │   │   │   ├── CorsConfig.java                  ← Permitir peticiones desde React
    │   │   │   └── OpenApiConfig.java               ← Swagger con autenticación JWT
    │   │   │
    │   │   ├── security/                            ── CAPA: SEGURIDAD ──
    │   │   │   ├── jwt/
    │   │   │   │   ├── JwtTokenProvider.java        ← Crea y valida tokens JWT
    │   │   │   │   └── JwtAuthenticationFilter.java ← Filtro HTTP: lee el token por request
    │   │   │   ├── UserDetailsServiceImpl.java      ← Carga usuario de BD para Spring Security
    │   │   │   └── JwtAuthenticationEntryPoint.java ← Responde 401 cuando no hay token
    │   │   │
    │   │   ├── entity/                              ── CAPA: ENTIDADES JPA ──
    │   │   │   ├── base/
    │   │   │   │   └── BaseEntity.java              ← Campos comunes: id, createdAt, updatedAt
    │   │   │   │
    │   │   │   ├── auth/                            ↳ Módulo: Seguridad y usuarios
    │   │   │   │   ├── Usuario.java                 ← Tabla: usuarios
    │   │   │   │   ├── Rol.java                     ← Tabla: roles
    │   │   │   │   ├── Permiso.java                 ← Tabla: permisos
    │   │   │   │   └── RefreshToken.java            ← Tabla: refresh_tokens
    │   │   │   │
    │   │   │   ├── cliente/                         ↳ Módulo: Clientes
    │   │   │   │   ├── Cliente.java                 ← Tabla: clientes
    │   │   │   │   └── TipoDocumento.java           ← Tabla: tipo_documento
    │   │   │   │
    │   │   │   ├── producto/                        ↳ Módulo: Catálogo
    │   │   │   │   ├── Producto.java                ← Tabla: productos
    │   │   │   │   ├── Categoria.java               ← Tabla: categorias (auto-referencia)
    │   │   │   │   ├── Marca.java                   ← Tabla: marcas
    │   │   │   │   └── UnidadMedida.java            ← Tabla: unidades_medida
    │   │   │   │
    │   │   │   ├── inventario/                      ↳ Módulo: Stock
    │   │   │   │   ├── Inventario.java              ← Tabla: inventario
    │   │   │   │   ├── Almacen.java                 ← Tabla: almacenes
    │   │   │   │   ├── MovimientoStock.java         ← Tabla: movimiento_stock
    │   │   │   │   └── TipoMovimientoStock.java     ← Tabla: tipo_movimiento_stock
    │   │   │   │
    │   │   │   ├── venta/                           ↳ Módulo: Ventas
    │   │   │   │   ├── Venta.java                   ← Tabla: ventas
    │   │   │   │   ├── DetalleVenta.java            ← Tabla: detalle_venta
    │   │   │   │   └── EstadoVenta.java             ← Tabla: estado_venta
    │   │   │   │
    │   │   │   ├── pago/                            ↳ Módulo: Pagos
    │   │   │   │   ├── Pago.java                    ← Tabla: pagos
    │   │   │   │   ├── DetallePago.java             ← Tabla: detalle_pago (pagos mixtos)
    │   │   │   │   ├── MetodoPago.java              ← Tabla: metodos_pago
    │   │   │   │   └── EstadoPago.java              ← Tabla: estado_pago
    │   │   │   │
    │   │   │   ├── comprobante/                     ↳ Módulo: Facturación SUNAT
    │   │   │   │   ├── Comprobante.java             ← Tabla: comprobantes
    │   │   │   │   └── TipoComprobante.java         ← Tabla: tipo_comprobante
    │   │   │   │
    │   │   │   └── auditoria/                       ↳ Módulo: Trazabilidad
    │   │   │       └── Auditoria.java               ← Tabla: auditoria
    │   │   │
    │   │   ├── repository/                          ── CAPA: REPOSITORIOS ──
    │   │   │   ├── UsuarioRepository.java           ← Queries sobre tabla usuarios
    │   │   │   ├── RolRepository.java               ← Queries sobre tabla roles
    │   │   │   ├── RefreshTokenRepository.java      ← Queries sobre refresh_tokens
    │   │   │   ├── ClienteRepository.java           ← Búsqueda por documento, nombre
    │   │   │   ├── ProductoRepository.java          ← Búsqueda full-text, por categoría
    │   │   │   ├── CategoriaRepository.java         ← Árbol de categorías
    │   │   │   ├── InventarioRepository.java        ← Stock actual, alertas stock bajo
    │   │   │   ├── MovimientoStockRepository.java   ← Historial de movimientos
    │   │   │   ├── VentaRepository.java             ← Ventas por fecha, usuario, estado
    │   │   │   ├── DetalleVentaRepository.java      ← Líneas de cada venta
    │   │   │   ├── PagoRepository.java              ← Pagos por venta y método
    │   │   │   └── ComprobanteRepository.java       ← Comprobantes por serie/correlativo
    │   │   │
    │   │   ├── dto/                                 ── CAPA: DATOS DE TRANSFERENCIA ──
    │   │   │   ├── request/                         ↳ Lo que el cliente ENVÍA al backend
    │   │   │   │   ├── LoginRequest.java            ← { email, password }
    │   │   │   │   ├── RegisterRequest.java         ← { nombre, apellido, email, password }
    │   │   │   │   ├── RefreshTokenRequest.java     ← { refreshToken }
    │   │   │   │   ├── ClienteRequest.java          ← Datos para crear/editar cliente
    │   │   │   │   ├── ProductoRequest.java         ← Datos para crear/editar producto
    │   │   │   │   ├── VentaRequest.java            ← Cabecera de venta + lista de detalles
    │   │   │   │   ├── DetalleVentaRequest.java     ← { productoId, cantidad, precio }
    │   │   │   │   └── PagoRequest.java             ← { metodoPagoId, monto, referencia }
    │   │   │   │
    │   │   │   └── response/                        ↳ Lo que el backend DEVUELVE al cliente
    │   │   │       ├── AuthResponse.java            ← { accessToken, refreshToken, usuario }
    │   │   │       ├── ClienteResponse.java         ← Datos del cliente sin campos internos
    │   │   │       ├── ProductoResponse.java        ← Producto + nombre categoría + stock
    │   │   │       ├── VentaResponse.java           ← Venta + detalles + estado
    │   │   │       ├── DetalleVentaResponse.java    ← Línea de venta con totales
    │   │   │       ├── PagoResponse.java            ← Pago con estado y referencia
    │   │   │       └── PageResponse.java            ← Paginación genérica { content, page, total }
    │   │   │
    │   │   ├── service/                             ── CAPA: LÓGICA DE NEGOCIO ──
    │   │   │   ├── AuthService.java                 ← Login, registro, refresh, logout
    │   │   │   ├── UsuarioService.java              ← CRUD de usuarios del sistema
    │   │   │   ├── ClienteService.java              ← CRUD de clientes
    │   │   │   ├── ProductoService.java             ← CRUD de productos y catálogo
    │   │   │   ├── InventarioService.java           ← Consulta stock, ajustes, traslados
    │   │   │   ├── VentaService.java                ← Flujo completo de venta
    │   │   │   ├── PagoService.java                 ← Procesar pago, webhook, reembolso
    │   │   │   ├── ComprobanteService.java          ← Emitir boleta/factura a SUNAT
    │   │   │   └── AuditoriaService.java            ← Registrar toda acción importante
    │   │   │
    │   │   ├── controller/                          ── CAPA: ENDPOINTS REST ──
    │   │   │   ├── AuthController.java              ← POST /api/auth/login, /register, /refresh
    │   │   │   ├── UsuarioController.java           ← CRUD /api/usuarios
    │   │   │   ├── ClienteController.java           ← CRUD /api/clientes
    │   │   │   ├── ProductoController.java          ← CRUD /api/productos
    │   │   │   ├── InventarioController.java        ← GET/PUT /api/inventario
    │   │   │   ├── VentaController.java             ← POST/GET /api/ventas
    │   │   │   └── PagoController.java             ← POST /api/pagos, /webhook
    │   │   │
    │   │   ├── exception/                           ── MANEJO DE ERRORES ──
    │   │   │   ├── GlobalExceptionHandler.java      ← Captura TODOS los errores del sistema
    │   │   │   ├── ResourceNotFoundException.java   ← Lanzar cuando no encuentra un registro (404)
    │   │   │   ├── BusinessException.java           ← Lanzar cuando viola regla de negocio (400)
    │   │   │   └── TokenException.java              ← Lanzar cuando el token es inválido (401)
    │   │   │
    │   │   └── util/                                ── UTILITARIOS ──
    │   │       ├── ApiResponse.java                 ← Respuesta estándar para TODOS los endpoints
    │   │       └── NumeroVentaGenerator.java        ← Genera: VEN-00000001, VEN-00000002...
    │   │
    │   └── resources/
    │       └── application.yaml                     ← BD, JWT, servidor, CORS
    │
    └── test/
        └── java/com/empresa/ventas/sistema_ventas/
            ├── service/
            │   └── VentaServiceTest.java            ← Test unitario del flujo de venta
            └── controller/
                └── AuthControllerTest.java          ← Test de integración de autenticación
```

**Totales:** 9 carpetas de código · 66 archivos Java · 1 YAML · 1 pom.xml

---

## 5. Descripción detallada por carpeta

---

### `pom.xml` — Raíz del proyecto

**Propósito:** Define todas las dependencias y el proceso de compilación de Maven.

**Contenido clave:**
- `parent`: `spring-boot-starter-parent` versión 3.2.x
- `dependencies`: Web, Security, JPA, PostgreSQL, JWT (jjwt), Validation, SpringDoc (Swagger), MapStruct
- `build > plugins`: `maven-compiler-plugin` con configuración de MapStruct como annotation processors (ambos deben estar juntos en el mismo plugin para que funcionen correctamente)
- `properties`: Java 17 como versión de compilación

---

### `application.yaml` — Configuración del sistema

**Propósito:** Centraliza toda la configuración de la aplicación. Separado del código para poder cambiar valores sin recompilar.

**Secciones:**
```yaml
server:
  port: 8080                        # Puerto del servidor

spring:
  datasource:
    url: jdbc:postgresql://...      # Conexión a PostgreSQL
    username / password             # Credenciales

  jpa:
    hibernate.ddl-auto: validate    # Valida el esquema, NO lo crea (ya existe)
    show-sql: true                  # Ver SQL en consola durante desarrollo

jwt:
  secret: clave-secreta-larga       # Mínimo 256 bits para HS256
  expiration: 900000                # Access token: 15 minutos en ms
  refresh-expiration: 604800000     # Refresh token: 7 días en ms

app:
  cors.allowed-origins:             # http://localhost:3000 (React)
```

---

### `SistemaVentasApplication.java` — Entrada de la aplicación

**Propósito:** Clase principal que arranca el servidor Spring Boot.

**Anotaciones:**
- `@SpringBootApplication` — Combina `@Configuration` + `@EnableAutoConfiguration` + `@ComponentScan`. Spring escanea todos los packages desde esta clase hacia abajo y registra todos los beans automáticamente.

**Contiene:** Solo el método `main()` que llama a `SpringApplication.run()`.

---

## CAPA: Config

### `config/SecurityConfig.java` — Configuración central de seguridad

**Propósito:** Define quién puede acceder a qué endpoint y cómo se autentica. Es el archivo más crítico de seguridad.

**Anotaciones:**
- `@Configuration` — Es una clase de configuración Spring
- `@EnableWebSecurity` — Activa Spring Security
- `@EnableMethodSecurity` — Permite usar `@PreAuthorize` en los controllers

**Beans que define:**

| Bean | Qué hace |
|---|---|
| `SecurityFilterChain filterChain(HttpSecurity)` | Define las reglas de acceso |
| `PasswordEncoder passwordEncoder()` | BCrypt con strength 10 |
| `AuthenticationManager authenticationManager()` | Gestiona el proceso de login |
| `AuthenticationProvider authenticationProvider()` | Usa UserDetailsService + BCrypt |

**Lógica del `filterChain`:**
```
- CSRF: desactivado (API REST no lo necesita)
- Sesión: STATELESS (no hay sesión, solo JWT)
- Rutas públicas: /api/auth/**, /swagger-ui/**, /v3/api-docs/**
- Todo lo demás: requiere autenticación
- Añade JwtAuthenticationFilter ANTES del filtro estándar de usuario/contraseña
```

---

### `config/CorsConfig.java` — Configuración de CORS

**Propósito:** Permite que el frontend React (puerto 3000) haga peticiones al backend (puerto 8080). Sin esto, el navegador bloquea todas las peticiones.

**Contiene:**
- Bean `CorsConfigurationSource` que permite:
  - Orígenes: `http://localhost:3000` (desarrollo), dominio de producción
  - Métodos: GET, POST, PUT, DELETE, PATCH
  - Headers: Authorization, Content-Type
  - `allowCredentials: true`

---

### `config/OpenApiConfig.java` — Configuración de Swagger

**Propósito:** Genera documentación automática de todos los endpoints en `/swagger-ui.html`. Permite probar la API directamente desde el navegador con autenticación JWT.

**Contiene:**
- Bean `OpenAPI` que configura el título, versión y descripción
- Componente `SecurityScheme` de tipo `Bearer JWT` para que Swagger envíe el token en las peticiones de prueba

---

## CAPA: Security

### `security/jwt/JwtTokenProvider.java` — Motor de JWT

**Propósito:** Es el corazón de la autenticación. Genera tokens JWT al hacer login y los valida en cada request.

**Campos:**
- `@Value("${jwt.secret}") String jwtSecret` — Clave secreta inyectada del YAML
- `@Value("${jwt.expiration}") long jwtExpiration` — Tiempo de vida en ms

**Métodos:**

| Método | Qué hace |
|---|---|
| `generateToken(UserDetails)` | Crea un JWT con email como subject, firmado con HS256 |
| `generateRefreshToken(UserDetails)` | Crea un refresh token de mayor duración |
| `extractUsername(String token)` | Lee el subject (email) del token |
| `validateToken(String token, UserDetails)` | Verifica firma, expiración y usuario |
| `isTokenExpired(String token)` | Comprueba si el token ya expiró |
| `getSigningKey()` | Convierte el secret en un objeto Key usando HMAC-SHA256 |

**Librería:** `io.jsonwebtoken` (JJWT 0.11.5)

---

### `security/jwt/JwtAuthenticationFilter.java` — Filtro de autenticación

**Propósito:** Se ejecuta en **CADA request** que llega al servidor. Lee el token del header `Authorization`, lo valida y carga al usuario en el contexto de seguridad de Spring.

**Hereda de:** `OncePerRequestFilter` (garantiza que se ejecuta una sola vez por request)

**Lógica del `doFilterInternal()`:**
```
1. Extrae el header: Authorization: Bearer <token>
2. Si no hay header → pasa la request al siguiente filtro (sin autenticar)
3. Extrae el token quitando "Bearer "
4. Llama a jwtTokenProvider.extractUsername(token) → obtiene el email
5. Carga el usuario: userDetailsService.loadUserByUsername(email)
6. Llama a jwtTokenProvider.validateToken(token, userDetails)
7. Si válido: crea un UsernamePasswordAuthenticationToken y lo pone
   en SecurityContextHolder (Spring sabrá que el usuario está autenticado)
8. Llama a filterChain.doFilter() para continuar con la request
```

---

### `security/UserDetailsServiceImpl.java` — Carga de usuario para Spring Security

**Propósito:** Cuando Spring Security necesita autenticar a alguien, llama a este servicio para obtener los datos del usuario desde la base de datos.

**Implementa:** `UserDetailsService` (interfaz de Spring Security)

**Método:** `loadUserByUsername(String email)`:
- Busca el usuario en la BD por email
- Si no existe → lanza `UsernameNotFoundException`
- Retorna un objeto `User` de Spring Security con: email, passwordHash y los permisos como `GrantedAuthority`
- Los permisos vienen de `usuario.getRoles()` y los permisos de cada rol

---

### `security/JwtAuthenticationEntryPoint.java` — Manejador de 401

**Propósito:** Cuando alguien accede a un endpoint protegido sin token (o con token inválido), Spring Security llama a esta clase para generar la respuesta de error.

**Implementa:** `AuthenticationEntryPoint`

**Método:** `commence()`:
- Pone el status HTTP 401
- Devuelve JSON: `{ "status": 401, "error": "No autorizado", "message": "..." }`

---

## CAPA: Entity

> **Convención de entidades:** Todas las entidades usan  para eliminar getters/setters/constructores. Todas extienden `BaseEntity` excepto las que tienen IDs compuestos o son tablas pivot.

### `entity/base/BaseEntity.java` — Clase base para entidades

**Propósito:** Campos comunes que se repiten en casi todas las tablas. Evita duplicar código en 20+ entidades.

**Anotaciones de clase:**
- `@MappedSuperclass` — JPA hereda los campos pero esta clase NO es una tabla por sí misma
- `@EntityListeners(AuditingEntityListener.class)` — Activa el llenado automático de fechas

**Campos:**
```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
Long id;

@CreationTimestamp
LocalDateTime createdAt;  // Se llena solo al insertar

@UpdateTimestamp
LocalDateTime updatedAt;  // Se actualiza solo al modificar
```

---

### `entity/auth/Usuario.java` — Tabla: `usuarios`

**Propósito:** Representa a los empleados del sistema (admin, vendedores, etc.).

**Implementa:** `UserDetails` (para que Spring Security pueda usarlo directamente)

**Campos clave:**
- `uuid` (UUID, generado automáticamente)
- `nombre`, `apellido`, `email` (unique)
- `passwordHash` — La contraseña nunca se guarda en texto plano, siempre BCrypt
- `activo`, `bloqueado`, `intentosFallidos`
- `ultimoLogin` (LocalDateTime)

**Relaciones:**
```
@ManyToMany(fetch = FetchType.EAGER)
@JoinTable(name = "usuario_rol", ...)
Set<Rol> roles;
```
`EAGER` aquí es intencional: Spring Security necesita los roles al cargar el usuario.

**Métodos de UserDetails que implementa:**
- `getAuthorities()` → combina roles y permisos
- `getUsername()` → retorna el email
- `isAccountNonLocked()` → retorna `!bloqueado`
- `isEnabled()` → retorna `activo`

---

### `entity/auth/Rol.java` — Tabla: `roles`

**Campos:** `nombre` (ADMIN, VENDEDOR...), `descripcion`, `activo`

**Relaciones:**
```
@ManyToMany(mappedBy = "roles")
Set<Usuario> usuarios;

@ManyToMany(fetch = FetchType.EAGER)
@JoinTable(name = "rol_permiso", ...)
Set<Permiso> permisos;
```

---

### `entity/auth/Permiso.java` — Tabla: `permisos`

**Campos:** `nombre` (VENTA_CREAR, PRODUCTO_EDITAR...), `descripcion`, `modulo`, `activo`

**Propósito:** Granularidad fina de acceso. Un rol tiene muchos permisos. Los controllers usarán `@PreAuthorize("hasAuthority('VENTA_CREAR')")`.

---

### `entity/auth/RefreshToken.java` — Tabla: `refresh_tokens`

**Propósito:** Guarda los refresh tokens activos. Permite invalidar todos los tokens de un usuario (logout desde todos los dispositivos).

**Campos:**
- `token` (String único, el refresh token)
- `usuarioId` (@ManyToOne → Usuario)
- `expiraAt` (LocalDateTime)
- `revocado` (boolean)
- `ipAddress`

---

### `entity/cliente/Cliente.java` — Tabla: `clientes`

**Campos clave:**
- `uuid`, `nombre`, `apellido`, `razonSocial`
- `email`, `telefono`, `direccion`
- `distrito`, `provincia`, `departamento`
- `tipoDocumento` (@ManyToOne → TipoDocumento)
- `numeroDocumento` — Unique junto con tipoDocumento

**Restricción:** La combinación `(tipoDocumentoId, numeroDocumento)` es única. Un RUC no se puede repetir.

---

### `entity/cliente/TipoDocumento.java` — Tabla: `tipo_documento`

**Propósito:** Tabla de catálogo. Valores fijos: DNI, RUC, CE, PASAPORTE.

**Campos:** `codigo` (unique), `descripcion`

---

### `entity/producto/Producto.java` — Tabla: `productos`

**La entidad más importante del catálogo.**

**Campos clave:**
- `uuid`, `codigo` (SKU único), `codigoBarras` (unique)
- `nombre`, `descripcion`
- `precioCosto`, `precioVenta`, `precioMayoreo`
- `igvIncluido`, `afectoIgv` (para calcular IGV correctamente)
- `activo`, `destacado`

**Relaciones:**
```
@ManyToOne → Categoria
@ManyToOne → Marca
@ManyToOne → UnidadMedida
@ManyToOne → Usuario (createdBy)
@OneToMany(mappedBy = "producto") → List<Inventario>
```

---

### `entity/producto/Categoria.java` — Tabla: `categorias`

**Particularidad: auto-referencia para subcategorías.**

```java
@ManyToOne
@JoinColumn(name = "categoria_padre_id")
Categoria categoriaPadre;              // null si es categoría raíz

@OneToMany(mappedBy = "categoriaPadre")
List<Categoria> subcategorias;        // Lista de hijos
```

Esto permite árbol ilimitado: Electrónica → Celulares → Android

---

### `entity/producto/Marca.java` — Tabla: `marcas`

**Campos:** `nombre` (unique), `descripcion`, `logoUrl`, `activo`

---

### `entity/producto/UnidadMedida.java` — Tabla: `unidades_medida`

**Campos:** `codigo` (UND, KG, LT...), `descripcion`, `codigoSunat` (para facturación electrónica SUNAT)

---

### `entity/inventario/Inventario.java` — Tabla: `inventario`

**Propósito:** Guarda el stock actual de cada producto en cada almacén.

**Campos:**
- `productoId` (@ManyToOne → Producto)
- `almacenId` (@ManyToOne → Almacen)
- `stockActual` (BigDecimal)
- `stockMinimo` — Si stockActual <= stockMinimo, se genera alerta
- `stockMaximo`
- `ubicacion` (pasillo/estante)

**Restricción:** La combinación `(productoId, almacenId)` es única. Un producto tiene UN registro de inventario por almacén.

---

### `entity/inventario/Almacen.java` — Tabla: `almacenes`

**Campos:** `nombre`, `descripcion`, `direccion`, `principal` (boolean), `activo`

---

### `entity/inventario/MovimientoStock.java` — Tabla: `movimiento_stock`

**Propósito:** Log inmutable de cada cambio de stock. Nunca se edita ni elimina.

**Campos:**
- `producto` (@ManyToOne)
- `almacen` (@ManyToOne)
- `tipoMovimiento` (@ManyToOne → TipoMovimientoStock)
- `cantidad` (siempre positivo, el tipo indica si es entrada o salida)
- `stockAnterior`, `stockPosterior` — Para auditoría completa
- `referenciaTipo` (VENTA, COMPRA, AJUSTE)
- `referenciaId` — ID de la venta o compra que generó el movimiento
- `motivo`, `usuario`

---

### `entity/inventario/TipoMovimientoStock.java` — Tabla: `tipo_movimiento_stock`

**Propósito:** Catálogo de tipos: ENTRADA_COMPRA, SALIDA_VENTA, AJUSTE_POSITIVO, DEVOLUCION_CLIENTE...

---

### `entity/venta/Venta.java` — Tabla: `ventas`

**La entidad central del sistema.**

**Campos clave:**
- `uuid`, `numeroVenta` (VEN-00000001, unique)
- `cliente` (@ManyToOne → Cliente, puede ser null para ventas anónimas)
- `usuario` (@ManyToOne → Usuario, el vendedor)
- `almacen` (@ManyToOne)
- `estadoVenta` (@ManyToOne → EstadoVenta)
- `subtotal`, `descuentoTotal`, `igv`, `total` (BigDecimal)
- `moneda` (PEN/USD), `tipoCambio`
- `fechaVenta`, `observaciones`
- `fechaAnulacion`, `anuladoPor`, `motivoAnulacion` — Para trazabilidad de anulaciones

**Relaciones:**
```
@OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
List<DetalleVenta> detalles;        // Al guardar la venta, se guardan los detalles
```

---

### `entity/venta/DetalleVenta.java` — Tabla: `detalle_venta`

**Propósito:** Cada línea de producto dentro de una venta.

**Campos:**
- `venta` (@ManyToOne, la venta padre)
- `producto` (@ManyToOne)
- `cantidad`, `precioUnitario`
- `descuentoPorcentaje`, `descuentoMonto`
- `precioFinal` — precio después del descuento
- `subtotal`, `igv`, `total`
- `productoNombre` — **Snapshot**: guarda el nombre del producto en el momento de la venta. Si el producto cambia de nombre mañana, el historial sigue correcto.
- `productoCodigo` — Snapshot del SKU

---

### `entity/venta/EstadoVenta.java` — Tabla: `estado_venta`

**Propósito:** Catálogo de estados. Valores: PENDIENTE, PAGADO, ANULADO, DEVUELTO.

---

### `entity/pago/Pago.java` — Tabla: `pagos`

**La entidad que registra CADA transacción de pago.**

**Campos:**
- `uuid`, `venta` (@ManyToOne)
- `metodoPago` (@ManyToOne → MetodoPago)
- `estadoPago` (@ManyToOne → EstadoPago)
- `monto`, `moneda`
- **Efectivo:** `montoRecibido`, `vuelto`
- **Pasarela (Niubiz):** `referenciaPasarela`, `codigoAutorizacion`, `ultimos4Digitos`, `marcaTarjeta`
- **Billeteras (Yape/Plin):** `numeroOperacion`, `telefonoOrigen`
- `fechaPago`, `observaciones`

---

### `entity/pago/DetallePago.java` — Tabla: `detalle_pago`

**Propósito:** Para ventas con pago MIXTO. Ejemplo: una venta de S/150 pagada con S/100 en efectivo + S/50 en Yape. Cada método se registra como un `DetallePago`.

**Campos:** `venta`, `metodoPago`, `monto`, `referencia`

---

### `entity/pago/MetodoPago.java` — Tabla: `metodos_pago`

**Propósito:** Catálogo: EFECTIVO, TARJETA_CREDITO, TARJETA_DEBITO, YAPE, PLIN, TRANSFERENCIA, DEPOSITO.

**Campo:** `requierePasarela` (boolean) — Si es `true`, el pago debe pasar por Niubiz antes de confirmar.

---

### `entity/pago/EstadoPago.java` — Tabla: `estado_pago`

**Propósito:** Catálogo: PENDIENTE, APROBADO, RECHAZADO, ANULADO, REEMBOLSADO.

---

### `entity/comprobante/Comprobante.java` — Tabla: `comprobantes`

**Propósito:** Boletas y facturas electrónicas. Cumple con los requisitos de SUNAT.

**Campos:**
- `venta` (@OneToOne → Venta, una venta tiene un solo comprobante)
- `tipoComprobante` (@ManyToOne → TipoComprobante)
- `serie` (B001/F001), `correlativo` (00000001)
- `numeroCompleto` (B001-00000001, unique)
- **Datos del receptor:** `clienteTipoDoc`, `clienteNombreDoc`, `clienteNombre` — Snapshot, porque los datos del cliente pueden cambiar después
- `subtotal`, `descuento`, `igv`, `total`
- **SUNAT:** `estadoSunat` (PENDIENTE/ACEPTADO/RECHAZADO), `hashCdr`, `xmlContent`, `cdrContent`, `pdfUrl`, `qrData`

---

### `entity/comprobante/TipoComprobante.java` — Tabla: `tipo_comprobante`

**Propósito:** Catálogo SUNAT: 03=Boleta, 01=Factura, 07=Nota de Crédito, 08=Nota de Débito.

---

### `entity/auditoria/Auditoria.java` — Tabla: `auditoria`

**Propósito:** Log de todas las acciones importantes. Nunca se modifica ni elimina.

**Campos:**
- `usuario` (@ManyToOne, puede ser null si es un sistema automatizado)
- `accion` (CREATE, UPDATE, DELETE, LOGIN, LOGOUT, ANULAR_VENTA...)
- `modulo` (VENTAS, PRODUCTOS, USUARIOS...)
- `tablaAfectada`, `registroId`
- `datosAnteriores` (`@Column(columnDefinition = "jsonb")`) — Estado ANTES del cambio en JSON
- `datosNuevos` (`@Column(columnDefinition = "jsonb")`) — Estado DESPUÉS del cambio en JSON
- `ipAddress`, `userAgent`
- `exitoso` (boolean), `mensaje`

**Nota:** `id` es `BigSerial` (Long) porque esta tabla puede tener millones de registros.

---

## CAPA: Repository

> Todos los repositorios extienden `JpaRepository<Entidad, Long>` que ya provee: `save()`, `findById()`, `findAll()`, `delete()`, `count()` sin escribir código.

### `repository/UsuarioRepository.java`

```java
interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByDni(String dni);
}
```

---

### `repository/RolRepository.java`

```java
interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombre(String nombre);
}
```

---

### `repository/RefreshTokenRepository.java`

```java
interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUsuarioId(Long usuarioId);               // Logout total
    List<RefreshToken> findByUsuarioIdAndRevocadoFalse(Long usuarioId);
}
```

---

### `repository/ClienteRepository.java`

```java
interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByTipoDocumentoIdAndNumeroDocumento(Long tipoId, String numero);
    Page<Cliente> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(
        String nombre, String apellido, Pageable pageable);
    boolean existsByTipoDocumentoIdAndNumeroDocumento(Long tipoId, String numero);
}
```

---

### `repository/ProductoRepository.java`

```java
interface ProductoRepository extends JpaRepository<Producto, Long> {
    Optional<Producto> findByCodigoBarras(String barras);
    Optional<Producto> findByCodigo(String codigo);
    Page<Producto> findByCategoriaIdAndActivoTrue(Long categoriaId, Pageable pageable);
    Page<Producto> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre, Pageable pageable);
    boolean existsByCodigoBarras(String barras);

    // Consulta JPQL personalizada
    @Query("SELECT p FROM Producto p WHERE p.activo = true ORDER BY p.nombre ASC")
    List<Producto> findAllActivos();
}
```

---

### `repository/CategoriaRepository.java`

```java
interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    List<Categoria> findByCategoriaPadreIsNullAndActivoTrue(); // Raíces
    List<Categoria> findByCategoriaPadreId(Long padreId);      // Hijos
}
```

---

### `repository/InventarioRepository.java`

```java
interface InventarioRepository extends JpaRepository<Inventario, Long> {
    Optional<Inventario> findByProductoIdAndAlmacenId(Long productoId, Long almacenId);
    List<Inventario> findByProductoId(Long productoId);

    // Productos con stock bajo (para alertas)
    @Query("SELECT i FROM Inventario i WHERE i.stockActual <= i.stockMinimo")
    List<Inventario> findStockBajo();
}
```

---

### `repository/MovimientoStockRepository.java`

```java
interface MovimientoStockRepository extends JpaRepository<MovimientoStock, Long> {
    List<MovimientoStock> findByProductoIdOrderByCreatedAtDesc(Long productoId);
    List<MovimientoStock> findByReferenciaTypeAndReferenciaId(String tipo, Long id);
    Page<MovimientoStock> findByAlmacenId(Long almacenId, Pageable pageable);
}
```

---

### `repository/VentaRepository.java`

```java
interface VentaRepository extends JpaRepository<Venta, Long> {
    Optional<Venta> findByNumeroVenta(String numeroVenta);
    Page<Venta> findByUsuarioId(Long usuarioId, Pageable pageable);
    Page<Venta> findByClienteId(Long clienteId, Pageable pageable);
    Page<Venta> findByEstadoVentaId(Long estadoId, Pageable pageable);
    Page<Venta> findByFechaVentaBetween(LocalDateTime inicio, LocalDateTime fin, Pageable pageable);

    @Query("SELECT SUM(v.total) FROM Venta v WHERE DATE(v.fechaVenta) = CURRENT_DATE AND v.estadoVenta.codigo = 'PAGADO'")
    BigDecimal totalVentasHoy();
}
```

---

### `repository/DetalleVentaRepository.java`

```java
interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {
    List<DetalleVenta> findByVentaId(Long ventaId);

    // Productos más vendidos
    @Query("SELECT d.productoId, SUM(d.cantidad) as total FROM DetalleVenta d GROUP BY d.productoId ORDER BY total DESC")
    List<Object[]> findProductosMasVendidos(Pageable pageable);
}
```

---

### `repository/PagoRepository.java`

```java
interface PagoRepository extends JpaRepository<Pago, Long> {
    Optional<Pago> findByVentaId(Long ventaId);
    Optional<Pago> findByReferenciaPasarela(String referencia); // Para el webhook
    List<Pago> findByEstadoPagoId(Long estadoId);
}
```

---

### `repository/ComprobanteRepository.java`

```java
interface ComprobanteRepository extends JpaRepository<Comprobante, Long> {
    Optional<Comprobante> findByVentaId(Long ventaId);
    Optional<Comprobante> findByNumeroCompleto(String numero);
    Optional<Comprobante> findTopByTipoComprobanteIdOrderByCorrelativoDesc(Long tipoId); // Para generar el siguiente número
}
```

---

## CAPA: DTO

> Los DTOs son los objetos que "viajan" por HTTP. **Nunca se envía una Entity directamente** al cliente (expone la estructura interna y puede incluir datos sensibles como passwordHash).

### `dto/request/LoginRequest.java`

```
Campos:
  - email (String, @NotBlank, @Email)
  - password (String, @NotBlank, @Size(min=6))
```

---

### `dto/request/RegisterRequest.java`

```
Campos:
  - nombre, apellido (String, @NotBlank)
  - email (String, @NotBlank, @Email)
  - password (String, @NotBlank, @Size(min=8))
  - dni (String, opcional)
  - telefono (String, opcional)
```

---

### `dto/request/RefreshTokenRequest.java`

```
Campos:
  - refreshToken (String, @NotBlank)
```

---

### `dto/request/ClienteRequest.java`

```
Campos:
  - tipoDocumentoId (Long, @NotNull)
  - numeroDocumento (String, @NotBlank)
  - nombre (String, @NotBlank)
  - apellido, razonSocial (String, opcionales)
  - email, telefono, direccion, distrito, provincia, departamento
```

---

### `dto/request/ProductoRequest.java`

```
Campos:
  - codigo, codigoBarras (String, únicos, opcionales)
  - nombre (String, @NotBlank)
  - descripcion, imagenUrl (opcionales)
  - categoriaId, marcaId, unidadMedidaId (Long, @NotNull)
  - precioCosto, precioVenta (BigDecimal, @NotNull, @DecimalMin("0.0"))
  - precioMayoreo (BigDecimal, opcional)
  - igvIncluido, afectoIgv (boolean)
  - destacado (boolean)
```

---

### `dto/request/VentaRequest.java`

```
Campos:
  - clienteId (Long, opcional - venta puede ser anónima)
  - almacenId (Long, @NotNull)
  - moneda (String, default "PEN")
  - observaciones (String, opcional)
  - detalles (List<DetalleVentaRequest>, @NotEmpty - mínimo 1 producto)
```

---

### `dto/request/DetalleVentaRequest.java`

```
Campos:
  - productoId (Long, @NotNull)
  - cantidad (BigDecimal, @NotNull, @DecimalMin("0.001"))
  - descuentoPorcentaje (BigDecimal, @DecimalMin("0"), @DecimalMax("100"))
```

---

### `dto/request/PagoRequest.java`

```
Campos:
  - ventaId (Long, @NotNull)
  - metodoPagoId (Long, @NotNull)
  - monto (BigDecimal, @NotNull)
  - montoRecibido (BigDecimal, para efectivo)
  - referenciaExterna (String, para Yape/Plin/transferencia)
  - telefonoOrigen (String, para billeteras)
```

---

### `dto/response/AuthResponse.java`

```
Campos:
  - accessToken (String, el JWT de 15 minutos)
  - refreshToken (String, el token de 7 días)
  - tokenType ("Bearer")
  - expiresIn (long, segundos)
  - usuario:
      - id, uuid, nombre, apellido, email
      - roles (List<String>)
      - permisos (List<String>)
```

---

### `dto/response/ClienteResponse.java`

```
Campos: todos los de ClienteRequest + id, uuid, createdAt
Excluye: campos internos de JPA
```

---

### `dto/response/ProductoResponse.java`

```
Campos: todos los del producto +
  - nombreCategoria (String, no el objeto completo)
  - nombreMarca (String)
  - nombreUnidadMedida (String)
  - stockActual (BigDecimal, del inventario del almacén principal)
```

---

### `dto/response/VentaResponse.java`

```
Campos:
  - id, uuid, numeroVenta
  - cliente (ClienteResponse, puede ser null)
  - vendedor: { id, nombre, apellido }
  - estadoVenta: { codigo, descripcion }
  - subtotal, descuentoTotal, igv, total
  - moneda, fechaVenta
  - detalles (List<DetalleVentaResponse>)
  - pago (PagoResponse, puede ser null si está pendiente)
```

---

### `dto/response/DetalleVentaResponse.java`

```
Campos:
  - productoNombre, productoCodigo (del snapshot)
  - cantidad, precioUnitario, descuentoMonto
  - precioFinal, subtotal, igv, total
```

---

### `dto/response/PagoResponse.java`

```
Campos:
  - id, uuid, monto, moneda
  - metodoPago: { codigo, descripcion }
  - estadoPago: { codigo, descripcion }
  - codigoAutorizacion, ultimos4Digitos (para tarjetas)
  - numeroOperacion (para billeteras)
  - fechaPago
```

---

### `dto/response/PageResponse.java`

**Propósito:** Envuelve cualquier lista paginada de forma consistente.

```
Campos genéricos <T>:
  - content (List<T>)  ← Los registros de esta página
  - pageNumber (int)   ← Página actual (base 0)
  - pageSize (int)     ← Registros por página
  - totalElements (long) ← Total de registros en la BD
  - totalPages (int)   ← Total de páginas
  - last (boolean)     ← ¿Es la última página?
```

---

## CAPA: Service

> Cada método de servicio que modifica datos debe ser `@Transactional`. Si algo falla a mitad del proceso, se hace rollback automático. Por ejemplo: si al guardar el detalle de venta falla, también se revierte el guardado de la venta.

### `service/AuthService.java`

**Métodos:**

| Método | Lógica |
|---|---|
| `login(LoginRequest)` | Autentica email+password con Spring Security → genera accessToken + refreshToken → guarda RefreshToken en BD → retorna AuthResponse |
| `register(RegisterRequest)` | Valida email único → codifica password con BCrypt → asigna rol VENDEDOR por defecto → guarda Usuario → retorna AuthResponse |
| `refreshToken(RefreshTokenRequest)` | Busca RefreshToken en BD → valida que no esté revocado ni expirado → genera nuevo accessToken → retorna AuthResponse |
| `logout(String refreshToken)` | Busca RefreshToken → marca como `revocado = true` → guarda |
| `logoutAll(Long usuarioId)` | Elimina TODOS los RefreshTokens del usuario (cierra sesión en todos los dispositivos) |

---

### `service/UsuarioService.java`

**Métodos:** `listar(Pageable)`, `buscarPorId(Long)`, `crear(RegisterRequest)`, `actualizar(Long, RegisterRequest)`, `cambiarPassword(Long, String, String)`, `toggleActivo(Long)`, `asignarRol(Long, Long)`, `quitarRol(Long, Long)`

---

### `service/ClienteService.java`

**Métodos:** `listar(Pageable)`, `buscar(String, Pageable)`, `buscarPorDocumento(Long tipoId, String numero)`, `buscarPorId(Long)`, `crear(ClienteRequest)`, `actualizar(Long, ClienteRequest)`, `toggleActivo(Long)`

---

### `service/ProductoService.java`

**Métodos:** `listar(Pageable)`, `buscarPorNombre(String, Pageable)`, `buscarPorCategoria(Long, Pageable)`, `buscarPorBarras(String)`, `buscarPorId(Long)`, `crear(ProductoRequest)`, `actualizar(Long, ProductoRequest)`, `toggleActivo(Long)`

---

### `service/InventarioService.java`

**Métodos:**

| Método | Qué hace |
|---|---|
| `consultarStock(Long productoId)` | Stock en todos los almacenes |
| `consultarStock(Long productoId, Long almacenId)` | Stock en almacén específico |
| `alertasStockBajo()` | Lista productos por debajo del mínimo |
| `ajustarStock(Long productoId, Long almacenId, BigDecimal cantidad, String motivo)` | Ajuste manual con registro en movimiento_stock |
| `descontarStock(Long productoId, Long almacenId, BigDecimal cantidad, Long ventaId)` | Descuenta stock al confirmar pago. Lanza `BusinessException` si no hay suficiente stock |
| `reponerStock(Long productoId, Long almacenId, BigDecimal cantidad, Long compraId)` | Aumenta stock al registrar compra |

---

### `service/VentaService.java`

**El servicio más crítico del sistema. Orquesta todo el flujo de venta.**

**Métodos:**

| Método | Lógica detallada |
|---|---|
| `crearVenta(VentaRequest, Long usuarioId)` | 1. Valida que cada producto existe y está activo. 2. Verifica stock suficiente. 3. Calcula subtotales, IGV, total. 4. Genera `numeroVenta`. 5. Guarda `Venta` con estado PENDIENTE. 6. Guarda cada `DetalleVenta`. 7. Registra en auditoría. 8. Retorna `VentaResponse` |
| `confirmarPago(Long ventaId, PagoRequest)` | 1. Valida que la venta existe y está PENDIENTE. 2. Registra el `Pago`. 3. Descuenta stock por cada detalle. 4. Cambia estado a PAGADO. 5. Llama a `comprobanteService.emitir()`. 6. Registra auditoría |
| `anularVenta(Long ventaId, String motivo, Long usuarioId)` | 1. Valida que la venta existe y puede anularse. 2. Si tenía pago APROBADO: revierte stock. 3. Cambia estado a ANULADO. 4. Registra motivo y usuario. 5. Auditoría |
| `listar(Pageable, filtros)` | Lista con filtros opcionales: fecha, usuario, estado, cliente |
| `buscarPorId(Long)` | Busca con todos sus detalles y pago |
| `reporteVentasHoy()` | Total de ventas y monto del día |

---

### `service/PagoService.java`

**Métodos:**

| Método | Qué hace |
|---|---|
| `procesarPago(PagoRequest)` | Si método requiere pasarela: llama a Niubiz y espera confirmación. Si es efectivo/Yape: registra directamente como APROBADO |
| `procesarWebhook(String payload, String firma)` | Recibe notificación de Niubiz, valida la firma, busca el pago por referencia, actualiza estado, llama a `ventaService.confirmarPago()` |
| `buscarPorVenta(Long ventaId)` | Retorna el pago de una venta |

---

### `service/ComprobanteService.java`

**Métodos:**

| Método | Qué hace |
|---|---|
| `emitir(Venta)` | Determina tipo (Boleta si cliente es persona natural, Factura si tiene RUC). Genera el número correlativo. Crea el XML SUNAT. Llama al OSE (Nubefact). Guarda respuesta. Genera PDF URL |
| `reenviarSunat(Long comprobanteId)` | Reintenta envío a SUNAT si falló |
| `generarPdf(Long comprobanteId)` | Genera el PDF del comprobante |
| `buscarPorVenta(Long ventaId)` | Retorna el comprobante de una venta |

---

### `service/AuditoriaService.java`

**Propósito:** Registrar en la tabla `auditoria` cada acción importante del sistema. Se llama desde otros servicios.

**Método principal:**
```java
void registrar(
    Long usuarioId,
    String accion,         // "VENTA_CREADA", "PAGO_APROBADO"...
    String modulo,         // "VENTAS", "PRODUCTOS"...
    String tablaAfectada,  // "ventas"
    Long registroId,
    Object datosAnteriores, // Se convierte a JSON
    Object datosNuevos,     // Se convierte a JSON
    HttpServletRequest request // Para extraer IP y User-Agent
)
```

---

## CAPA: Controller

> Todos los controllers siguen el mismo patrón de respuesta usando `ApiResponse<T>`. Nunca se devuelve la entidad JPA cruda.

### `controller/AuthController.java` — `/api/auth`

| Endpoint | Método | Acceso | Qué hace |
|---|---|---|---|
| `/api/auth/login` | POST | Público | Llama a authService.login() |
| `/api/auth/register` | POST | Solo ADMIN | Crea nuevo usuario del sistema |
| `/api/auth/refresh` | POST | Público | Genera nuevo access token |
| `/api/auth/logout` | POST | Autenticado | Revoca el refresh token |

---

### `controller/UsuarioController.java` — `/api/usuarios`

| Endpoint | Método | Permiso | Qué hace |
|---|---|---|---|
| `/api/usuarios` | GET | USUARIO_VER | Lista paginada |
| `/api/usuarios/{id}` | GET | USUARIO_VER | Buscar por ID |
| `/api/usuarios/{id}` | PUT | USUARIO_EDITAR | Actualizar |
| `/api/usuarios/{id}/toggle` | PATCH | USUARIO_EDITAR | Activar/Desactivar |
| `/api/usuarios/{id}/roles/{rolId}` | POST | ADMIN | Asignar rol |

---

### `controller/ClienteController.java` — `/api/clientes`

| Endpoint | Método | Permiso | Qué hace |
|---|---|---|---|
| `/api/clientes` | GET | CLIENTE_VER | Lista paginada con búsqueda |
| `/api/clientes/{id}` | GET | CLIENTE_VER | Buscar por ID |
| `/api/clientes/documento` | GET | CLIENTE_VER | Buscar por tipo+número documento |
| `/api/clientes` | POST | CLIENTE_CREAR | Crear cliente |
| `/api/clientes/{id}` | PUT | CLIENTE_EDITAR | Actualizar |

---

### `controller/ProductoController.java` — `/api/productos`

| Endpoint | Método | Permiso | Qué hace |
|---|---|---|---|
| `/api/productos` | GET | PRODUCTO_VER | Lista paginada con filtros |
| `/api/productos/{id}` | GET | PRODUCTO_VER | Buscar por ID |
| `/api/productos/barras/{codigo}` | GET | PRODUCTO_VER | Buscar por código de barras |
| `/api/productos` | POST | PRODUCTO_CREAR | Crear producto |
| `/api/productos/{id}` | PUT | PRODUCTO_EDITAR | Actualizar |
| `/api/productos/{id}/toggle` | PATCH | PRODUCTO_EDITAR | Activar/Desactivar |

---

### `controller/InventarioController.java` — `/api/inventario`

| Endpoint | Método | Permiso | Qué hace |
|---|---|---|---|
| `/api/inventario/producto/{id}` | GET | INVENTARIO_VER | Stock del producto |
| `/api/inventario/alertas` | GET | INVENTARIO_VER | Productos con stock bajo |
| `/api/inventario/ajuste` | POST | INVENTARIO_AJUSTAR | Ajuste manual de stock |

---

### `controller/VentaController.java` — `/api/ventas`

| Endpoint | Método | Permiso | Qué hace |
|---|---|---|---|
| `/api/ventas` | GET | VENTA_VER | Lista con filtros y paginación |
| `/api/ventas/{id}` | GET | VENTA_VER | Detalle completo |
| `/api/ventas` | POST | VENTA_CREAR | Crear venta nueva |
| `/api/ventas/{id}/anular` | POST | VENTA_ANULAR | Anular venta |
| `/api/ventas/reporte/hoy` | GET | REPORTE_VER | Resumen de ventas del día |

---

### `controller/PagoController.java` — `/api/pagos`

| Endpoint | Método | Permiso | Qué hace |
|---|---|---|---|
| `/api/pagos` | POST | VENTA_CREAR | Procesar un pago |
| `/api/pagos/webhook` | POST | Público | Recibir notificación de pasarela (Niubiz) |
| `/api/pagos/venta/{ventaId}` | GET | VENTA_VER | Pago de una venta |

---

## CAPA: Exception

### `exception/GlobalExceptionHandler.java`

**Propósito:** Con `@RestControllerAdvice`, intercepta TODAS las excepciones que ocurran en cualquier controller o service y las convierte en respuestas JSON consistentes.

**Excepciones que maneja:**

| Excepción | HTTP | Cuándo ocurre |
|---|---|---|
| `ResourceNotFoundException` | 404 | No se encuentra un registro en BD |
| `BusinessException` | 400 | Violación de regla de negocio |
| `TokenException` | 401 | Token inválido o expirado |
| `MethodArgumentNotValidException` | 400 | Falla `@Valid` en un DTO |
| `AccessDeniedException` | 403 | No tiene permiso para la acción |
| `DataIntegrityViolationException` | 409 | Violación de constraint único en BD |
| `Exception` (genérica) | 500 | Cualquier error no controlado |

**Formato de respuesta de error:**
```json
{
  "status": 400,
  "error": "Solicitud inválida",
  "message": "El producto con id 5 no tiene stock suficiente",
  "timestamp": "2024-01-15T10:30:00",
  "path": "/api/ventas"
}
```

---

### `exception/ResourceNotFoundException.java`

```java
// Uso en servicio:
throw new ResourceNotFoundException("Producto", "id", productoId);
// Genera: "Producto no encontrado con id: 5"

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String recurso, String campo, Object valor) {
        super(recurso + " no encontrado con " + campo + ": " + valor);
    }
}
```

---

### `exception/BusinessException.java`

```java
// Uso en servicio:
throw new BusinessException("Stock insuficiente. Disponible: 3, solicitado: 10");

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
```

---

### `exception/TokenException.java`

```java
// Uso en JwtTokenProvider:
throw new TokenException("Token expirado");
throw new TokenException("Token inválido");

public class TokenException extends RuntimeException {
    public TokenException(String message) { super(message); }
}
```

---

## CAPA: Util

### `util/ApiResponse.java`

**Propósito:** Todos los endpoints devuelven el mismo formato JSON. Esto hace que el frontend pueda manejar las respuestas de forma genérica y predecible.

```java
// Éxito con datos:
ApiResponse.success(data)
// → { "success": true, "message": "OK", "data": {...} }

// Éxito con mensaje:
ApiResponse.success("Venta anulada correctamente", null)
// → { "success": true, "message": "Venta anulada correctamente", "data": null }

// Error:
ApiResponse.error("Email ya registrado")
// → { "success": false, "message": "Email ya registrado", "data": null }
```

**Campos de `ApiResponse<T>`:**
- `boolean success`
- `String message`
- `T data` (genérico)
- `LocalDateTime timestamp`

---

### `util/NumeroVentaGenerator.java`

**Propósito:** Genera números de venta correlativos únicos tipo `VEN-00000001`.

**Lógica:**
```
1. Consulta en BD la última venta: SELECT MAX(numero_venta) FROM ventas
2. Extrae el número: "VEN-00000042" → 42
3. Incrementa: 42 + 1 = 43
4. Formatea con ceros: String.format("VEN-%08d", 43) → "VEN-00000043"
5. Anotado con @Synchronized para evitar duplicados en concurrencia
```

---

## 6. Conexión entre capas (resumen visual)

```
LoginRequest (DTO)
    ↓ recibe
AuthController
    ↓ llama
AuthService
    ↓ usa                    ↓ usa
UsuarioRepository      JwtTokenProvider
    ↓ busca                  ↓ genera
Usuario (Entity)       accessToken + refreshToken
    ↓
    └──────────────────────────────────┐
                                       ↓
                               AuthResponse (DTO)
                                       ↓ devuelve
                               ResponseEntity<ApiResponse<AuthResponse>>
                                       ↓ al cliente
                               { "success": true, "data": { "accessToken": "..." } }
```

---

## 7. Reglas que NUNCA se deben romper

1. **Un controller nunca toca un repository directamente.** Siempre pasa por el service.
2. **Una entity nunca se devuelve directamente al cliente.** Siempre se convierte a DTO.
3. **Una contraseña nunca se guarda en texto plano.** Siempre BCrypt.
4. **El stock nunca se descuenta antes de confirmar el pago.** Solo después del webhook o confirmación.
5. **Todo cambio importante se registra en `auditoria`.** Especialmente anulaciones y ajustes de stock.
6. **Los servicios que modifican datos son `@Transactional`.** Para garantizar consistencia.
7. **Los DTOs tienen validaciones `@Valid`.** El controller nunca llega al service con datos inválidos.
