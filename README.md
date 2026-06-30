# Sistema Web para la Gestión de Rendimiento de Madera en un Aserradero

Aplicación web con **Spring Boot + MySQL** que registra el ingreso de madera, calcula
su rendimiento (60 % aprovechable / 40 % desperdicio), controla el inventario, factura
compras y ventas, y genera reportes semanales de ganancia.

El proyecto está dividido en dos partes que viven en el mismo programa:

- **API REST** (backend, Java/Spring Boot) con arquitectura por capas.
- **Frontend HTML + JavaScript** simple que consume la API (carpeta `static`).

---

## 1. Requisitos

- **Java 17** o superior (`java -version`)
- **Maven** (incluido en muchos IDE) o el wrapper `mvnw`
- **MySQL 8** corriendo en `localhost:3306`
- Un navegador web

---

## 2. Cómo ejecutar

### Paso 1 — Configura la base de datos
La aplicación crea la base y las tablas automáticamente (`ddl-auto=update`), así que
**solo necesitas tener MySQL encendido**. Si tu usuario/clave de MySQL no son
`root` / `root`, edítalos en `src/main/resources/application.properties`:

```properties
spring.datasource.username=root
spring.datasource.password=root
```

> Opcional: si prefieres crear el esquema a mano, ejecuta `database/aserradero.sql`.

### Paso 2 — Arranca el backend
Desde la carpeta del proyecto:

```bash
mvn spring-boot:run
```

(o ejecuta la clase `AserraderoApplication` desde tu IDE).

Al arrancar se crean automáticamente los roles y dos usuarios de prueba.

### Paso 3 — Abre el sistema
Ve a **http://localhost:8080/login.html** e inicia sesión:

| Usuario     | Contraseña    | Rol           |
|-------------|---------------|---------------|
| `admin`     | `admin123`    | ADMINISTRADOR |
| `encargado` | `encargado123`| ENCARGADO     |

---

## 3. Arquitectura por capas

```
Navegador (HTML + JS)
        │  (peticiones REST con autenticación Basic)
        ▼
Controller  →  recibe las peticiones HTTP y devuelve JSON
        ▼
Service     →  contiene la lógica de negocio y las reglas
        ▼
Repository  →  acceso a datos con Spring Data JPA
        ▼
Model       →  entidades mapeadas a las tablas de MySQL
```

Carpetas dentro de `src/main/java/com/aserradero`:

- **`model`** — entidades JPA (tablas): `Proveedor`, `Cliente`, `IngresoMadera`,
  `Rendimiento`, `ProductoMadera`, `FacturaProveedor`, `FacturaCliente`, `Usuario`, `Rol`…
- **`repository`** — interfaces `JpaRepository` para cada entidad.
- **`dto`** — objetos para recibir datos del frontend y para el reporte.
- **`service`** — lógica de negocio (cubicación, rendimiento, inventario, facturación, reportes).
- **`controller`** — endpoints REST.
- **`config`** — seguridad (`SecurityConfig`) y datos iniciales (`DataInitializer`).
- **`exception`** — manejo centralizado de errores con respuestas JSON claras.

El frontend está en `src/main/resources/static` (`login.html`, `index.html`,
`css/style.css`, `js/app.js`).

---

## 4. Flujo del negocio

```
Ingreso de madera → Factura proveedor → Procesamiento diario → Inventario → Ventas → Reportes
```

1. Se registra un **proveedor**.
2. Llega un camión → se registra el **ingreso de madera** con sus medidas.
   - El sistema calcula las **pulgadas ingresadas**:
     `alto del camión × largo del camión × largo de la tuca × 0.56 × 362`.
   - Calcula además un **rendimiento estándar 60/40** que queda solo como
     *referencia esperada*, no como dato definitivo.
   - **El ingreso NO aumenta el inventario.** Solo registra la materia prima que llegó.
3. Se registra la **factura del proveedor**, que toma un ingreso existente y calcula
   `total compra = pulgadas ingresadas × precio compra`.
4. Se registra el **procesamiento diario**: las pulgadas que realmente se procesaron
   de un ingreso. Cada procesamiento:
   - descuenta del disponible del ingreso (`disponible = ingresado − procesado`), y
   - **aumenta el inventario vendible** con las pulgadas procesadas.
5. Se registra una **venta (factura de cliente)**: el sistema valida que haya
   inventario suficiente y lo **descuenta**. `total venta = cantidad × precio venta`.
6. Los **reportes** muestran el **rendimiento real**:
   `aprovechamiento real % = madera procesada ÷ madera ingresada × 100`,
   `desperdicio real = ingresada − procesada`, más ventas, compras,
   ganancia actual (`ventas − compras`), valor del inventario restante y
   ganancia estimada final (`ventas + valor inventario − compras`).

---

