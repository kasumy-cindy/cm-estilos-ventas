-- Migración para una base cm_estilos_ventas ya existente.
-- Ejecutar conectado a cm_estilos_ventas antes de levantar la aplicación.

ALTER TABLE producto ADD COLUMN IF NOT EXISTS imagen_url VARCHAR(500);

CREATE TABLE IF NOT EXISTS resena_producto (
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
        REFERENCES producto(id_producto) ON DELETE CASCADE ON UPDATE CASCADE
);

UPDATE producto SET imagen_url = '/images/blusa.svg' WHERE sku = 'BLU-001';
UPDATE producto SET imagen_url = '/images/vestido.svg' WHERE sku = 'VES-001';
UPDATE producto SET imagen_url = '/images/pantalon.svg' WHERE sku = 'PAN-001';
