package qunu.finance.bank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import qunu.finance.bank.entity.AccountEntity;
import qunu.finance.bank.repository.AccountRepo;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepo accountRepo;

    public Optional<AccountEntity> findAccount(Long accountId) {
        return accountRepo.findById(accountId);
    }

    public AccountEntity save(AccountEntity account) {
        try {
            return accountRepo.save(account);
        } catch (OptimisticLockingFailureException ex) {
            return null;
        }
    }
}
