CREATE TABLE users (
    id UUID PRIMARY KEY,
    email VARCHAR(100) not null,
    senha VARCHAR(255) not null
);

CREATE TABLE tickets (
    id UUID PRIMARY KEY,
    user_id UUID,
    line_id UUID,
    status VARCHAR(25),
    payment_transaction_id VARCHAR(255),
    qr_code_hash VARCHAR(255),

    CONSTRAINT fk_tickets_user
            FOREIGN KEY (user_id)
            REFERENCES users (id)
);