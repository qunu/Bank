package qunu.finance.bank.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import qunu.finance.bank.entity.AccountEntity;
import qunu.finance.bank.entity.TransactionEntity;
import qunu.finance.bank.enums.TransactionType;
import qunu.finance.bank.model.DepositRequest;
import qunu.finance.bank.model.ReportRequest;
import qunu.finance.bank.model.Role;
import qunu.finance.bank.repository.TransactionRepo;
import qunu.finance.bank.service.AccountService;
import qunu.finance.bank.service.ReportService;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = {Role.REPORT})
class RestEndpointReportTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @MockBean
    private ReportService reportService;


    @Test
    void given_report_when_NoAccountFound_then_BadRequest() throws Exception {
        long accountId = 1l;
        String body = getReportRequest(accountId, 0, 5);

        MvcResult createResult = mockMvc
                .perform(post("/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andReturn();

        assertEquals("Account ID not found : " + accountId, createResult.getResponse().getContentAsString());
        verify(accountService, times(1)).findAccount(Mockito.any());
        verify(reportService, times(0)).report(Mockito.any(), Mockito.any());
    }


    @Test
    void given_report_when_AccountFound_then_Ok() throws Exception {
        long accountId = 1l;
        int pageNo = 0;
        int pageSize = 5;

        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(accountId);
        accountEntity.setBalance(BigDecimal.ZERO);

        when(accountService.findAccount(accountId)).thenReturn(Optional.of(accountEntity));

        List<TransactionEntity> slicePage = new ArrayList<>();
        slicePage.add(TransactionEntity.builder().transactionRef("TXN_REF")
                .amount(BigDecimal.TEN)
                .transactionType(TransactionType.DEPOSIT.getId())
                .id(1l).build());

        Slice<TransactionEntity> slice = new SliceImpl<>(slicePage);
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.Direction.DESC, "createdAt");

        when(reportService.report(accountId, paging)).thenReturn(slice);

        String body = getReportRequest(accountId, pageNo, pageSize);

        MvcResult createResult = mockMvc
                .perform(post("/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();
        String bodySlice = objectMapper.writeValueAsString(slice);

        assertEquals(bodySlice, createResult.getResponse().getContentAsString());
        verify(accountService, times(1)).findAccount(Mockito.any());
        verify(reportService, times(1)).report(Mockito.any(), Mockito.any());
    }


    @Test
    void given_report_when_AccountFoundNoTransaction_then_Ok() throws Exception {
        long accountId = 1l;
        int pageNo = 0;
        int pageSize = 5;

        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(accountId);
        accountEntity.setBalance(BigDecimal.ZERO);

        when(accountService.findAccount(accountId)).thenReturn(Optional.of(accountEntity));

        List<TransactionEntity> slicePage = new ArrayList<>();

        Slice<TransactionEntity> slice = new SliceImpl<>(slicePage);
        Pageable paging = PageRequest.of(pageNo, pageSize, Sort.Direction.DESC, "createdAt");

        when(reportService.report(accountId, paging)).thenReturn(slice);

        String body = getReportRequest(accountId, pageNo, pageSize);

        MvcResult createResult = mockMvc
                .perform(post("/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals("No transactions found for Account ID : " + accountId, createResult.getResponse().getContentAsString());
        verify(accountService, times(1)).findAccount(Mockito.any());
        verify(reportService, times(1)).report(Mockito.any(), Mockito.any());
    }


    private String getReportRequest(long accountId, int pageNo, int pageSize) throws JsonProcessingException {
        ReportRequest reportRequest = new ReportRequest();
        reportRequest.setAccountId(accountId);
        reportRequest.setPageNo(pageNo);
        reportRequest.setPageSize(pageSize);

        ObjectMapper objectMapper = new ObjectMapper();
        String body = objectMapper.writeValueAsString(reportRequest);
        return body;
    }

}