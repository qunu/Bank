package qunu.finance.bank.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@RequiredArgsConstructor
public class ReportRequest {
    @NotNull
    private Long accountId;
    @NotNull
    @Min(value = 0, message = "Page number must be greater than or equal to 0")
    private int pageNo;
    @NotNull
    @Min(value = 1, message = "Page size must be greater than or equal to 1")
    private int pageSize;
}
