# events-service

Servicio backend desarrollado en Java con Spring Boot y Maven para la gestión de eventos.

## Características

- API RESTful para operaciones sobre eventos.
- Persistencia en base de datos PostgreSQL.
- Configuración de perfiles para distintos entornos (producción, test).
- Integración continua y despliegue automático usando GitHub Actions y Render.com.
- Soporte para Docker.

## Requisitos

- Java 21
- Maven 3.8+
- PostgreSQL
- Docker (opcional, para despliegue en contenedores)

## Configuración

Las variables de entorno necesarias para la base de datos de producción se definen en `application-prod.properties`:

- `POSTGRES_HOST`
- `POSTGRES_PORT`
- `POSTGRES_DB`
- `POSTGRES_USER`
- `POSTGRES_PASSWORD`

## Ejecución local

Se recomienda usar Docker Compose para levantar la base de datos y la aplicación localmente. Ejemplo:


### 1. Clona el repositorio
```bash
git clone https://github.com/tu-usuario/events-service.git
cd events-service
```
#### 2. Levanta los servicios (app + base de datos)
```bash
docker compose up --build
```
#### 4. La API estará disponible en http://localhost:8080/api/v1/events


## Colección Postman

Se incluye una colección Postman para facilitar la prueba de la API de gestión de eventos.

### Enlace de descarga

[Descargar colección Postman](postman/EventsService.postman_collection.json)

Importá esta colección en Postman para ejecutar los endpoints disponibles.

### Endpoints incluidos

| Método | Endpoint              | Descripción                  |
|--------|-----------------------|------------------------------|
| GET    | `/api/v1/events`      | Listar todos los eventos     |
| POST   | `/api/v1/events`      | Crear un nuevo evento        |
| GET    | `/api/v1/events/{id}` | Obtener un evento por ID     |
| PUT    | `/api/v1/events/{id}` | Actualizar un evento         |
| DELETE | `/api/v1/events/{id}` | Eliminar un evento           |

### Variables

La colección utiliza una variable llamada `baseUrl`, modificá su valor según el entorno en el que estés ejecutando la API.

## Despliegue en Producción

La aplicación está desplegada en Render con una base de datos en CleverCloud y disponible en:

[https://events-service-dlmr.onrender.com/api/v1/events](https://events-service-dlmr.onrender.com/api/v1/events)


### Imagen en DockerHub

La imagen Docker se encuentra publicada en DockerHub:

[https://hub.docker.com/r/jerecalvet/events-service](https://hub.docker.com/r/jerecalvet/events-service)
