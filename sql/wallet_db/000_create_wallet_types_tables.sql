USE wallet_db;

CREATE TABLE wallet_types
(
    id                          INT             NOT NULL    AUTO_INCREMENT,
    name                        CHAR(30)        NOT NULL,
    minimum_balance             DECIMAL(32,2)   DEFAULT 0,
    PRIMARY KEY(id),
    UNIQUE KEY(name)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4;

ALTER TABLE wallet_types ADD COLUMN is_deleted BOOLEAN DEFAULT FALSE;
ALTER TABLE wallet_types ADD COLUMN creation_date DATETIME;
ALTER TABLE wallet_types ADD COLUMN last_update DATETIME;
ALTER TABLE wallet_types ADD COLUMN last_updated_by VARCHAR(150);
delimiter //
CREATE TRIGGER wallet_types_insert_trigger BEFORE INSERT ON wallet_types
FOR EACH ROW BEGIN
    SET NEW.creation_date = NOW();
    SET NEW.last_updated_by = USER();
    SET NEW.last_update = NOW();
END;//
CREATE TRIGGER wallet_types_update_trigger BEFORE UPDATE ON wallet_types
FOR EACH ROW
BEGIN
    SET NEW.last_updated_by = USER();
    SET NEW.last_update = NOW();
END;//
delimiter ;

insert into wallet_types set name = "REGULAR", minimum_balance = 0.0;
insert into wallet_types set name = "OVERDRAFT", minimum_balance = -50000;