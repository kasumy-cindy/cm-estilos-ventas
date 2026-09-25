-- Consultas para ejecutar en pgAdmin.

SELECT * FROM categoria ORDER BY id_categoria;
SELECT * FROM proveedor ORDER BY id_proveedor;
SELECT * FROM metodo_pago ORDER BY id_metodo_pago;
SELECT p.*, c.nombre AS categoria
FROM producto p JOIN categoria c ON c.id_categoria = p.id_categoria
ORDER BY p.id_producto;

SELECT vp.id_variante, p.sku, p.nombre, vp.talla, vp.color,
       vp.stock_actual, vp.stock_critico,
       CASE WHEN vp.stock_actual <= vp.stock_critico THEN 'Stock Crítico' ELSE 'Disponible' END AS estado
FROM variante_producto vp
JOIN producto p ON p.id_producto = vp.id_producto
ORDER BY vp.id_variante;

-- RN03: el trigger debe producir impuesto 18% y total 118.00.
INSERT INTO venta (id_cliente, id_usuario, tipo_venta, subtotal)
VALUES (1, 2, 'Física', 100.00)
RETURNING id_venta, subtotal, impuesto, monto_total;

-- RN01/RN02: la siguiente línea debe descontar stock; si supera stock debe fallar.
INSERT INTO detalle_venta (id_venta, id_variante, cantidad, precio_unit)
VALUES (1, 1, 2, 45.00);

SELECT id_variante, stock_actual FROM variante_producto WHERE id_variante = 1;

-- Productos con stock crítico.
SELECT p.nombre, vp.talla, vp.color, vp.stock_actual, vp.stock_critico
FROM variante_producto vp
JOIN producto p ON p.id_producto = vp.id_producto
WHERE vp.stock_actual <= vp.stock_critico;

-- Reporte de ventas.
SELECT v.id_venta, v.fecha_hora, c.nombres AS cliente,
       v.tipo_venta, v.subtotal, v.impuesto, v.monto_total
FROM venta v JOIN cliente c ON c.id_cliente = v.id_cliente
ORDER BY v.fecha_hora DESC;
