package qunu.finance.bank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import qunu.finance.bank.entity.TransactionEntity;
import qunu.finance.bank.repository.TransactionRepo;


@Service
@RequiredArgsConstructor
public class ReportService {

    private final TransactionRepo transactionRepo;

    public Slice<TransactionEntity> report(Long accountId, Pageable pageable) {
        return transactionRepo.findAllByAccountId(accountId, pageable);
    }
}
