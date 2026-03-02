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
