package qunu.finance.bank.repository;


import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import qunu.finance.bank.entity.TransactionEntity;


@Repository
public interface TransactionRepo extends CrudRepository<TransactionEntity, Long> {
    Slice<TransactionEntity> findAllByAccountId(Long accountId, Pageable pageable);
}