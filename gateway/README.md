# Medilabo API Gateway

API Gateway service for the Medilabo application using Spring Cloud Gateway.

## Overview

This gateway serves as the single entry point for the Medilabo application, routing requests to the appropriate backend services and frontend application.

## Technology Stack

- **Spring Boot**: 4.0.3
- **Spring Cloud Gateway**: 2025.1.0
- **Java**: 21

## Routes

| Path               | Destination             | Description                        |
| ------------------ | ----------------------- | ---------------------------------- |
| `/api/patients/**` | `http://localhost:9090` | Backend API for patient management |
| `/**`              | `http://localhost:3000` | Frontend application (catch-all)   |

## Configuration

Routes are configured in [`RouteConfig.java`](src/main/java/com/medilabo/gateway/config/RouteConfig.java).

Default service URLs are defined in [`UriConfig.java`](src/main/java/com/medilabo/gateway/config/UriConfig.java):

- Backend: `http://localhost:9090`
- Frontend: `http://localhost:3000`

To override these defaults, set the following properties in `application.properties`:

```properties
backend=http://localhost:9090
frontend=http://localhost:3000
```

## Running the Application

### Prerequisites

- Java 21
- Maven 3.6+

### Start the Gateway

```bash
mvn spring-boot:run
```

The gateway will start on the default port **8080**.

### Build

```bash
mvn clean package
```

### Run Tests

```bash
mvn test
```

## Usage Examples

Assuming the gateway runs on port 8080:

**Access Frontend:**

```bash
curl http://localhost:8080/
```

**Access Patient API:**

```bash
curl http://localhost:8080/api/patients/jaden_soroka
```

## Architecture

```
Client Request
      ↓
   Gateway (8080)
      ↓
      ├─→ /api/patients/** → Backend Service (9090)
      └─→ /**              → Frontend App (3000)
```
