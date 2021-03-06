DROP TABLE IF EXISTS WALLET_TRANSACTION;

CREATE TABLE WALLET_TRANSACTION (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_name VARCHAR(50) NOT NULL,
    wallet_id INT NOT NULL,
    transaction_type VARCHAR(10) NOT NULL,
    amount DOUBLE NOT NULL,
    timestamp TIMESTAMP NOT NULL
);

CREATE TABLE WALLET (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_name VARCHAR(50) NOT NULL,
    balance DOUBLE NOT NULL DEFAULT 0,
    timestamp TIMESTAMP NOT NULL
);