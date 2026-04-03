# Medilabo Solutions

Monorepo for a Java/Spring Boot microservices-based medical application.

## Repository Structure

- `patientmanagement/`: patient records service (CRUD and patient data persistence, PostgreSQL).
- `physiciannotes/`: physician notes service (patient notes persistence, MongoDB).
- `diabetesassessment/`: diabetes risk assessment service (risk calculation from patient data + notes).
- `gateway/`: API gateway and routing entry point for cross-service access.
- `frontend/`: server-side web UI (Thymeleaf templates) that interacts with backend services through gateway routes.

Each module is an independent Maven Spring Boot project with its own `pom.xml`, source, and tests.

## Docker

All services are containerized with Dockerfiles and orchestrated from the root compose file.

### Prerequisites

- **Docker** 24+ and **Docker Compose** v2 (`docker compose` — not `docker-compose`)
- Ports **3000, 8080, 9090, 9091, 9092, 5432, 27017** must be free on the host
- No local Java or Maven installation required — the Dockerfiles build the JARs inside the container

### Run Full Stack (root docker-compose)

Use the root `docker-compose.yml` to run the complete application stack.

From the repository root:

```bash
docker compose up --build
```

The first build will take several minutes as Maven downloads dependencies inside each container. Subsequent runs without `--build` will be faster.

Wait until all services have started. You can follow progress with:

```bash
docker compose logs -f
```

### Accessing the Application

| Service                 | URL                     | Notes                                              |
| ----------------------- | ----------------------- | -------------------------------------------------- |
| **Frontend UI**         | `http://localhost:3000` | Start here — login page                            |
| **Gateway**             | `http://localhost:8080` | API entry point                                    |
| Patient Management API  | `http://localhost:9090` | Backend service                                    |
| Physician Notes API     | `http://localhost:9091` | Backend service                                    |
| Diabetes Assessment API | `http://localhost:9092` | Backend service                                    |
| PostgreSQL              | `localhost:5432`        | DB: `medilabo`, user: `postgres`, pass: `postgres` |
| MongoDB                 | `localhost:27017`       | DB: `medilabo`                                     |

### Login Credentials

The application uses a single shared account configured in the gateway:

| Username | Password   |
| -------- | ---------- |
| `user`   | `password` |

Navigate to `http://localhost:3000`, enter these credentials, and you will be redirected to the main patient list.

### API Testing (Postman)

A Postman collection is included at the repository root: `MediLabo.postman_collection.json`.

Import it into Postman to explore and test the API endpoints directly against the gateway (`http://localhost:8080`). To authenticate, call `POST /auth/login` with body `{"username":"user","password":"password"}` and use the returned `token` as a `Bearer` token on subsequent requests.

### Stop the Stack

```bash
docker compose down
```

Stop and remove volumes (resets all database data):

```bash
docker compose down -v
```

### Run Service-Specific Stacks

Each backend service also includes its own local `docker-compose.yml` for focused development/testing:

- `patientmanagement/docker-compose.yml`
- `physiciannotes/docker-compose.yml`
- `diabetesassessment/docker-compose.yml`

Example:

```bash
cd diabetesassessment
docker compose up --build
```

## Architecture

```text
Client
  ↓
Gateway (8080)
  ├─→ /api/patients/** → Patient Management (9090) → PostgreSQL
  ├─→ /api/notes/**    → Physician Notes (9091)    → MongoDB
  ├─→ /api/risk/**     → Diabetes Assessment (9092) → PostgreSQL + MongoDB
  └─→ /**              → Frontend (3000)
```

Gateway routes are environment-driven in Docker (`URI_BACKEND`, `URI_PHYSICIAN_NOTES`, `URI_DIABETES_ASSESSMENT`, `URI_FRONTEND`) and map to container hostnames in the root compose network.

All services run with dependency ordering and health checks to improve startup reliability.

## Green Code Recommendations

Green code is the concept of environmental sustainability in coding practices. This means that developers should maintain
a focus on minimizing energy consumption throughout the software development lifecycle. It is important to keep a balance
between performance, scalability, and efficiency when writing code, to lower environmental footprint.

A good example of green code principals NOT being followed is the current state of the physical infrastructure supporting
AI. With efficiency NOT being put at the forefront, the environmental impact of AI datacenters is massive. The water, electicity,
and land usage is vast, even to the point of impacting entire towns in terms of resource usage.
