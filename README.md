# 💰 Simple Wallet Service 💻

![Packagist](https://img.shields.io/packagist/l/doctrine/orm.svg)

Wallet which will allow credit, debit of amount and cancellation of debit/credit transactions and shows current balance.

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

*This project is licensed under the terms of the MIT license.*
