-- Ejecutar conectado a cm_estilos_ventas después del esquema.

INSERT INTO categoria (nombre) VALUES
('Blusas'), ('Vestidos'), ('Pantalones'), ('Accesorios');

-- Contraseña BCrypt de prueba para ambos usuarios: password
INSERT INTO usuario (username, password, rol) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Administrador'),
('cajero', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Cajero');

INSERT INTO cliente (tipo_documento, num_documento, nombres, correo, telefono) VALUES
('DNI', '70123456', 'Carlos Huayhua', 'carlos@example.com', '987654321'),
('DNI', '70234567', 'Cliente General', 'cliente@example.com', '986123456');

INSERT INTO metodo_pago (nombre, activo) VALUES
('Efectivo', TRUE), ('Yape/Plin', TRUE), ('Tarjeta', TRUE), ('Transferencia', TRUE);

INSERT INTO proveedor (ruc, razon_social, contacto, telefono, correo, direccion, activo) VALUES
('20123456789', 'Moda Andina S.A.C.', 'Rosa Quispe', '987111222', 'ventas@modaandina.com', 'Ayacucho', TRUE),
('20987654321', 'Textiles del Centro E.I.R.L.', 'Luis Huamán', '986333444', 'contacto@textilescentro.com', 'Huancayo', TRUE);

INSERT INTO producto (id_categoria, sku, nombre, precio_venta) VALUES
(1, 'BLU-001', 'Blusa manga larga', 45.00),
(2, 'VES-001', 'Vestido casual', 85.00),
(3, 'PAN-001', 'Pantalón jean', 95.00);

UPDATE producto SET imagen_url = '/images/blusa.svg' WHERE sku = 'BLU-001';
UPDATE producto SET imagen_url = '/images/vestido.svg' WHERE sku = 'VES-001';
UPDATE producto SET imagen_url = '/images/pantalon.svg' WHERE sku = 'PAN-001';

INSERT INTO variante_producto (id_producto, talla, color, stock_actual, stock_critico) VALUES
(1, 'M', 'Azul', 10, 3),
(1, 'L', 'Negro', 4, 2),
(2, 'M', 'Rojo', 8, 2),
(3, '32', 'Azul', 1, 2);
