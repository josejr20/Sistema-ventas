-- ==========================================================
-- SISTEMA DE VENTAS EMPRESARIAL - ESQUEMA COMPLETO
-- Base de Datos: PostgreSQL
-- Backend:       Spring Boot 3 + Spring Security
-- ==========================================================

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE DATABASE ventas_db;

-- ==========================================================
-- MÓDULO 1: SEGURIDAD Y USUARIOS
-- ==========================================================

CREATE TABLE roles (
    id          SERIAL PRIMARY KEY,
    nombre      VARCHAR(50)  NOT NULL UNIQUE,  -- ADMIN, VENDEDOR, SUPERVISOR...
    descripcion VARCHAR(200),
    activo      BOOLEAN      DEFAULT TRUE,
    created_at  TIMESTAMP    DEFAULT NOW(),
    updated_at  TIMESTAMP    DEFAULT NOW()
);

CREATE TABLE permisos (
    id          SERIAL PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL UNIQUE,  -- VENTA_CREAR, PRODUCTO_EDITAR...
    descripcion VARCHAR(200),
    modulo      VARCHAR(50)  NOT NULL,         -- VENTAS, PRODUCTOS, USUARIOS, REPORTES
    activo      BOOLEAN      DEFAULT TRUE,
    created_at  TIMESTAMP    DEFAULT NOW()
);

CREATE TABLE rol_permiso (
    rol_id     INTEGER NOT NULL REFERENCES roles(id)    ON DELETE CASCADE,
    permiso_id INTEGER NOT NULL REFERENCES permisos(id) ON DELETE CASCADE,
    PRIMARY KEY (rol_id, permiso_id)
);

CREATE TABLE usuarios (
    id                SERIAL       PRIMARY KEY,
    uuid              UUID         DEFAULT uuid_generate_v4() UNIQUE NOT NULL,
    nombre            VARCHAR(100) NOT NULL,
    apellido          VARCHAR(100) NOT NULL,
    email             VARCHAR(150) NOT NULL UNIQUE,
    password_hash     VARCHAR(255) NOT NULL,   -- BCrypt via Spring Security
    dni               VARCHAR(15)  UNIQUE,
    telefono          VARCHAR(15),
    activo            BOOLEAN      DEFAULT TRUE,
    ultimo_login      TIMESTAMP,
    intentos_fallidos SMALLINT     DEFAULT 0,
    bloqueado         BOOLEAN      DEFAULT FALSE,
    created_at        TIMESTAMP    DEFAULT NOW(),
    updated_at        TIMESTAMP    DEFAULT NOW()
);

CREATE TABLE usuario_rol (
    usuario_id  INTEGER NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    rol_id      INTEGER NOT NULL REFERENCES roles(id)    ON DELETE CASCADE,
    asignado_por INTEGER REFERENCES usuarios(id),
    asignado_at TIMESTAMP DEFAULT NOW(),
    PRIMARY KEY (usuario_id, rol_id)
);

-- JWT Refresh Tokens
CREATE TABLE refresh_tokens (
    id          SERIAL    PRIMARY KEY,
    token       TEXT      NOT NULL UNIQUE,
    usuario_id  INTEGER   NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    expira_at   TIMESTAMP NOT NULL,
    revocado    BOOLEAN   DEFAULT FALSE,
    ip_address  VARCHAR(45),
    created_at  TIMESTAMP DEFAULT NOW()
);


-- ==========================================================
-- MÓDULO 2: CLIENTES
-- ==========================================================

CREATE TABLE tipo_documento (
    id          SERIAL      PRIMARY KEY,
    codigo      VARCHAR(10) NOT NULL UNIQUE,  -- DNI, RUC, CE, PASAPORTE
    descripcion VARCHAR(50) NOT NULL
);

