package qunu.finance.bank.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import qunu.finance.bank.entity.AccountEntity;
import qunu.finance.bank.model.TransferRequest;
import qunu.finance.bank.service.AccountService;
import qunu.finance.bank.service.TransactionService;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static java.lang.String.format;

@Component
@Slf4j
@RequiredArgsConstructor
public class TransferHandler {

    private final AccountService accountService;

    private final TransactionService transactionService;

    public ResponseEntity transfer(TransferRequest transfer) {
        log.info("Transfer handler {}", transfer);
        ResponseEntity response;

        Optional<AccountEntity> originAccountOptional = accountService.findAccount(transfer.getOriginAccountId());
        if (!originAccountOptional.isPresent()) {
            log.error("Origin Account ID not found {}", transfer.getOriginAccountId());
            return ResponseEntity.badRequest().body(format("Origin Account ID not found : %d", transfer.getOriginAccountId()));
        } else if (originAccountOptional.get().getBalance().subtract(transfer.getAmount()).compareTo(BigDecimal.ZERO) < 0) {
            log.error("Origin Account Insufficient funds calculated {} - {}", originAccountOptional.get().getBalance(), transfer.getAmount());
            return ResponseEntity.badRequest().body(format("Insufficient funds : %s", originAccountOptional.get().getBalance().toString()));
        }

        Optional<AccountEntity> destAccountOptional = accountService.findAccount(transfer.getDestinationAccountId());

        if (destAccountOptional.isPresent()) {
            AccountEntity originAccount = originAccountOptional.get();
            AccountEntity destAccount = destAccountOptional.get();

            originAccount.setBalance(originAccount.getBalance().subtract(transfer.getAmount()));
            destAccount.setBalance(destAccount.getBalance().add(transfer.getAmount()));

            AccountEntity resultOrigin = accountService.save(originAccount);
            if (resultOrigin == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(format("Account Conflict : %d", originAccount.getId()));
            }

            AccountEntity resultDestination = accountService.save(destAccount);
            if (resultDestination == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(format("Account Destination Conflict : %d", originAccount.getId()));
            }


            String transactionRef = UUID.randomUUID().toString();
            transactionService.save(transfer, transactionRef);

            log.info("Transfer success {} : {} - {} : {} - {}", resultOrigin.getId(), resultOrigin.getBalance(),
                    resultDestination.getId(), resultDestination.getBalance(), transactionRef);
            response = ResponseEntity.ok().body(format("Transaction Ref : %s | Balance : %s", transactionRef, resultOrigin.getBalance()));
        } else {
            log.error("Destination Account ID not found {}", transfer.getDestinationAccountId());
            response = ResponseEntity.badRequest().body("Destination Account ID not found");
        }

        return response;
    }
}
