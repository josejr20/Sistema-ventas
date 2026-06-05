# Revisión del Backend — Sistema de Ventas
> Resultado: **No compilará ni correrá correctamente** hasta corregir los bugs críticos.  
> La arquitectura y estructura de capas está muy bien diseñada. Son bugs puntuales, no de diseño.

---

## Resumen ejecutivo

| Severidad | Cantidad | Impacto |
|---|---|---|
| 🔴 No compila | 2 bugs | El proyecto no puede compilarse con `mvn package` |
| 🔴 Falla en runtime | 4 bugs | Compila pero lanza excepciones al ejecutar |
| 🟡 Comportamiento incorrecto | 4 bugs | Funciona pero hace cosas erróneas |
| 🟠 Menores / limpieza | 3 issues | No rompen nada pero deben limpiarse |

---

## 🔴 BUG 1 — MapStruct: campos de destino que no existen en los DTOs
**Archivos:** `mapper/ClienteMapper.java`, `mapper/ProductoMapper.java`, `mapper/VentaMapper.java`, `mapper/DetalleVentaMapper.java`  
**Efecto:** El proyecto no compilará. MapStruct genera error al no encontrar los campos target.

### Problema

Los 4 mappers apuntan a campos que no existen en los Response DTOs:

```java
// ClienteMapper.java — ❌ ClienteResponse NO tiene estos campos
@Mapping(source = "tipoDocumento.codigo",       target = "tipoDocumentoCodigo")
@Mapping(source = "tipoDocumento.descripcion",  target = "tipoDocumentoDescripcion")

// ProductoMapper.java — ❌ ProductoResponse NO tiene estos campos
@Mapping(source = "categoria.nombre",           target = "nombreCategoria")
@Mapping(source = "marca.nombre",               target = "nombreMarca")

// VentaMapper.java — ❌ VentaResponse NO tiene estos campos
@Mapping(source = "usuario.nombre",             target = "vendedorNombre")
@Mapping(source = "estadoVenta.codigo",         target = "estadoCodigo")

// DetalleVentaMapper.java — ❌ usa la relación lazy en vez del snapshot
@Mapping(source = "producto.nombre",            target = "productoNombre")
// DetalleVenta YA tiene productoNombre como campo snapshot. No hace falta.
```

Además, **ningún controller usa los mappers**. Los mappers están definidos pero nunca se inyectan en ningún controller.

### Fix — Opción A: corregir los mappers para que coincidan con los DTOs existentes

```java
// ClienteMapper.java — ✅
@Mapper(componentModel = "spring")
public interface ClienteMapper {
    // ClienteResponse tiene TipoDocumento tipoDocumento directamente → sin @Mapping necesario
    ClienteResponse toResponse(Cliente cliente);
    List<ClienteResponse> toResponseList(List<Cliente> clientes);
}

// ProductoMapper.java — ✅
@Mapper(componentModel = "spring")
public interface ProductoMapper {
    // ProductoResponse tiene Categoria, Marca, UnidadMedida directamente → sin @Mapping necesario
    // stockActual no viene de Producto, se debe setear manualmente después del mapeo
    @Mapping(target = "stockActual", ignore = true)
    ProductoResponse toResponse(Producto producto);
}

// VentaMapper.java — ✅
@Mapper(componentModel = "spring", uses = {DetalleVentaMapper.class, ClienteMapper.class})
public interface VentaMapper {
    @Mapping(source = "usuario.id",       target = "vendedor.id")
    @Mapping(source = "usuario.nombre",   target = "vendedor.nombre")
    @Mapping(source = "usuario.apellido", target = "vendedor.apellido")
    @Mapping(target = "pago",             ignore = true) // se carga aparte
    VentaResponse toResponse(Venta venta);
}

// DetalleVentaMapper.java — ✅
@Mapper(componentModel = "spring")
public interface DetalleVentaMapper {
    // productoNombre y productoCodigo ya están como campos directos en DetalleVenta
    // MapStruct los mapea automáticamente sin @Mapping
    DetalleVentaResponse toResponse(DetalleVenta detalle);
    List<DetalleVentaResponse> toResponseList(List<DetalleVenta> detalles);
}
```

