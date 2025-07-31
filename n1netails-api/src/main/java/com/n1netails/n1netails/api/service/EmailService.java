package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.exception.type.EmailTemplateNotFoundException;
import com.n1netails.n1netails.api.model.request.SendMailRequest;
import jakarta.mail.MessagingException;

/**
 * EmailService
 */
public interface EmailService {
    void sendMail(SendMailRequest sendMailRequest) throws EmailTemplateNotFoundException, MessagingException;
}
