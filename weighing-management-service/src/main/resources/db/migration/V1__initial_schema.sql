CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    enabled BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_user_role CHECK (role IN ('USER', 'ADMIN'))
);

CREATE TABLE branches (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(2) NOT NULL,
    address VARCHAR(200),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE grain_types (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(200),
    purchase_price_per_ton DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE trucks (
    id BIGSERIAL PRIMARY KEY,
    plate VARCHAR(10) NOT NULL UNIQUE,
    model VARCHAR(50),
    tare_weight DECIMAL(10, 2) NOT NULL,
    branch_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_truck_branch FOREIGN KEY (branch_id) REFERENCES branches(id)
);

CREATE TABLE scales (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    location VARCHAR(100),
    branch_id BIGINT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_scale_branch FOREIGN KEY (branch_id) REFERENCES branches(id)
);

CREATE TABLE transport_transactions (
    id BIGSERIAL PRIMARY KEY,
    truck_id BIGINT NOT NULL,
    grain_type_id BIGINT NOT NULL,
    branch_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS',
    start_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    end_time TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transaction_truck FOREIGN KEY (truck_id) REFERENCES trucks(id),
    CONSTRAINT fk_transaction_grain_type FOREIGN KEY (grain_type_id) REFERENCES grain_types(id),
    CONSTRAINT fk_transaction_branch FOREIGN KEY (branch_id) REFERENCES branches(id),
    CONSTRAINT chk_transaction_status CHECK (status IN ('IN_PROGRESS', 'COMPLETED', 'CANCELLED'))
);

CREATE TABLE weighings (
    id BIGSERIAL PRIMARY KEY,
    transaction_id BIGINT NOT NULL,
    scale_id BIGINT NOT NULL,
    gross_weight DECIMAL(10, 2) NOT NULL,
    tare_weight DECIMAL(10, 2) NOT NULL,
    net_weight DECIMAL(10, 2) NOT NULL,
    load_cost DECIMAL(12, 2) NOT NULL,
    weighing_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_weighing_transaction FOREIGN KEY (transaction_id) REFERENCES transport_transactions(id),
    CONSTRAINT fk_weighing_scale FOREIGN KEY (scale_id) REFERENCES scales(id)
);

CREATE INDEX idx_trucks_branch_id ON trucks(branch_id);
CREATE INDEX idx_trucks_plate ON trucks(plate);

CREATE INDEX idx_scales_branch_id ON scales(branch_id);
CREATE INDEX idx_scales_code ON scales(code);

CREATE INDEX idx_transport_transactions_truck_id ON transport_transactions(truck_id);
CREATE INDEX idx_transport_transactions_grain_type_id ON transport_transactions(grain_type_id);
CREATE INDEX idx_transport_transactions_branch_id ON transport_transactions(branch_id);
CREATE INDEX idx_transport_transactions_status ON transport_transactions(status);

CREATE INDEX idx_weighings_transaction_id ON weighings(transaction_id);
CREATE INDEX idx_weighings_scale_id ON weighings(scale_id);
CREATE INDEX idx_weighings_weighing_time ON weighings(weighing_time);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);

INSERT INTO users (username, email, password, role, enabled)
VALUES (
           'admin',
           'admin@local.com',
           '$2a$10$L.UgJx/qrl.864AFi4jq0.Ly4p9CcsGCc4fgBuBcGi6rsh9rvZwFy',
           'ADMIN',
           true
       );

INSERT INTO branches (id, name, city, state, address)
VALUES (
           1,
           'Matriz',
           'Blumenau',
           'SC',
           'Rua Bahia, 1000'
       );

INSERT INTO grain_types (id, name, description, purchase_price_per_ton)
VALUES
    (1, 'SOJA', 'Soja padrão', 1500.00),
    (2, 'MILHO', 'Milho amarelo', 1200.00);

INSERT INTO trucks (id, plate, model, tare_weight, branch_id)
VALUES (
           1,
           'ABC3378',
           'Scania R450',
           8500.00,
           1
       );

INSERT INTO scales (id, code, location, branch_id, active)
VALUES (
           1,
           '60874355-daef-4ec7-83e0-0afe34b96de1',
           'Pátio principal',
           1,
           true
       );

INSERT INTO transport_transactions (id, truck_id, grain_type_id, branch_id, status)
VALUES (
           1,
           1,
           1,
           1,
           'IN_PROGRESS'
       );