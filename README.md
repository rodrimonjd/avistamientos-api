# API REST de Avistamientos de Aves

Trabajo en clase de Ingeniería de Software 2 (ISW-2), Sesión 17.
API REST para registrar avistamientos de aves, construida con Java + Spring Boot y H2 (base de datos embebida).

## Autor

- Nombre: Juan David Rodriguez Gonzalez

## Uso de IA

- IA utilizada: Claude 
- Para qué se usó: ayuda con errores de ejecución, configuración de la consola de H2.

## Tecnologías

- Java 17 o superior
- Spring Boot
- H2
- Maven 
- Swagger UI

## Requisitos previos

- JDK 17 o superior.

No necesita instalar Maven: el proyecto trae `mvnw` y `mvnw.cmd`.

## Instalación y ejecución

1. Clonar el repositorio:

```
git clone https://github.com/rodrimonjd/avistamientos-api
cd NOMBRE_DE_LA_CARPETA
```

2. Ejecutar la API:

Windows (PowerShell):

.\mvnw.cmd spring-boot:run

Mac / Linux:

./mvnw spring-boot:run

3. Cuando aparezca `Started ... in X seconds`, la API está corriendo en `http://localhost:8080`.

Para detenerla, presione `Ctrl + C` en la terminal.

## Base de datos

Los datos se guardan en un archivo H2 dentro de la carpeta `data/` (se crea automáticamente al ejecutar la API), por lo que sobreviven a los reinicios.

Para ver los datos directamente, abra la consola de H2 en `http://localhost:8080/h2-console` con:

| Campo    | Valor                                |
|----------|--------------------------------------|
| JDBC URL | `jdbc:h2:file:./data/avistamientos`  |
| User     | `juan`                               |
| Password | `rodriguez`                          |

Y ejecuta:

```sql
SELECT * FROM AVISTAMIENTO;
```

## Documentación interactiva (Swagger)

Con la API corriendo, abra `http://localhost:8080/` para probar los endpoints desde el navegador.

## Modelo del recurso

Cada avistamiento tiene:

| Campo       | Tipo   | Descripción                                   |
|-------------|--------|-----------------------------------------------|
| `id`        | número | Lo asigna el sistema                          |
| `especie`   | texto  | Nombre de la especie (ej. "colibrí")          |
| `lugar`     | texto  | Dónde se observó                              |
| `fecha`     | texto  | Formato `AAAA-MM-DD`                          |
| `observador`| texto  | Quién lo registró                             |

## Endpoints

| Operación                  | Método y ruta               | Respuestas                              |
|----------------------------|-----------------------------|-----------------------------------------|
| Listar avistamientos       | `GET /avistamientos`        | 200 con la lista                        |
| Ver un avistamiento        | `GET /avistamientos/{id}`   | 200, o 404 si no existe                 |
| Registrar un avistamiento  | `POST /avistamientos`       | 201 con el creado, o 400 si faltan datos|
| Actualizar un avistamiento | `PUT /avistamientos/{id}`   | 200, o 404 si no existe                 |
| Eliminar un avistamiento   | `DELETE /avistamientos/{id}`| 200 o 204, o 404 si no existe           |

### Ejemplos con curl

**Listar todos (200):**

```
curl http://localhost:8080/avistamientos
```

**Ver uno (200 o 404):**

```
curl http://localhost:8080/avistamientos/1
```

**Registrar (201 o 400):**

```
curl -X POST http://localhost:8080/avistamientos \
  -H "Content-Type: application/json" \
  -d '{"especie":"colibrí","lugar":"Bogotá","fecha":"2026-10-08","observador":"Ana"}'
```

**Actualizar (200 o 404):**

```
curl -X PUT http://localhost:8080/avistamientos/1 \
  -H "Content-Type: application/json" \
  -d '{"especie":"colibrí","lugar":"Chía","fecha":"2026-10-09","observador":"Ana"}'
```

**Eliminar (200/204 o 404):**

```
curl -X DELETE http://localhost:8080/avistamientos/1
```

**Ejemplo de error 400 (faltan datos):**

```
curl -i -X POST http://localhost:8080/avistamientos \
  -H "Content-Type: application/json" \
  -d '{"especie":"colibrí"}'
```

**Ejemplo de error 404 (no existe):**

```
curl -i http://localhost:8080/avistamientos/9999
```
