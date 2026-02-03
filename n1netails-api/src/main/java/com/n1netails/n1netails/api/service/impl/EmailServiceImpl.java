package com.n1netails.n1netails.api.service.impl;

import com.n1netails.n1netails.api.exception.type.EmailTemplateNotFoundException;
import com.n1netails.n1netails.api.model.entity.ForgotPasswordRequestEntity;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.model.request.KudaTailRequest;
import com.n1netails.n1netails.api.model.request.SendMailRequest;
import com.n1netails.n1netails.api.service.EmailService;
import com.n1netails.n1netails.api.util.JavaMailSenderUtil;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * EmailServiceImpl
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Qualifier("emailService")
public class EmailServiceImpl implements EmailService {

    @Value("${n1netails.ui}")
    private String n1netailsUi;

    @Value("${spring.mail.from}")
    private String from;

    private final JavaMailSenderUtil mailUtils;

    private final Map<String, Instant> lastEmailSentAt = new ConcurrentHashMap<>();
    private final Duration alertEmailCooldown = Duration.ofMinutes(10);

    @Override
    public void sendWelcomeEmail(UsersEntity usersEntity) {
        log.info("call to sendWelcomeEmail");
        try {
            SendMailRequest welcomeMailRequest = new SendMailRequest();
            welcomeMailRequest.setNotificationTemplateName("welcome");
            welcomeMailRequest.setTo(usersEntity.getEmail());
            Map<String, String> subjectParams = new HashMap<>();
            subjectParams.put("username", usersEntity.getUsername());
            welcomeMailRequest.setSubjectParams(subjectParams);
            Map<String, String> bodyParams = new HashMap<>();
            bodyParams.put("username", usersEntity.getUsername());
            bodyParams.put("n1netailsEmail", this.from);
            welcomeMailRequest.setBodyParams(bodyParams);

            log.info("before async call");
            this.mailUtils.sendMail(welcomeMailRequest).exceptionally(ex -> {
                log.warn("Failed to send welcome email to {}: {}", usersEntity.getEmail(), ex.getMessage());
                return null;
            });

        } catch (EmailTemplateNotFoundException | MessagingException e) {
            log.warn("Unexpected error while preparing welcome email for {}: {}", usersEntity.getEmail(), e.getMessage());
        }

        log.info("after call to sendWelcomeEmail");
    }

    @Override
    public void sendNotificationEmail(String email, KudaTailRequest request) {
        Instant now = Instant.now();
        Instant lastSent = lastEmailSentAt.get(email);
        if (lastSent != null && Duration.between(lastSent, now).compareTo(alertEmailCooldown) < 0) {
            log.info("Rate limit: skipping alert email to {}", email);
            return;
        }
        lastEmailSentAt.put(email, now);
        String username = email.substring(0, email.indexOf("@"));

        try {
            SendMailRequest alertMailRequest = new SendMailRequest();
            alertMailRequest.setNotificationTemplateName("alert");
            alertMailRequest.setTo(email);
            Map<String, String> subjectParams = new HashMap<>();
            subjectParams.put("tailLevel", request.getLevel());
            alertMailRequest.setSubjectParams(subjectParams);
            Map<String, String> bodyParams = new HashMap<>();
            bodyParams.put("username", username);
            bodyParams.put("tailLevel", request.getLevel());
            bodyParams.put("tailTitle", request.getTitle());
            bodyParams.put("tailDescription", request.getDescription());
            bodyParams.put("n1netailsUi", this.n1netailsUi);
            bodyParams.put("n1netailsEmail", this.from);
            alertMailRequest.setBodyParams(bodyParams);

            this.mailUtils.sendMail(alertMailRequest).exceptionally(ex -> {
                log.warn("Failed to send alert email to {}: {}", email, ex.getMessage());
                return null;
            });

        } catch (EmailTemplateNotFoundException | MessagingException e) {
            log.warn("Unexpected error while preparing alert email for {}: {}", email, e.getMessage());
        }
    }

    @Override
    public void sendPasswordResetEmail(ForgotPasswordRequestEntity forgotPasswordRequest) {
        SendMailRequest forgotPasswordMailRequest = new SendMailRequest();
        forgotPasswordMailRequest.setNotificationTemplateName("forgot_password_reset");
        forgotPasswordMailRequest.setTo(forgotPasswordRequest.getUser().getEmail());
        forgotPasswordMailRequest.setBodyParams(Map.of(
                "username", forgotPasswordRequest.getUser().getUsername(),
                "resetPasswordLink", n1netailsUi + "/#/reset-password?request_id=" + forgotPasswordRequest.getId(),
                "n1netailsEmail", this.from
        ));
        try {
            this.mailUtils.sendMail(forgotPasswordMailRequest).exceptionally(ex -> {
                log.warn("Failed to password reset email to {}: {}", forgotPasswordRequest.getUser().getEmail(), ex.getMessage());
                return null;
            });
        } catch (EmailTemplateNotFoundException | MessagingException e) {
            log.warn("Unexpected error while preparing password reset email for {}: {}", forgotPasswordRequest.getUser().getEmail(), e.getMessage());
        }
    }

}
