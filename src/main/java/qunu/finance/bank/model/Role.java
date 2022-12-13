package qunu.finance.bank.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role implements GrantedAuthority {

    public static final String REPORT = "REPORT";
    public static final String DEPOSIT = "DEPOSIT";
    public static final String TRANSFER = "TRANSFER";

    public static final String WITHDRAW = "WITHDRAW";
    public static final String BALANCE = "BALANCE";

    private String authority;

}
