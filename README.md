# Medilabo Solutions

Monorepo for a Java/Spring Boot microservices-based medical application.

## Repository structure

- `patientmanagement/`: patient records service (CRUD and patient data persistence).
- `frontend/`: server-side web UI (Thymeleaf templates) that interacts with backend services.
- `gateway/`: API gateway and routing entry point for cross-service access.

Each module is an independent Maven Spring Boot project with its own `pom.xml`, source, and tests.
