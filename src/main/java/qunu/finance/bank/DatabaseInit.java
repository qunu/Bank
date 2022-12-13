package qunu.finance.bank;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import qunu.finance.bank.entity.AccountEntity;
import qunu.finance.bank.entity.TransactionEntity;
import qunu.finance.bank.entity.UserEntity;
import qunu.finance.bank.enums.TransactionType;
import qunu.finance.bank.repository.AccountRepo;
import qunu.finance.bank.repository.TransactionRepo;
import qunu.finance.bank.repository.UserRepo;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DatabaseInit implements ApplicationListener<ApplicationReadyEvent> {

    private final List<String> usernames = List.of("ryan", "laura");
    private final List<String> usernamesReport = List.of("ethan", "willow");

    private static final String PASSWORD = "adminPassword";

    private final PasswordEncoder passwordEncoder;

    private final UserRepo userRepo;

    private final AccountRepo accountRepo;

    private final TransactionRepo transactionRepo;


    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {

        usernames.stream().forEach(username -> {

            UserEntity userEntity = new UserEntity();
            userEntity.setUsername(username);
            userEntity.setPassword(passwordEncoder.encode(PASSWORD));
            userEntity.setRoles("REPORT,DEPOSIT,TRANSFER");
            userEntity.setCreatedAt(Instant.now());

            UserEntity saveUser = userRepo.save(userEntity);

            AccountEntity account = new AccountEntity();
            account.setUserId(saveUser.getId());
            account.setCreatedAt(Instant.now());
            account.setBalance(BigDecimal.TEN);

            AccountEntity saveAccount = accountRepo.save(account);

            TransactionEntity transactionEntity = getTransactionEntity(saveAccount.getId(), "a");
            transactionRepo.save(transactionEntity);
            transactionEntity = getTransactionEntity(saveAccount.getId(), "b");
            transactionRepo.save(transactionEntity);
            transactionEntity = getTransactionEntity(saveAccount.getId(), "c");
            transactionRepo.save(transactionEntity);
            transactionEntity = getTransactionEntity(saveAccount.getId(), "d");
            transactionRepo.save(transactionEntity);
            transactionEntity = getTransactionEntity(saveAccount.getId(), "e");
            transactionRepo.save(transactionEntity);
        });

        usernamesReport.stream().forEach(username -> {
            UserEntity userEntity = new UserEntity();
            userEntity.setUsername(username);
            userEntity.setPassword(passwordEncoder.encode(PASSWORD));
            userEntity.setRoles("REPORT");
            userEntity.setCreatedAt(Instant.now());

            UserEntity saveUser = userRepo.save(userEntity);
        });

    }

    private TransactionEntity getTransactionEntity(long accountId, String transactionRef) {
        return TransactionEntity.builder()
                .createdAt(Instant.now())
                .transactionRef(transactionRef)
                .transactionType(TransactionType.DEPOSIT.getId())
                .amount(BigDecimal.valueOf(2l))
                .accountId(accountId)
                .build();
    }
}
