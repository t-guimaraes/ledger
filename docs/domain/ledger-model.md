# Ledger Domain Model

## Why we do NOT store balances

In traditional systems, balance is stored directly in the account table.

This creates problems:

- race conditions
- inconsistent state under concurrency
- difficulty in auditing historical changes

---

## Ledger approach: Event-based balance

We store only immutable financial facts:

- Transactions
- Entries (DEBIT / CREDIT)

Balance is always derived:

```sql
SUM(CREDIT) - SUM(DEBIT)
```
---

## Double-entry model

Every transfer produces:

- 1 DEBIT entry (sender)
- 1 CREDIT entry (receiver)

This guarantees:

- every debit has a matching credit
- system is always balanced globally
- audit trail is complete

---

## Example

Transfer: 100.00 from A → B

```text
| Account | Type   | Amount |
| ------- | ------ | ------ |
| A       | DEBIT  | 100    |
| B       | CREDIT | 100    |
```
---

## Benefits

### 1. Auditability

Every state change is recorded permanently.

### 2. Consistency

No risk of balance desync.

### 3. Traceability

Every transaction can be reconstructed.

### 4. Scalability

Reads can be optimized independently from writes.

---

## Trade-off
- Balance queries are computationally heavier
- Requires proper indexing or read optimization later (CQRS-ready)