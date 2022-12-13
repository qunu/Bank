package qunu.finance.bank.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

import java.time.Instant;

@Entity
@Table(name = "transaction")
@Builder
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Column(nullable = false)
    private Long accountId;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private int transactionType;

    @Column(nullable = false)
    private String transactionRef;

}
