# Session Service – SaaS Conference Platform Microservice

## 📖 Problem Statement

You are developing a **microservice** for a SaaS platform that manages conference presentations.  
This service is responsible for **handling session submissions from speakers** and exposing endpoints for creating, retrieving, updating, and deleting session information.

---

## 🧠 Architectural Overview

### ⚙️ Technology Stack

- **Java 17**
- **Dropwizard** (REST API framework)
- **JDBI** (Lightweight SQL library)
- **MySQL** (Relational DB)
- **Docker & Docker Compose** (for packaging and orchestration)
- **JUnit 5** (unit testing)
- **Logback** (structured logging)
- **API Key authentication** (basic header auth)

### 📦 Key Components

| Layer       | Class/Package                    | Responsibility |
|-------------|----------------------------------|----------------|
| Controller  | `SessionController`              | REST endpoints |
| Service     | `SessionService`                 | Business logic |
| DAO         | `SessionDAO`                     | SQL layer (via JDBI) |
| Model       | `Session`, `SessionDTO`          | Domain + Payload objects |
| Auth        | `ApiKeyAuthFilter`               | API Key validation |
| Config      | `AppConfiguration`               | YAML-driven setup |
| Errors      | `GenericExceptionMapper`, `SessionNotFoundException` | Centralized error handling |

---

## 🔧 How to Run the Application

### 💻 1. Prerequisites

- Java 17
- Maven 3.6+
- Docker & Docker Compose

---

### 🐳 2. Build & Run with Docker

```bash
# Package the app
mvn clean package

# Run with docker-compose
docker-compose up --build
```

App runs at: [http://localhost:8080](http://localhost:8080)  
MySQL runs at: `localhost:3306` (user/pass from `config.yml`)

---

### ⚙️ 3. Run Locally Without Docker

```bash
java -jar target/session-service.jar server src/main/resources/config.yml
```

---

## 📮 API Endpoints

| Method | Endpoint             | Description |
|--------|----------------------|-------------|
| POST   | `/sessions`          | Submit a new session |
| GET    | `/sessions/{id}`     | Retrieve a session by ID |
| GET    | `/sessions`          | List all sessions |
| PUT    | `/sessions/{id}`     | Update a session |
| DELETE | `/sessions/{id}`     | Delete a session |

🛡 All endpoints require an API key header:  
`X-API-Key: your-key-here`

---

## 🛠 Trade-offs Considered

- ❌ No database migrations (Flyway) — opted for simple table/index creation in code
- ✅ Lightweight Dropwizard instead of Spring Boot — faster boot, less memory
- ✅ API key over OAuth — simpler but less secure
- ❌ No Swagger UI — can be added for better DX
- ✅ Indexes added on frequently queried fields (title, speaker)
- ✅ Proper exception mapping with 404 + validation errors
- ✅ Structured logs (stdout, production ready)

---

## ✅ Tests & Validation

- Tests for controllers, service layer, and filter
- Input validation for title, speaker, and URL
- Custom error handling for missing/invalid sessions

---

## 🚀 Future Improvements

- Use **Flyway** for schema versioning
- Add **DTOs + mapper layer** for payload abstraction
- Support **OAuth 2 / JWT** auth
- Add **rate-limiting** & caching layer

---

