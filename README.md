# Ledger 💰

A modern fintech backend built with Kotlin, Spring Boot and Hexagonal Architecture.

Ledger simulates the core of a financial system with double-entry bookkeeping, ensuring consistency, traceability and auditability of all monetary operations.

---

## ⚙️ Tech Stack

- Kotlin 2.1
- Spring Boot 3
- PostgreSQL
- Redis
- Kafka
- Flyway
- Testcontainers
- OpenAPI (Swagger)
- Docker

---

## 🧠 Architecture

This project follows **Hexagonal Architecture (Ports & Adapters)** to isolate business rules from infrastructure concerns.  
```Domain → Application → Ports → Adapters → Infrastructure``

---
### Key principles:

- Domain is framework-agnostic
- Business rules are fully isolated
- Infrastructure is replaceable
- High testability and maintainability

---

## 💡 Core Features

### 💸 Financial Transfers
- Transfer money between accounts
- Ensures transactional consistency
- Prevents double spending

### 📊 Balance Calculation
- Balance is derived from ledger entries
- No stored balance (event-sourced style ledger)

### 🔐 Idempotency
- Prevents duplicate transfer processing
- Redis-based idempotency key tracking

### 🧾 Double-Entry Bookkeeping
Each transfer generates:

- 1 DEBIT entry
- 1 CREDIT entry

Ensuring full auditability

---

## 🔄 Transfer Flow

```
HTTP Request
↓
Controller (Adapter)
↓
Input Port (Use Case)
↓
Domain Service
↓
Output Ports
↓
PostgreSQL / Redis
```

---

## 🧱 Project Structure
```
com.tguimaraes.ledger.core
├── domain
├── application
├── adapter
│ ├── inbound (REST)
│ └── outbound (DB, Redis)
├── config
```

---

## 🧪 Testing Strategy

- Unit tests for domain logic
- Web layer tests (MockMvc + MockK)
- Integration tests with Testcontainers

Containers used:
- PostgreSQL
- Kafka
- Redis

---

## 📡 API Documentation

Swagger available at:
```
/swagger-ui.html
```

---

## 📦 How to run

### Start dependencies
```bash
docker-compose up -d
```

### Run application
```bash
./gradlew bootRun
```

---

## 📌 Example endpoints

### Create transfer
```bash
POST /transfers
Idempotency-Key: <uuid>
```

```json
{
  "fromAccountId": "uuid",
  "toAccountId": "uuid",
  "amount": 100.00
}
```

### Get balance
```bash
GET /accounts/{accountId}/balance
```

---

## 🧭 Design Highlights

- Hexagonal Architecture
- Domain-driven design principles
- Strong separation of concerns
- Idempotent API design
- Event-driven ready (Kafka prepared)
- Audit-friendly ledger model

---

## 🚀 Future Improvements
- Outbox pattern
- Event sourcing evolution
- CQRS separation
- Anti-fraud service
- Distributed tracing (OpenTelemetry)
- Rate limiting

---

## 👤 Author
Thiago Henrique Guimarães  
📧 tguimaraes.dev@gmail.com  
🔗 GitHub: https://github.com/t-guimaraes  
🔗 LinkedIn: https://www.linkedin.com/in/t-guimaraes  

---

## 🪪 License
MIT