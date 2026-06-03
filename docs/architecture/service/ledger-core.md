# Ledger Core Architecture

## Overview

`ledger-core` is the main financial service of the Ledger platform.

It is responsible for:

* account management
* financial transfers
* transaction processing
* ledger bookkeeping
* balance calculation
* financial consistency

The service was designed using Hexagonal Architecture (Ports and Adapters) to isolate the business domain from infrastructure concerns.

The core business rules remain independent from:

* HTTP
* databases
* Kafka
* Redis
* Spring Framework
* external services

This approach improves:

* maintainability
* scalability
* testability
* modularity
* resilience

---

# Monorepo Structure

```text
ledger/
 ├── apps/
 │    └── ledger-core/
 │
 ├── infrastructure/
 │
 ├── docs/
 │    └── architecture/
 │
 ├── build.gradle.kts
 ├── settings.gradle.kts
 │
 └── docker-compose.yml
```

The repository is structured as a Gradle multi-module monorepo.

Future services may include:

```text
ledger-auth
ledger-antifraud
ledger-notification
ledger-analytics
```

Each service is expected to evolve independently while following shared architectural standards.

---

# Architectural Style

The service follows Hexagonal Architecture (Ports and Adapters).

## High-Level Architecture

```text
          ┌─────────────────────┐
          │     External        │
          │ HTTP / Kafka / DB   │
          └──────────┬──────────┘
                     │
         ┌───────────▼───────────┐
         │       Adapters        │
         └───────────┬───────────┘
                     │
         ┌───────────▼───────────┐
         │         Ports         │
         └───────────┬───────────┘
                     │
         ┌───────────▼───────────┐
         │        Domain         │
         └───────────────────────┘
```

The domain is the center of the architecture and must not depend on external frameworks or infrastructure.

---

# Package Structure

```text
com/ledger/core
│
├── domain
│    ├── model
│    ├── service
│    ├── event
│    └── exception
│
├── application
│    ├── dto
│    ├── service
│    ├── usecase
│    └── port
│         ├── input
│         └── output
│
├── adapters
│    ├── input
│    │     ├── http
│    │     └── kafka
│    │
│    └── output
│          ├── persistence
│          ├── kafka
│          └── redis
│
├── config
│
└── LedgerApplication.kt
```

---

# Layer Responsibilities

## Domain Layer

Contains pure business logic and financial rules.

The domain must not depend on:

* Spring
* databases
* messaging brokers
* infrastructure frameworks

### Responsibilities

* financial consistency
* transfer validation
* balance rules
* ledger bookkeeping
* domain events
* business invariants

### Examples

```text
Account
Transaction
Entry
TransferDomainService
```

---

## Application Layer

Responsible for orchestrating use cases.

Coordinates:

* repositories
* domain services
* transactions
* event publishing
* cache access

### Examples

```text
CreateTransferUseCase
GetBalanceUseCase
```

---

## Ports

Ports define contracts between the application core and external systems.

### Input Ports

Represent operations exposed by the application.

Examples:

```text
CreateTransferInputPort
GetBalanceInputPort
```

### Output Ports

Represent infrastructure dependencies required by the core.

Examples:

```text
AccountRepositoryPort
TransactionRepositoryPort
EventPublisherPort
CachePort
```

---

## Adapters

Adapters implement infrastructure integrations.

The business core does not know implementation details.

### Input Adapters

Receive external communication.

Examples:

* REST Controllers
* Kafka Consumers

### Output Adapters

Implement infrastructure access.

Examples:

* PostgreSQL
* Redis
* Kafka Producers

---

# Financial Ledger Model

The system uses a double-entry bookkeeping model.

Balances are not stored directly.

Instead, balances are derived from ledger entries.

Every transfer generates:

```text
1 DEBIT entry
1 CREDIT entry
```

This model guarantees:

* auditability
* traceability
* consistency
* financial integrity

---

# Transfer Flow

```text
Client
   ↓
REST API
   ↓
Input Adapter
   ↓
Input Port
   ↓
Use Case
   ↓
Domain Service
   ↓
Output Ports
   ↓
Persistence / Kafka / Redis
```

---

# Event-Driven Architecture

The service publishes domain events using Kafka.

Examples:

* TransferCreatedEvent
* TransferCompletedEvent
* TransferFailedEvent

This enables:

* asynchronous communication
* service decoupling
* scalability
* reactive integrations

---

# Consistency and Concurrency

The system is designed to support:

* optimistic locking
* idempotency
* transactional consistency
* retry mechanisms
* distributed event processing

These patterns help prevent:

* double spending
* race conditions
* duplicated requests

---

# Observability

The platform includes:

* Spring Boot Actuator
* Prometheus
* Grafana
* OpenTelemetry

Metrics examples:

```text
ledger_transfer_success_total
ledger_transfer_failure_total
ledger_transfer_duration
```

---

# Testing Strategy

## Unit Tests

Focused on:

* domain services
* use cases
* business rules

Tools:

* JUnit 5
* MockK

---

## Integration Tests

Executed using Testcontainers.

Containers:

* PostgreSQL
* Redis
* Kafka

This simulates a production-like environment during automated tests.

---

# Technology Stack

## Backend

* Kotlin
* Spring Boot 3

## Database

* PostgreSQL

## Messaging

* Kafka

## Cache

* Redis

## Infrastructure

* Docker
* Docker Compose

## Observability

* Prometheus
* Grafana
* OpenTelemetry

## Testing

* JUnit 5
* MockK
* Testcontainers

---

# Future Improvements

The architecture is prepared for:

* CQRS
* Saga Pattern
* Outbox Pattern
* Distributed Tracing
* API Gateway
* Anti-Fraud Engine
* Rate Limiting
* Dead Letter Queues
* Event Sourcing

---

# Conclusion

`ledger-core` was designed to simulate a modern fintech backend focused on:

* scalability
* consistency
* resiliency
* maintainability

The project aims to demonstrate enterprise-grade backend engineering practices commonly used in financial systems and distributed architectures.