CREATE TABLE clientes (
    id                 SERIAL       PRIMARY KEY,
    uuid               UUID         DEFAULT uuid_generate_v4() UNIQUE NOT NULL,
    tipo_documento_id  INTEGER      REFERENCES tipo_documento(id),
    numero_documento   VARCHAR(20),
    nombre             VARCHAR(100) NOT NULL,
    apellido           VARCHAR(100),
    razon_social       VARCHAR(200),           -- Para facturas empresariales (RUC)
    email              VARCHAR(150),
    telefono           VARCHAR(15),
    direccion          TEXT,
    distrito           VARCHAR(100),
    provincia          VARCHAR(100),
    departamento       VARCHAR(100),
    activo             BOOLEAN      DEFAULT TRUE,
    created_at         TIMESTAMP    DEFAULT NOW(),
    updated_at         TIMESTAMP    DEFAULT NOW(),
    CONSTRAINT uq_cliente_documento UNIQUE (tipo_documento_id, numero_documento)
);


-- ==========================================================
-- MÓDULO 3: PRODUCTOS Y CATÁLOGO
-- ==========================================================

-- Soporta subcategorías con auto-referencia
CREATE TABLE categorias (
    id                SERIAL       PRIMARY KEY,
    nombre            VARCHAR(100) NOT NULL,
    descripcion       VARCHAR(200),
    categoria_padre_id INTEGER     REFERENCES categorias(id),  -- Para subcategorías
    imagen_url        VARCHAR(500),
    activo            BOOLEAN      DEFAULT TRUE,
    orden             SMALLINT     DEFAULT 0,
    created_at        TIMESTAMP    DEFAULT NOW(),
    updated_at        TIMESTAMP    DEFAULT NOW()
);

CREATE TABLE marcas (
    id          SERIAL       PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(200),
    logo_url    VARCHAR(500),
    activo      BOOLEAN      DEFAULT TRUE,
    created_at  TIMESTAMP    DEFAULT NOW()
);

CREATE TABLE unidades_medida (
    id           SERIAL      PRIMARY KEY,
    codigo       VARCHAR(10) NOT NULL UNIQUE,  -- UND, KG, LT, MT, CJ
    descripcion  VARCHAR(50) NOT NULL,
    codigo_sunat VARCHAR(10)                   -- Código SUNAT para facturación electrónica
);

CREATE TABLE productos (
    id               SERIAL        PRIMARY KEY,
    uuid             UUID          DEFAULT uuid_generate_v4() UNIQUE NOT NULL,
    codigo           VARCHAR(50)   UNIQUE,       -- SKU interno
    codigo_barras    VARCHAR(100)  UNIQUE,
    nombre           VARCHAR(200)  NOT NULL,
    descripcion      TEXT,
    categoria_id     INTEGER       REFERENCES categorias(id),
    marca_id         INTEGER       REFERENCES marcas(id),
    unidad_medida_id INTEGER       REFERENCES unidades_medida(id),
    precio_costo     NUMERIC(12,2) NOT NULL DEFAULT 0,
    precio_venta     NUMERIC(12,2) NOT NULL,
    precio_mayoreo   NUMERIC(12,2),             -- Precio especial por volumen
    igv_incluido     BOOLEAN       DEFAULT TRUE,
    afecto_igv       BOOLEAN       DEFAULT TRUE, -- Algunos están exonerados
    imagen_url       VARCHAR(500),
    activo           BOOLEAN       DEFAULT TRUE,
    destacado        BOOLEAN       DEFAULT FALSE,
    created_by       INTEGER       REFERENCES usuarios(id),
    created_at       TIMESTAMP     DEFAULT NOW(),
    updated_at       TIMESTAMP     DEFAULT NOW()
);


-- ==========================================================
-- MÓDULO 4: INVENTARIO
-- ==========================================================

CREATE TABLE almacenes (
    id          SERIAL       PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,
    descripcion VARCHAR(200),
    direccion   TEXT,
    principal   BOOLEAN      DEFAULT FALSE,
    activo      BOOLEAN      DEFAULT TRUE,
    created_at  TIMESTAMP    DEFAULT NOW()
);

CREATE TABLE inventario (
    id           SERIAL        PRIMARY KEY,
    producto_id  INTEGER       NOT NULL REFERENCES productos(id),
    almacen_id   INTEGER       NOT NULL REFERENCES almacenes(id),
    stock_actual NUMERIC(12,3) NOT NULL DEFAULT 0,
    stock_minimo NUMERIC(12,3) DEFAULT 0,   -- Dispara alerta de reposición
    stock_maximo NUMERIC(12,3),
    ubicacion    VARCHAR(100),               -- Pasillo, estante, etc.
    updated_at   TIMESTAMP     DEFAULT NOW(),
    CONSTRAINT uq_inventario_prod_alm UNIQUE (producto_id, almacen_id)
);

