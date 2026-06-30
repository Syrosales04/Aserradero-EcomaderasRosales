-- ============================================================================
--  Sistema Web para la Gestion de Rendimiento de Madera en un Aserradero
--  Script de base de datos MySQL
-- ============================================================================
--
--  IMPORTANTE:
--  La aplicacion esta configurada con  spring.jpa.hibernate.ddl-auto=update,
--  por lo que Spring Boot CREA AUTOMATICAMENTE la base de datos y todas las
--  tablas la primera vez que arranca. Tambien crea los roles y los usuarios
--  por defecto (admin/admin123 y encargado/encargado123).
--
--  Este script se incluye para fines academicos / de documentacion y para
--  quien prefiera crear el esquema manualmente. Puedes ejecutarlo en MySQL
--  Workbench o por consola:  mysql -u root -p < aserradero.sql
-- ============================================================================

CREATE DATABASE IF NOT EXISTS aserradero_db
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE aserradero_db;

-- ---------------------------------------------------------------------------
--  Roles y usuarios
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS roles (
  id      BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre  VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS usuarios (
  id        BIGINT AUTO_INCREMENT PRIMARY KEY,
  username  VARCHAR(60)  NOT NULL UNIQUE,
  password  VARCHAR(255) NOT NULL,
  nombre    VARCHAR(100) NOT NULL,
  estado    BOOLEAN      NOT NULL DEFAULT TRUE,
  id_rol    BIGINT       NOT NULL,
  CONSTRAINT fk_usuario_rol FOREIGN KEY (id_rol) REFERENCES roles(id)
);

-- ---------------------------------------------------------------------------
--  Proveedores y clientes
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS proveedores (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre          VARCHAR(100) NOT NULL,
  cedula_juridica VARCHAR(30),
  telefono        VARCHAR(30),
  correo          VARCHAR(100),
  direccion       VARCHAR(200),
  estado          BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS clientes (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre     VARCHAR(100) NOT NULL,
  cedula     VARCHAR(30),
  telefono   VARCHAR(30),
  correo     VARCHAR(100),
  direccion  VARCHAR(200),
  estado     BOOLEAN NOT NULL DEFAULT TRUE
);

-- ---------------------------------------------------------------------------
--  Ingreso de madera y rendimiento
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS ingresos_madera (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  id_proveedor  BIGINT NOT NULL,
  fecha_ingreso DATE   NOT NULL,
  placa_camion  VARCHAR(20),
  largo_camion  DOUBLE NOT NULL,
  ancho_camion  DOUBLE NOT NULL,
  alto_carga    DOUBLE NOT NULL,
  volumen_total DOUBLE,
  tipo_madera   VARCHAR(60),
  unidad_medida VARCHAR(30),
  precio_compra DOUBLE,
  precio_venta  DOUBLE,
  estado        VARCHAR(20) NOT NULL DEFAULT 'REGISTRADO',
  CONSTRAINT fk_ingreso_proveedor FOREIGN KEY (id_proveedor) REFERENCES proveedores(id)
);

CREATE TABLE IF NOT EXISTS rendimientos (
  id                       BIGINT AUTO_INCREMENT PRIMARY KEY,
  id_ingreso               BIGINT NOT NULL UNIQUE,
  volumen_total            DOUBLE,
  porcentaje_aprovechable  DOUBLE,
  volumen_aprovechable     DOUBLE,
  porcentaje_desperdicio   DOUBLE,
  volumen_desperdicio      DOUBLE,
  fecha_calculo            DATE,
  CONSTRAINT fk_rendimiento_ingreso FOREIGN KEY (id_ingreso) REFERENCES ingresos_madera(id)
);

-- ---------------------------------------------------------------------------
--  Inventario de productos de madera
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS productos_madera (
  id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
  tipo_madera         VARCHAR(60) NOT NULL,
  unidad_medida       VARCHAR(30),
  cantidad_total      DOUBLE NOT NULL DEFAULT 0,
  cantidad_disponible DOUBLE NOT NULL DEFAULT 0,
  precio_compra       DOUBLE,
  precio_venta        DOUBLE,
  fecha_registro      DATE,
  estado              VARCHAR(20) NOT NULL DEFAULT 'DISPONIBLE'
);

-- ---------------------------------------------------------------------------
--  Facturas de proveedor (compras)
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS facturas_proveedor (
  id             BIGINT AUTO_INCREMENT PRIMARY KEY,
  id_proveedor   BIGINT NOT NULL,
  id_ingreso     BIGINT,
  fecha_factura  DATE NOT NULL,
  numero_factura VARCHAR(40),
  subtotal       DOUBLE DEFAULT 0,
  total          DOUBLE DEFAULT 0,
  estado         VARCHAR(20) NOT NULL DEFAULT 'ACTIVA',
  CONSTRAINT fk_fp_proveedor FOREIGN KEY (id_proveedor) REFERENCES proveedores(id),
  CONSTRAINT fk_fp_ingreso   FOREIGN KEY (id_ingreso)   REFERENCES ingresos_madera(id)
);

CREATE TABLE IF NOT EXISTS detalle_factura_proveedor (
  id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
  id_factura_proveedor  BIGINT NOT NULL,
  tipo_madera           VARCHAR(60),
  cantidad              DOUBLE,
  precio_unitario       DOUBLE,
  subtotal              DOUBLE,
  CONSTRAINT fk_dfp_factura FOREIGN KEY (id_factura_proveedor) REFERENCES facturas_proveedor(id)
);

-- ---------------------------------------------------------------------------
--  Facturas de cliente (ventas)
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS facturas_cliente (
  id             BIGINT AUTO_INCREMENT PRIMARY KEY,
  id_cliente     BIGINT NOT NULL,
  fecha_factura  DATE NOT NULL,
  numero_factura VARCHAR(40),
  subtotal       DOUBLE DEFAULT 0,
  total          DOUBLE DEFAULT 0,
  estado         VARCHAR(20) NOT NULL DEFAULT 'ACTIVA',
  CONSTRAINT fk_fc_cliente FOREIGN KEY (id_cliente) REFERENCES clientes(id)
);

CREATE TABLE IF NOT EXISTS detalle_factura_cliente (
  id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
  id_factura_cliente  BIGINT NOT NULL,
  id_producto         BIGINT NOT NULL,
  cantidad            DOUBLE,
  precio_unitario     DOUBLE,
  subtotal            DOUBLE,
  CONSTRAINT fk_dfc_factura  FOREIGN KEY (id_factura_cliente) REFERENCES facturas_cliente(id),
  CONSTRAINT fk_dfc_producto FOREIGN KEY (id_producto)        REFERENCES productos_madera(id)
);

-- ============================================================================
--  DATOS DE EJEMPLO (opcionales)
-- ============================================================================
--  Nota: la aplicacion crea los roles y los usuarios por defecto al arrancar.
--  Si ejecutas este script ANTES del primer arranque, puedes crear los roles
--  aqui. Las contrasenas de los usuarios se guardan encriptadas (BCrypt) por
--  la aplicacion, por eso NO se insertan usuarios manualmente en este script.

INSERT IGNORE INTO roles (id, nombre) VALUES (1, 'ADMINISTRADOR'), (2, 'ENCARGADO');

INSERT IGNORE INTO proveedores (id, nombre, cedula_juridica, telefono, correo, direccion, estado) VALUES
  (1, 'Maderas del Norte S.A.', '3-101-123456', '2222-1111', 'ventas@maderasnorte.cr', 'Ciudad Quesada', TRUE),
  (2, 'Aserradero El Roble',     '3-101-654321', '2233-4455', 'contacto@elroble.cr',   'Perez Zeledon',  TRUE);

INSERT IGNORE INTO clientes (id, nombre, cedula, telefono, correo, direccion, estado) VALUES
  (1, 'Construcciones Vargas', '1-1111-2222', '8888-0000', 'compras@vargas.cr', 'San Jose', TRUE),
  (2, 'Muebleria La Madera',   '2-3333-4444', '8777-1212', 'info@lamadera.cr',  'Heredia',  TRUE);
