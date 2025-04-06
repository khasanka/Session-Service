# Session Service – SaaS Conference Platform Microservice

## 📖 Problem Statement

Developing a **microservice** for a SaaS platform that manages conference presentations.  
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

# Part 2: Theoretical Questions – Answer Sheet

---

## 1. AWS Design Question

> Deploy the microservice on AWS in a highly available and scalable manner.

### ✅ Recommended AWS Architecture:

| Component     | AWS Service                | Description |
|---------------|----------------------------|-------------|
| Compute       | **AWS Fargate (ECS)**      | Containerized backend, managed by AWS |
| Database      | **Amazon Aurora MySQL**    | Auto-scaling, high availability RDS |
| Storage       | **Amazon S3**              | For uploaded session files |
| Security      | **IAM, API Gateway, WAF**  | Secure API access, rate limiting, DDoS protection |
| Load Balancer | **Application Load Balancer** | Distributes traffic across Fargate tasks |
| Scaling       | **Auto Scaling, Multi-AZ** | Handles traffic bursts and zone failures |

---

## 2. Debugging & Performance Optimization

> `/sessions` endpoint takes 10 seconds. How to identify and fix?

### ✅ Steps to Optimize:

1. **Analyze SQL performance** with `EXPLAIN`
2. **Add proper indexes** on columns like `title`, `speaker`
3. **Enable Dropwizard metrics** to time DAO layer
4. **Introduce pagination** (LIMIT, OFFSET)
5. **Use HikariCP** for connection pooling
6. **Consider caching** (e.g., Redis for read-heavy workloads)

---

## 3. Security Scenario – Under DDoS

### ✅ Infrastructure Hardening (AWS):

- AWS WAF for IP blocking/rate limiting
- API Gateway throttling policies
- CloudFront CDN with edge caching
- Auto Scaling and health checks
- Route53 failover routing

### ✅ Code-Level Mitigation:

- Rate-limiting per API key/IP
- Early rejection before hitting database
- Input validation & authentication
- Log and alert on abnormal spikes

---

## 4. Code Review Question

### ❌ Original Code:
```java
public List<Session> getSessions() {
    Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/conferences", "user", "pass");
    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT * FROM sessions");
    List<Session> sessions = new ArrayList<>();
    while (rs.next()) {
        sessions.add(new Session(rs.getInt("id"), rs.getString("title"), rs.getString("description")));
    }
    return sessions;
}
```

### 🔍 Problems:
1. ❌ No resource cleanup – no `close()` or try-with-resources
2. ❌ Hardcoded DB credentials – should be injected via config/env
3. ❌ No pagination or query limit – leads to slow response on large datasets

---

### ✅ Refactored Version:
```java
public List<Session> getSessions() {
    List<Session> sessions = new ArrayList<>();
    String sql = "SELECT id, title, description FROM sessions";

    try (Connection conn = dataSource.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            sessions.add(new Session(
                rs.getString("id"),
                rs.getString("title"),
                rs.getString("description")));
        }
    } catch (SQLException ex) {
        // Handle properly
    }
    return sessions;
}
```

