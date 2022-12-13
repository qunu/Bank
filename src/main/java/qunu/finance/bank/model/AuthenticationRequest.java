package qunu.finance.bank.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@RequiredArgsConstructor
public class AuthenticationRequest {
    @NotNull
    private String username;
    @NotNull
    private String password;
}
