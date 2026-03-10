CREATE TABLE IF NOT EXISTS rces_machine
(
    id          BIGINT(16) PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(255),
    description VARCHAR(512),
    document_id BINARY(16),
    number      INT NOT NULL UNIQUE,
    FOREIGN KEY (document_id) REFERENCES rces.documents (id)
);

CREATE TABLE machine_admitted_employees
(
    machine_id  BIGINT(16) NOT NULL,
    employee_id BIGINT     NOT NULL,
    PRIMARY KEY (machine_id, employee_id),
    FOREIGN KEY (machine_id) REFERENCES rces_machine (id) ON DELETE CASCADE,
    FOREIGN KEY (employee_id) REFERENCES employees (id) ON DELETE CASCADE,
    INDEX idx_employee_id (employee_id)
);

CREATE TABLE IF NOT EXISTS machine_responsible_employees
(
    machine_id  BIGINT(16) NOT NULL,
    employee_id BIGINT     NOT NULL,
    PRIMARY KEY (machine_id, employee_id),
    FOREIGN KEY (machine_id) REFERENCES rces_machine (id) ON DELETE CASCADE,
    FOREIGN KEY (employee_id) REFERENCES employees (id) ON DELETE CASCADE,
    INDEX idx_employee_id (employee_id)
);
