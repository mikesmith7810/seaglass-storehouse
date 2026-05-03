CREATE TABLE location (
    id   BIGSERIAL    PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE category (
    id   BIGSERIAL    PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE item (
    id          BIGSERIAL      PRIMARY KEY,
    description TEXT           NOT NULL,
    category_id BIGINT         NOT NULL REFERENCES category(id),
    location_id BIGINT         NOT NULL REFERENCES location(id),
    price       DECIMAL(10,2)  NOT NULL,
    height_cm   DECIMAL(10,2),
    width_cm    DECIMAL(10,2),
    depth_cm    DECIMAL(10,2),
    photo_url   VARCHAR(500),
    created_at  TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP      NOT NULL DEFAULT NOW()
);
