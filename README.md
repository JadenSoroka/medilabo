# Medilabo Solutions

Monorepo for a Java/Spring Boot microservices-based medical application.

## Repository structure

- `patientmanagement/`: patient records service (CRUD and patient data persistence).
- `frontend/`: server-side web UI (Thymeleaf templates) that interacts with backend services.
- `gateway/`: API gateway and routing entry point for cross-service access.

Each module is an independent Maven Spring Boot project with its own `pom.xml`, source, and tests.

## Docker

All services have been containerized with multi-stage Dockerfiles for optimized builds and deployment.

### Run Full Stack (root docker-compose)

Use the root `docker-compose.yml` to run the complete application stack (frontend, gateway, backend, and PostgreSQL).

From the repository root:

```bash
docker-compose up --build
```

Services available:

- **Gateway (entry point)**: `http://localhost:8080` - Routes all requests to appropriate services
- **Frontend UI**: `http://localhost:3000` - Direct access to the web interface
- **Patient Management API**: `http://localhost:9090` - Backend REST API
- **PostgreSQL**: `localhost:5432` - Database service

Stop the stack:

```bash
docker-compose down
```

Stop and remove volumes (resets database data):

```bash
docker-compose down -v
```

### Test Backend + Database Only

For testing just the patient management service with its database, use the docker-compose file in the `patientmanagement/` directory:

```bash
cd patientmanagement
docker-compose up --build
```

This starts only:

- Patient Management API: `http://localhost:9090`
- PostgreSQL: `localhost:5432`

## Architecture

```
Client Request
      ↓
   Gateway (8080)
      ↓
      ├─→ /api/patients/** → Patient Management Service (9090) → PostgreSQL
      └─→ /**              → Frontend (3000) → Gateway /api/**
```

All services run in Docker containers with automated dependency management and health checks.
