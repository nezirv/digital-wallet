package com.hubpay.digitalwallet.service;

import com.hubpay.digitalwallet.exception.WalletException;
import com.hubpay.digitalwallet.model.Transaction;
import com.hubpay.digitalwallet.model.Wallet;
import com.hubpay.digitalwallet.repository.TransactionRepository;
import com.hubpay.digitalwallet.repository.WalletRepository;
import com.hubpay.digitalwallet.request.TransactionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;

import static com.hubpay.digitalwallet.model.TransactionType.CREDIT;
import static com.hubpay.digitalwallet.model.TransactionType.DEBIT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(transactionService, "maxCreditAmount", 1000);
        ReflectionTestUtils.setField(transactionService, "minCreditAmount", 10);
        ReflectionTestUtils.setField(transactionService, "maxDebitAmount", 1000);
    }

    @Test
    public void canCreditAccountWithinLimits() {
        Wallet testWallet1 = new Wallet(0d, "user_name", Instant.now());
        Wallet testWallet2 = new Wallet(100d, "user_name", Instant.now());

        Transaction transaction = new Transaction("user_name", 1, CREDIT, 100d, Instant.now());

        when(walletRepository.findById(any())).thenReturn(Optional.of(testWallet1));

        when(walletRepository.save(any())).thenReturn(testWallet2);
        when(transactionRepository.save(any())).thenReturn(transaction);

        TransactionRequest request = new TransactionRequest("user_name", 1, CREDIT.name(), 100d);
        Wallet wallet = transactionService.executeTransaction(request);

        assertThat(wallet.getBalance()).isEqualTo(100);
    }

    @Test
    public void canDebitAccountWithinLimits() {
        Wallet testWallet1 = new Wallet(500d, "user_name", Instant.now());
        Wallet testWallet2 = new Wallet(400d, "user_name", Instant.now());

        Transaction transaction = new Transaction("user_name", 1, DEBIT, 100d, Instant.now());

        when(walletRepository.findById(any())).thenReturn(Optional.of(testWallet1));

        when(walletRepository.save(any())).thenReturn(testWallet2);
        when(transactionRepository.save(any())).thenReturn(transaction);

        TransactionRequest request = new TransactionRequest("user_name", 1, DEBIT.name(), 100d);
        Wallet wallet = transactionService.executeTransaction(request);

        assertThat(wallet.getBalance()).isEqualTo(400d);
    }

    @Test
    public void whenWalletNotFoundThrowException() {
        Wallet testWallet = new Wallet(0d, "user_name", Instant.now());
        when(walletRepository.findById(any())).thenReturn(Optional.empty());

        WalletException thrown = assertThrows(WalletException.class, () -> transactionService.debitWallet("user_name", 1, 300d));

        assertThat(thrown.getMessage()).isEqualTo("could not find wallet 1");
    }

    @Test
    public void whenBalanceNegativeThrowException() {
        Wallet testWallet = new Wallet(0d, "user_name", Instant.now());
        when(walletRepository.findById(any())).thenReturn(Optional.of(testWallet));

        WalletException thrown = assertThrows(WalletException.class, () -> transactionService.debitWallet("user_name", 1, 300d));

        assertThat(thrown.getMessage()).isEqualTo("balance is negative");
    }

    @Test
    public void whenMaxCreditLimitExceededThrowException() {
        Wallet testWallet = new Wallet(0d, "user_name", Instant.now());
        when(walletRepository.findById(any())).thenReturn(Optional.of(testWallet));

        WalletException thrown = assertThrows(WalletException.class, () -> transactionService.creditWallet("user_name", 1, 50000d));

        assertThat(thrown.getMessage()).isEqualTo("credit amount above maximum 1000");
    }

    @Test
    public void whenCreditLimitBelowMinThrowException() {
        Wallet testWallet = new Wallet(0d, "user_name", Instant.now());
        when(walletRepository.findById(any())).thenReturn(Optional.of(testWallet));

        WalletException thrown = assertThrows(WalletException.class, () -> transactionService.creditWallet("user_name", 1, 1d));

        assertThat(thrown.getMessage()).isEqualTo("credit amount below minimum 10");
    }

}