### Fix — Opción B (más simple a corto plazo): añadir `@JsonIgnore` en back-references y retornar entidades

Ver Bug #6 para detalles de `@JsonIgnore`. Los mappers se pueden quitar si se toma esta ruta.

---

## 🔴 BUG 2 — `@NotBlank` en un campo `Long`
**Archivo:** `dto/request/ClienteRequest.java`, línea 10  
**Efecto:** No compilará con error de constraint inválido. `@NotBlank` solo aplica a `String/CharSequence`.

```java
// ❌ Antes
@NotBlank(message = "El ID del tipo de documento es obligatorio")
private Long tipoDocumentoId;

// ✅ Después
@NotNull(message = "El ID del tipo de documento es obligatorio")
private Long tipoDocumentoId;
```

---

## 🔴 BUG 3 — Contraseña guardada en texto plano
**Archivo:** `service/AuthService.java`, método `register()`  
**Efecto:** El registro guarda la contraseña sin cifrar. El login posterior siempre fallará porque Spring Security compara texto plano contra un hash BCrypt.

```java
// ❌ Antes — AuthService.java (además, PasswordEncoder no está inyectado)
.passwordHash(request.getPassword())

// ✅ Después — añadir PasswordEncoder al constructor de AuthService:
@RequiredArgsConstructor
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;  // ← AÑADIR

    // En register():
    .passwordHash(passwordEncoder.encode(request.getPassword()))  // ✅
```

---

## 🔴 BUG 4 — Entidades extienden `BaseEntity` pero las tablas no tienen las columnas
**Efecto:** Hibernate generará INSERT/SELECT con columnas que no existen en PostgreSQL → `ERROR: column "created_at" of relation "estado_venta" does not exist`.

### Tabla de inconsistencias

| Entidad Java | Tabla SQL | Columnas faltantes en SQL |
|---|---|---|
| `EstadoVenta extends BaseEntity` | `estado_venta` | `created_at`, `updated_at`, `activo` |
| `DetalleVenta extends BaseEntity` | `detalle_venta` | `created_at`, `updated_at` |
| `Inventario extends BaseEntity` | `inventario` | `created_at` (solo tiene `updated_at`) |
| `MovimientoStock extends BaseEntity` | `movimiento_stock` | `updated_at` (solo tiene `created_at`) |
| `Auditoria extends BaseEntity` | `auditoria` | `updated_at` (solo tiene `created_at`) |
| `TipoDocumento` (sin BaseEntity) | `tipo_documento` | `activo` |
| `TipoMovimientoStock` (sin BaseEntity) | `tipo_movimiento_stock` | `activo` |
| `TipoComprobante` (sin BaseEntity) | `tipo_comprobante` | `activo` |

### Fix — Opción A (recomendada): añadir las columnas al schema SQL

Ejecutar este SQL en PostgreSQL para completar el esquema:

```sql
-- Completar estado_venta
ALTER TABLE estado_venta
    ADD COLUMN IF NOT EXISTS activo     BOOLEAN   DEFAULT TRUE,
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT NOW(),
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT NOW();

-- Completar detalle_venta
ALTER TABLE detalle_venta
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT NOW(),
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT NOW();

-- Completar inventario (solo falta created_at)
ALTER TABLE inventario
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT NOW();

-- Completar movimiento_stock (solo falta updated_at)
ALTER TABLE movimiento_stock
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT NOW();

-- Completar auditoria (solo falta updated_at)
ALTER TABLE auditoria
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT NOW();

-- Completar tablas de catálogo
ALTER TABLE tipo_documento       ADD COLUMN IF NOT EXISTS activo BOOLEAN DEFAULT TRUE;
ALTER TABLE tipo_movimiento_stock ADD COLUMN IF NOT EXISTS activo BOOLEAN DEFAULT TRUE;
ALTER TABLE tipo_comprobante     ADD COLUMN IF NOT EXISTS activo BOOLEAN DEFAULT TRUE;
```

### Fix — Opción B: quitar la herencia de BaseEntity de las entidades con tablas incompletas

