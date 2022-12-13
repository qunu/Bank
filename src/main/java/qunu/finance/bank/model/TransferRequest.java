package qunu.finance.bank.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@RequiredArgsConstructor
public class TransferRequest {
    @NotNull
    private Long originAccountId;
    @NotNull
    @DecimalMin(value = "0", inclusive = false)
    private BigDecimal amount;
    @NotNull
    private String currency;
    @NotNull
    private Long destinationAccountId;
}