CREATE TABLE tipo_movimiento_stock (
    id          SERIAL      PRIMARY KEY,
    codigo      VARCHAR(30) NOT NULL UNIQUE,
    descripcion VARCHAR(100) NOT NULL
);

CREATE TABLE movimiento_stock (
    id               BIGSERIAL     PRIMARY KEY,
    producto_id      INTEGER       NOT NULL REFERENCES productos(id),
    almacen_id       INTEGER       NOT NULL REFERENCES almacenes(id),
    tipo_movimiento_id INTEGER     NOT NULL REFERENCES tipo_movimiento_stock(id),
    cantidad         NUMERIC(12,3) NOT NULL,
    stock_anterior   NUMERIC(12,3) NOT NULL,
    stock_posterior  NUMERIC(12,3) NOT NULL,
    referencia_tipo  VARCHAR(30),   -- VENTA, COMPRA, AJUSTE
    referencia_id    INTEGER,       -- ID de la venta/compra que generó el movimiento
    motivo           TEXT,
    usuario_id       INTEGER        REFERENCES usuarios(id),
    created_at       TIMESTAMP      DEFAULT NOW()
);


-- ==========================================================
-- MÓDULO 5: VENTAS
-- ==========================================================

CREATE TABLE estado_venta (
    id          SERIAL      PRIMARY KEY,
    codigo      VARCHAR(20) NOT NULL UNIQUE,  -- PENDIENTE, PAGADO, ANULADO, DEVUELTO
    descripcion VARCHAR(100) NOT NULL
);

CREATE TABLE ventas (
    id               SERIAL        PRIMARY KEY,
    uuid             UUID          DEFAULT uuid_generate_v4() UNIQUE NOT NULL,
    numero_venta     VARCHAR(20)   NOT NULL UNIQUE,   -- VEN-00000001
    cliente_id       INTEGER       REFERENCES clientes(id),
    usuario_id       INTEGER       NOT NULL REFERENCES usuarios(id),  -- Vendedor
    almacen_id       INTEGER       REFERENCES almacenes(id),
    estado_venta_id  INTEGER       NOT NULL REFERENCES estado_venta(id),
    -- Montos
    subtotal         NUMERIC(12,2) NOT NULL DEFAULT 0,
    descuento_total  NUMERIC(12,2) DEFAULT 0,
    igv              NUMERIC(12,2) DEFAULT 0,          -- 18%
    total            NUMERIC(12,2) NOT NULL DEFAULT 0,
    moneda           VARCHAR(3)    DEFAULT 'PEN',       -- PEN, USD
    tipo_cambio      NUMERIC(8,4)  DEFAULT 1.0,
    observaciones    TEXT,
    -- Auditoría de anulación
    fecha_venta      TIMESTAMP     DEFAULT NOW(),
    fecha_anulacion  TIMESTAMP,
    anulado_por      INTEGER       REFERENCES usuarios(id),
    motivo_anulacion TEXT,
    created_at       TIMESTAMP     DEFAULT NOW(),
    updated_at       TIMESTAMP     DEFAULT NOW()
);

CREATE TABLE detalle_venta (
    id                   SERIAL        PRIMARY KEY,
    venta_id             INTEGER       NOT NULL REFERENCES ventas(id) ON DELETE CASCADE,
    producto_id          INTEGER       NOT NULL REFERENCES productos(id),
    cantidad             NUMERIC(12,3) NOT NULL,
    precio_unitario      NUMERIC(12,2) NOT NULL,
    descuento_porcentaje NUMERIC(5,2)  DEFAULT 0,
    descuento_monto      NUMERIC(12,2) DEFAULT 0,
    precio_final         NUMERIC(12,2) NOT NULL,  -- Precio después del descuento
    subtotal             NUMERIC(12,2) NOT NULL,  -- precio_final * cantidad
    igv                  NUMERIC(12,2) DEFAULT 0,
    total                NUMERIC(12,2) NOT NULL,
    -- Snapshot del producto al momento de vender (historial inmutable)
    producto_nombre      VARCHAR(200)  NOT NULL,
    producto_codigo      VARCHAR(50)
);


