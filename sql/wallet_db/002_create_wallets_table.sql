USE wallet_db;

CREATE TABLE wallets
(
    id                       INT            NOT NULL    AUTO_INCREMENT,
    wallet_id                CHAR(16)       NOT NULL,
    wallet_type_id           INT            NOT NULL,
    status_type_id           INT            NOT NULL,
    balance                  DECIMAL(32,2)  NOT NULL DEFAULT 0,
    PRIMARY KEY(id),
    INDEX(wallet_id)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

ALTER TABLE wallets ADD COLUMN is_deleted BOOLEAN DEFAULT FALSE;
ALTER TABLE wallets ADD COLUMN creation_date DATETIME;
ALTER TABLE wallets ADD COLUMN last_update DATETIME;
ALTER TABLE wallets ADD COLUMN last_updated_by VARCHAR(150);
delimiter //
CREATE TRIGGER wallets_insert_trigger BEFORE INSERT ON wallets
FOR EACH ROW BEGIN
    SET NEW.creation_date = NOW();
    SET NEW.last_updated_by = USER();
    SET NEW.last_update = NOW();
    SET NEW.wallet_id = CONCAT("AG", CONCAT(LPAD(NEW.wallet_type_id, 3, "0"), LPAD((select auto_increment FROM information_schema.tables WHERE table_name = "wallets" AND table_schema = DATABASE()), 11, "0")));
END;//
CREATE TRIGGER wallets_update_trigger BEFORE UPDATE ON wallets
FOR EACH ROW
BEGIN
    SET NEW.last_updated_by = USER();
    SET NEW.last_update = NOW();
END;//
delimiter ;
