package qunu.finance.bank.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import qunu.finance.bank.handler.DepositHandler;
import qunu.finance.bank.handler.ReportHandler;
import qunu.finance.bank.handler.TransferHandler;
import qunu.finance.bank.model.*;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

@Tag(name = "Bank")
@RestController
@Slf4j
@RequiredArgsConstructor
public class RestEndpoint {

    private final DepositHandler depositHandler;
    private final TransferHandler transferHandler;
    private final ReportHandler reportHandler;

    @RolesAllowed(Role.DEPOSIT)
    @PostMapping("/deposit")
    public ResponseEntity deposit(@Valid @RequestBody DepositRequest deposit) {
        log.info("Deposit Call {}", deposit);
        ResponseEntity result = depositHandler.deposit(deposit);
        log.info("Deposit Call Response {}", result);
        return result;
    }

    @RolesAllowed(Role.TRANSFER)
    @PostMapping("/transfer")
    public ResponseEntity transfer(@Valid @RequestBody TransferRequest transfer) {
        log.info("Transfer Call {}", transfer);
        ResponseEntity result = transferHandler.transfer(transfer);
        log.info("Transfer Call {}", result);
        return result;

    }

    @RolesAllowed(Role.REPORT)
    @PostMapping("/report")
    public ResponseEntity report(@Valid @RequestBody ReportRequest report) {
        log.info("Report Call {}", report);
        Pageable paging = PageRequest.of(report.getPageNo(), report.getPageSize(), Sort.Direction.DESC, "createdAt");
        ResponseEntity result = reportHandler.report(report, paging);
        log.info("Report Call {}", result);
        return result;
    }

}
