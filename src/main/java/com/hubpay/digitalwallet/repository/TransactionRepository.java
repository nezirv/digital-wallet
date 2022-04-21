package com.hubpay.digitalwallet.repository;

import com.hubpay.digitalwallet.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
}
