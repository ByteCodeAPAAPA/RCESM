CREATE TABLE IF NOT EXISTS rces.employees
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(100) NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,
    mlm_node     VARCHAR(50)  NOT NULL,
    role         VARCHAR(255) NOT NULL,
    is_active    BOOLEAN      NOT NULL DEFAULT TRUE,
    chat_id      BIGINT       NOT NULL,
    version      BIGINT       NOT NULL DEFAULT 0,
    created_date TIMESTAMP,
    updated_date TIMESTAMP    NULL,
    created_by   BIGINT       NOT NULL DEFAULT 1,
    updated_by   BIGINT       NULL,

    INDEX idx_name (name),
    FOREIGN KEY (created_by) REFERENCES rces.employees (id),
    FOREIGN KEY (updated_by) REFERENCES rces.employees (id)
);

CREATE TABLE IF NOT EXISTS rces.subdivision
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    code         VARCHAR(255) NOT NULL UNIQUE,
    name         VARCHAR(255) NOT NULL UNIQUE,
    version      BIGINT       NOT NULL DEFAULT 0,
    created_date TIMESTAMP    NULL,
    updated_date TIMESTAMP    NULL,
    created_by   BIGINT       NOT NULL DEFAULT 1,
    updated_by   BIGINT       NULL,

    FOREIGN KEY (created_by) REFERENCES rces.employees (id),
    FOREIGN KEY (updated_by) REFERENCES rces.employees (id),

    INDEX idx_name (name)
);

CREATE TABLE IF NOT EXISTS rces.customerorder
(
    id           BINARY(16) PRIMARY KEY,
    str_code     VARCHAR(100) NOT NULL,
    version      BIGINT       NOT NULL DEFAULT 0,
    created_date TIMESTAMP    NULL,
    updated_date TIMESTAMP    NULL,
    created_by   BIGINT       NOT NULL DEFAULT 1,
    updated_by   BIGINT       NULL,

    INDEX idx_name (str_code),
    FOREIGN KEY (created_by) REFERENCES rces.employees (id),
    FOREIGN KEY (updated_by) REFERENCES rces.employees (id)
);

CREATE TABLE IF NOT EXISTS rces.documents
(
    id           BINARY(16) PRIMARY KEY,
    name         VARCHAR(150) NOT NULL UNIQUE,
    version      BIGINT       NOT NULL DEFAULT 0,
    created_date TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP    NULL ON UPDATE CURRENT_TIMESTAMP,
    created_by   BIGINT       NOT NULL DEFAULT 1,
    updated_by   BIGINT       NULL,

    INDEX idx_name (name),
    FOREIGN KEY (created_by) REFERENCES rces.employees (id),
    FOREIGN KEY (updated_by) REFERENCES rces.employees (id)
);

CREATE TABLE IF NOT EXISTS rces.document_files
(
    id             BINARY(16) PRIMARY KEY,
    base_file_name VARCHAR(250)                                                     NOT NULL,
    type           ENUM ('PDF', 'DOC', 'XLS', 'XLSX', 'DOCX', 'XML', 'TXT', 'JSON') NOT NULL,
    content        LONGBLOB                                                         NOT NULL,
    document_id    BINARY(16)                                                       NOT NULL
);


ALTER TABLE rces.document_files
    ADD CONSTRAINT fk_document_files_document
        FOREIGN KEY (document_id) REFERENCES rces.documents (id)
            ON DELETE CASCADE ON UPDATE CASCADE;

