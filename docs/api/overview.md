# API Overview — Ledger Core

## Base URL
```
/accounts
/transfers
```

---

## Endpoints

### GET /accounts/{id}/balance

Returns current computed balance.

### Response

```json
{
  "accountId": "uuid",
  "balance": 100.00
}
```

### POST /transfers
Creates a financial transfer.

### Headers:
```
Idempotency-Key: unique-key
```

### Body:
```json
{
  "fromAccountId": "uuid",
  "toAccountId": "uuid",
  "amount": 100.00
}
```
---

## Idempotency
All transfer requests require:
```
Idempotency-Key
```

### Behavior:
- prevents duplicate processing
- stored in Redis for 1 hour
- ensures at-most-once semantics at API level

---

## Validation Rules
- amount > 0
- fromAccountId != toAccountId
- account must exist
- sufficient balance required

---

## Error Model
Uses RFC 7807 Problem Details:

- 400 → invalid request
- 404 → account not found
- 409 → idempotency conflict
- 422 → insufficient balance