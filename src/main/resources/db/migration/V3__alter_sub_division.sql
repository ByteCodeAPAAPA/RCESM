INSERT INTO rces.subdivision (code, name, created_date, created_by) VALUES
    ('workShop1', 'Цех №1', NOW(), 1),
    ('workShop2', 'Цех №3', NOW(), 1),
    ('workShop3', 'Цех №4', NOW(), 1),
    ('workShop4', 'Цех №5', NOW(), 1),
    ('workShop5', 'Цех №6', NOW(), 1),
    ('workShop6', 'Цех №7', NOW(), 1),
    ('workShop7', 'Цех №8', NOW(), 1),
    ('workShop8', 'Участок комплектации', NOW(), 1),
    ('mechanic', 'ОГМ', NOW(), 1),
    ('builder', 'ОРС', NOW(), 1),
    ('YTO', 'УТО', NOW(), 1),
    ('OPiO', 'ОПиО', NOW(), 1),
    ('protection', 'ОТиПК', NOW(), 1),
    ('energy', 'ОГЭ', NOW(), 1);

#Employee
ALTER TABLE rces.employees ADD COLUMN subdivision_id BIGINT NULL;
UPDATE employees e
    JOIN rces.subdivision s ON e.mlm_node = s.code
SET e.subdivision_id = s.id
WHERE e.mlm_node IS NOT NULL;
ALTER TABLE rces.employees DROP COLUMN mlm_node;
ALTER TABLE rces.employees
    ADD CONSTRAINT fk_employee_subdivision
        FOREIGN KEY (subdivision_id) REFERENCES rces.subdivision(id);

ALTER TABLE rces_history.employees_history ADD COLUMN subdivision_id BIGINT NULL;
UPDATE rces_history.employees_history e
    JOIN rces.subdivision s ON e.mlm_node = s.code
SET e.subdivision_id = s.id
WHERE e.mlm_node IS NOT NULL;
ALTER TABLE rces_history.employees_history DROP COLUMN mlm_node;

#Requests
ALTER TABLE rces.requests ADD COLUMN subdivision_id BIGINT NULL;
UPDATE rces.requests e
    JOIN rces.subdivision s ON e.mlm_node = s.code
SET e.subdivision_id = s.id
WHERE e.mlm_node IS NOT NULL;
ALTER TABLE rces.requests DROP COLUMN mlm_node;
ALTER TABLE rces.requests MODIFY subdivision_id BIGINT NOT NULL;
ALTER TABLE rces.requests
    ADD CONSTRAINT fk_request_subdivision
        FOREIGN KEY (subdivision_id) REFERENCES rces.subdivision(id);

ALTER TABLE rces_history.requests_history ADD COLUMN subdivision_id BIGINT NULL;
UPDATE rces_history.requests_history e
    JOIN rces.subdivision s ON e.mlm_node = s.code
SET e.subdivision_id = s.id
WHERE e.mlm_node IS NOT NULL;
ALTER TABLE rces_history.requests_history DROP COLUMN mlm_node;
