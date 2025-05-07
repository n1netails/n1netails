package com.n1ne.n1netails.api.service;

import com.n1ne.n1netails.api.exception.EmailExistException;
import com.n1ne.n1netails.api.exception.UserNotFoundException;
import com.n1ne.n1netails.api.model.entity.Users;
import com.n1ne.n1netails.api.model.request.UserRegisterRequest;

public interface UserService {

    Users findUserByEmail(String email);
    Users register(UserRegisterRequest user) throws UserNotFoundException, EmailExistException;
}