```java
// EstadoVenta.java — ✅ sin extends BaseEntity
@Entity
@Table(name = "estado_venta")
public class EstadoVenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false, length = 20)
    private String codigo;

    @Column(nullable = false)
    private String descripcion;
    // Sin activo, sin createdAt, sin updatedAt — igual que el schema SQL
}
```

Aplicar el mismo patrón a: `DetalleVenta`, `Inventario` (conservar solo `updatedAt`), `MovimientoStock` (conservar solo `createdAt`), `Auditoria` (conservar solo `createdAt`).

---

## 🔴 BUG 5 — `Comprobante.correlativo`: tipo mismatch Java vs SQL
**Archivo:** `entity/comprobante/Comprobante.java` y `SQL/schema_sistema_ventas.sql`  
**Efecto:** Hibernate no puede mapear `int` Java a `VARCHAR(10)` SQL → falla al insertar/leer comprobantes.

```java
// ❌ Antes — Comprobante.java
private int correlativo;

// ✅ Después — mantener int en Java y cambiar el SQL:
```
```sql
-- ❌ Antes en schema SQL
correlativo  VARCHAR(10)   NOT NULL,

-- ✅ Después
correlativo  INTEGER       NOT NULL,
```

Si la tabla ya existe en tu BD, ejecutar:
```sql
ALTER TABLE comprobantes ALTER COLUMN correlativo TYPE INTEGER USING correlativo::INTEGER;
```

---

## 🔴 BUG 6 — `LazyInitializationException` + referencias circulares en JSON
**Archivos:** Todos los controllers + entidades con relaciones bidireccionales  
**Efecto:** En cuanto cualquier endpoint devuelva datos, Jackson lanzará `LazyInitializationException` o `StackOverflowError`.

### Por qué ocurre

Con `spring.jpa.open-in-view: false` (correcto), la sesión Hibernate se cierra al terminar el método del service. Cuando Jackson intenta serializar la entidad retornada por el controller, encuentra asociaciones LAZY no cargadas → excepción.

Además, las relaciones bidireccionales crean bucles infinitos:
```
Venta.detalles → DetalleVenta.venta → Venta.detalles → ∞
Usuario.roles  → Rol.usuarios       → Usuario.roles  → ∞
Categoria      → Categoria.padre    → Categoria      → ∞
```

### Fix — Añadir `@JsonIgnore` en las colecciones de back-reference

Esta es la solución más rápida sin cambiar los controllers ni crear mappers complejos:

```java
// Rol.java
@JsonIgnore  // ← AÑADIR
@ManyToMany(mappedBy = "roles")
private Set<Usuario> usuarios = new HashSet<>();

// Permiso.java
@JsonIgnore  // ← AÑADIR
@ManyToMany(mappedBy = "permisos")
private Set<Rol> roles = new HashSet<>();

// DetalleVenta.java
@JsonIgnore  // ← AÑADIR
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "venta_id", nullable = false)
private Venta venta;

// Categoria.java
@JsonIgnore  // ← AÑADIR
@OneToMany(mappedBy = "categoriaPadre")
private Set<Categoria> subcategorias = new HashSet<>();

@JsonIgnore  // ← AÑADIR
@OneToMany(mappedBy = "categoria")
private Set<Producto> productos = new HashSet<>();

// EstadoVenta.java
@JsonIgnore  // ← AÑADIR
@OneToMany(mappedBy = "estadoVenta")
private Set<Venta> ventas = new HashSet<>();

// MetodoPago.java, EstadoPago.java — igual
@JsonIgnore
@OneToMany(mappedBy = "metodoPago")
private Set<Pago> pagos = new HashSet<>();

// TipoDocumento.java
@JsonIgnore
@OneToMany(mappedBy = "tipoDocumento")
private Set<Cliente> clientes = new HashSet<>();

// Marca.java, UnidadMedida.java — igual
@JsonIgnore
@OneToMany(mappedBy = "marca")
private Set<Producto> productos = new HashSet<>();

// TipoMovimientoStock.java
@JsonIgnore
@OneToMany(mappedBy = "tipoMovimiento")
private Set<MovimientoStock> movimientos = new HashSet<>();

// TipoComprobante.java
@JsonIgnore
@OneToMany(mappedBy = "tipoComprobante")
private Set<Comprobante> comprobantes = new HashSet<>();

// Almacen.java
@JsonIgnore
@OneToMany(mappedBy = "almacen")
private Set<Inventario> inventarios = new HashSet<>();
```

