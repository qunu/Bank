package qunu.finance.bank.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import qunu.finance.bank.entity.TransactionEntity;
import qunu.finance.bank.enums.TransactionType;
import qunu.finance.bank.event.TransactionEvent;
import qunu.finance.bank.model.DepositRequest;
import qunu.finance.bank.model.TransferRequest;
import qunu.finance.bank.repository.TransactionRepo;

import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService {

    private final ApplicationEventPublisher eventPublisher;
    private final TransactionRepo transactionRepo;

    @EventListener
    public void process(TransactionEvent event) {
        TransactionEntity transaction = transactionRepo.save(event.getTransactionEntity());
        log.info("Saved Transaction {}", transaction);
    }

    public void save(DepositRequest deposit, String transactionRef) {

        TransactionEntity transactionEntity = TransactionEntity.builder()
                .transactionRef(transactionRef)
                .amount(deposit.getAmount())
                .accountId(deposit.getAccountId())
                .transactionType(TransactionType.DEPOSIT.getId())
                .createdAt(Instant.now())
                .build();

        TransactionEvent transactionEvent = new TransactionEvent(transactionEntity);
        log.info("Publish Transaction event {}", transactionEntity);
        eventPublisher.publishEvent(transactionEvent);
    }

    public void save(TransferRequest transferRequest, String transactionRef) {

        TransactionEntity transactionEntityOrigin = TransactionEntity.builder()
                .transactionRef(transactionRef)
                .amount(transferRequest.getAmount().negate())
                .accountId(transferRequest.getOriginAccountId())
                .transactionType(TransactionType.TRANSFER.getId())
                .createdAt(Instant.now())
                .build();

        TransactionEntity transactionEntityDest = TransactionEntity.builder()
                .transactionRef(transactionRef)
                .amount(transferRequest.getAmount())
                .accountId(transferRequest.getDestinationAccountId())
                .transactionType(TransactionType.TRANSFER.getId())
                .createdAt(Instant.now())
                .build();

        TransactionEvent transactionEventOrigin = new TransactionEvent(transactionEntityOrigin);
        log.info("Publish Transaction event {}", transactionEntityOrigin);
        eventPublisher.publishEvent(transactionEventOrigin);

        TransactionEvent transactionEventDest = new TransactionEvent(transactionEntityDest);
        log.info("Publish Transaction event {}", transactionEntityDest);
        eventPublisher.publishEvent(transactionEventDest);
    }

}
