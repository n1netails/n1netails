package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.exception.type.EmailExistException;
import com.n1netails.n1netails.api.exception.type.UserNotFoundException;
import com.n1netails.n1netails.api.model.entity.Users;
import com.n1netails.n1netails.api.model.request.UserRegisterRequest;

public interface UserService {

    Users findUserByEmail(String email);
    Users register(UserRegisterRequest user) throws UserNotFoundException, EmailExistException;
    Users editUser(Users user);
}
