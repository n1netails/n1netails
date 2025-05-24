package com.n1netails.n1netails.api.controller;

import com.n1netails.n1netails.api.exception.type.EmailExistException;
import com.n1netails.n1netails.api.exception.type.UserNotFoundException;
import com.n1netails.n1netails.api.model.UserPrincipal;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.model.request.UserLoginRequest;
import com.n1netails.n1netails.api.model.request.UserRegisterRequest;
import com.n1netails.n1netails.api.model.response.HttpErrorResponse;
import com.n1netails.n1netails.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import com.n1netails.n1netails.api.model.request.ChangePasswordRequest; // Added
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; // Added
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication; // Added
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.util.Map; // Added
import java.util.stream.Collectors;

import static com.n1netails.n1netails.api.constant.ControllerConstant.APPLICATION_JSON;
import static com.n1netails.n1netails.api.constant.ProjectSecurityConstant.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "Users Controller", description = "Operations related to Users")
@RestController
@RequestMapping(path = {"/api/user"}, produces = APPLICATION_JSON)
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    @Operation(
            summary = "Edit user profile",
            description = "Edit logged in user profile",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User updated successfully",
                            content = @Content(schema = @Schema(implementation = UsersEntity.class))),
                    @ApiResponse(responseCode = "401", description = "Authentication failed",
                            content = @Content(schema = @Schema(implementation = HttpErrorResponse.class)))
            }
    )
    @PostMapping(value = "/edit", consumes = APPLICATION_JSON)
    public ResponseEntity<UsersEntity> editUser(
            @RequestHeader(AUTHORIZATION) String authorizationHeader,
            @RequestBody UsersEntity user
    ) throws AccessDeniedException {
        try {
            String token = authorizationHeader.substring(TOKEN_PREFIX.length());
            String authEmail = jwtDecoder.decode(token).getSubject();
            log.info("auth email: {}", authEmail);
            UsersEntity editUser = userService.editUser(user);
            return new ResponseEntity<>(editUser, OK);
        } catch (JwtException e) {
            throw new AccessDeniedException(ACCESS_DENIED_MESSAGE);
        }
    }

    @Operation(
            summary = "Login user and return user details with JWT token",
            description = "Authenticates a user and returns the user object along with a JWT in the `Jwt-Token` header.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User authenticated successfully",
                            content = @Content(schema = @Schema(implementation = UsersEntity.class))),
                    @ApiResponse(responseCode = "401", description = "Authentication failed",
                            content = @Content(schema = @Schema(implementation = HttpErrorResponse.class)))
            }
    )
    @PostMapping(value = "/login", consumes = APPLICATION_JSON)
    public ResponseEntity<UsersEntity> login(@RequestBody UserLoginRequest user) {

        log.info("attempting user login");
        authenticate(user.getEmail(), user.getPassword());
        UsersEntity loginUser = userService.findUserByEmail(user.getEmail());
        UserPrincipal userPrincipal = new UserPrincipal(loginUser);
        HttpHeaders jwtHeader = setJwtHeader(userPrincipal);
        return new ResponseEntity<>(loginUser, jwtHeader, OK);
    }

    @Operation(
            summary = "Register new user and return user details with JWT token",
            description = "Registers a new user and returns the user object along with a JWT in the `Jwt-Token` header.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User registered successfully",
                            content = @Content(schema = @Schema(implementation = UsersEntity.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request or user already exists",
                            content = @Content(schema = @Schema(implementation = HttpErrorResponse.class)))
            }
    )
    @PostMapping(value = "/register", consumes = APPLICATION_JSON)
    public ResponseEntity<UsersEntity> register(@RequestBody UserRegisterRequest user) throws UserNotFoundException, EmailExistException {

        String password = user.getPassword();
        // Regex: at least 8 chars, 1 uppercase, 1 special char
        String passwordPattern = "^(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-={}\\[\\]:;'\"\\\\|,.<>/?]).{8,}$";
        if (password == null || !password.matches(passwordPattern)) {
            return ResponseEntity
                    .badRequest()
                    .body(null);
        }

        UsersEntity newUser = userService.register(user);
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

    @Operation(
            summary = "Change user password",
            description = "Allows an authenticated user to change their password.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Password changed successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request (e.g., current password mismatch, validation error)",
                            content = @Content(schema = @Schema(implementation = HttpErrorResponse.class))),
                    @ApiResponse(responseCode = "401", description = "User not authenticated",
                            content = @Content(schema = @Schema(implementation = HttpErrorResponse.class)))
            }
    )
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody @Valid ChangePasswordRequest request, Authentication authentication) {
        String userEmail = authentication.getName();
        userService.changePassword(userEmail, request.getCurrentPassword(), request.getNewPassword());
        // Consider creating a more structured success response if needed
        return ResponseEntity.ok().body(Map.of("message", "Password changed successfully."));
    }
}
