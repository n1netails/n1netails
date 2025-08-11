package com.n1netails.n1netails.api.controller;

import com.n1netails.n1netails.api.exception.type.EmailTemplateNotFoundException;
import com.n1netails.n1netails.api.exception.type.ForgotPasswordRequestExpiredException;
import com.n1netails.n1netails.api.exception.type.ForgotPasswordRequestNotFoundException;
import com.n1netails.n1netails.api.exception.type.PasswordRegexException;
import com.n1netails.n1netails.api.exception.type.PreviousPasswordMatchException;
import com.n1netails.n1netails.api.exception.type.UserNotFoundException;
import com.n1netails.n1netails.api.model.entity.ForgotPasswordRequestEntity;
import com.n1netails.n1netails.api.model.request.ForgotPasswordResetRequest;
import com.n1netails.n1netails.api.model.request.PasswordResetRequest;
import com.n1netails.n1netails.api.repository.ForgotPasswordRequestRepository;
import com.n1netails.n1netails.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.n1netails.n1netails.api.constant.ControllerConstant.APPLICATION_JSON;
import static com.n1netails.n1netails.api.constant.ProjectSecurityConstant.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "Password Controller", description = "Operations for managing credentials")
@RestController
@RequestMapping(path = {"/ninetails/password"}, produces = APPLICATION_JSON)
public class PasswordController {

    private final UserService userService;
    private final JwtDecoder jwtDecoder;
    private final ForgotPasswordRequestRepository forgotPasswordRequestRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(
            @RequestHeader(AUTHORIZATION) String authorizationHeader,
            @RequestBody PasswordResetRequest request
    ) throws AccessDeniedException, PasswordRegexException, UserNotFoundException {

        log.info("password reset request initiated");
        if (request.getNewPassword() == null || !request.getNewPassword().matches(PASSWORD_REGEX)) {
            throw new PasswordRegexException(PASSWORD_REGEX_EXCEPTION_MESSAGE);
        }

        try {
            String token = authorizationHeader.substring(TOKEN_PREFIX.length());
            String authEmail = jwtDecoder.decode(token).getSubject();

            if (authEmail.equals(request.getEmail())) {
                userService.updatePassword(request.getEmail(), request.getNewPassword());
                return ResponseEntity.ok("Password updated successfully");
            } else {
                throw new AccessDeniedException(ACCESS_DENIED_MESSAGE);
            }
        } catch (JwtException e) {
            throw new AccessDeniedException(ACCESS_DENIED_MESSAGE);
        }
    }

    @Operation(
            summary = "Request a reset password when user forgot password",
            description = "Post request for user to submit when the user forgot password",
            responses = {
                    @ApiResponse(responseCode = "202", description = "Request success",
                            content = @Content(schema = @Schema(implementation = String.class))
                    ),
            }
    )
    @PostMapping("/forgot")
    public ResponseEntity<String> forgotPasswordRequest(@RequestParam String email) throws MessagingException, EmailTemplateNotFoundException {
        log.info("Request password request received for email: {}", email);
        userService.forgotPasswordRequest(email);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Request success");
    }

    @PutMapping("/reset/forgot")
    public ResponseEntity<String> resetPasswordOnForgot(@RequestBody ForgotPasswordResetRequest forgotPasswordResetRequest) throws UserNotFoundException {
        Optional<ForgotPasswordRequestEntity> optional =
                forgotPasswordRequestRepository.findById(forgotPasswordResetRequest.getRequestId());
        if (optional.isEmpty()) {
            throw new ForgotPasswordRequestNotFoundException("Request not found");
        }
        ForgotPasswordRequestEntity requestEntity = optional.get();
        LocalDateTime now = LocalDateTime.now();
        if (requestEntity.getExpiredAt().isBefore(now)) {
            throw new ForgotPasswordRequestExpiredException("Request expired");
        }
        String oldPassword = requestEntity.getUser().getPassword();
        String newPassword = forgotPasswordResetRequest.getNewRawPassword();
        if (passwordEncoder.matches(newPassword, oldPassword)) {
            throw new PreviousPasswordMatchException("Password is matched with old password");
        }
        userService.updatePassword(requestEntity.getUser().getEmail(), newPassword);
        forgotPasswordRequestRepository.delete(requestEntity);
        return ResponseEntity.ok("Password changed successfully. You can now login with your new password");
    }
}
