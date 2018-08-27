USE wallet_db;

CREATE TABLE transactions
(
    id                       INT            NOT NULL    AUTO_INCREMENT,
    transaction_id           CHAR(32)       NOT NULL,
    wallet_id                CHAR(16)       NOT NULL,
    transaction_type_id      INT            NOT NULL,
    status_type_id           INT            NOT NULL,
    amount                   DECIMAL(32,2)  NOT NULL,
    log_message              TEXT,
    PRIMARY KEY(id),
    INDEX(wallet_id, transaction_id)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

ALTER TABLE transactions ADD COLUMN is_deleted BOOLEAN DEFAULT FALSE;
ALTER TABLE transactions ADD COLUMN creation_date DATETIME;
ALTER TABLE transactions ADD COLUMN last_update DATETIME;
ALTER TABLE transactions ADD COLUMN last_updated_by VARCHAR(150);
delimiter //
CREATE TRIGGER transactions_insert_trigger BEFORE INSERT ON transactions
FOR EACH ROW BEGIN
    SET NEW.creation_date = NOW();
    SET NEW.last_updated_by = USER();
    SET NEW.last_update = NOW();
    SET NEW.transaction_id = MD5(CONCAT("AG", CONCAT(NEW.wallet_id, CONCAT(CONCAT(DATE(NOW()), NEW.transaction_type_id), (select auto_increment FROM information_schema.tables WHERE table_name = "transactions" AND table_schema = DATABASE())))));
END;//
CREATE TRIGGER transactions_update_trigger BEFORE UPDATE ON transactions
FOR EACH ROW
BEGIN
    SET NEW.last_updated_by = USER();
    SET NEW.last_update = NOW();
END;//
delimiter ;
