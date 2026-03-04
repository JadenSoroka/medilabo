# MediLabo Frontend

Spring Boot + Thymeleaf frontend for managing MediLabo patients.

## Important details

- **Tech stack:** Java 21, Spring Boot 4, Spring MVC, Thymeleaf, WebClient
- **App port:** `3000` (configured in `src/main/resources/application.properties`)
- **Backend dependency:** Expects the patient API at `http://localhost:8080`
- **Main purpose:** Render patient pages and forward CRUD actions to the backend API

## Project structure

- `src/main/java/com/medilabo/frontend/controller` — MVC routes and page actions
- `src/main/java/com/medilabo/frontend/service` — HTTP calls to backend (`/api/patients`)
- `src/main/java/com/medilabo/frontend/domain` — `Patient` model
- `src/main/resources/templates` — Thymeleaf pages (`index`, `patient`, create/update forms)

## Run locally

```bash
./mvnw spring-boot:run
```

Then open: `http://localhost:3000`

## Docker

The frontend includes a multi-stage Dockerfile that builds and packages the application in an optimized container.

### Build the Docker image

```bash
docker build -t medilabo-frontend .
```

### Run the Docker container

```bash
docker run -p 3000:3000 medilabo-frontend
```

To connect to a backend running on the host machine:

```bash
docker run -p 3000:3000 -e PATIENT_API_URL=http://host.docker.internal:8080 medilabo-frontend
```

### Docker features

- **Multi-stage build:** Uses JDK 21 for building, JRE 21 for runtime (smaller image)
- **Layer caching:** Dependencies are cached separately from source code for faster rebuilds
- **JVM optimization:** Pre-configured memory settings for container environments

## Test

```bash
./mvnw test
```

## Main routes

- `GET /` — home page
- `GET /patient` — patient list page
- `GET /createPatientForm` — create patient form
- `GET /updatePatientForm?id={id}` — update patient form
- `POST /patients` — create patient
- `PUT /patients/{id}` — update patient
- `POST /patients/{id}/delete` — delete patient
