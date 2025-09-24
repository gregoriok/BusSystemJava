-- Flyway Migration: V1__create-all-tables.sql

-- Tabela: lines (Linhas de ônibus)
CREATE TABLE lines (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

-- Tabela: stops (Pontos de Parada)
CREATE TABLE stops (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    latitude NUMERIC(10, 8) NOT NULL,
    longitude NUMERIC(11, 8) NOT NULL,
    description TEXT
);

-- Tabela: bus (Ônibus)
CREATE TABLE bus (
    id UUID PRIMARY KEY,
    license_plate VARCHAR(10) NOT NULL UNIQUE,
    capacity INT,
    current_latitude NUMERIC(10, 8),
    current_longitude NUMERIC(11, 8),
    last_location_update TIMESTAMP WITH TIME ZONE,
    type VARCHAR(25) NOT NULL,
    number INT NOT NULL,
    current_line_id UUID,
    next_stop_id UUID,

    CONSTRAINT fk_bus_current_line
        FOREIGN KEY (current_line_id)
        REFERENCES lines (id),

    CONSTRAINT fk_bus_next_stop
        FOREIGN KEY (next_stop_id)
        REFERENCES stops (id)
);

-- Tabela de Junção: line_stops (Roteiro das Paradas)
CREATE TABLE line_stops (
    line_id UUID,
    stop_id UUID,
    sequence INT NOT NULL,

    PRIMARY KEY (line_id, stop_id),

    CONSTRAINT fk_line_stops_line
        FOREIGN KEY (line_id)
        REFERENCES lines (id),

    CONSTRAINT fk_line_stops_stop
        FOREIGN KEY (stop_id)
        REFERENCES stops (id)
);