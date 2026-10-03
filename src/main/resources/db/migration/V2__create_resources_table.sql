CREATE TABLE resources
(
    id         BIGSERIAL PRIMARY KEY,
    title      VARCHAR(255) NOT NULL,
    content    TEXT         NOT NULL,
    owner_id   BIGINT       NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL,
    updated_at TIMESTAMPTZ  NOT NULL,

    CONSTRAINT fk_resources_owner
        FOREIGN KEY (owner_id)
            REFERENCES users (id)
);
CREATE INDEX idx_resources_owner_id
    ON resources (owner_id);