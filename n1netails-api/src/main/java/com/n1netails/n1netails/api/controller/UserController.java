package com.n1netails.n1netails.api.controller;

import com.n1netails.n1netails.api.exception.type.EmailExistException;
import com.n1netails.n1netails.api.exception.type.PasswordRegexException;
import com.n1netails.n1netails.api.exception.type.EmailExistException;
import com.n1netails.n1netails.api.exception.type.PasswordRegexException;
import com.n1netails.n1netails.api.exception.type.UserNotFoundException;
import com.n1netails.n1netails.api.model.UserPrincipal;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.model.request.UpdateUserRoleRequestDto;
import com.n1netails.n1netails.api.model.request.UserLoginRequest;
import com.n1netails.n1netails.api.model.request.UserRegisterRequest;
import com.n1netails.n1netails.api.model.response.HttpErrorResponse;
import com.n1netails.n1netails.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.time.Instant;
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

            if (authEmail.equals(user.getEmail())) {
                log.info("auth email: {}", authEmail);
                UsersEntity editUser = userService.editUser(user);
                return new ResponseEntity<>(editUser, OK);
            } else {
                throw new AccessDeniedException(ACCESS_DENIED_MESSAGE);
            }
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
    public ResponseEntity<UsersEntity> register(@RequestBody UserRegisterRequest user) throws UserNotFoundException, EmailExistException, PasswordRegexException {

        String password = user.getPassword();
        if (password == null || !password.matches(PASSWORD_REGEX)) {
            throw new PasswordRegexException(PASSWORD_REGEX_EXCEPTION_MESSAGE);
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

    @PutMapping("/{userId}/role")
    @PreAuthorize("hasAuthority('user:super')") // 'user:super' is unique to SUPER_ADMIN_AUTHORITIES
    public ResponseEntity<?> updateUserRole(@PathVariable Long userId, @Valid @RequestBody UpdateUserRoleRequestDto roleDto) {
        try {
            UsersEntity updatedUser = userService.updateUserRole(userId, roleDto.getRoleName());
            return ResponseEntity.ok(updatedUser);
        } catch (UserNotFoundException e) {
            // Consider using @ControllerAdvice for exception handling
            // Returning a more structured error response could be beneficial
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new HttpErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage()));
            // TODO FIX RESPONSE
            return null;
        } catch (RuntimeException e) {
            // Catching generic runtime for invalid role name or unsupported role
//            return ResponseEntity.badRequest().body(new HttpErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
            // TODO FIX RESPONSE
            return null;
        }
    }
}
