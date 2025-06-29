package com.n1netails.n1netails.api.repository.impl;

import com.n1netails.n1netails.api.model.dto.PasskeySummary;
import com.n1netails.n1netails.api.model.entity.PasskeyCredentialEntity;
import com.n1netails.n1netails.api.repository.PasskeyCredentialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Repository
public class PasskeyCredentialRepositoryImpl implements PasskeyCredentialRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<PasskeySummary> findPasskeyByCredentialId(byte[] credentialId) {
        log.info("findPasskeyByCredentialId: {}", credentialId);
        String sql = "SELECT id, credential_id, public_key_cose, signature_count, user_handle, last_used_at, registered_at, user_id FROM ntail.passkey_credentials WHERE credential_id = ?";

        List<PasskeySummary> results = jdbcTemplate.query(sql, new Object[]{credentialId}, (rs, rowNum) ->
                        new PasskeySummary(
                                rs.getLong("id"),
                                rs.getBytes("credential_id"),
                                rs.getBytes("public_key_cose"),
                                rs.getLong("signature_count"),
                                rs.getBytes("user_handle"),
                                rs.getDate("last_used_at"),
                                rs.getDate("registered_at"),
                                rs.getLong("user_id")
                        )
        );
        return results.stream().findFirst();
    }

    @Override
    public List<PasskeySummary> findPasskeyByUserIdForUserRegistration(Long userId) {
        log.info("findPasskeyByUserIdForUserRegistration: {}", userId);
        String sql = "SELECT id, credential_id, public_key_cose, signature_count, user_handle, last_used_at, registered_at, user_id FROM ntail.passkey_credentials WHERE user_id = ?";

        List<PasskeySummary> results = jdbcTemplate.query(sql, new Object[]{userId}, (rs, rowNum) ->
                new PasskeySummary(
                        rs.getLong("id"),
                        rs.getBytes("credential_id"),
                        rs.getBytes("public_key_cose"),
                        rs.getLong("signature_count"),
                        rs.getBytes("user_handle"),
                        rs.getDate("last_used_at"),
                        rs.getDate("registered_at"),
                        rs.getLong("user_id")
                )
        );
        return results;
    }

    @Override
    public Optional<PasskeySummary> findPasskeyByUserHandle(byte[] userHandle) {
        log.info("findPasskeyByUserHandle: {}", userHandle);
        String sql = "SELECT id, credential_id, public_key_cose, signature_count, user_handle, last_used_at, registered_at, user_id FROM ntail.passkey_credentials WHERE user_handle = ?";

        List<PasskeySummary> results = jdbcTemplate.query(sql, new Object[]{userHandle}, (rs, rowNum) ->
                new PasskeySummary(
                        rs.getLong("id"),
                        rs.getBytes("credential_id"),
                        rs.getBytes("public_key_cose"),
                        rs.getLong("signature_count"),
                        rs.getBytes("user_handle"),
                        rs.getDate("last_used_at"),
                        rs.getDate("registered_at"),
                        rs.getLong("user_id")
                )
        );
        return results.stream().findFirst();
    }

    @Override
    @Transactional
    public void updatePasskeySummary(PasskeySummary ps) {
        log.info("savePasskeySummary");
        String sql = "UPDATE ntail.passkey_credentials SET " +
                "credential_id = ?, " +
                "last_used_at = ?, " +
                "public_key_cose = ?, " +
                "signature_count = ?, " +
                "user_handle = ? " +
                "WHERE id = ? RETURNING id";

        Long generatedId = jdbcTemplate.queryForObject(sql, Long.class,
                ps.getCredentialId(),
                ps.getLastUsedAt(),
                ps.getPublicKeyCose(),
                ps.getSignatureCount(),
                ps.getUserHandle(),
                ps.getId()
        );
        log.info("PASSKEY SUMMARY SAVED");
        ps.setId(generatedId);
    }

    @Override
    @Transactional
    public void savePasskeyCredential(PasskeyCredentialEntity p) {
        log.info("savePasskeyCredentialManually");
        String sql = "INSERT INTO ntail.passkey_credentials " +
                "(aaguid, attestation_object, attestation_type, backup_eligible, backup_state, credential_id, device_name, last_used_at, public_key_cose, registered_at, signature_count, user_id, user_handle, uv_initialized) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

        Long generatedId = jdbcTemplate.queryForObject(sql, Long.class,
                p.getAaguid(),
                p.getAttestationObject(),
                p.getAttestationType(),
                p.getBackupEligible(),
                p.getBackupState(),
                p.getCredentialId(),
                p.getDeviceName(),
                p.getLastUsedAt(),
                p.getPublicKeyCose(),
                p.getRegisteredAt(),
                p.getSignatureCount(),
                p.getUser().getId(),
                p.getUserHandle(),
                p.getUvInitialized()
        );
        p.setId(generatedId);
    }
}
