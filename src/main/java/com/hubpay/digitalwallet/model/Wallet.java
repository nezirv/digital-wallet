package com.hubpay.digitalwallet.model;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "WALLET")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Double balance;

    private String userName;

    private Instant timestamp;

    public Wallet(){}

    public Wallet(Double balance, String userName, Instant timestamp) {
        this.balance = balance;
        this.userName = userName;
        this.timestamp = timestamp;
    }

    public Integer getId() {
        return id;
    }

    public Double getBalance() {
        return balance;
    }

    public String getUserName() {
        return userName;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
