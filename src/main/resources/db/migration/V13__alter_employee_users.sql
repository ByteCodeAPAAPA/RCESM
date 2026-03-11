SET FOREIGN_KEY_CHECKS = 0;

INSERT INTO subdivision (id, code, name, created_date, created_by)
VALUES (1, 'EMPTY', 'Заглушка', NOW(), 1);

INSERT INTO employees (id, name, password, role, is_active, chat_id, created_date, created_by, subdivision_id)
VALUES (1, 'system', 'system', 'SYSTEM', TRUE, 0, NOW(), 1, 1);

INSERT INTO employees (id, name, password, role, is_active, chat_id, created_date, created_by, subdivision_id)
VALUES (2, 'admin', 'admin', 'ADMIN', TRUE, 0, NOW(), 1, 1);

SET FOREIGN_KEY_CHECKS = 1;