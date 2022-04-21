package com.hubpay.digitalwallet.repository;

import com.hubpay.digitalwallet.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Integer> {
}