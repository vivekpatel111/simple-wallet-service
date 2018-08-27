USE wallet_db;

CREATE TABLE transaction_status_logs
(
    id                       INT            NOT NULL    AUTO_INCREMENT,
    transaction_id           CHAR(32)       NOT NULL,
    wallet_id                CHAR(16)       NOT NULL,
    old_status_type_id       INT            NOT NULL,
    new_status_type_id       INT            NOT NULL,
    status_updated_by        VARCHAR(150)   NOT NULL,
    status_update_date       DATETIME       NOT NULL,
    PRIMARY KEY(id),
    INDEX(wallet_id, transaction_id)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

ALTER TABLE transaction_status_logs ADD COLUMN creation_date DATETIME;
ALTER TABLE transaction_status_logs ADD COLUMN last_update DATETIME;
ALTER TABLE transaction_status_logs ADD COLUMN last_updated_by VARCHAR(150);
delimiter //
CREATE TRIGGER transaction_status_logs_insert_trigger BEFORE INSERT ON transaction_status_logs
FOR EACH ROW BEGIN
    SET NEW.creation_date = NOW();
    SET NEW.last_updated_by = USER();
    SET NEW.last_update = NOW();
END;//
CREATE TRIGGER transaction_status_logs_update_trigger BEFORE UPDATE ON transaction_status_logs
FOR EACH ROW
BEGIN
    SET NEW.last_updated_by = USER();
    SET NEW.last_update = NOW();
END;//
delimiter ;
