package com.hubpay.digitalwallet.model;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "WALLET_TRANSACTION")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String userName;

    private Integer walletId;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    private double amount;

    private Instant timestamp;

    protected Transaction(){}

    public Transaction(String userName, Integer walletId, TransactionType transactionType, double amount, Instant timestamp) {
        this.userName = userName;
        this.walletId = walletId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public Integer getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public Integer getWalletId() {
        return walletId;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public double getAmount() {
        return amount;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