---

## 🟡 BUG 7 — `clienteRepository.findById(null)` lanza excepción
**Archivo:** `service/VentaService.java`, método `crearVenta()`  
**Efecto:** Si `clienteId` es null (venta anónima), Spring Data lanza `IllegalArgumentException: id must not be null`.

```java
// ❌ Antes
.cliente(clienteRepository.findById(request.getClienteId()).orElse(null))

// ✅ Después
.cliente(request.getClienteId() != null
    ? clienteRepository.findById(request.getClienteId()).orElse(null)
    : null)
```

---

## 🟡 BUG 8 — `usuarioId` hardcodeado como `1L` en los controllers
**Archivos:** `controller/VentaController.java`, `controller/InventarioController.java`  
**Efecto:** Todas las ventas y ajustes de inventario quedan registradas al usuario con ID 1, sin importar quién esté autenticado.

```java
// ❌ Antes
Venta venta = ventaService.crearVenta(request, 1L);

// ✅ Después — obtener el usuario autenticado del SecurityContext
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@PostMapping
public ResponseEntity<ApiResponse<Venta>> crear(@Valid @RequestBody VentaRequest request) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    Usuario usuario = (Usuario) auth.getPrincipal();
    Venta venta = ventaService.crearVenta(request, usuario.getId());
    return ResponseEntity.ok(ApiResponse.success(venta));
}
```

Aplicar el mismo patrón en `VentaController.anular()` e `InventarioController.ajustarStock()`.

---

## 🟡 BUG 9 — `fechaVenta` no se establece en `crearVenta()`
**Archivo:** `service/VentaService.java`  
**Efecto:** JPA envía NULL explícito en el INSERT para `fecha_venta`. PostgreSQL ignora el `DEFAULT NOW()` cuando se inserta NULL. La columna queda vacía.

```java
// ✅ Añadir al builder de Venta en crearVenta():
Venta venta = Venta.builder()
    .uuid(UUID.randomUUID())
    .numeroVenta(numeroVentaGenerator.generarNumeroVenta())
    .fechaVenta(LocalDateTime.now())  // ← AÑADIR ESTA LÍNEA
    // ...resto del builder
    .build();
```

---

## 🟡 BUG 10 — Cálculo de IGV incorrecto cuando `igvIncluido = true`
**Archivo:** `service/VentaService.java`, método `crearVenta()`  
**Efecto:** Si el precio ya incluye IGV, el sistema suma 18% encima del precio, cobrando de más al cliente.

Actualmente el código siempre hace: `total = subtotal + (subtotal × 18%)` independientemente de si el IGV ya estaba en el precio.

```java
// ✅ Lógica correcta
BigDecimal igvLinea = BigDecimal.ZERO;
BigDecimal subtotalSinIgv = subtotalLinea;

if (producto.isAfectoIgv()) {
    if (producto.isIgvIncluido()) {
        // Precio YA incluye IGV → extraer IGV (base imponible = precio × 100/118)
        igvLinea = subtotalLinea
            .multiply(BigDecimal.valueOf(18))
            .divide(BigDecimal.valueOf(118), 2, RoundingMode.HALF_UP);
        subtotalSinIgv = subtotalLinea.subtract(igvLinea);
    } else {
        // Precio NO incluye IGV → añadir IGV encima
        igvLinea = subtotalLinea.multiply(BigDecimal.valueOf(0.18))
                                .setScale(2, RoundingMode.HALF_UP);
    }
}

BigDecimal totalLinea = subtotalSinIgv.add(igvLinea);

DetalleVenta detalle = DetalleVenta.builder()
    .subtotal(subtotalSinIgv)
    .igv(igvLinea)
    .total(totalLinea)
    // ...
    .build();
```

---

## 🟠 ISSUE 11 — `JwtConfig.java` completamente vacío
**Archivo:** `config/JwtConfig.java`  
**Efecto:** Ninguno, pero es código muerto.  
**Fix:** Eliminar el archivo o llenarlo con las propiedades JWT.

---

