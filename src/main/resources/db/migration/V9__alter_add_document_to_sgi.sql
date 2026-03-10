ALTER TABLE rces.plan_sgi
    ADD COLUMN document_id BINARY(16),
    ADD CONSTRAINT fk_plan_sgi_document FOREIGN KEY (document_id) REFERENCES rces.documents (id);

ALTER TABLE rces_history.plan_sgi_history
    ADD COLUMN document_id BINARY(16);