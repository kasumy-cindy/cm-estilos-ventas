# C&M Estilos - Spring Boot + PostgreSQL

Proyecto completo para el sistema de ventas e inventario de la tienda de ropa C&M Estilos. Está escrito sin Lombok: las entidades y DTO contienen constructores, getters y setters explícitos. Incluye backend REST, vistas HTML, CSS, JavaScript y PostgreSQL.

## Requisitos

- Java 21
- Maven 3.9 o superior
- PostgreSQL 16 o superior
- Spring Tools Suite (STS) o cualquier IDE compatible con Maven

## Importar en STS

1. Extrae el ZIP.
2. En STS selecciona **File > Import > Maven > Existing Maven Projects**.
3. Selecciona la carpeta `cm-estilos-ventas`.
4. Ejecuta primero los scripts SQL de `src/main/resources/database` en pgAdmin.
5. Revisa `src/main/resources/application.properties` y coloca tu contraseña real.
6. Ejecuta `CmEstilosVentasApplication.java` como **Spring Boot App**.

## Vistas incluidas

El sistema incluye 14 archivos HTML: 12 páginas del panel administrativo, `index.html` y `tienda.html`, que es la tienda pública para clientes.

En `http://localhost:8080/tienda.html` el cliente puede consultar el catálogo con imágenes, agregar productos al carrito, ingresar sus datos, registrar un pedido online y dejar reseñas de cada prenda. El pedido descuenta stock mediante los triggers de PostgreSQL y queda visible para el administrador en el panel de ventas.

La carpeta `src/main/resources/static` contiene las vistas y `static/js/app.js` conecta los formularios con los endpoints REST mediante `fetch` y JWT. No son pantallas decorativas: los formularios guardan y consultan PostgreSQL.

## Orden de ejecución en pgAdmin

1. `00_rol_y_base.sql`: conectado a la base `postgres`.
2. `01_esquema_y_triggers.sql`: conectado a `cm_estilos_ventas`.
3. `02_permisos.sql`: conectado a `cm_estilos_ventas`.
4. `03_datos_prueba.sql`: opcional, conectado a `cm_estilos_ventas`.
5. `04_consultas_verificacion.sql`: consultas para probar tablas, triggers y reportes.
6. `05_imagenes_y_resenas.sql`: migración para imágenes de prendas y reseñas de clientes.

Si ya creaste manualmente la base o el usuario, puedes omitir las sentencias que ya ejecutaste.

Si tu instalación de pgAdmin usa el usuario y contraseña `root`, puedes colocar esos valores en `application.properties`. El proyecto también incluye el usuario aplicativo `usr_cmestilos` definido en el script de base de datos.

El esquema incluye tablas de categorías, productos, variantes, clientes, usuarios, métodos de pago, ventas, detalles de venta, proveedores, pedidos a proveedores y detalles de pedidos. También incluye la columna `activo` para descontinuar productos y proveedores sin eliminarlos.

## Credenciales de prueba

El script de datos crea:

- Usuario: `admin`
- Contraseña: `password`
- Rol: `Administrador`

También crea:

- Usuario: `cajero`
- Contraseña: `password`
- Rol: `Cajero`

Las contraseñas se almacenan con BCrypt. No se guardan en texto plano.

## Endpoints principales

| Método | Ruta | Acceso |
|---|---|---|
| POST | `/api/auth/login` | Público |
| GET | `/api/categorias` | Administrador/Cajero |
| GET/POST/PUT/DELETE | `/api/productos` | Según rol |
| GET | `/api/catalogo` | Público |
| GET | `/api/tienda/metodos-pago` | Público |
| POST | `/api/tienda/checkout` | Público |
| GET/PUT | `/api/inventario` | Administrador/Cajero |
| GET/POST/PUT/DELETE | `/api/clientes` | Autenticado |
| POST | `/api/ventas` | Administrador/Cajero |
| POST | `/api/ventas/online` | Público |
| GET | `/api/ventas` | Administrador/Cajero |
| GET | `/api/usuarios` | Administrador |
| GET/POST/PUT/DELETE | `/api/metodos-pago` | Administrador/Cajero |
| GET/POST/PUT/DELETE | `/api/proveedores` | Administrador |
| GET/POST/PATCH | `/api/pedidos-proveedor` | Administrador |
| GET | `/api/reportes/resumen` | Administrador |
| GET | `/api/reportes/ventas` | Administrador |

Para las rutas protegidas agrega el encabezado:

```text
Authorization: Bearer TU_TOKEN_JWT
```

## Ejemplo de login

```json
{
  "username": "admin",
  "password": "password"
}
```

## Ejemplo de venta

```json
{
  "clienteId": 1,
  "tipoVenta": "Física",
  "metodoPagoId": 1,
  "items": [
    { "varianteId": 1, "cantidad": 2 }
  ]
}
```

El servicio calcula el subtotal y PostgreSQL completa automáticamente el impuesto IGV de 18% y el monto total mediante el trigger definido en el documento.

## Ejecutar y publicar

En local, ejecuta la aplicación y abre `http://localhost:8080/tienda.html`. Para publicar, el proyecto incluye `Dockerfile`: súbelo a GitHub, crea una base PostgreSQL y un Web Service Docker en Render, y configura `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` y `APP_JWT_SECRET` como variables privadas. Ejecuta los scripts SQL de `src/main/resources/database` en la base publicada antes de iniciar la aplicación.

La página principal `/` redirige directamente a `/tienda.html`, por lo que el cliente puede usar solamente el enlace principal del servidor. El panel administrativo continúa disponible en `/login.html`.

Para una demostración académica puedes usar los planes gratuitos de Render. El Web Service gratuito puede suspenderse cuando no recibe visitas y la base PostgreSQL gratuita tiene una duración limitada; realiza copias de seguridad de la base si necesitas conservar los datos.
