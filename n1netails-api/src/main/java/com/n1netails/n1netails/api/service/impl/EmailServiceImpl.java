package com.n1netails.n1netails.api.service.impl;

import com.n1netails.n1netails.api.exception.type.EmailTemplateNotFoundException;
import com.n1netails.n1netails.api.model.entity.EmailNotificationTemplateEntity;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.model.request.KudaTailRequest;
import com.n1netails.n1netails.api.model.request.SendMailRequest;
import com.n1netails.n1netails.api.repository.EmailNotificationTemplateRepository;
import com.n1netails.n1netails.api.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
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

    @Value("${n1netails.email.enabled}")
    private boolean emailEnabled;

    @Value("${spring.mail.from}")
    private String from;

    private static final String MAIL_PARAM_OPENING = "{{";
    private static final String MAIL_PARAM_CLOSING = "}}";

    private final Map<String, Instant> lastEmailSentAt = new ConcurrentHashMap<>();
    private final Duration alertEmailCooldown = Duration.ofMinutes(10);

    private final JavaMailSender mailSender;
    private final EmailNotificationTemplateRepository emailNotificationTemplateRepository;

    @Async
    @Override
    public CompletableFuture<Void> sendMail(SendMailRequest sendMailRequest) throws EmailTemplateNotFoundException, MessagingException {
        if (!emailEnabled) return CompletableFuture.completedFuture(null);

        Optional<EmailNotificationTemplateEntity> optionalTemplate = emailNotificationTemplateRepository
                .findByName(sendMailRequest.getNotificationTemplateName());
        if (optionalTemplate.isEmpty()) {
            throw new EmailTemplateNotFoundException(
                    String.format("Email template with name " + sendMailRequest.getNotificationTemplateName() + " not found")
            );
        }
        EmailNotificationTemplateEntity template = optionalTemplate.get();
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        mimeMessageHelper.setTo(sendMailRequest.getTo());
        mimeMessageHelper.setFrom(from);
        mimeMessageHelper.setSubject(this.applyParameters(template.getSubject(), sendMailRequest.getSubjectParams()));
        mimeMessageHelper.setText(this.applyParameters(template.getHtmlBody(), sendMailRequest.getBodyParams()), true);
        mimeMessageHelper.setCc(sendMailRequest.getCc().toArray(String[]::new));
        mimeMessageHelper.setBcc(sendMailRequest.getBcc().toArray(String[]::new));
        mailSender.send(mimeMessage);

        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void sendWelcomeEmail(UsersEntity usersEntity) {
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

            this.sendMail(welcomeMailRequest).exceptionally(ex -> {
                log.warn("Failed to send welcome email to {}: {}", usersEntity.getEmail(), ex.getMessage());
                return null;
            });

        } catch (EmailTemplateNotFoundException | MessagingException e) {
            log.warn("Unexpected error while preparing welcome email for {}: {}", usersEntity.getEmail(), e.getMessage());
        }
    }

    @Override
    public void sendAlertEmail(UsersEntity usersEntity, KudaTailRequest request) {
        String email = usersEntity.getEmail();
        Instant now = Instant.now();
        Instant lastSent = lastEmailSentAt.get(email);
        if (lastSent != null && Duration.between(lastSent, now).compareTo(alertEmailCooldown) < 0) {
            log.info("Rate limit: skipping alert email to {}", email);
            return;
        }
        lastEmailSentAt.put(email, now);

        try {
            SendMailRequest alertMailRequest = new SendMailRequest();
            alertMailRequest.setNotificationTemplateName("alert");
            alertMailRequest.setTo(usersEntity.getEmail());
            Map<String, String> subjectParams = new HashMap<>();
            subjectParams.put("tailLevel", request.getLevel());
            alertMailRequest.setSubjectParams(subjectParams);
            Map<String, String> bodyParams = new HashMap<>();
            bodyParams.put("username", usersEntity.getUsername());
            bodyParams.put("tailLevel", request.getLevel());
            bodyParams.put("tailTitle", request.getTitle());
            bodyParams.put("tailDescription", request.getDescription());
            bodyParams.put("n1netailsUi", this.n1netailsUi);
            bodyParams.put("n1netailsEmail", this.from);
            alertMailRequest.setBodyParams(bodyParams);

            this.sendMail(alertMailRequest).exceptionally(ex -> {
                log.warn("Failed to send alert email to {}: {}", usersEntity.getEmail(), ex.getMessage());
                return null;
            });

        } catch (EmailTemplateNotFoundException | MessagingException e) {
            log.warn("Unexpected error while preparing alert email for {}: {}", usersEntity.getEmail(), e.getMessage());
        }
    }

    private String applyParameters(String content, Map<String, String> parameters) {
        String result = content;
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            String paramName = entry.getKey();
            String paramValue = entry.getValue() != null ? entry.getValue() : "";
            String pattern = MAIL_PARAM_OPENING + paramName + MAIL_PARAM_CLOSING;
            result = result.replace(pattern, paramValue);
        }
        return result;
    }
}
