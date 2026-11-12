# PayFlow — Payment Transaction Management System

A RESTful backend application built using Java and Spring Boot to manage payment transactions with PostgreSQL persistence, idempotency handling, validation, and automated testing.

## Features

- **Create Payments:** Create payment transactions with sender, receiver, amount, and currency details.
- **Retrieve Payments:** Fetch individual payments or list transactions with pagination and sorting.
- **Update Payment Status:** Transition payments from `PENDING` to `SUCCESS` or `FAILED`.
- **Idempotency:** Prevent duplicate payment creation when clients retry requests using the same idempotency key.
- **Input Validation:** Validate payment amounts, currency codes, and required fields.
- **Exception Handling:** Centralized exception handling with appropriate HTTP status codes.
- **API Documentation:** Interactive API documentation using Swagger UI.
- **Automated Testing:** Unit tests with JUnit 5 and Mockito, and PostgreSQL integration tests.
- **CI Pipeline:** GitHub Actions workflow to build the application and run tests automatically.

## Tech Stack

| Component | Technology |
|---|---|
| Language | Java 25 |
| Backend Framework | Spring Boot |
| Database | PostgreSQL 17 |
| ORM | Spring Data JPA / Hibernate |
| API | REST |
| API Documentation | OpenAPI / Swagger |
| Testing | JUnit 5, Mockito |
| Build Tool | Maven |
| CI/CD | GitHub Actions |

## Architecture

```text
Client (Postman / Swagger UI)
             |
             v
      REST Controller
             |
             v
       Payment Service
             |
             v
     Payment Repository
             |
             v
         PostgreSQL

     Global Exception Handler
     handles API errors centrally
```

## API Endpoints

Base URL: `http://localhost:8080`

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/payments` | Create a payment |
| GET | `/api/payments/{id}` | Retrieve a payment |
| GET | `/api/payments` | Retrieve paginated payments |
| PATCH | `/api/payments/{id}/status` | Update payment status |

### Create Payment

**POST** `/api/payments`

Header:

```text
Content-Type: application/json
Idempotency-Key: unique-request-123
```

Request body:

```json
{
  "senderAccount": "ACC1001",
  "receiverAccount": "ACC2001",
  "amount": 1500.00,
  "currency": "INR"
}
```

### Retrieve Payments

**GET** `/api/payments?page=0&size=10&sort=createdAt,desc`

### Update Payment Status

**PATCH** `/api/payments/{id}/status`

Request body:

```json
{
  "status": "SUCCESS"
}
```

## Local Setup

### Prerequisites

- Java 25
- PostgreSQL 17
- Git

### 1. Clone the repository

```bash
git clone <https://github.com/ShreshthaRoy/PayFlow>
cd PayFlow
```

### 2. Create the PostgreSQL database

Create a database named `payflow` and configure a database user with the required permissions.

### 3. Configure the database password

Set the database password as an environment variable:

```bash
export DB_PASSWORD='your_database_password'
```

The application reads the password from `DB_PASSWORD`; no database credentials need to be stored in the source code.

### 4. Run the application

```bash
./mvnw spring-boot:run
```

The application starts at:

`http://localhost:8080`

## API Documentation

Swagger UI:

`http://localhost:8080/swagger-ui/index.html`

Use Swagger UI to explore and test the available REST endpoints.

## Running Tests

Run the complete test suite:

```bash
./mvnw clean verify
```

The test suite includes unit tests and PostgreSQL integration tests.

## Continuous Integration

GitHub Actions automatically builds the application and executes the test suite on pushes and pull requests targeting the `main` branch.

## Project Scope

PayFlow is a simulated payment transaction management backend developed for learning and demonstrating backend engineering concepts. It does not connect to actual banking networks or process real financial transactions.

---

**Author:** Shreshtha Roy