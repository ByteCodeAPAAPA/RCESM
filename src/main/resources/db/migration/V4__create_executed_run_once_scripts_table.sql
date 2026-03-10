CREATE TABLE rces_history.executed_run_once_scripts
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    name           VARCHAR(255) NOT NULL,
    execution_date TIMESTAMP,

    UNIQUE KEY uk_executed_run_once_scripts_name (name),
    INDEX idx_name (name)
);