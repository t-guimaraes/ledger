# ADR-001: Hexagonal Architecture

## Status
Accepted

---

## Context

The system requires:

- high testability
- clear separation of concerns
- ability to evolve infrastructure (Kafka, DB, Redis)
- strong domain isolation (fintech requirement)

---

## Decision

We adopted **Hexagonal Architecture (Ports and Adapters)**.

---

## Structure

- Domain → pure business rules
- Application → use case orchestration
- Ports → contracts
- Adapters → infrastructure implementations

---

## Consequences

### Positive

- high testability (domain isolated)
- easy mocking of infrastructure
- scalable architecture
- clean fintech-grade separation

### Negative

- more boilerplate code
- learning curve for junior devs
- more interfaces and indirection

---

## Why this matters for fintech

Financial systems require:

- determinism
- auditability
- consistency

Hexagonal architecture enforces these constraints by design.