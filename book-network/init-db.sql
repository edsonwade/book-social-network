-- init-db.sql
CREATE TABLE IF NOT EXISTS role
(
    id                 SERIAL PRIMARY KEY,
    created_date       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    name               VARCHAR(255) NOT NULL
);

-- Insert the "USER" role if it doesn't exist
INSERT INTO role (name)
SELECT 'USER'
WHERE NOT EXISTS (SELECT 1 FROM role WHERE name = 'USER');
