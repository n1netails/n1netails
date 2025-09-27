package com.n1netails.n1netails.api.controller;

import com.n1netails.n1netails.api.exception.type.*;
import com.n1netails.n1netails.api.model.UserPrincipal;
import com.n1netails.n1netails.api.model.entity.OrganizationEntity;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.model.request.UpdateUserRoleRequest;
import com.n1netails.n1netails.api.model.request.UserLoginRequest;
import com.n1netails.n1netails.api.model.request.UserRegisterRequest;
import com.n1netails.n1netails.api.model.response.HttpErrorResponse;
import com.n1netails.n1netails.api.repository.UserRepository;
import com.n1netails.n1netails.api.service.AuthorizationService;
import com.n1netails.n1netails.api.service.EmailService;
import com.n1netails.n1netails.api.service.UserService;
import com.n1netails.n1netails.api.util.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.Set;
import java.util.stream.Collectors;

import static com.n1netails.n1netails.api.constant.ProjectSecurityConstant.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "Users Controller", description = "Operations related to Users")
@RestController
@RequestMapping(path = {"/ninetails/user"}, produces = APPLICATION_JSON_VALUE)
public class UserController {

    private static final String N1NETAILS_ORGANIZATION_NAME = "n1netails"; // C

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final AuthorizationService authorizationService;
    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final JwtDecoder jwtDecoder;
    private final EmailService emailService;

