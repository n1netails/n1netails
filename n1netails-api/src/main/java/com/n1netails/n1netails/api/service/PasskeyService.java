package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.exception.type.EmailExistException;
import com.n1netails.n1netails.api.exception.type.UserNotFoundException;
import com.n1netails.n1netails.api.model.dto.passkey.*;
import com.yubico.webauthn.data.exception.Base64UrlException;

public interface PasskeyService {

    PasskeyRegistrationStartResponseDto startRegistration(PasskeyRegistrationStartRequestDto request) throws UserNotFoundException, Base64UrlException, EmailExistException;
    boolean finishRegistration(PasskeyRegistrationFinishRequestDto request) throws UserNotFoundException;

    PasskeyAuthenticationStartResponseDto startAuthentication(PasskeyAuthenticationStartRequestDto request);
    PasskeyAuthenticationResponseDto finishAuthentication(PasskeyAuthenticationFinishRequestDto request);
}
