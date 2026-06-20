CREATE TABLE idempotency_keys (
    idempotency_key VARCHAR(255) PRIMARY KEY,
    created_at TIMESTAMP NOT NULL
);
