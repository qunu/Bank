package qunu.finance.bank.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import qunu.finance.bank.entity.UserEntity;
import qunu.finance.bank.model.AuthenticationRequest;

import java.time.Instant;

import static java.util.stream.Collectors.joining;

@Tag(name = "Authentication")
@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthApi {

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;

    @PostMapping("login")
    public ResponseEntity login(@RequestBody AuthenticationRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            UserEntity user = (UserEntity) authentication.getPrincipal();

            Instant now = Instant.now();
            long expiry = 36000L;

            String scope = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(joining(" "));

            JwtClaimsSet claims = JwtClaimsSet.builder()
                    .issuer("qunu.bank")
                    .issuedAt(now)
                    .expiresAt(now.plusSeconds(expiry))
                    .subject(user.getId() + user.getUsername())
                    .claim("roles", scope)
                    .build();

            String token = this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

            log.info("AUTHORIZED {}", token);
            return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, token).body(user.getUsername());
        } catch (BadCredentialsException ex) {
            log.error("UNAUTHORIZED", ex);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

}
