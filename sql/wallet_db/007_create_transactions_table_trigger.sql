use wallet_db;

DROP TRIGGER IF EXISTS transaction_status_logs_trigger;

delimiter //
CREATE TRIGGER `transaction_status_logs_trigger` 
AFTER UPDATE ON `transactions` 
FOR EACH ROW
BEGIN
    INSERT INTO transaction_status_logs(transaction_id, wallet_id, old_status_type_id, new_status_type_id, status_updated_by, status_update_date) VALUES \
        (OLD.transaction_id, OLD.wallet_id, OLD.status_type_id, NEW.status_type_id, OLD.last_updated_by, OLD.last_update);
END;//
delimiter ;
