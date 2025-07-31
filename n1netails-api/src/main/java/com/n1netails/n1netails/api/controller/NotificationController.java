package com.n1netails.n1netails.api.controller;

import com.n1netails.n1netails.api.exception.type.EmailTemplateNotFoundException;
import com.n1netails.n1netails.api.model.request.SendMailRequest;
import com.n1netails.n1netails.api.service.EmailService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
@RestController
@RequestMapping(path = {"/ninetails/notification"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class NotificationController {

    private final EmailService emailService;

    @PostMapping("/email")
    public ResponseEntity<?> sendMail(@RequestBody SendMailRequest request)
            throws EmailTemplateNotFoundException, MessagingException {
        emailService.sendMail(request);
        return ResponseEntity.accepted().build();
    }
}
