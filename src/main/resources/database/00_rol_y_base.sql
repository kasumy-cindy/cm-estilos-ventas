-- Ejecutar conectado a la base postgres, usando un usuario administrador.
-- Cambia la contraseña antes de usar este archivo en un entorno real.

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'usr_cmestilos') THEN
        CREATE ROLE usr_cmestilos LOGIN PASSWORD 'Cambiar_Esta_Clave_123!';
    END IF;
END $$;

-- Ejecutar esta sentencia por separado si la base todavía no existe:
CREATE DATABASE cm_estilos_ventas
    WITH OWNER = postgres
    ENCODING = 'UTF8'
    TEMPLATE = template0;