-- ==========================================================
-- MÓDULO 6: PAGOS
-- ==========================================================

CREATE TABLE metodos_pago (
    id                 SERIAL       PRIMARY KEY,
    codigo             VARCHAR(30)  NOT NULL UNIQUE,
    descripcion        VARCHAR(100) NOT NULL,
    requiere_pasarela  BOOLEAN      DEFAULT FALSE,  -- Niubiz, etc.
    activo             BOOLEAN      DEFAULT TRUE,
    created_at         TIMESTAMP    DEFAULT NOW()
);

CREATE TABLE estado_pago (
    id          SERIAL      PRIMARY KEY,
    codigo      VARCHAR(20) NOT NULL UNIQUE,  -- PENDIENTE, APROBADO, RECHAZADO, ANULADO
    descripcion VARCHAR(100) NOT NULL
);

CREATE TABLE pagos (
    id                    SERIAL        PRIMARY KEY,
    uuid                  UUID          DEFAULT uuid_generate_v4() UNIQUE NOT NULL,
    venta_id              INTEGER       NOT NULL REFERENCES ventas(id),
    metodo_pago_id        INTEGER       NOT NULL REFERENCES metodos_pago(id),
    estado_pago_id        INTEGER       NOT NULL REFERENCES estado_pago(id),
    monto                 NUMERIC(12,2) NOT NULL,
    moneda                VARCHAR(3)    DEFAULT 'PEN',
    -- Efectivo
    monto_recibido        NUMERIC(12,2),
    vuelto                NUMERIC(12,2),
    -- Pasarela (Niubiz / Mercado Pago)
    referencia_pasarela   VARCHAR(200),  -- ID de transacción
    codigo_autorizacion   VARCHAR(100),
    ultimos_4_digitos     VARCHAR(4),
    marca_tarjeta         VARCHAR(30),   -- VISA, MASTERCARD, AMEX
    -- Billeteras y transferencias (Yape, Plin, Transferencia)
    numero_operacion      VARCHAR(100),
    telefono_origen       VARCHAR(15),
    observaciones         TEXT,
    fecha_pago            TIMESTAMP     DEFAULT NOW(),
    created_at            TIMESTAMP     DEFAULT NOW(),
    updated_at            TIMESTAMP     DEFAULT NOW()
);

-- Para ventas con pago mixto: Ej. S/50 en efectivo + S/30 en tarjeta
CREATE TABLE detalle_pago (
    id             SERIAL        PRIMARY KEY,
    venta_id       INTEGER       NOT NULL REFERENCES ventas(id),
    metodo_pago_id INTEGER       NOT NULL REFERENCES metodos_pago(id),
    monto          NUMERIC(12,2) NOT NULL,
    referencia     VARCHAR(200),
    created_at     TIMESTAMP     DEFAULT NOW()
);


-- ==========================================================
-- MÓDULO 7: COMPROBANTES ELECTRÓNICOS (SUNAT)
-- ==========================================================

CREATE TABLE tipo_comprobante (
    id           SERIAL      PRIMARY KEY,
    codigo       VARCHAR(5)  NOT NULL UNIQUE,  -- 01=FACTURA, 03=BOLETA, 07=NOTA_CREDITO
    descripcion  VARCHAR(50) NOT NULL,
    serie_default VARCHAR(5)                   -- B001 para boleta, F001 para factura
);

