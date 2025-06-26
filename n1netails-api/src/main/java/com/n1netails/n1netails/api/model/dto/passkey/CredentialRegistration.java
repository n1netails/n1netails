package com.n1netails.n1netails.api.model.dto.passkey;

import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.UserIdentity;

import java.time.Instant;

public class CredentialRegistration {

    private final RegisteredCredential credential;
    private final UserIdentity userIdentity;
    private final Instant registrationTime;

    public CredentialRegistration(RegisteredCredential credential, UserIdentity userIdentity, Instant registrationTime) {
        this.credential = credential;
        this.userIdentity = userIdentity;
        this.registrationTime = registrationTime;
    }

    public static Builder builder() {
        return new Builder();
    }

    public RegisteredCredential getCredential() {
        return credential;
    }

    public UserIdentity getUserIdentity() {
        return userIdentity;
    }

    public Instant getRegistrationTime() {
        return registrationTime;
    }

    public static class Builder {
        private RegisteredCredential credential;
        private UserIdentity userIdentity;
        private Instant registrationTime;

        public Builder credential(RegisteredCredential credential) {
            this.credential = credential;
            return this;
        }

        public Builder userIdentity(UserIdentity userIdentity) {
            this.userIdentity = userIdentity;
            return this;
        }

        public Builder registrationTime(Instant registrationTime) {
            this.registrationTime = registrationTime;
            return this;
        }

        public CredentialRegistration build() {
            return new CredentialRegistration(credential, userIdentity, registrationTime);
        }
    }
}
