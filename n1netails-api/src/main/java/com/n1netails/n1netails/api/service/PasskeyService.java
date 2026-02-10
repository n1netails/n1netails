package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.exception.type.EmailExistException;
import com.n1netails.n1netails.api.exception.type.UserNotFoundException;
import com.n1netails.n1netails.api.model.dto.passkey.*;
import com.yubico.webauthn.data.exception.Base64UrlException;
/**
 * Service responsible for passkey (WebAuthn) registration and authentication.
 *
 * <p>This service defines the contract for managing passkey-based
 * authentication flows, including registration and login using
 * WebAuthn-compliant authenticators.</p>
 *
 * <p>Each operation is split into a start and finish phase, identified
 * by a temporary {@code flowId}. Implementations may reject requests
 * if the flow is invalid or expired.</p>
 */
public interface PasskeyService {


    /**
     * Initiates a passkey registration flow for an existing user.
     *
     * @param request registration start request
     * @return registration options and a flow identifier
     *
     * @throws UserNotFoundException if the user does not exist
     */
    PasskeyRegistrationStartResponseDto startRegistration(PasskeyRegistrationStartRequestDto request) throws UserNotFoundException, Base64UrlException, EmailExistException;

    /**
     * Completes a passkey registration flow.
     *
     * @param request registration finish request
     * @return {@code true} if registration succeeds, {@code false} otherwise
     *
     * @throws UserNotFoundException if the associated user cannot be resolved
     */
    boolean finishRegistration(PasskeyRegistrationFinishRequestDto request) throws UserNotFoundException;

    /**
     * Initiates a passkey authentication (login) flow.
     *
     * <p>If an email is provided in the request, authentication is scoped to
     * the corresponding user. If no email is provided, discoverable credentials
     * may be used.</p>
     *
     * @param request authentication start request
     * @return authentication options and a flow identifier
     */
    PasskeyAuthenticationStartResponseDto startAuthentication(PasskeyAuthenticationStartRequestDto request);

    /**
     * Completes a passkey authentication flow.
     *
     * <p>Validates the provided passkey assertion and, on success,
     * authenticates the user associated with the credential.</p>
     *
     * @param request authentication finish request
     * @return authentication result including success status and token data
     */
    PasskeyAuthenticationResponseDto finishAuthentication(PasskeyAuthenticationFinishRequestDto request);
}
