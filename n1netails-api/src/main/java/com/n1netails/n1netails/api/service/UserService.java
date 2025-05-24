package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.exception.type.EmailExistException;
import com.n1netails.n1netails.api.exception.type.EmailExistException;
import com.n1netails.n1netails.api.exception.type.UserNotFoundException;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.model.request.UserRegisterRequest;
import com.n1netails.n1netails.api.model.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    UsersEntity findUserByEmail(String email);
    UsersEntity register(UserRegisterRequest user) throws UserNotFoundException, EmailExistException;
    UsersEntity editUser(UsersEntity user);
    Page<UserResponse> getAllUsers(Pageable pageable);
    void changePassword(String email, String currentPassword, String newPassword);
}
