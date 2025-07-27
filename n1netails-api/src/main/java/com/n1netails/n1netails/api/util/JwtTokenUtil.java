package com.n1netails.n1netails.api.util;

import com.n1netails.n1netails.api.model.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.stream.Collectors;

import static com.n1netails.n1netails.api.constant.ProjectSecurityConstant.EXPIRATION_TIME;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenUtil {

    private final JwtEncoder jwtEncoder;

    public String createToken(UserPrincipal userPrincipal) {
        log.info("== createToken");
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(EXPIRATION_TIME)) // EXPIRATION_TIME from constants
                .subject(userPrincipal.getUsername()) // email
                .claim("authorities", userPrincipal.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .claim("userId", (userPrincipal.getUser()).getUserId()) // Add userId to token
                .claim("id", userPrincipal.getId())
                .build();

        JwtEncoderParameters parameters = JwtEncoderParameters.from(claimsSet);
        log.info("getting JWT TOKEN");
        return jwtEncoder.encode(parameters).getTokenValue();
    }
}
