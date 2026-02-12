package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.exception.type.EmailExistException;
import com.n1netails.n1netails.api.exception.type.EmailTemplateNotFoundException;
import com.n1netails.n1netails.api.exception.type.InvalidRoleException;
import com.n1netails.n1netails.api.exception.type.UserNotFoundException;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.model.request.UserRegisterRequest;
import jakarta.mail.MessagingException;

/**
 * Service responsible for managing application users and authentication.
 *
 * <p>This service provides methods for user registration, editing, role management,
 * password updates, login tracking, and password recovery. Methods may throw exceptions
 * for missing users, invalid roles, or duplicate emails.</p>
 */
public interface UserService {

    /**
     * Finds a user by their email.
     *
     * @param email the email of the user to search for
     * @return the {@link UsersEntity} if found, {@code null} if no user exists with the given email
     */
    UsersEntity findUserByEmail(String email);


    /**
     * Registers a new user.
     *
     * @param user the registration request containing user details
     * @return the newly created {@link UsersEntity}
     * @throws UserNotFoundException if a required associated entity (like default organization) is missing
     * @throws EmailExistException   if a user with the same email already exists
     */
    UsersEntity register(UserRegisterRequest user) throws UserNotFoundException, EmailExistException;

    /**
     * Edits an existing user's details.
     *
     * @param user the user object containing updated information
     * @return the updated {@link UsersEntity}
     * @throws RuntimeException if no user exists with the email provided in the request
     */
    UsersEntity editUser(UsersEntity user);

    /**
     * Updates a user's password.
     *
     * @param email       the email of the user
     * @param newPassword the new password to set
     * @return the updated {@link UsersEntity}
     * @throws UserNotFoundException if no user exists with the given email
     */
    UsersEntity updatePassword(String email, String newPassword) throws UserNotFoundException;

    /**
     * Updates a user's role and authorities.
     *
     * @param userId      the ID of the user
     * @param newRoleName the new role name to assign
     * @return the updated {@link UsersEntity}
     * @throws UserNotFoundException if no user exists with the given ID
     * @throws InvalidRoleException  if the role name is invalid or unsupported
     */
    UsersEntity updateUserRole(Long userId, String newRoleName) throws UserNotFoundException, InvalidRoleException;

    /**
     * Initiates a forgot-password request for the user.
     *
     * <p>If no user exists with the provided email, the method does nothing.</p>
     *
     * @param email the email of the user requesting a password reset
     * @throws MessagingException             if sending the email fails
     * @throws EmailTemplateNotFoundException if the email template cannot be found
     */
    void forgotPasswordRequest(String email) throws MessagingException, EmailTemplateNotFoundException;

    /**
     * Marks a user as having completed the tutorial.
     *
     * @param email the email of the user
     * @throws UserNotFoundException if no user exists with the given email
     */
    void completeTutorial(String email) throws UserNotFoundException;
}
