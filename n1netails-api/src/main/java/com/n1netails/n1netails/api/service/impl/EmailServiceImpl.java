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
    private static final String MAIL_PARAM_OPENING = "{{";
    private static final String MAIL_PARAM_CLOSING = "}}";

    private final JavaMailSender mailSender;
    private final EmailNotificationTemplateRepository emailNotificationTemplateRepository;
    private final String FROM = "n1netails.org@gmail.com";
    @Override
    public void sendMail(SendMailRequest sendMailRequest) throws EmailTemplateNotFoundException, MessagingException {
        Optional<EmailNotificationTemplateEntity> optionalTemplate = emailNotificationTemplateRepository
                .findById(sendMailRequest.getNotificationTemplateId());
        if (optionalTemplate.isEmpty()) {
            throw new EmailTemplateNotFoundException(
                    String.format("Email with template id " + sendMailRequest.getNotificationTemplateId() + " not found")
            );
        }
        EmailNotificationTemplateEntity template = optionalTemplate.get();
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setTo(sendMailRequest.getTo());
        mimeMessageHelper.setFrom(FROM);
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
