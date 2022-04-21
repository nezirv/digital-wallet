package com.hubpay.digitalwallet.response;

import com.hubpay.digitalwallet.model.Transaction;

import java.util.List;

public final class TransactionResponse {

    private List<Transaction> transactions;

    public TransactionResponse() {}

    public TransactionResponse(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }
}