CREATE TABLE IF NOT EXISTS rces.requests
(
    id                BINARY(16) PRIMARY KEY,
    type_request      VARCHAR(50) NOT NULL,
    work_date         DATETIME,
    request_number    INT,
    employee_id       BIGINT,
    customer_order_id BINARY(16),
    reason            VARCHAR(50),
    qty               INT,
    mlm_node          VARCHAR(50),
    item              VARCHAR(50),
    status_id         VARCHAR(50),
    comment           VARCHAR(255),
    reason_wr         VARCHAR(255),
    description       TEXT,
    closed_date       DATETIME,
    closed_employee   BIGINT,
    chat_id           BIGINT,
    message_id        INT,
    score             VARCHAR(50),
    control           VARCHAR(255),
    comment_agreed    VARCHAR(255),
    title             VARCHAR(255),
    qty_rejected      INT                  DEFAULT 0,
    frozen            BOOLEAN              DEFAULT FALSE,
    version           BIGINT      NOT NULL DEFAULT 0,
    created_date      TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
    updated_date      TIMESTAMP   NULL ON UPDATE CURRENT_TIMESTAMP,
    created_by        BIGINT      NOT NULL DEFAULT 1,
    updated_by        BIGINT      NULL,

    FOREIGN KEY (employee_id) REFERENCES rces.employees (id),
    FOREIGN KEY (closed_employee) REFERENCES rces.employees (id),
    FOREIGN KEY (customer_order_id) REFERENCES rces.customerorder (id),
    FOREIGN KEY (created_by) REFERENCES rces.employees (id),
    FOREIGN KEY (updated_by) REFERENCES rces.employees (id)
);

CREATE TABLE IF NOT EXISTS rces.inconsistencies
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(255) NOT NULL UNIQUE,
    control_type VARCHAR(255) NOT NULL,
    version      BIGINT       NOT NULL DEFAULT 0,
    created_date TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP    NULL ON UPDATE CURRENT_TIMESTAMP,
    created_by   BIGINT       NOT NULL DEFAULT 1,
    updated_by   BIGINT       NULL,

    FOREIGN KEY (created_by) REFERENCES rces.employees (id),
    FOREIGN KEY (updated_by) REFERENCES rces.employees (id)
);

CREATE TABLE IF NOT EXISTS rces.plan_sgi
(
    id            BINARY(16) PRIMARY KEY,
    number        INT,
    workshop      VARCHAR(255),
    event         VARCHAR(255),
    actions       VARCHAR(499),
    department    VARCHAR(50),
    employee_id   BIGINT,
    parent_sgi_id BINARY(16),
    desired_date  DATE,
    plan_date     DATE,
    executions_id BINARY(16),
    color         VARCHAR(50),
    note          VARCHAR(1000),
    comment       VARCHAR(1000),
    agreed        BOOLEAN,
    version       BIGINT    NOT NULL DEFAULT 0,
    created_date  TIMESTAMP          DEFAULT CURRENT_TIMESTAMP,
    updated_date  TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    created_by    BIGINT    NOT NULL DEFAULT 1,
    updated_by    BIGINT    NULL,

    FOREIGN KEY (employee_id) REFERENCES rces.employees (id),
    FOREIGN KEY (parent_sgi_id) REFERENCES rces.plan_sgi (id),
    FOREIGN KEY (created_by) REFERENCES rces.employees (id),
    FOREIGN KEY (updated_by) REFERENCES rces.employees (id)
);

CREATE TABLE IF NOT EXISTS rces.fact_execution_sgi
(
    id             BINARY(16) PRIMARY KEY,
    sgi_id         BINARY(16) UNIQUE,
    execution_date DATE,
    report         VARCHAR(1000),
    version        BIGINT    NOT NULL DEFAULT 0,
    created_date   TIMESTAMP          DEFAULT CURRENT_TIMESTAMP,
    updated_date   TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    created_by     BIGINT    NOT NULL DEFAULT 1,
    updated_by     BIGINT    NULL,

    FOREIGN KEY (sgi_id) REFERENCES rces.plan_sgi (id),
    FOREIGN KEY (created_by) REFERENCES rces.employees (id),
    FOREIGN KEY (updated_by) REFERENCES rces.employees (id)
);


ALTER TABLE rces.plan_sgi
    ADD CONSTRAINT fk_plan_sgi_executions
        FOREIGN KEY (executions_id) REFERENCES rces.fact_execution_sgi (id);

