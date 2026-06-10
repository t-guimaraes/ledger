# Architecture Diagrams — Ledger Core

This document describes the main architectural flows and structure of the `ledger-core` service.

---

## 1. High-Level Hexagonal Architecture

```text
                ┌──────────────────────────┐
                │   External Systems       │
                │  HTTP / Redis / Kafka   │
                └───────────┬──────────────┘
                            │
                ┌───────────▼──────────────┐
                │       Adapters           │
                │ inbound / outbound       │
                └───────────┬──────────────┘
                            │
                ┌───────────▼──────────────┐
                │   Application Layer      │
                │   Use Cases / Ports      │
                └───────────┬──────────────┘
                            │
                ┌───────────▼──────────────┐
                │        Domain            │
                │ Business Rules / Model   │
                └──────────────────────────┘
```
---

## 2. Transfer Flow  

```text
Client
  ↓
TransferController (Inbound Adapter)
  ↓
CreateTransferInputPort
  ↓
CreateTransferUseCase
  ↓
TransferDomainService
  ↓
EntryQueryPort (balance check)
  ↓
EntryRepositoryPort (persist entries)
  ↓
TransactionRepositoryPort (persist transaction)
  ↓
IdempotencyPort (Redis)
```

---

## 3. Ledger Data Model (Double Entry) 

```text
Transaction
   ├── Entry (DEBIT)  → fromAccount
   └── Entry (CREDIT) → toAccount
```

---

## 4. Balance Calculation Flow

```text
entries table
   ↓
SUM(CREDIT - DEBIT)
   ↓
computed balance (no stored balance)
```

This ensures:

- no data drift
- full auditability
- correctness under concurrency

---