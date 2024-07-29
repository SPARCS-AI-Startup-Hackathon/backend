CREATE TABLE IF NOT EXISTS authority (
    authority_name VARCHAR(50) NOT NULL,
    PRIMARY KEY (authority_name)
);
INSERT INTO authority (authority_name) VALUES ('ROLE_USER');
INSERT INTO authority (authority_name) VALUES ('ROLE_ADMIN');