    @Operation(
            summary = "Get user self",
            description = "Get logged in user profile",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User profile located successfully",
                            content = @Content(schema = @Schema(implementation = UsersEntity.class))),
                    @ApiResponse(responseCode = "401", description = "Authentication failed",
                            content = @Content(schema = @Schema(implementation = HttpErrorResponse.class)))
            }
    )
    @GetMapping("/self")
    public ResponseEntity<UsersEntity> getCurrentUser(@RequestHeader(AUTHORIZATION) String authorizationHeader) throws AccessDeniedException {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new AccessDeniedException("Missing or invalid Authorization header");
        }

        try {
            String token = authorizationHeader.substring(TOKEN_PREFIX.length());
            Long id = jwtDecoder.decode(token).getClaim("id");
            log.info("Fetching current user with ID: {}", id);
            UsersEntity user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Get current user not found."));
            if (!user.isEnabled() || !user.isActive() || !user.isNotLocked()) {
                throw new AccessDeniedException("User is disabled or locked");
            }
            return ResponseEntity.ok(user);
        } catch (JwtException | UserNotFoundException e) {
            throw new AccessDeniedException(ACCESS_DENIED_MESSAGE);
        }
    }

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
    @PostMapping(value = "/edit", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<UsersEntity> editUser(
            @RequestHeader(AUTHORIZATION) String authorizationHeader,
            @RequestBody UsersEntity user
    ) throws AccessDeniedException, UserNotFoundException {

        UserPrincipal editingPrincipal = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        // Basic check: user can only edit their own profile through this specific endpoint
        if (editingPrincipal.getUsername().equals(user.getEmail())) {
            log.info("User {} editing their own profile.", editingPrincipal.getUsername());
            UsersEntity editUser = userService.editUser(user); // userService.editUser should ensure it's loading by ID or a unique field
            return new ResponseEntity<>(editUser, OK);
        } else {
            log.warn("User {} attempted to edit profile of user {} via /edit endpoint.", editingPrincipal.getUsername(), user.getEmail());
            throw new AccessDeniedException("You can only edit your own profile using this endpoint.");
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
    @PostMapping(value = "/login", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<UsersEntity> login(@RequestBody UserLoginRequest user) {

        log.info("attempting user login");
        authenticate(user.getEmail(), user.getPassword());
        log.info("finding user by email");
        UsersEntity loginUser = userService.findUserByEmail(user.getEmail());
        log.info("logged in users organizations: {}", loginUser.getOrganizations());
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
    @PostMapping(value = "/register", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<UsersEntity> register(@RequestBody UserRegisterRequest user) throws UserNotFoundException, EmailExistException, PasswordRegexException {

        String password = user.getPassword();
        if (password == null || !password.matches(PASSWORD_REGEX)) {
            throw new PasswordRegexException(PASSWORD_REGEX_EXCEPTION_MESSAGE);
        }

        UsersEntity newUser = userService.register(user);
        authenticate(user.getEmail(), user.getPassword());
        UserPrincipal userPrincipal = new UserPrincipal(newUser);
        HttpHeaders jwtHeader = setJwtHeader(userPrincipal);
        emailService.sendWelcomeEmail(newUser);
        return new ResponseEntity<>(newUser, jwtHeader, OK);
    }

    @PutMapping("/{userId}/role")
    @PreAuthorize("hasAuthority('user:super')") // 'user:super' is unique to SUPER_ADMIN_AUTHORITIES
    public ResponseEntity<?> updateUserRole(
            @RequestHeader(AUTHORIZATION) String authorizationHeader,
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRoleRequest updateUserRoleRequest
    ) throws UserNotFoundException, InvalidRoleException, AccessDeniedException {
        UserPrincipal adminPrincipal = authorizationService.getCurrentUserPrincipal(authorizationHeader); // Admin making the call
        log.info("Admin {} attempting to update role for user ID: {} to role: {}", adminPrincipal.getUsername(), userId, updateUserRoleRequest.getRoleName());

        UsersEntity targetUserEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Target user with ID " + userId + " not found."));
        UserPrincipal targetUserPrincipal = new UserPrincipal(targetUserEntity);

        // Authorization Check 1: Prevent changing Super Admin's role.
        if (authorizationService.isSuperAdmin(targetUserPrincipal)) {
            log.warn("Admin {} attempted to change the role of Super Admin user {}", adminPrincipal.getUsername(), targetUserPrincipal.getUsername());
            throw new AccessDeniedException("Cannot change the role of a Super Admin.");
        }

        // Authorization Check 2: If making a user Super Admin, they must belong exclusively to "n1netails" org.
        if (isNewRoleSuperAdmin(updateUserRoleRequest.getRoleName())) {
            log.info("Attempting to promote user {} to Super Admin. Checking organization membership.", targetUserPrincipal.getUsername());
            Set<OrganizationEntity> targetUserOrgs = targetUserPrincipal.getOrganizations();
            boolean targetIsInOnlyN1netailsOrg = targetUserOrgs.stream()
                    .anyMatch(org -> N1NETAILS_ORGANIZATION_NAME.equals(org.getName())) && targetUserOrgs.size() == 1;

            if (!targetIsInOnlyN1netailsOrg) {
                log.warn("User {} cannot be made Super Admin. They belong to {} organizations, must be exclusively in '{}'. Organizations: {}",
                        targetUserPrincipal.getUsername(), targetUserOrgs.size(), N1NETAILS_ORGANIZATION_NAME,
                        targetUserOrgs.stream().map(OrganizationEntity::getName).collect(Collectors.toList()));
                throw new AccessDeniedException("User must belong exclusively to the '" + N1NETAILS_ORGANIZATION_NAME + "' organization to become a Super Admin.");
            }
            log.info("User {} meets organization criteria for Super Admin promotion.", targetUserPrincipal.getUsername());
        }

        UsersEntity updatedUser = userService.updateUserRole(userId, updateUserRoleRequest.getRoleName());
        log.info("User {} role updated to {} by admin {}", updatedUser.getEmail(), updatedUser.getRole(), adminPrincipal.getUsername());
        return ResponseEntity.ok(updatedUser);
    }

    private boolean isNewRoleSuperAdmin(String roleNameFromRequest) {
        return "ROLE_SUPER_ADMIN".equalsIgnoreCase(roleNameFromRequest);
    }

    private void authenticate(String email, String password) {
        log.info("attempting to authenticate with password");
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
    }

    private HttpHeaders setJwtHeader(UserPrincipal userPrincipal) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Jwt-Token", jwtTokenUtil.createToken(userPrincipal));
        return headers;
    }
}
