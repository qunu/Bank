package qunu.finance.bank.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import qunu.finance.bank.entity.AccountEntity;
import qunu.finance.bank.entity.TransactionEntity;
import qunu.finance.bank.model.ReportRequest;
import qunu.finance.bank.service.AccountService;
import qunu.finance.bank.service.ReportService;

import java.util.Optional;

import static java.lang.String.format;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReportHandler {

    private final ReportService reportService;

    private final AccountService accountService;

    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public ResponseEntity report(ReportRequest report, Pageable pageable) {
        log.info("Report handler {}", report);
        ResponseEntity response;

        Optional<AccountEntity> accountEntityOptional = accountService.findAccount(report.getAccountId());

        if (accountEntityOptional.isPresent()) {
            Slice<TransactionEntity> reportSlice = reportService.report(report.getAccountId(), pageable);

            if (!reportSlice.getContent().isEmpty()) {
                String body = null;
                try {
                    body = objectMapper.writeValueAsString(reportSlice);
                } catch (JsonProcessingException e) {
                    log.error("Error converting object to json", e);
                    return ResponseEntity.internalServerError().build();
                }

                log.info("Report success {}", report.getAccountId());
                response = ResponseEntity.ok().body(body);
            } else {
                log.info("No Transactions found {}", report.getAccountId());
                response = ResponseEntity.ok().body(format("No transactions found for Account ID : %d", report.getAccountId()));
            }
        } else {
            log.error("Account ID not found {}", report.getAccountId());
            response = ResponseEntity.badRequest().body(format("Account ID not found : %d", report.getAccountId()));
        }

        return response;
    }
}
