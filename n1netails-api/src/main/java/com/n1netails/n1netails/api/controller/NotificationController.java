package com.n1netails.n1netails.api.controller;

import com.n1netails.n1netails.api.exception.type.EmailTemplateNotFoundException;
import com.n1netails.n1netails.api.model.request.SendMailRequest;
import com.n1netails.n1netails.api.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * NotificationController
 */
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Notification Controller", description = "Operations related to Notification")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping(path = {"/ninetails/notification"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class NotificationController {

    private final EmailService emailService;

    @Operation(summary = "Create email (Used for demo purposes will be removed in the future as other services will be utilizing the email service in the background.)", responses = {
            @ApiResponse(responseCode = "202", description = "Email request accepted"),
            @ApiResponse(responseCode = "403", description = "Unauthorized")
    })
    @PostMapping("/email")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> sendMail(@RequestBody SendMailRequest request)
            throws EmailTemplateNotFoundException, MessagingException {
        emailService.sendMail(request);
        return ResponseEntity.accepted().build();
    }
}
