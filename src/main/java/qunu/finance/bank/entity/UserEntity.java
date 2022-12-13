package qunu.finance.bank.entity;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import qunu.finance.bank.model.Role;

import javax.persistence.*;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "users")
@Data
public class UserEntity implements UserDetails {

    private boolean enabled = true;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column
    private String roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<Role> rolesList = new ArrayList<>();

        String[] split = this.roles.split(",");
        Arrays.stream(split).forEach(role -> {
            Role roleAdd = new Role();
            roleAdd.setAuthority(role);
            rolesList.add(roleAdd);
        });

        return rolesList;
    }

    @Override
    public boolean isAccountNonExpired() {
        return enabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return enabled;

    }

    @Override
    public boolean isCredentialsNonExpired() {
        return enabled;
    }

}
