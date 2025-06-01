package com.n1netails.n1netails.api.controller;

import com.n1netails.n1netails.api.exception.type.PasswordRegexException;
import com.n1netails.n1netails.api.exception.type.UserNotFoundException;
import com.n1netails.n1netails.api.model.request.PasswordResetRequest;
import com.n1netails.n1netails.api.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

import static com.n1netails.n1netails.api.constant.ControllerConstant.APPLICATION_JSON;
import static com.n1netails.n1netails.api.constant.ProjectSecurityConstant.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "Password Controller", description = "Operations for managing credentials")
@RestController
@RequestMapping(path = {"/api/password"}, produces = APPLICATION_JSON)
public class PasswordController {

    private final UserService userService;
    private final JwtDecoder jwtDecoder;

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
}
