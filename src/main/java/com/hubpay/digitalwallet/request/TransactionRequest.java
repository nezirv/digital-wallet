package com.hubpay.digitalwallet.request;

public final class TransactionRequest {

    private final String userName;
    private final Integer walletId;
    private final String transactionType;
    private final double amount;

    public TransactionRequest(String userName, Integer walletId, String transactionType, double amount) {
        this.userName = userName;
        this.walletId = walletId;
        this.transactionType = transactionType;
        this.amount = amount;
    }

    public String getUserName() {
        return userName;
    }

    public Integer getWalletId() {
        return walletId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public double getAmount() {
        return amount;
    }
}
