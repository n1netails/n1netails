package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.model.entity.ForgotPasswordRequestEntity;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.model.request.KudaTailRequest;

/**
 * Service responsible for sending user-facing emails.
 *
 * <p>
 * Provides operations for dispatching transactional and notification
 * emails using predefined templates. Email delivery is handled
 * asynchronously and failures are logged without propagating
 * exceptions to callers.
 * </p>
 */
public interface EmailService {

    /**
     * Sends a welcome email to a newly registered user.
     *
     * <p>
     * This operation is executed asynchronously and does not guarantee
     * successful delivery. Failures are handled internally.
     * </p>
     *
     * @param usersEntity the user to receive the welcome email
     */
    void sendWelcomeEmail(UsersEntity usersEntity);

    /**
     * Sends an alert notification email related to a tail event.
     *
     * <p>
     * Alert emails may be rate-limited to prevent excessive
     * notifications being sent to the same recipient.
     * </p>
     *
     * @param email   the recipient email address
     * @param request the tail details used to populate the email content
     */
    void sendNotificationEmail(String email, KudaTailRequest request);

    /**
     * Sends a password reset email to a user.
     *
     * <p>
     * The email contains a password reset link associated with the
     * provided password reset request.
     * </p>
     *
     * @param forgotPasswordRequest the password reset request containing
     *                              user and reset details
     */
    void sendPasswordResetEmail(ForgotPasswordRequestEntity forgotPasswordRequest);
}
