package com.hubpay.digitalwallet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hubpay.digitalwallet.model.Transaction;
import com.hubpay.digitalwallet.model.TransactionType;
import com.hubpay.digitalwallet.model.Wallet;
import com.hubpay.digitalwallet.repository.TransactionRepository;
import com.hubpay.digitalwallet.repository.WalletRepository;
import com.hubpay.digitalwallet.request.TransactionRequest;
import com.hubpay.digitalwallet.response.TransactionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import static com.hubpay.digitalwallet.model.TransactionType.CREDIT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class DigitalWalletApplicationTests {

    protected MockMvc mvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    WalletRepository walletRepository;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper.registerModule(new JavaTimeModule());
        transactionRepository.deleteAll();
        walletRepository.deleteAll();
    }

    @Test
    void canFetchTransactionsWithoutPagination() throws Exception {
        String uri = "/digital-wallet/transactions";

        List<Transaction> transactions = fetchTransactions(uri).getTransactions();

        assertThat(transactions.size()).isEqualTo(100);
        assertThat(transactions.get(0).getUserName()).isEqualTo("user_name0");
    }

    @Test
    void canFetchTransactionsWithPagination() throws Exception {
        String uri = "/digital-wallet/transactions?page=3&size=10";

        List<Transaction> transactions = fetchTransactions(uri).getTransactions();

        assertThat(transactions.size()).isEqualTo(10);
        assertThat(transactions.get(0).getUserName()).isEqualTo("user_name30");
    }

    @Test
    void canExecuteValidTransactions() {
        String uri = "/digital-wallet/transaction";

        Wallet testWallet = walletRepository.save(new Wallet(0d, "user_name", Instant.now()));

        IntStream.range(0, 10).forEach(value -> {
            TransactionRequest request = new TransactionRequest(
                    "user_name"+value, testWallet.getId(), CREDIT.name(), 10000);

            try {
                mvc.perform(
                        post(uri)
                                .contentType(APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isOk())
                        .andReturn();
            } catch (Exception e) {
                fail(e.getMessage(), e);
            }
        });

        List<Transaction> transactions = transactionRepository.findAll();
        assertThat(transactions.size()).isEqualTo(10);

        Wallet wallet = walletRepository.findById(testWallet.getId()).get();
        assertThat(wallet.getBalance()).isEqualTo(100000);
    }

    @Test
    void willRespondNotAcceptableWhenWalletExceptionThrown() {
        String uri = "/digital-wallet/transaction";

        Wallet testWallet = walletRepository.save(new Wallet(0d, "user_name", Instant.now()));

        TransactionRequest request = new TransactionRequest(
                "user_name", testWallet.getId(), CREDIT.name(), 999999);

        try {
            mvc.perform(
                    post(uri)
                            .contentType(APPLICATION_JSON_VALUE)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotAcceptable())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage(), e);
        }

    }

    private TransactionResponse fetchTransactions(String uri) throws Exception {
        List<Transaction> testTransactions = new ArrayList<>();
        IntStream.range(0, 100).forEach(value -> {
            testTransactions.add(new Transaction(
                    "user_name"+value,
                    100,
                    randomTransactionType(),
                    1000,
                    Instant.now()));
        });

        transactionRepository.saveAll(testTransactions);

        MvcResult mvcResult = mvc.perform(
                get(uri))
                .andExpect(status().isOk())
                .andReturn();;

        String content = mvcResult.getResponse().getContentAsString();
        return objectMapper.readValue(content,TransactionResponse.class);
    }

    private TransactionType randomTransactionType()  {
        return TransactionType.values()[new Random().nextInt(TransactionType.values().length)];
    }
}