CREATE TABLE comprobantes (
    id                   SERIAL        PRIMARY KEY,
    venta_id             INTEGER       NOT NULL REFERENCES ventas(id) UNIQUE,
    tipo_comprobante_id  INTEGER       NOT NULL REFERENCES tipo_comprobante(id),
    serie                VARCHAR(5)    NOT NULL,     -- B001 / F001
    correlativo          VARCHAR(10)   NOT NULL,     -- 00000001
    numero_completo      VARCHAR(20)   NOT NULL UNIQUE, -- B001-00000001
    -- Datos del receptor al momento de emisión (snapshot)
    cliente_tipo_doc     VARCHAR(10),
    cliente_numero_doc   VARCHAR(20),
    cliente_nombre       VARCHAR(200),
    cliente_direccion    TEXT,
    -- Totales
    subtotal             NUMERIC(12,2) NOT NULL,
    descuento            NUMERIC(12,2) DEFAULT 0,
    igv                  NUMERIC(12,2) NOT NULL,
    total                NUMERIC(12,2) NOT NULL,
    -- Integración SUNAT / OSE (Nubefact, etc.)
    estado_sunat         VARCHAR(30)   DEFAULT 'PENDIENTE', -- PENDIENTE, ACEPTADO, RECHAZADO
    hash_cdr             VARCHAR(500),
    xml_content          TEXT,          -- XML enviado a SUNAT
    cdr_content          TEXT,          -- CDR de respuesta SUNAT
    pdf_url              VARCHAR(500),
    qr_data              TEXT,
    fecha_emision        TIMESTAMP     DEFAULT NOW(),
    fecha_envio_sunat    TIMESTAMP,
    created_at           TIMESTAMP     DEFAULT NOW(),
    updated_at           TIMESTAMP     DEFAULT NOW()
);


-- ==========================================================
-- MÓDULO 8: AUDITORÍA
-- ==========================================================

CREATE TABLE auditoria (
    id               BIGSERIAL    PRIMARY KEY,
    usuario_id       INTEGER      REFERENCES usuarios(id),
    accion           VARCHAR(50)  NOT NULL,   -- CREATE, UPDATE, DELETE, LOGIN, ANULAR_VENTA
    modulo           VARCHAR(50)  NOT NULL,   -- VENTAS, PRODUCTOS, USUARIOS, PAGOS
    tabla_afectada   VARCHAR(100),
    registro_id      VARCHAR(50),
    datos_anteriores JSONB,                   -- Estado previo al cambio
    datos_nuevos     JSONB,                   -- Estado posterior al cambio
    ip_address       VARCHAR(45),
    user_agent       TEXT,
    exitoso          BOOLEAN      DEFAULT TRUE,
    mensaje          TEXT,
    created_at       TIMESTAMP    DEFAULT NOW()
);


-- ==========================================================
-- FUNCIONES Y TRIGGERS
-- ==========================================================

-- Trigger para actualizar updated_at automáticamente
CREATE OR REPLACE FUNCTION fn_update_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_usuarios_updated_at
    BEFORE UPDATE ON usuarios FOR EACH ROW EXECUTE FUNCTION fn_update_updated_at();

CREATE TRIGGER trg_clientes_updated_at
    BEFORE UPDATE ON clientes FOR EACH ROW EXECUTE FUNCTION fn_update_updated_at();

CREATE TRIGGER trg_productos_updated_at
    BEFORE UPDATE ON productos FOR EACH ROW EXECUTE FUNCTION fn_update_updated_at();

CREATE TRIGGER trg_ventas_updated_at
    BEFORE UPDATE ON ventas FOR EACH ROW EXECUTE FUNCTION fn_update_updated_at();

CREATE TRIGGER trg_pagos_updated_at
    BEFORE UPDATE ON pagos FOR EACH ROW EXECUTE FUNCTION fn_update_updated_at();

CREATE TRIGGER trg_comprobantes_updated_at
    BEFORE UPDATE ON comprobantes FOR EACH ROW EXECUTE FUNCTION fn_update_updated_at();


-- Trigger para actualizar inventario.updated_at
CREATE OR REPLACE FUNCTION fn_update_inventario_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_inventario_updated_at
    BEFORE UPDATE ON inventario FOR EACH ROW EXECUTE FUNCTION fn_update_inventario_timestamp();


-- ==========================================================
-- ÍNDICES PARA PERFORMANCE
-- ==========================================================

-- Usuarios
CREATE INDEX idx_usuarios_email    ON usuarios(email);
CREATE INDEX idx_usuarios_activo   ON usuarios(activo);
CREATE INDEX idx_refresh_token_uid ON refresh_tokens(usuario_id);
CREATE INDEX idx_refresh_revocado  ON refresh_tokens(revocado) WHERE revocado = FALSE;

