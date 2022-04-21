package com.hubpay.digitalwallet.controller;

import com.hubpay.digitalwallet.exception.WalletException;
import com.hubpay.digitalwallet.model.Transaction;
import com.hubpay.digitalwallet.request.TransactionRequest;
import com.hubpay.digitalwallet.response.TransactionResponse;
import com.hubpay.digitalwallet.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/digital-wallet")
public class TransactionController {

    private final Logger LOG = LoggerFactory.getLogger(TransactionController.class);

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping(path = "/transactions", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<TransactionResponse> getAllTransactions(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size) {

        Pageable pageable = page == null ? Pageable.unpaged() : PageRequest.of(page, size);

        List<Transaction> transactions = transactionService.getAllTransactions(pageable);

        return ResponseEntity.ok(new TransactionResponse(transactions));
    }


    @PostMapping(path = "/transaction", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> executeTransaction(@RequestBody TransactionRequest request) {

        transactionService.executeTransaction(request);

        return ResponseEntity.ok().build();
    }

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
    public String handleWalletException(WalletException ex) {
        LOG.debug(ex.getMessage(), ex);
        return ex.getMessage();
    }
}
