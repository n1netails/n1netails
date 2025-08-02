package com.n1netails.n1netails.api.service.impl;

import com.n1netails.n1netails.api.exception.type.EmailTemplateNotFoundException;
import com.n1netails.n1netails.api.model.entity.EmailNotificationTemplateEntity;
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
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

/**
 * EmailServiceImpl
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Qualifier("emailService")
public class EmailServiceImpl implements EmailService {

    @Value("${n1netails.email.enabled}")
    private boolean emailEnabled;

    @Value("${spring.mail.from}")
    private String from;

    private static final String MAIL_PARAM_OPENING = "{{";
    private static final String MAIL_PARAM_CLOSING = "}}";

    private final JavaMailSender mailSender;
    private final EmailNotificationTemplateRepository emailNotificationTemplateRepository;
    @Override
    public void sendMail(SendMailRequest sendMailRequest) throws EmailTemplateNotFoundException, MessagingException {
        if (!emailEnabled) return;

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