-- Productos (incluye full-text search en español)
CREATE INDEX idx_productos_categoria  ON productos(categoria_id);
CREATE INDEX idx_productos_barras     ON productos(codigo_barras);
CREATE INDEX idx_productos_activo     ON productos(activo);
CREATE INDEX idx_productos_fts        ON productos USING GIN(to_tsvector('spanish', nombre));

-- Inventario
CREATE INDEX idx_inventario_producto  ON inventario(producto_id);
CREATE INDEX idx_inventario_stock_bajo ON inventario(stock_actual)
    WHERE stock_actual <= stock_minimo;   -- Índice parcial para alertas

-- Movimiento de stock
CREATE INDEX idx_mov_stock_producto   ON movimiento_stock(producto_id);
CREATE INDEX idx_mov_stock_fecha      ON movimiento_stock(created_at);
CREATE INDEX idx_mov_stock_referencia ON movimiento_stock(referencia_tipo, referencia_id);

-- Ventas
CREATE INDEX idx_ventas_fecha         ON ventas(fecha_venta);
CREATE INDEX idx_ventas_cliente       ON ventas(cliente_id);
CREATE INDEX idx_ventas_usuario       ON ventas(usuario_id);
CREATE INDEX idx_ventas_estado        ON ventas(estado_venta_id);
CREATE INDEX idx_ventas_numero        ON ventas(numero_venta);

-- Detalle de venta
CREATE INDEX idx_detalle_venta_venta    ON detalle_venta(venta_id);
CREATE INDEX idx_detalle_venta_producto ON detalle_venta(producto_id);

-- Pagos
CREATE INDEX idx_pagos_venta   ON pagos(venta_id);
CREATE INDEX idx_pagos_estado  ON pagos(estado_pago_id);
CREATE INDEX idx_pagos_fecha   ON pagos(fecha_pago);

-- Comprobantes
CREATE INDEX idx_comprobantes_numero ON comprobantes(numero_completo);
CREATE INDEX idx_comprobantes_sunat  ON comprobantes(estado_sunat);

-- Auditoría
CREATE INDEX idx_auditoria_usuario ON auditoria(usuario_id);
CREATE INDEX idx_auditoria_modulo  ON auditoria(modulo);
CREATE INDEX idx_auditoria_fecha   ON auditoria(created_at DESC);


-- ==========================================================
-- VISTAS ÚTILES
-- ==========================================================

-- Productos con stock bajo
CREATE VIEW v_stock_bajo AS
SELECT
    p.id, p.codigo, p.nombre,
    i.stock_actual, i.stock_minimo,
    (i.stock_minimo - i.stock_actual) AS faltante,
    a.nombre AS almacen
FROM inventario i
JOIN productos p ON p.id = i.producto_id
JOIN almacenes  a ON a.id = i.almacen_id
WHERE i.stock_actual <= i.stock_minimo
  AND p.activo = TRUE
ORDER BY faltante DESC;

-- Resumen de ventas del día
CREATE VIEW v_ventas_hoy AS
SELECT
    v.id,
    v.numero_venta,
    CONCAT(u.nombre, ' ', u.apellido)  AS vendedor,
    COALESCE(c.razon_social, CONCAT(c.nombre, ' ', c.apellido), 'Sin cliente') AS cliente,
    v.total,
    v.moneda,
    ev.descripcion  AS estado,
    v.fecha_venta
FROM ventas v
JOIN usuarios    u  ON u.id  = v.usuario_id
LEFT JOIN clientes c ON c.id = v.cliente_id
JOIN estado_venta ev ON ev.id = v.estado_venta_id
WHERE DATE(v.fecha_venta) = CURRENT_DATE
ORDER BY v.fecha_venta DESC;

-- Reporte de ventas por método de pago
CREATE VIEW v_ventas_por_metodo_pago AS
SELECT
    mp.descripcion AS metodo_pago,
    COUNT(p.id)        AS total_transacciones,
    SUM(p.monto)       AS monto_total
FROM pagos p
JOIN metodos_pago mp ON mp.id = p.metodo_pago_id
JOIN estado_pago  ep ON ep.id = p.estado_pago_id
WHERE ep.codigo = 'APROBADO'
GROUP BY mp.descripcion
ORDER BY monto_total DESC;


