CREATE TABLE outbox_event
(
    id             UUID PRIMARY KEY,
    aggregate_id   UUID         NOT NULL,
    aggregate_type VARCHAR(100) NOT NULL,
    event_type     VARCHAR(150) NOT NULL,
    payload        JSONB        NOT NULL,
    status         VARCHAR(20)  NOT NULL,
    retry_count    INT          NOT NULL,
    created_at     TIMESTAMP    NOT NULL,
    published_at   TIMESTAMP
);

CREATE INDEX idx_outbox_status
    ON outbox_event (status);