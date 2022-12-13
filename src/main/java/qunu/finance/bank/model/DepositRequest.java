package qunu.finance.bank.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@RequiredArgsConstructor
public class DepositRequest {
    @NotNull
    private Long accountId;
    @NotNull
    @DecimalMin(value = "0", inclusive = false)
    private BigDecimal amount;
    @NotNull
    private String currency;
}