## 5. Endpoints principales de la API

| Método | Ruta                                   | Descripción                              |
|--------|----------------------------------------|------------------------------------------|
| GET    | `/api/auth/me`                         | Datos del usuario autenticado            |
| GET/POST/PUT/DELETE | `/api/proveedores`        | CRUD de proveedores                      |
| GET/POST/PUT/DELETE | `/api/clientes`           | CRUD de clientes                         |
| POST   | `/api/ingresos`                        | Registra ingreso (calcula pulgadas)      |
| GET    | `/api/ingresos/disponibles-procesamiento` | Ingresos con su disponible por procesar |
| GET    | `/api/ingresos/{id}/rendimiento`       | Rendimiento estándar de un ingreso       |
| GET/POST | `/api/procesamientos`                | Lista y registra procesamiento diario    |
| PUT    | `/api/procesamientos/{id}/anular`      | Anula un procesamiento                    |
| GET    | `/api/productos`                       | Inventario de madera                     |
| POST   | `/api/facturas-proveedor`              | Registra factura de compra               |
| PUT    | `/api/facturas-proveedor/{id}/anular`  | Anula una factura de compra              |
| POST   | `/api/facturas-cliente`                | Registra venta (descuenta inventario)    |
| PUT    | `/api/facturas-cliente/{id}/anular`    | Anula una venta                          |
| GET    | `/api/reportes/semana-actual`          | Reporte de la semana en curso            |
| GET    | `/api/reportes?desde=&hasta=`          | Reporte por rango de fechas              |
| GET/POST/DELETE | `/api/usuarios` *(solo admin)*| Gestión de usuarios                      |

---

## 6. Seguridad y roles

- Autenticación **HTTP Basic** sobre la API; las contraseñas se guardan
  encriptadas con **BCrypt**.
- Dos roles: **ADMINISTRADOR** (acceso total, incluida la gestión de usuarios) y
  **ENCARGADO** (operación diaria, sin gestión de usuarios).
- El frontend guarda las credenciales en `sessionStorage` y las envía en cada
  petición; al cerrar sesión se borran.

---

## 7. Cumplimiento de las reglas de negocio

- **Rendimiento real (no solo 60/40)** → `ReporteService` calcula
  `aprovechamiento real = procesado ÷ ingresado`. El 60/40 de `RendimientoService`
  queda como **estándar esperado** de comparación.
- **El ingreso NO aumenta inventario** → `IngresoMaderaService` solo registra la
  materia prima y calcula las pulgadas.
- **El inventario sube con el procesamiento real** → `ProcesamientoMaderaService` +
  `ProductoMaderaService.ingresarAprovechable(...)`.
- **No se puede procesar más de lo disponible del ingreso** → el procesamiento valida
  `ingresado − procesado`.
- **No se puede vender más de lo disponible** → `ProductoMaderaService.descontar(...)`
  lanza un error si la cantidad supera el stock.
- **El inventario baja al facturar una venta** → `FacturaClienteService` (transaccional).
- **La factura de proveedor usa un ingreso y no se duplica** → `FacturaProveedorService`
  valida que el ingreso exista y que no tenga ya una factura activa.
- **Una factura anulada no afecta los reportes** → `ReporteService` solo suma
  facturas en estado `ACTIVA`.
- **Reportes filtrables por fecha** → `GET /api/reportes?desde=&hasta=`.

---

## 8. Nota de diseño (importante para la defensa)

El **rendimiento real** se obtiene del **procesamiento diario**, no del estimado fijo.
El ingreso solo guarda la materia prima (pulgadas ingresadas con la fórmula
`alto × largo camión × largo tuca × 0.56 × 362`). El inventario vendible se llena
únicamente con las pulgadas que se procesan día a día, y el reporte compara lo
procesado contra lo ingresado para obtener el aprovechamiento verdadero. El 60/40
se conserva como **estándar esperado** de referencia. Además, al ingreso se le
añadieron los campos `tipoMadera`, `precioCompra` y `precioVenta` para clasificar
la madera procesada por tipo y poder costear compras y ventas por pulgada.

---

## 9. Estructura del proyecto

```
aserradero-rendimiento/
├── pom.xml
├── README.md
├── database/
│   └── aserradero.sql
└── src/main/
    ├── java/com/aserradero/
    │   ├── AserraderoApplication.java
    │   ├── config/        (SecurityConfig, DataInitializer)
    │   ├── controller/    (endpoints REST)
    │   ├── dto/           (objetos de petición y reporte)
    │   ├── exception/     (manejo de errores)
    │   ├── model/         (entidades / tablas)
    │   ├── repository/    (acceso a datos)
    │   └── service/       (lógica de negocio)
    └── resources/
        ├── application.properties
        └── static/        (login.html, index.html, css, js)
```
