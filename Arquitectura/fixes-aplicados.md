# Fixes Aplicados — Sistema de Ventas Backend

## Checklist completado

| # | Bug/Issue | Estado | Archivos modificados |
|---|-----------|--------|-------------------|
| 1 | `@NotBlank` → `@NotNull` en ClienteRequest.tipoDocumentoId | ✅ Corregido | `ClienteRequest.java` |
| 2 | PasswordEncoder no inyectado en AuthService | ✅ Corregido | `AuthService.java` |
| 3 | `@Builder.Default` en todas las entidades | ✅ Corregido | 20 entities |
| 4 | `@JsonIgnore` en back-references de relaciones | ✅ Corregido | `Rol.java`, `Permiso.java`, `DetalleVenta.java`, `Categoria.java`, `EstadoVenta.java`, `MetodoPago.java`, `EstadoPago.java`, `TipoDocumento.java`, `Marca.java`, `UnidadMedida.java`, `TipoMovimientoStock.java`, `TipoComprobante.java`, `Almacen.java` |
| 5 | Schema mismatch - columnas faltantes | ✅ SQL añadido | `schema_sistema_ventas.sql` |
| 6 | Correlativo INTEGER en comprobantes | ✅ SQL añadido | `schema_sistema_ventas.sql` |
| 7 | `clienteId null` en VentaService | ✅ Corregido | `VentaService.java` |
| 8 | UsuarioId del SecurityContext | ✅ Corregido | `VentaController.java`, `InventarioController.java` |
| 9 | `fechaVenta` en Venta | ✅ Corregido | `VentaService.java` |
| 10 | Cálculo IGV con igvIncluido=true | ✅ Corregido | `VentaService.java` |
| 11 | PageResponse duplicado | ✅ Eliminado | `dto/response/PageResponse.java` |
| 12 | JwtConfig.java vacío | ✅ Eliminado | `config/JwtConfig.java` |
| 13 | Flyway deshabilitado | ✅ Configurado con variables de entorno | `application.yaml` |

---

## Detalles de cambios críticos

### 1. PasswordEncoder en AuthService
```java
// Antes: password guardado en texto plano
.passwordHash(request.getPassword())

// Después: password encriptada con BCrypt
.passwordHash(passwordEncoder.encode(request.getPassword()))
```

### 2. Entities sin BaseEntity (tablas sin timestamps)
Las siguientes entidades fueron modificadas para NO extender `BaseEntity` ya que sus tablas SQL no tienen `created_at/updated_at`:

- `EstadoVenta` - solo tiene id, codigo, descripcion
- `EstadoPago` - solo tiene id, codigo, descripcion
- `TipoDocumento` - solo tiene id, codigo, descripcion + activo
- `TipoMovimientoStock` - solo tiene id, codigo, descripcion + activo
- `TipoComprobante` - solo tiene id, codigo, descripcion, serie_default + activo
- `DetalleVenta` - mantiene BaseEntity (ahora tiene columnas agregadas)
- `Inventario` - mantiene BaseEntity (ahora tiene created_at)
- `MovimientoStock` - mantiene BaseEntity (ahora tiene updated_at)
- `Auditoria` - mantiene BaseEntity (ahora tiene updated_at)

### 3. Query HQL corregida
```java
// Antes - falla en Hibernate 6
@Query("SELECT SUM(v.total) FROM Venta v WHERE DATE(v.fechaVenta) = CURRENT_DATE ...")

// Después - funciona con Hibernate 6
@Query("SELECT SUM(v.total) FROM Venta v WHERE CAST(v.fechaVenta AS LocalDate) = CURRENT_DATE ...")
```

### 4. Repositorio TipoComprobante corregido
Se removió método incorrecto `findTopByTipoComprobanteIdOrderByCorrelativoDesc` que pertenece a `ComprobanteRepository`.

### 5. Variables de entorno en application.yaml
```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:ventas_db}
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD}
jwt:
  secret: ${JWT_SECRET}
server:
  port: ${SERVER_PORT:8080}
```

---

## Backend listo para ejecución

Todos los bugs críticos mencionados en `revision-backend.md` han sido corregidos. El proyecto compila exitosamente.