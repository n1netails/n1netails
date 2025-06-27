package com.n1netails.n1netails.api.service;

import com.yubico.webauthn.data.AuthenticatorAssertionResponse;
import com.yubico.webauthn.data.AuthenticatorAttestationResponse;
import com.yubico.webauthn.data.ClientRegistrationExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
import com.yubico.webauthn.data.PublicKeyCredentialRequestOptions;
import com.yubico.webauthn.data.UserIdentity;
// No longer using the generic User model here, will use username string
import java.util.Optional;

public interface PasskeyService {

    /**
     * Starts the passkey registration process for a given user identified by email.
     * Assumes the user account (identified by email) already exists.
     *
     * @param email The email of the user for whom to start registration.
     * @param relyingPartyId The ID of the relying party (this application).
     * @param relyingPartyName The name of the relying party.
     * @param clientOrigin The origin of the client making the request.
     * @return PublicKeyCredentialCreationOptions to be sent to the client.
     *         The JSON representation of these options should be stored by the client
     *         and sent back during the finishRegistration step.
     */
    PublicKeyCredentialCreationOptions startRegistration(String email, String relyingPartyId, String relyingPartyName, String clientOrigin);

    /**
     * Finishes the passkey registration process for a user identified by email.
     *
     * @param email The email of the user completing registration.
     * @param relyingPartyId The ID of the relying party.
     * @param clientOrigin The origin of the client.
     * @param attestationResponse The response from the authenticator.
     * @param clientExtensions The client extension outputs, if any.
     * @param originalCreationOptionsJson The JSON string of PublicKeyCredentialCreationOptions from startRegistration.
     * @return true if registration was successful, false otherwise.
     */
    boolean finishRegistration(String email, String relyingPartyId, String clientOrigin, AuthenticatorAttestationResponse attestationResponse, ClientRegistrationExtensionOutputs clientExtensions, String originalCreationOptionsJson);

    /**
     * Starts the passkey authentication process.
     * Can be initiated with an email (for non-discoverable credentials lookup) or without (for discoverable credentials).
     *
     * @param email Optional email of the user attempting to authenticate. If null or empty, discoverable login is assumed.
     * @param relyingPartyId The ID of the relying party.
     * @return PublicKeyCredentialRequestOptions to be sent to the client.
     *         The JSON representation of these options should be stored by the client
     *         and sent back during the finishAuthentication step.
     */
    PublicKeyCredentialRequestOptions startAuthentication(String email, String relyingPartyId);

    /**
     * Finishes the passkey authentication process.
     * The user is identified from the assertion response.
     *
     * @param relyingPartyId The ID of the relying party.
     * @param clientOrigin The origin of the client.
     * @param assertionResponse The assertion response from the authenticator.
     * @return Optional containing the UserIdentity if authentication is successful, empty otherwise.
     */
    Optional<UserIdentity> finishAuthentication(String relyingPartyId, String clientOrigin, AuthenticatorAssertionResponse assertionResponse, String originalRequestOptionsJson);

    // Additional methods can be added here, e.g., for managing credentials (listing, deleting)
}
