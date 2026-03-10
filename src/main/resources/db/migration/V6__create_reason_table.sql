CREATE TABLE IF NOT EXISTS rces.reason
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    text         VARCHAR(255),
    type_request VARCHAR(50)
);