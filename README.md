# 💰 Simple Wallet Service 💻

![Packagist](https://img.shields.io/packagist/l/doctrine/orm.svg) 

Wallet which will allow credit, debit of amount and cancellation of debit/credit transactions and shows current balance.

#### Setup project
1. Setup mysql database `wallet_db`
(given setup file will create database and tables with root user with no password.)
    ```shell
    sh setup.sh
    ```

1. Setup maven project `wallet-service`
    ```shell
    mvm clean install
    ```

#### Build project
```shell
mvn clean compile assembly:single
```
Above command will generate `jar` file with all dependencies.

#### Run project
Run project directly using `maven` with below command
```shell
mvn exec:java
```

OR

Run project from generated `jar` file using below command
```shell
java -cp target/wallet-service-1.0.0-SNAPSHOT-jar-with-dependencies.jar com.wallet.WalletService
```

## API

Download Postman collection for API

[![Run in Postman](https://run.pstmn.io/button.svg)](https://app.getpostman.com/run-collection/462b92ee5bb69bdfbf24)

## Database Structure

| Table Name | Description |
| --- | --- |
| transaction_status_types | Contains transaction status cycle like - <ul><li>NEW</li><li>SUCCESSFUL</li><li>FAILED</li><li>CANCELLED</li></ul> |
| transaction_types | Contains transaction types like - <ul><li>CREDIT</li><li>DEBIT</li></ul> |
| transactions | Contains transaction data with timestamp and status |
| transaction_status_logs | Contains transaction log data with timestamp, old and new status |
| wallet_status_types | Contains wallet status like - <ul><li>ACTIVE</li><li>INACTIVE</li></ul> |
| wallet_types | Contains wallet types with minimum balance like - <ul><li>REGULAR</li><li>OVERDRAFT</li></ul> |
| wallets | Contains wallet information with current balance |

*This project is licensed under the terms of the MIT license.*
