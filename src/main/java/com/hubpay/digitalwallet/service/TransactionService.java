package com.hubpay.digitalwallet.service;

import com.hubpay.digitalwallet.exception.WalletException;
import com.hubpay.digitalwallet.model.Transaction;
import com.hubpay.digitalwallet.model.TransactionType;
import com.hubpay.digitalwallet.model.Wallet;
import com.hubpay.digitalwallet.repository.TransactionRepository;
import com.hubpay.digitalwallet.repository.WalletRepository;
import com.hubpay.digitalwallet.request.TransactionRequest;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    @Value("${api.money.credit.max}")
    private long maxCreditAmount;

    @Value("${api.money.credit.min}")
    private long minCreditAmount;

    @Value("${api.money.debit.max}")
    private long maxDebitAmount;

    private final TransactionRepository transactionRepository;

    private final WalletRepository walletRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository,
                              WalletRepository walletRepository) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
    }

    public List<Transaction> getAllTransactions(Pageable pageable) {
        return IterableUtils.toList(transactionRepository.findAll(pageable));
    }

    public Wallet executeTransaction(TransactionRequest request) {
        Wallet wallet;
        switch (TransactionType.valueOf(request.getTransactionType())) {
            case CREDIT:
                wallet = creditWallet(request.getUserName(), request.getWalletId(), request.getAmount());
                break;
            case DEBIT:
                wallet = debitWallet(request.getUserName(), request.getWalletId(), request.getAmount());
                break;
            default:
                throw new WalletException("Invalid Transaction Type");
        }

        return wallet;
    }

    @Transactional
    protected Wallet creditWallet(String userName, Integer walletId, Double amount) {
        Optional<Wallet> wallet = walletRepository.findById(walletId);
        if (wallet.isEmpty()) {
            throw new WalletException(String.format("could not find wallet %s", walletId));
        }

        wallet.ifPresent(w -> {
            validateCredit(amount);
            transactionRepository.save(new Transaction(userName, walletId, TransactionType.CREDIT, amount, Instant.now()));
            w.setBalance(w.getBalance() + amount);
            w.setTimestamp(Instant.now());
            walletRepository.save(w);
        });

        return wallet.get();
    }

    protected Wallet debitWallet(String userName, Integer walletId, Double amount) {
        Optional<Wallet> wallet = walletRepository.findById(walletId);
        if (wallet.isEmpty()) {
            throw new WalletException(String.format("could not find wallet %s", walletId));
        }

        wallet.ifPresent(w -> {
            validateDebit(w, amount);
            transactionRepository.save(new Transaction(userName, walletId, TransactionType.DEBIT, amount, Instant.now()));
            w.setBalance(w.getBalance() - amount);
            w.setTimestamp(Instant.now());
            walletRepository.save(w);
        });

        return wallet.get();
    }

    private void validateCredit(Double amount) {
        if (amount < minCreditAmount) {
            throw new WalletException(String.format("credit amount below minimum %s", minCreditAmount));
        }
        if (amount > maxCreditAmount) {
            throw new WalletException(String.format("credit amount above maximum %s",  maxCreditAmount));
        }
    }

    private void validateDebit(Wallet wallet, Double amount) {
        if (amount > maxDebitAmount) {
            throw new WalletException(String.format("debit amount above maximum %s", maxDebitAmount));
        }
        if (wallet.getBalance() - amount < 0) {
            throw new WalletException("balance is negative");
        }
    }
}
