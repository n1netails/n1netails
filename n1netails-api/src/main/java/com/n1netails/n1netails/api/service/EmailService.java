package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.exception.type.EmailTemplateNotFoundException;
import com.n1netails.n1netails.api.model.entity.ForgotPasswordRequestEntity;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.model.request.KudaTailRequest;
import com.n1netails.n1netails.api.model.request.SendMailRequest;
import jakarta.mail.MessagingException;

import java.util.concurrent.CompletableFuture;

/**
 * EmailService
 */
public interface EmailService {
    CompletableFuture<Void> sendMail(SendMailRequest sendMailRequest) throws EmailTemplateNotFoundException, MessagingException;
    void sendWelcomeEmail(UsersEntity usersEntity);
    void sendAlertEmail(UsersEntity usersEntity, KudaTailRequest request);
    void sendPasswordResetEmail(ForgotPasswordRequestEntity forgotPasswordRequest) throws MessagingException, EmailTemplateNotFoundException;
}
