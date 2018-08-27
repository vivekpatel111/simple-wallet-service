USE wallet_db;

CREATE TABLE transaction_types
(
    id                  INT         NOT NULL    AUTO_INCREMENT,
    name                CHAR(30)    NOT NULL,
    PRIMARY KEY(id),
    UNIQUE KEY(name)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

ALTER TABLE transaction_types ADD COLUMN is_deleted BOOLEAN DEFAULT FALSE;
ALTER TABLE transaction_types ADD COLUMN creation_date DATETIME;
ALTER TABLE transaction_types ADD COLUMN last_update DATETIME;
ALTER TABLE transaction_types ADD COLUMN last_updated_by VARCHAR(150);
delimiter //
CREATE TRIGGER transaction_types_insert_trigger BEFORE INSERT ON transaction_types
FOR EACH ROW BEGIN
    SET NEW.creation_date = NOW();
    SET NEW.last_updated_by = USER();
    SET NEW.last_update = NOW();
END;//
CREATE TRIGGER transaction_types_update_trigger BEFORE UPDATE ON transaction_types
FOR EACH ROW
BEGIN
    SET NEW.last_updated_by = USER();
    SET NEW.last_update = NOW();
END;//
delimiter ;

insert into transaction_types set name = "CREDIT";
insert into transaction_types set name = "DEBIT";