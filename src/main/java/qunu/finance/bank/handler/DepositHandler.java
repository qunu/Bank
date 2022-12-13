package qunu.finance.bank.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import qunu.finance.bank.entity.AccountEntity;
import qunu.finance.bank.model.DepositRequest;
import qunu.finance.bank.service.AccountService;
import qunu.finance.bank.service.TransactionService;

import java.util.Optional;
import java.util.UUID;

import static java.lang.String.format;

@Component
@Slf4j
@RequiredArgsConstructor
public class DepositHandler {

    private final AccountService accountService;
    private final TransactionService transactionService;

    public ResponseEntity deposit(DepositRequest deposit) {
        log.info("Deposit handler {}", deposit);
        ResponseEntity response;

        Optional<AccountEntity> accountEntityOptional = accountService.findAccount(deposit.getAccountId());

        if (accountEntityOptional.isPresent()) {
            AccountEntity account = accountEntityOptional.get();
            account.setBalance(account.getBalance().add(deposit.getAmount()));
            AccountEntity saveAccount = accountService.save(account);
            if (saveAccount == null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(format("Account Conflict : %d", deposit.getAccountId()));
            }

            String transactionRef = UUID.randomUUID().toString();
            transactionService.save(deposit, transactionRef);

            log.info("Deposit success {} - {} - {}", deposit.getAccountId(), saveAccount.getBalance(), transactionRef);
            response = ResponseEntity.ok().body(format("Transaction Ref : %s | Balance : %s", transactionRef, saveAccount.getBalance()));
        } else {
            log.error("Account ID not found {}", deposit.getAccountId());
            response = ResponseEntity.badRequest().body(format("Account ID not found : %d", deposit.getAccountId()));
        }

        return response;
    }
}
