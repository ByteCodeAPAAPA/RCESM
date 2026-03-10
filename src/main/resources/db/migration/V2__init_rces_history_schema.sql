CREATE TABLE IF NOT EXISTS rces_history.revinfo
(
    rev        BIGINT AUTO_INCREMENT PRIMARY KEY,
    revtstmp   BIGINT,
    changed_by BIGINT,
    CONSTRAINT fk_revinfo_employee FOREIGN KEY (changed_by) REFERENCES rces.employees (id)
);

CREATE TABLE IF NOT EXISTS rces_history.employees_history
(
    id        BIGINT NOT NULL,
    rev       BIGINT NOT NULL,
    revtype   TINYINT,
    name      VARCHAR(255),
    password  VARCHAR(255),
    mlm_node  VARCHAR(50),
    role      VARCHAR(255),
    is_active BOOLEAN,
    chat_id   BIGINT,

    PRIMARY KEY (id, rev),
    FOREIGN KEY (rev) REFERENCES rces_history.revinfo (rev)

);

CREATE TABLE IF NOT EXISTS rces_history.customerorder_history
(
    id       BINARY(16)   NOT NULL,
    rev      BIGINT       NOT NULL,
    revtype  TINYINT,
    str_code VARCHAR(100) NOT NULL,

    PRIMARY KEY (id, rev),
    FOREIGN KEY (rev) REFERENCES rces_history.revinfo (rev)
);

CREATE TABLE IF NOT EXISTS rces_history.inconsistencies_history
(
    id           BIGINT NOT NULL,
    rev          BIGINT NOT NULL,
    revtype      TINYINT,
    name         VARCHAR(255),
    control_type VARCHAR(255),

    PRIMARY KEY (id, rev),
    FOREIGN KEY (rev) REFERENCES rces_history.revinfo (rev)
);

CREATE TABLE IF NOT EXISTS rces_history.documents_history
(
    id      BINARY(16) NOT NULL,
    rev     BIGINT     NOT NULL,
    revtype TINYINT,
    name    VARCHAR(150),

    PRIMARY KEY (id, rev),
    FOREIGN KEY (rev) REFERENCES rces_history.revinfo (rev)
);

CREATE TABLE IF NOT EXISTS rces_history.plan_spe_history
(
    number             INT    NOT NULL,
    rev                BIGINT NOT NULL,
    revtype            TINYINT,
    name               VARCHAR(255),
    type               VARCHAR(255),
    out_number         VARCHAR(255),
    accuracy_class     VARCHAR(255),
    limit_measurement  VARCHAR(255),
    sub_division_id    BIGINT,
    employee_id        BIGINT,
    mark               VARCHAR(255),
    date_preparation   DATE,
    date_verification  DATE,
    certificate_number VARCHAR(255),
    periodicity        INT,
    document_id        BINARY(16),
    status             VARCHAR(50),
    color              VARCHAR(50),

    PRIMARY KEY (number, rev),
    FOREIGN KEY (rev) REFERENCES rces_history.revinfo (rev)
);

CREATE TABLE IF NOT EXISTS rces_history.requests_history
(
    id                BINARY(16),
    rev               BIGINT      NOT NULL,
    revtype           TINYINT,
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
    qty_rejected      INT     DEFAULT 0,
    frozen            BOOLEAN DEFAULT FALSE,

    PRIMARY KEY (id, rev),
    FOREIGN KEY (rev) REFERENCES rces_history.revinfo (rev)
);

CREATE TABLE IF NOT EXISTS rces_history.plan_sgi_history
(
    id            BINARY(16),
    rev           BIGINT NOT NULL,
    revtype       TINYINT,
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

    PRIMARY KEY (id, rev),
    FOREIGN KEY (rev) REFERENCES rces_history.revinfo (rev)
);

CREATE TABLE IF NOT EXISTS rces_history.fact_execution_sgi_history
(
    id             BINARY(16),
    rev            BIGINT NOT NULL,
    revtype        TINYINT,
    sgi_id         BINARY(16),
    execution_date DATE,
    report         VARCHAR(1000),

    PRIMARY KEY (id, rev),
    FOREIGN KEY (rev) REFERENCES rces_history.revinfo (rev)
);

CREATE TABLE IF NOT EXISTS rces_history.inconsistencies_history
(
    id           BIGINT       NOT NULL,
    rev          BIGINT       NOT NULL,
    revtype      TINYINT,
    name         VARCHAR(255) NOT NULL,
    control_type VARCHAR(255) NOT NULL,

    PRIMARY KEY (id, rev),
    FOREIGN KEY (rev) REFERENCES rces_history.revinfo (rev)
);

CREATE TABLE IF NOT EXISTS rces_history.subdivision_history
(
    id      BIGINT NOT NULL,
    rev     BIGINT NOT NULL,
    revtype TINYINT,
    code    VARCHAR(255),
    name    VARCHAR(255),

    PRIMARY KEY (id, rev),
    FOREIGN KEY (rev) REFERENCES rces_history.revinfo (rev)
);

CREATE TABLE IF NOT EXISTS rces_history.request_incosistencies_history
(
    id              BIGINT NOT NULL AUTO_INCREMENT,
    rev             BIGINT NOT NULL,
    revtype         TINYINT,
    request_id      BINARY(16),
    incosistency_id BIGINT,
    PRIMARY KEY (id, REV),
    FOREIGN KEY (REV) REFERENCES rces_history.revinfo (REV)
);