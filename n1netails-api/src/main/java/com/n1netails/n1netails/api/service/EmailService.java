package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.model.entity.ForgotPasswordRequestEntity;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.model.request.KudaTailRequest;

/**
 * EmailService
 */
public interface EmailService {
    void sendWelcomeEmail(UsersEntity usersEntity);
    void sendNotificationEmail(String email, KudaTailRequest request);
    void sendPasswordResetEmail(ForgotPasswordRequestEntity forgotPasswordRequest);
}