CREATE TABLE IF NOT EXISTS rces.plan_spe
(
    number             INT AUTO_INCREMENT PRIMARY KEY,
    name               VARCHAR(255) NOT NULL,
    type               VARCHAR(255) NOT NULL,
    out_number         VARCHAR(255) NOT NULL,
    accuracy_class     VARCHAR(255),
    limit_measurement  VARCHAR(255),
    sub_division_id    BIGINT       NOT NULL,
    employee_id        BIGINT       NOT NULL,
    mark               VARCHAR(255),
    date_preparation   DATE,
    date_verification  DATE,
    certificate_number VARCHAR(255),
    periodicity        INT          NOT NULL,
    document_id        BINARY(16),
    status             VARCHAR(50)           DEFAULT 'NONE',
    color              VARCHAR(50)           DEFAULT 'NONE',
    version            BIGINT       NOT NULL DEFAULT 0,
    created_date       TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    updated_date       TIMESTAMP    NULL ON UPDATE CURRENT_TIMESTAMP,
    created_by         BIGINT       NOT NULL DEFAULT 1,
    updated_by         BIGINT       NULL,

    FOREIGN KEY (sub_division_id) REFERENCES rces.subdivision (id),
    FOREIGN KEY (employee_id) REFERENCES rces.employees (id),
    FOREIGN KEY (document_id) REFERENCES rces.documents (id),
    FOREIGN KEY (created_by) REFERENCES rces.employees (id),
    FOREIGN KEY (updated_by) REFERENCES rces.employees (id),

    INDEX idx_name (name)
);

CREATE TABLE IF NOT EXISTS rces.images
(
    id          BINARY(16) PRIMARY KEY,
    name        VARCHAR(255),
    data        LONGBLOB,
    request_id  BINARY(16),
    sgi_id      BINARY(16),
    sgim_id     BINARY(16),
    document_id BINARY(16),

    FOREIGN KEY (request_id) REFERENCES rces.requests (id),
    FOREIGN KEY (sgi_id) REFERENCES rces.fact_execution_sgi (id),
    FOREIGN KEY (sgim_id) REFERENCES rces.plan_sgi (id),
    FOREIGN KEY (document_id) REFERENCES rces.documents (id)
);

CREATE TABLE IF NOT EXISTS rces.inconsistencies
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(255) NOT NULL UNIQUE,
    control_type VARCHAR(255) NOT NULL,
    version      BIGINT       NOT NULL DEFAULT 0,
    created_date TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP    NULL ON UPDATE CURRENT_TIMESTAMP,
    created_by   BIGINT       NOT NULL DEFAULT 1,
    updated_by   BIGINT       NULL,

    FOREIGN KEY (created_by) REFERENCES rces.employees (id),
    FOREIGN KEY (updated_by) REFERENCES rces.employees (id),

    INDEX idx_name (name)
);

CREATE TABLE IF NOT EXISTS rces.request_incosistencies
(
    request_id      BINARY(16),
    incosistency_id BIGINT,

    FOREIGN KEY (request_id) REFERENCES rces.requests (id),
    FOREIGN KEY (incosistency_id) REFERENCES rces.inconsistencies (id)
);

#Устарело, но мастеру подземелий надо
CREATE TABLE IF NOT EXISTS rces.request_log
(
    id         BINARY(16) NOT NULL PRIMARY KEY,
    date       DATETIME(6),
    metadata   JSON,
    request_id BINARY(16),
    user_id    BIGINT,

    FOREIGN KEY (user_id) REFERENCES rces.employees (id),
    FOREIGN KEY (request_id) REFERENCES rces.requests (id)
);

CREATE TABLE IF NOT EXISTS rces.sgi_log
(
    id         BINARY(16) NOT NULL PRIMARY KEY,
    date       DATETIME(6),
    metadata   JSON,
    sgi_id BINARY(16),
    user_id    BIGINT,

    FOREIGN KEY (user_id) REFERENCES rces.employees (id),
    FOREIGN KEY (sgi_id) REFERENCES rces.plan_sgi (id)
);