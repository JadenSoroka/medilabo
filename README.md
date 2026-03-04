# Medilabo Solutions

Monorepo for a Java/Spring Boot microservices-based medical application.

## Repository structure

- `patientmanagement/`: patient records service (CRUD and patient data persistence).
- `frontend/`: server-side web UI (Thymeleaf templates) that interacts with backend services.
- `gateway/`: API gateway and routing entry point for cross-service access.

Each module is an independent Maven Spring Boot project with its own `pom.xml`, source, and tests.

## Run with Docker Compose (root)

Use the root `docker-compose.yml` to run the full stack (frontend, gateway, backend, and PostgreSQL) together.

From the repository root:

```bash
docker compose up --build
```

Services:

- Frontend: `http://localhost:3000`
- Gateway (recommended entry point): `http://localhost:8080`
- Patient management backend: `http://localhost:9090`
- PostgreSQL: `localhost:5432`

Stop the stack:

```bash
docker compose down
```

Stop and remove volumes (reset database data):

```bash
docker compose down -v
```
