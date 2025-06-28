package com.n1netails.n1netails.api.model.dto.passkey;

import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.UserIdentity;
import lombok.Getter;

import java.time.Instant;

@Getter
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
