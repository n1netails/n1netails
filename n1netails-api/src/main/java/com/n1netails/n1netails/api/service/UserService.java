package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.exception.type.EmailExistException;
import com.n1netails.n1netails.api.exception.type.EmailTemplateNotFoundException;
import com.n1netails.n1netails.api.exception.type.InvalidRoleException;
import com.n1netails.n1netails.api.exception.type.UserNotFoundException;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.model.request.UserRegisterRequest;
import jakarta.mail.MessagingException;

public interface UserService {

    UsersEntity findUserByEmail(String email);
    UsersEntity register(UserRegisterRequest user) throws UserNotFoundException, EmailExistException;
    UsersEntity editUser(UsersEntity user);
    UsersEntity updatePassword(String email, String newPassword) throws UserNotFoundException;
    UsersEntity updateUserRole(Long userId, String newRoleName) throws UserNotFoundException, InvalidRoleException;
    void forgotPasswordRequest(String email) throws MessagingException, EmailTemplateNotFoundException;
    void completeTutorial(String email) throws UserNotFoundException;
}
