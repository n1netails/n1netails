package com.n1netails.n1netails.api.repository;

import com.n1netails.n1netails.api.model.dto.PasskeySummary;
import com.n1netails.n1netails.api.model.entity.PasskeyCredentialEntity;

import java.util.List;
import java.util.Optional;

public interface PasskeyCredentialRepository {

    Optional<PasskeySummary> findPasskeyByCredentialId(byte[] credentialId);

    List<PasskeySummary> findPasskeyByUserIdForUserRegistration(Long userId);

    Optional<PasskeySummary> findPasskeyByUserHandle(byte[] userHandle);

    void updatePasskeySummary(PasskeySummary passkeySummary);

    void savePasskeyCredential(PasskeyCredentialEntity passkeyCredentialEntity);
}