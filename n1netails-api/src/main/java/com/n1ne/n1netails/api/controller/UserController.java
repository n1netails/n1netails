package com.n1ne.n1netails.api.controller;

import com.n1ne.n1netails.api.exception.EmailExistException;
import com.n1ne.n1netails.api.exception.UserNotFoundException;
import com.n1ne.n1netails.api.model.UserPrincipal;
import com.n1ne.n1netails.api.model.entity.Users;
import com.n1ne.n1netails.api.model.request.UserLoginRequest;
import com.n1ne.n1netails.api.model.request.UserRegisterRequest;
import com.n1ne.n1netails.api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.stream.Collectors;

import static com.n1ne.n1netails.api.constant.ProjectSecurityConstant.EXPIRATION_TIME;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = {"/api/user"})
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;

    @PostMapping("/login")
    public ResponseEntity<Users> login(@RequestBody UserLoginRequest user) {

        authenticate(user.getEmail(), user.getPassword());
        Users loginUser = userService.findUserByEmail(user.getEmail());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        HttpHeaders jwtHeader = setJwtHeader(userPrincipal);
        return new ResponseEntity<>(loginUser, jwtHeader, OK);
    }

    @PostMapping("/register")
    public ResponseEntity<Users> register(@RequestBody UserRegisterRequest user) throws UserNotFoundException, EmailExistException {

        Users newUser = userService.register(user);
        authenticate(user.getEmail(), user.getPassword());
        UserPrincipal userPrincipal = new UserPrincipal(newUser);
        HttpHeaders jwtHeader = setJwtHeader(userPrincipal);
        return new ResponseEntity<>(newUser, jwtHeader, OK);
    }

    private void authenticate(String email, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
    }

    private HttpHeaders setJwtHeader(UserPrincipal userPrincipal) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Jwt-Token", createToken(userPrincipal));
        return headers;
    }

    private String createToken(UserPrincipal userPrincipal) {
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(EXPIRATION_TIME))
                .subject(userPrincipal.getUsername())
                .claim("scope", createScope(userPrincipal))
                .build();

        JwtEncoderParameters parameters = JwtEncoderParameters.from(claimsSet);
        return jwtEncoder.encode(parameters).getTokenValue();
    }

    private String createScope(UserPrincipal userPrincipal) {
        return userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
    }
}
