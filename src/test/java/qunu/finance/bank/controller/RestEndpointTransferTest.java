package qunu.finance.bank.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
import qunu.finance.bank.model.TransferRequest;
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
@WithMockUser(roles = {Role.TRANSFER})
class RestEndpointTransferTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @MockBean
    private TransactionService transactionService;

    @Test
    void given_transfer_when_NoAccountsFound_then_BadRequest() throws Exception {
        String body = getTransferRequest(BigDecimal.valueOf(60l));

        MvcResult createResult = mockMvc
                .perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertEquals("Origin Account ID not found : 1", createResult.getResponse().getContentAsString());
        verify(accountService, times(1)).findAccount(Mockito.any());
        verify(accountService, times(0)).save(Mockito.any());
        verify(transactionService, times(0)).save(Mockito.any(DepositRequest.class), Mockito.anyString());
    }


    @Test
    void given_transfer_when_OriginAccountInsufficient_then_BadRequest() throws Exception {
        long accountIdOrig = 1l;
        long accountIdDest = 2l;

        String body = getTransferRequest(BigDecimal.valueOf(60l));

        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(accountIdOrig);
        accountEntity.setBalance(BigDecimal.ZERO);

        when(accountService.findAccount(accountIdOrig)).thenReturn(Optional.of(accountEntity));
        when(accountService.save(Mockito.any())).thenReturn(accountEntity);

        AccountEntity accountEntityDest = new AccountEntity();
        accountEntityDest.setId(accountIdDest);
        accountEntityDest.setBalance(BigDecimal.ZERO);

        when(accountService.findAccount(accountIdDest)).thenReturn(Optional.of(accountEntityDest));

        MvcResult createResult = mockMvc
                .perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andReturn();

        Assertions.assertThat(createResult.getResponse().getContentAsString()).contains("Insufficient funds : 0");
        verify(accountService, times(0)).save(Mockito.any());
        verify(transactionService, times(0)).save(Mockito.any(DepositRequest.class), Mockito.anyString());
    }

    @Test
    void given_transfer_when_OriginAccountRich_then_OK() throws Exception {
        long accountIdOrig = 1l;
        long accountIdDest = 2l;

        String body = getTransferRequest(BigDecimal.valueOf(60l));

        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(accountIdOrig);
        accountEntity.setBalance(BigDecimal.valueOf(100l));

        when(accountService.findAccount(accountIdOrig)).thenReturn(Optional.of(accountEntity));
        when(accountService.save(accountEntity)).thenReturn(accountEntity);

        AccountEntity accountEntityDest = new AccountEntity();
        accountEntityDest.setId(accountIdDest);
        accountEntityDest.setBalance(BigDecimal.ZERO);

        when(accountService.findAccount(accountIdDest)).thenReturn(Optional.of(accountEntityDest));
        when(accountService.save(accountEntityDest)).thenReturn(accountEntityDest);

        MvcResult createResult = mockMvc
                .perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertThat(createResult.getResponse().getContentAsString()).contains("Transaction Ref : ");
        Assertions.assertThat(createResult.getResponse().getContentAsString()).contains("| Balance : 40");
        verify(accountService, times(2)).save(Mockito.any());
        verify(transactionService, times(1)).save(Mockito.any(TransferRequest.class), Mockito.anyString());
        assertEquals(BigDecimal.valueOf(40l), accountEntity.getBalance());
        assertEquals(BigDecimal.valueOf(60l), accountEntityDest.getBalance());
    }


    private String getTransferRequest(BigDecimal amount) throws JsonProcessingException {
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setAmount(amount);
        transferRequest.setCurrency("ZAR");
        transferRequest.setOriginAccountId(1l);
        transferRequest.setDestinationAccountId(2l);

        ObjectMapper objectMapper = new ObjectMapper();
        String body = objectMapper.writeValueAsString(transferRequest);
        return body;

    }

}
