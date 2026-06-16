ALTER TABLE entries
    ADD CONSTRAINT fk_entries_transaction
        FOREIGN KEY (transaction_id)
            REFERENCES transactions(id);

ALTER TABLE entries
    ADD CONSTRAINT fk_entries_account
        FOREIGN KEY (account_id)
            REFERENCES accounts(id);

CREATE INDEX idx_entries_account_id
    ON entries(account_id);

CREATE INDEX idx_entries_transaction_id
    ON entries(transaction_id);

CREATE INDEX idx_entries_account_type
    ON entries(account_id, type);