USE helpdesk_system;

-- Password: disha123
-- Sample Employee: Disha Pal (Female)
INSERT INTO users
(
    full_name,
    email,
    password_hash,
    role
)
VALUES
(
    'Disha Pal',
    'disha@helpdesk.com',
    '9cad7b49b42e98611466aa72c9abe5f23eddf7e31fa8df16657646212aba94e3',
    'EMPLOYEE'
)
ON DUPLICATE KEY UPDATE
    full_name = VALUES(full_name),
    password_hash = VALUES(password_hash),
    role = VALUES(role);

-- Password: agent123
INSERT INTO users
(
    full_name,
    email,
    password_hash,
    role
)
VALUES
(
    'Support Agent',
    'agent@helpdesk.com',
    'f44d1ac9bf0c69b083380b86dbdf3b73797150e3cca4820ac399f7917e607647',
    'AGENT'
)
ON DUPLICATE KEY UPDATE
    full_name = VALUES(full_name),
    password_hash = VALUES(password_hash),
    role = VALUES(role);

-- Password: admin123
INSERT INTO users
(
    full_name,
    email,
    password_hash,
    role
)
VALUES
(
    'System Administrator',
    'admin@helpdesk.com',
    '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9',
    'ADMIN'
)
ON DUPLICATE KEY UPDATE
    full_name = VALUES(full_name),
    password_hash = VALUES(password_hash),
    role = VALUES(role);