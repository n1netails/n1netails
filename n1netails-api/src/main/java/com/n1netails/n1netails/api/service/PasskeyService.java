package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.model.request.passkey.PasskeyLoginFinishRequest;
import com.n1netails.n1netails.api.model.request.passkey.PasskeyLoginStartRequest;
import com.n1netails.n1netails.api.model.request.passkey.PasskeyRegistrationFinishRequest;
import com.n1netails.n1netails.api.model.request.passkey.PasskeyRegistrationStartRequest;
import com.n1netails.n1netails.api.model.response.passkey.PasskeyAuthenticationResponse;
import com.n1netails.n1netails.api.model.response.passkey.PasskeyLoginStartResponse;
import com.n1netails.n1netails.api.model.response.passkey.PasskeyRegistrationStartResponse;

public interface PasskeyService {

    PasskeyRegistrationStartResponse startRegistration(PasskeyRegistrationStartRequest request);

    void finishRegistration(PasskeyRegistrationFinishRequest request);

    PasskeyLoginStartResponse startLogin(PasskeyLoginStartRequest request);

    PasskeyAuthenticationResponse finishLogin(PasskeyLoginFinishRequest request);
}
