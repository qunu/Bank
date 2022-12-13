package qunu.finance.bank.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.verification.VerificationMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import qunu.finance.bank.entity.AccountEntity;
import qunu.finance.bank.model.DepositRequest;
import qunu.finance.bank.model.Role;
import qunu.finance.bank.service.AccountService;
import qunu.finance.bank.service.TransactionService;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = {Role.DEPOSIT})
class RestEndpointDepositTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @MockBean
    private TransactionService transactionService;

    @Test
    void given_deposit_when_NoAccountFound_then_BadRequest() throws Exception {
        long accountId = 1l;
        String body = getDepositRequest(accountId);

        MvcResult createResult = mockMvc
                .perform(post("/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertEquals("Account ID not found : " + accountId, createResult.getResponse().getContentAsString());
        verify(accountService, times(0)).save(Mockito.any());
        verify(transactionService, times(0)).save(Mockito.any(DepositRequest.class), Mockito.anyString());
    }


    @Test
    void given_deposit_when_AccountFound_then_OK() throws Exception {
        long accountId = 1l;
        String body = getDepositRequest(accountId);

        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(accountId);
        accountEntity.setBalance(BigDecimal.ZERO);

        when(accountService.findAccount(accountId)).thenReturn(Optional.of(accountEntity));
        when(accountService.save(Mockito.any())).thenReturn(accountEntity);

        MvcResult createResult = mockMvc
                .perform(post("/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertThat(createResult.getResponse().getContentAsString()).contains("Transaction Ref : ");
        Assertions.assertThat(createResult.getResponse().getContentAsString()).contains("| Balance : 100");
        verify(transactionService, times(1)).save(Mockito.any(DepositRequest.class), Mockito.anyString());
    }

    private static String getDepositRequest(long accountId) throws JsonProcessingException {
        DepositRequest depositRequest = new DepositRequest();
        depositRequest.setAccountId(accountId);
        depositRequest.setAmount(BigDecimal.valueOf(100l));
        depositRequest.setCurrency("ZAR");

        ObjectMapper objectMapper = new ObjectMapper();
        String body = objectMapper.writeValueAsString(depositRequest);
        return body;
    }
}