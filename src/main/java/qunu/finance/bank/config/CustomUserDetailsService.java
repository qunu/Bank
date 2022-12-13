package qunu.finance.bank.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import qunu.finance.bank.repository.UserRepo;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo
                .findUserEntityByUsername(username)
                .orElseThrow(
                        () ->
                                new UsernameNotFoundException(
                                        format("Cant find User with username : %s", username)));
    }

}
