# MediLabo - Patient Management Microservice

A Spring Boot backend microservice for managing patient demographic information. This service provides RESTful APIs for creating, reading, updating, and deleting patient records stored in a PostgreSQL database.

## Features

- Patient CRUD operations (Create, Read, Update, Delete)
- PostgreSQL database persistence
- Input validation and exception handling
- RESTful API endpoints
- Spring Security integration

## Tech Stack

- **Java 21**
- **Spring Boot 4.0.3**
- **Spring Data JPA** - Database operations
- **Spring Security** - Authentication and authorization
- **PostgreSQL** - Database
- **Maven** - Build tool
- **Lombok** - Boilerplate code reduction

## Prerequisites

- Java 21 or higher
- Maven 3.6+
- PostgreSQL 12+

## Database Setup

### Start PostgreSQL Service

```bash
sudo systemctl start postgresql
sudo systemctl status postgresql
```

### Create Database

Access PostgreSQL:

```bash
sudo su postgres
psql
```

Create the database:

```sql
CREATE DATABASE medilabo;
```

Exit PostgreSQL:

```sql
\q
exit
```

### Database Configuration

The application connects to PostgreSQL with the following default settings (configured in `application.properties`):

- **Database**: medilabo
- **Port**: 5432
- **Username**: postgres
- **Password**: postgres

You can modify these settings in `src/main/resources/application.properties` if needed.

## Running the Application

### Using Maven Wrapper

```bash
./mvnw spring-boot:run
```

### Using Maven

```bash
mvn spring-boot:run
```

### Building and Running JAR

```bash
mvn clean package
java -jar target/medilabo-0.0.1-SNAPSHOT.jar
```

The application will start on **port 9090**.

## API Endpoints

Base URL: `http://localhost:9090`

- `GET /patients` - Get all patients
- `GET /patients/{id}` - Get patient by ID
- `POST /patients` - Create new patient
- `PUT /patients/{id}` - Update existing patient
- `DELETE /patients/{id}` - Delete patient

## Patient Model

```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "dateOfBirth": "1990-01-15",
  "gender": "M",
  "address": "123 Main St",
  "phone": "555-1234"
}
```
