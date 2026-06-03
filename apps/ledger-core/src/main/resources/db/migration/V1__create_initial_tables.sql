CREATE TABLE accounts (
                          id UUID PRIMARY KEY,
                          owner_name VARCHAR(255) NOT NULL,
                          created_at TIMESTAMP NOT NULL,
                          version BIGINT NOT NULL
);

CREATE TABLE transactions (
                              id UUID PRIMARY KEY,
                              amount NUMERIC(19,2) NOT NULL,
                              created_at TIMESTAMP NOT NULL
);

CREATE TABLE entries (
                         id UUID PRIMARY KEY,
                         transaction_id UUID NOT NULL,
                         account_id UUID NOT NULL,
                         type VARCHAR(20) NOT NULL,
                         amount NUMERIC(19,2) NOT NULL,
                         created_at TIMESTAMP NOT NULL
);