CREATE TABLE IF NOT EXISTS rces.state_standard_documents
(
    id                BINARY(16) PRIMARY KEY,
    name              VARCHAR(255) NOT NULL,
    type              VARCHAR(255) NOT NULL,
    date_verification DATE,
    document_id       BINARY(16),
    comment           TEXT,
    color             VARCHAR(50)           DEFAULT 'NONE',
    version           BIGINT       NOT NULL DEFAULT 0,
    created_date      TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    updated_date      TIMESTAMP    NULL ON UPDATE CURRENT_TIMESTAMP,
    created_by        BIGINT       NOT NULL DEFAULT 1,
    updated_by        BIGINT       NULL,

    FOREIGN KEY (document_id) REFERENCES rces.documents (id),
    FOREIGN KEY (created_by) REFERENCES rces.employees (id),
    FOREIGN KEY (updated_by) REFERENCES rces.employees (id),

    INDEX idx_name (name)
);

CREATE TABLE IF NOT EXISTS rces.standard_document_references
(
    document_id            BINARY(16),
    referenced_document_id BINARY(16),
    PRIMARY KEY (document_id, referenced_document_id),
    FOREIGN KEY (document_id) REFERENCES state_standard_documents (id),
    FOREIGN KEY (referenced_document_id) REFERENCES state_standard_documents (id)
);

CREATE TABLE IF NOT EXISTS rces_history.state_standard_documents_history
(
    id                BINARY(16) NOT NULL,
    name              VARCHAR(255),
    type              VARCHAR(255),
    date_verification DATE,
    document_id       BINARY(16),
    comment           TEXT,
    color             VARCHAR(50),

    rev               BIGINT     NOT NULL,
    revtype           TINYINT,

    PRIMARY KEY (id, rev),
    FOREIGN KEY (rev) REFERENCES rces_history.revinfo (rev)
);