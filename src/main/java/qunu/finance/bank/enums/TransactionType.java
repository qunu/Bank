package qunu.finance.bank.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TransactionType {
    DEPOSIT(1),
    TRANSFER(2);

    private final int id;
}