-- ==========================================================
-- DATOS INICIALES (SEEDS)
-- ==========================================================

-- Roles
INSERT INTO roles (nombre, descripcion) VALUES
('ADMIN',       'Administrador con acceso total'),
('SUPERVISOR',  'Supervisor de ventas y reportes'),
('VENDEDOR',    'Realiza ventas y consulta productos'),
('ALMACENERO',  'Gestiona inventario y stock'),
('CONTADOR',    'Acceso a reportes y comprobantes');

-- Permisos
INSERT INTO permisos (nombre, descripcion, modulo) VALUES
('VENTA_VER',           'Ver listado de ventas',          'VENTAS'),
('VENTA_CREAR',         'Crear nuevas ventas',            'VENTAS'),
('VENTA_ANULAR',        'Anular ventas',                  'VENTAS'),
('PRODUCTO_VER',        'Ver productos',                  'PRODUCTOS'),
('PRODUCTO_CREAR',      'Crear productos',                'PRODUCTOS'),
('PRODUCTO_EDITAR',     'Editar productos',               'PRODUCTOS'),
('PRODUCTO_ELIMINAR',   'Eliminar productos',             'PRODUCTOS'),
('INVENTARIO_VER',      'Ver inventario',                 'INVENTARIO'),
('INVENTARIO_AJUSTAR',  'Ajustar stock manualmente',      'INVENTARIO'),
('USUARIO_VER',         'Ver usuarios',                   'USUARIOS'),
('USUARIO_CREAR',       'Crear usuarios',                 'USUARIOS'),
('USUARIO_EDITAR',      'Editar usuarios',                'USUARIOS'),
('REPORTE_VER',         'Ver reportes del sistema',       'REPORTES'),
('CLIENTE_VER',         'Ver clientes',                   'CLIENTES'),
('CLIENTE_CREAR',       'Crear clientes',                 'CLIENTES'),
('CLIENTE_EDITAR',      'Editar clientes',                'CLIENTES');

-- Tipos de documento
INSERT INTO tipo_documento (codigo, descripcion) VALUES
('DNI',       'Documento Nacional de Identidad'),
('RUC',       'Registro Único de Contribuyentes'),
('CE',        'Carné de Extranjería'),
('PASAPORTE', 'Pasaporte');

-- Unidades de medida (con códigos SUNAT)
INSERT INTO unidades_medida (codigo, descripcion, codigo_sunat) VALUES
('UND', 'Unidad',    'NIU'),
('KG',  'Kilogramo', 'KGM'),
('LT',  'Litro',     'LTR'),
('MT',  'Metro',     'MTR'),
('CJ',  'Caja',      'BX'),
('PQ',  'Paquete',   'PK'),
('DOC', 'Docena',    'DZN'),
('PAR', 'Par',       'PR');

-- Estados de venta
INSERT INTO estado_venta (codigo, descripcion) VALUES
('PENDIENTE', 'Pendiente de pago'),
('PAGADO',    'Pagado y confirmado'),
('ANULADO',   'Anulado'),
('DEVUELTO',  'Devuelto');

-- Estados de pago
INSERT INTO estado_pago (codigo, descripcion) VALUES
('PENDIENTE',   'Pendiente de procesamiento'),
('APROBADO',    'Pago aprobado'),
('RECHAZADO',   'Rechazado por la pasarela'),
('ANULADO',     'Pago anulado'),
('REEMBOLSADO', 'Pago reembolsado al cliente');

-- Métodos de pago
INSERT INTO metodos_pago (codigo, descripcion, requiere_pasarela) VALUES
('EFECTIVO',        'Efectivo',                  FALSE),
('TARJETA_CREDITO', 'Tarjeta de Crédito',        TRUE),
('TARJETA_DEBITO',  'Tarjeta de Débito',         TRUE),
('YAPE',            'Yape',                      FALSE),
('PLIN',            'Plin',                      FALSE),
('TRANSFERENCIA',   'Transferencia Bancaria',     FALSE),
('DEPOSITO',        'Depósito Bancario',          FALSE);