## 🟠 ISSUE 12 — `PageResponse` duplicado en dos paquetes
Existe en:
- `dto/response/PageResponse.java` — sin el método `from()`. **Nunca se usa.**
- `util/PageResponse.java` — CON el método `from()`. Esta es la que usan los controllers.

**Fix:** Eliminar `dto/response/PageResponse.java` para evitar confusión.

---

## 🟠 ISSUE 13 — Flyway incluido pero deshabilitado con carpeta de migración vacía
`pom.xml` incluye Flyway, pero `flyway.enabled: false` y `db/migration/` está vacía.

**Fix — Opción A (recomendado):** Usar Flyway correctamente. Mover el SQL al formato de migración:

```
src/main/resources/db/migration/
└── V1__init_schema.sql    ← renombrar schema_sistema_ventas.sql a esto
```

Y en `application.yaml`:
```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
```

**Fix — Opción B:** Quitar Flyway del `pom.xml` si vas a gestionar el schema manualmente:
```xml
<!-- Eliminar estas dependencias de pom.xml -->
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-database-postgresql</artifactId>
</dependency>
```

---

## ✅ Lo que está muy bien — no tocar

| Componente | Estado | Por qué está bien |
|---|---|---|
| `pom.xml` | ✅ | Lombok antes que MapStruct en annotation processors (orden correcto) |
| `SecurityConfig.java` | ✅ | Estilo lambda DSL de Spring Security 6, sin WebSecurityConfigurerAdapter |
| `JwtTokenProvider.java` | ✅ | `@PostConstruct` correcto, JJWT 0.11.5 bien implementado |
| `JwtAuthenticationFilter.java` | ✅ | Extiende `OncePerRequestFilter` correctamente |
| `JwtAuthenticationEntryPoint.java` | ✅ | Devuelve JSON limpio en 401 |
| `UserDetailsServiceImpl.java` | ✅ | Carga usuario por email, bien integrado con Security |
| `GlobalExceptionHandler.java` | ✅ | Cubre todos los casos necesarios con responses consistentes |
| `application.yaml` | ✅ | Variables de entorno correctas, `open-in-view: false` correcto |
| `AuditConfig.java` | ✅ | `@EnableJpaAuditing` en clase separada, patrón correcto |
| `ApiResponse.java` | ✅ | Wrapper genérico limpio con los 4 métodos factory |
| `NumeroVentaGenerator.java` | ✅ | `synchronized` correcto para evitar duplicados concurrentes |
| Todos los Repositories | ✅ | Queries bien nombradas, JPQL correcto |
| `AuthService.login()` | ✅ | Usa `AuthenticationManager`, actualiza último login, genera tokens |
| Estructura de packages | ✅ | Separación por módulo de negocio, no por tipo técnico |

---

## Checklist de correcciones — en orden de prioridad

```
[ ] 1. @NotBlank → @NotNull en ClienteRequest.tipoDocumentoId
[ ] 2. Inyectar PasswordEncoder en AuthService y usarlo en register()
[ ] 3. Corregir los 4 Mappers (targets que no existen en los DTOs)
[ ] 4. Añadir @JsonIgnore a todas las colecciones back-reference (ver Bug 6)
[ ] 5. Resolver schema mismatch (Opción A recomendada: el SQL del ALTER TABLE)
[ ] 6. Cambiar correlativo de VARCHAR(10) a INTEGER en schema SQL
[ ] 7. Arreglar clienteId null en VentaService.crearVenta()
[ ] 8. Obtener usuarioId del SecurityContext en VentaController e InventarioController
[ ] 9. Añadir .fechaVenta(LocalDateTime.now()) en el builder de Venta
[10. Corregir cálculo IGV con igvIncluido=true
[11. Eliminar dto/response/PageResponse.java duplicado
[12. Eliminar JwtConfig.java vacío o rellenarlo
[13. Decidir: usar Flyway correctamente o quitar la dependencia
```

---

## Estimación de trabajo

Con los fixes 1-9 (los críticos), el backend **compilará y los endpoints básicos funcionarán** correctamente.

Los fixes 10-13 son mejoras que se pueden hacer antes de integrar el frontend para evitar deudas técnicas.

Una vez corregidos estos puntos, podemos pasar al frontend.
