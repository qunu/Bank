package qunu.finance.bank.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import qunu.finance.bank.entity.TransactionEntity;

@Getter
@RequiredArgsConstructor
public class TransactionEvent {
    private final TransactionEntity transactionEntity;
}
