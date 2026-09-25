-- Ejecutar conectado a cm_estilos_ventas como postgres.
GRANT CONNECT ON DATABASE cm_estilos_ventas TO usr_cmestilos;
GRANT USAGE ON SCHEMA public TO usr_cmestilos;
GRANT USAGE ON TYPE tipo_rol, tipo_venta TO usr_cmestilos;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO usr_cmestilos;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO usr_cmestilos;

ALTER DEFAULT PRIVILEGES IN SCHEMA public
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO usr_cmestilos;

ALTER DEFAULT PRIVILEGES IN SCHEMA public
GRANT USAGE, SELECT ON SEQUENCES TO usr_cmestilos;
