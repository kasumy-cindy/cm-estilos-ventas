-- Ejecutar conectado a cm_estilos_ventas.

CREATE TYPE tipo_rol AS ENUM ('Administrador', 'Cajero');
CREATE TYPE tipo_venta AS ENUM ('Física', 'Online');

CREATE TABLE categoria (
    id_categoria INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE cliente (
    id_cliente INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tipo_documento VARCHAR(10) NOT NULL,
    num_documento VARCHAR(15) NOT NULL UNIQUE,
    nombres VARCHAR(100) NOT NULL,
    correo VARCHAR(100) UNIQUE,
    telefono VARCHAR(15),
    CONSTRAINT ck_cliente_documento CHECK (num_documento ~ '^[0-9A-Za-z-]{6,15}$'),
    CONSTRAINT ck_cliente_correo CHECK (correo IS NULL OR correo ~ '^[^@[:space:]]+@[^@[:space:]]+\.[^@[:space:]]+$')
);

CREATE TABLE usuario (
    id_usuario INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol tipo_rol NOT NULL
);

CREATE TABLE metodo_pago (
    id_metodo_pago INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre VARCHAR(40) NOT NULL UNIQUE,
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE proveedor (
    id_proveedor INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ruc VARCHAR(15) NOT NULL UNIQUE,
    razon_social VARCHAR(120) NOT NULL,
    contacto VARCHAR(100),
    telefono VARCHAR(15),
    correo VARCHAR(100),
    direccion VARCHAR(180),
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE producto (
    id_producto INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_categoria INT NOT NULL,
    sku VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    precio_venta NUMERIC(10,2) NOT NULL,
    imagen_url VARCHAR(500),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT ck_producto_precio CHECK (precio_venta > 0),
    CONSTRAINT fk_producto_categoria FOREIGN KEY (id_categoria)
        REFERENCES categoria(id_categoria)
        ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE variante_producto (
    id_variante INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_producto INT NOT NULL,
    talla VARCHAR(10) NOT NULL,
    color VARCHAR(30) NOT NULL,
    stock_actual INT NOT NULL DEFAULT 0,
    stock_critico INT NOT NULL DEFAULT 5,
    CONSTRAINT ck_variante_stock CHECK (stock_actual >= 0),
    CONSTRAINT ck_variante_stock_critico CHECK (stock_critico >= 0),
    CONSTRAINT uq_variante_producto_talla_color UNIQUE (id_producto, talla, color),
    CONSTRAINT fk_variante_producto FOREIGN KEY (id_producto)
        REFERENCES producto(id_producto)
        ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE venta (
    id_venta INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_cliente INT NOT NULL,
    id_usuario INT NULL,
    id_metodo_pago INT NULL,
    fecha_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tipo_venta tipo_venta NOT NULL,
    subtotal NUMERIC(10,2) NOT NULL,
    impuesto NUMERIC(10,2),
    monto_total NUMERIC(10,2),
    CONSTRAINT ck_venta_subtotal CHECK (subtotal > 0),
    CONSTRAINT fk_venta_cliente FOREIGN KEY (id_cliente)
        REFERENCES cliente(id_cliente)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_venta_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_venta_metodo_pago FOREIGN KEY (id_metodo_pago)
        REFERENCES metodo_pago(id_metodo_pago)
        ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE detalle_venta (
    id_detalle INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_venta INT NOT NULL,
    id_variante INT NOT NULL,
    cantidad INT NOT NULL,
    precio_unit NUMERIC(10,2) NOT NULL,
    CONSTRAINT ck_detalle_cantidad CHECK (cantidad > 0),
    CONSTRAINT ck_detalle_precio CHECK (precio_unit > 0),
    CONSTRAINT fk_dv_venta FOREIGN KEY (id_venta)
        REFERENCES venta(id_venta)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_dv_variante FOREIGN KEY (id_variante)
        REFERENCES variante_producto(id_variante)
        ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE pedido_proveedor (
    id_pedido INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_proveedor INT NOT NULL,
    fecha_solicitud TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    observacion VARCHAR(250),
    total NUMERIC(10,2) NOT NULL DEFAULT 0,
    CONSTRAINT ck_pedido_estado CHECK (estado IN ('PENDIENTE', 'RECIBIDO', 'CANCELADO')),
    CONSTRAINT ck_pedido_total CHECK (total >= 0),
    CONSTRAINT fk_pedido_proveedor FOREIGN KEY (id_proveedor)
        REFERENCES proveedor(id_proveedor)
        ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE detalle_pedido_proveedor (
    id_detalle_pedido INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_pedido INT NOT NULL,
    id_variante INT NOT NULL,
    cantidad INT NOT NULL,
    precio_unit NUMERIC(10,2) NOT NULL,
    CONSTRAINT ck_detalle_pedido_cantidad CHECK (cantidad > 0),
    CONSTRAINT ck_detalle_pedido_precio CHECK (precio_unit > 0),
    CONSTRAINT fk_detalle_pedido_pedido FOREIGN KEY (id_pedido)
        REFERENCES pedido_proveedor(id_pedido)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_detalle_pedido_variante FOREIGN KEY (id_variante)
        REFERENCES variante_producto(id_variante)
        ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE resena_producto (
    id_resena INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_producto INT NOT NULL,
    nombre_cliente VARCHAR(100) NOT NULL,
    correo VARCHAR(100),
    puntuacion INT NOT NULL,
    comentario VARCHAR(500) NOT NULL,
    fecha_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    aprobada BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT ck_resena_puntuacion CHECK (puntuacion BETWEEN 1 AND 5),
    CONSTRAINT fk_resena_producto FOREIGN KEY (id_producto)
        REFERENCES producto(id_producto)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE OR REPLACE FUNCTION fn_validar_stock_venta() RETURNS TRIGGER AS $$
DECLARE
    stock_disponible INT;
BEGIN
    SELECT stock_actual INTO stock_disponible
    FROM variante_producto
    WHERE id_variante = NEW.id_variante
    FOR UPDATE;

    IF stock_disponible IS NULL THEN
        RAISE EXCEPTION 'La variante seleccionada no existe';
    END IF;

    IF stock_disponible < NEW.cantidad THEN
        RAISE EXCEPTION 'Stock insuficiente para la variante seleccionada';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_validar_stock_venta
BEFORE INSERT ON detalle_venta
FOR EACH ROW EXECUTE FUNCTION fn_validar_stock_venta();

CREATE OR REPLACE FUNCTION fn_descontar_stock() RETURNS TRIGGER AS $$
BEGIN
    UPDATE variante_producto
    SET stock_actual = stock_actual - NEW.cantidad
    WHERE id_variante = NEW.id_variante;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_descontar_stock
AFTER INSERT ON detalle_venta
FOR EACH ROW EXECUTE FUNCTION fn_descontar_stock();

CREATE OR REPLACE FUNCTION fn_calcular_impuesto_venta() RETURNS TRIGGER AS $$
BEGIN
    NEW.impuesto := ROUND(NEW.subtotal * 0.18, 2);
    NEW.monto_total := ROUND(NEW.subtotal + NEW.impuesto, 2);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_calcular_impuesto_venta
BEFORE INSERT OR UPDATE OF subtotal ON venta
FOR EACH ROW EXECUTE FUNCTION fn_calcular_impuesto_venta();
