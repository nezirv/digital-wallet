package com.hubpay.digitalwallet.controller;

import com.hubpay.digitalwallet.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController controller;

    @Test
    public void getsAllTransactionsWithoutPaginationWhenNoParameters() {
        ArgumentCaptor<Pageable> pageableArgumentCaptor = ArgumentCaptor.forClass(Pageable.class);
        when(transactionService.getAllTransactions(pageableArgumentCaptor.capture())).thenReturn(Collections.emptyList());

        controller.getAllTransactions(null, null);

        assertThat(pageableArgumentCaptor.getValue()).isEqualTo(Pageable.unpaged());
    }

    @Test
    public void getsAllTransactionsWithPaginationWhenSuppliedParameters() {
        ArgumentCaptor<Pageable> pageableArgumentCaptor = ArgumentCaptor.forClass(Pageable.class);
        when(transactionService.getAllTransactions(pageableArgumentCaptor.capture())).thenReturn(Collections.emptyList());

        controller.getAllTransactions(1, 100);

        assertThat(pageableArgumentCaptor.getValue()).isEqualTo(PageRequest.of(1, 100));
    }
}