-- Tipos de comprobante SUNAT
INSERT INTO tipo_comprobante (codigo, descripcion, serie_default) VALUES
('03', 'Boleta de Venta', 'B001'),
('01', 'Factura',         'F001'),
('07', 'Nota de Crédito', 'NC01'),
('08', 'Nota de Débito',  'ND01');

-- Tipos de movimiento de stock
INSERT INTO tipo_movimiento_stock (codigo, descripcion) VALUES
('ENTRADA_COMPRA',      'Entrada por orden de compra'),
('SALIDA_VENTA',        'Salida por venta registrada'),
('AJUSTE_POSITIVO',     'Ajuste positivo de inventario'),
('AJUSTE_NEGATIVO',     'Ajuste negativo de inventario'),
('DEVOLUCION_CLIENTE',  'Devolución de cliente'),
('DEVOLUCION_PROVEEDOR','Devolución a proveedor'),
('TRASLADO_ENTRADA',    'Traslado entre almacenes - entrada'),
('TRASLADO_SALIDA',     'Traslado entre almacenes - salida');

-- Almacén inicial
INSERT INTO almacenes (nombre, descripcion, principal) VALUES
('Almacén Principal', 'Almacén central de la empresa', TRUE);

-- ==========================================================
-- COMPLEMENTOS DE COLUMNAS FALTANTES (compatibilidad con entidades Java)
-- ==========================================================

-- Completar estado_venta (faltan activo, created_at, updated_at)
ALTER TABLE estado_venta
    ADD COLUMN IF NOT EXISTS activo     BOOLEAN   DEFAULT TRUE,
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT NOW(),
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT NOW();

-- Completar detalle_venta (faltan created_at, updated_at)
ALTER TABLE detalle_venta
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT NOW(),
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT NOW();

-- Completar inventario (falta created_at)
ALTER TABLE inventario
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT NOW();

-- Completar movimiento_stock (falta updated_at)
ALTER TABLE movimiento_stock
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT NOW();

-- Completar auditoria (falta updated_at)
ALTER TABLE auditoria
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT NOW();

-- Completar tablas de catálogo
ALTER TABLE tipo_documento       ADD COLUMN IF NOT EXISTS activo BOOLEAN DEFAULT TRUE;
ALTER TABLE tipo_movimiento_stock ADD COLUMN IF NOT EXISTS activo BOOLEAN DEFAULT TRUE;
ALTER TABLE tipo_comprobante     ADD COLUMN IF NOT EXISTS activo BOOLEAN DEFAULT TRUE;

-- Corregir correlativo a INTEGER en comprobantes
ALTER TABLE comprobantes
    ALTER COLUMN correlativo TYPE INTEGER USING correlativo::INTEGER;

-- Trigger para auditoria updated_at
CREATE OR REPLACE FUNCTION fn_update_auditoria_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_auditoria_updated_at
    BEFORE UPDATE ON auditoria FOR EACH ROW EXECUTE FUNCTION fn_update_auditoria_timestamp();

-- Trigger para movimiento_stock updated_at
CREATE TRIGGER trg_movimiento_stock_updated_at
    BEFORE UPDATE ON movimiento_stock FOR EACH ROW EXECUTE FUNCTION fn_update_inventario_timestamp();

-- Trigger para detalle_venta updated_at
CREATE TRIGGER trg_detalle_venta_updated_at
    BEFORE UPDATE ON detalle_venta FOR EACH ROW EXECUTE FUNCTION fn_update_updated_at();

-- Trigger para estado_venta updated_at
CREATE TRIGGER trg_estado_venta_updated_at
    BEFORE UPDATE ON estado_venta FOR EACH ROW EXECUTE FUNCTION fn_update_updated_at();

-- ==========================================================
-- FIN DEL ESQUEMA
-- 26 tablas | 3 vistas | 6 triggers | índices optimizados
-- ==========================================================

-- ==========================================================
-- VERIFICACIÓN POST-CORRECCIONES
-- ==========================================================
-- Ejecutar este query para confirmar que todas las columnas existen:
SELECT table_name, column_name
FROM information_schema.columns
WHERE table_schema = 'public'
AND table_name IN ('estado_venta', 'detalle_venta', 'inventario', 'movimiento_stock', 'auditoria')
ORDER BY table_name, column_name;
