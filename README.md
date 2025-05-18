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

```bash
# 1. Clona el repositorio
git clone https://github.com/tu-usuario/events-service.git
cd events-service

# 2. Levanta los servicios (app + base de datos)
docker compose up --build

# 4. La API estará disponible en http://localhost:8080