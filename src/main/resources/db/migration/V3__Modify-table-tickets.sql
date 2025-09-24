ALTER TABLE users
RENAME COLUMN senha TO password;

ALTER TABLE tickets
ADD CONSTRAINT uk_tickets_payment_transaction_id UNIQUE (payment_transaction_id);

ALTER TABLE tickets
ADD CONSTRAINT uk_tickets_qr_code_hash UNIQUE (qr_code_hash);