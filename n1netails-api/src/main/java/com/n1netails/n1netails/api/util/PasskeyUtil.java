package com.n1netails.n1netails.api.util;

import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.exception.Base64UrlException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

@Slf4j
public class PasskeyUtil {

    private static final SecureRandom random = new SecureRandom();

    // === UTILITY METHODS ===
    public static @NonNull ByteArray generateUserHandle(UsersEntity user) throws Base64UrlException {
        log.info("== generateUserHandle");
        // User handle MUST be stable and unique for the user.
        // It should NOT be PII if possible, and MUST NOT change for a given user.
        // The Yubico library expects a ByteArray. Max 64 bytes.
        // Ensure it's at least 1 byte, preferably more for uniqueness.
        Long userId = user.getId();
        if (userId == null) {
            // This should not happen for an existing user
            log.error("User ID is null or blank for user: {}. Cannot generate user handle.", user.getUsername());
            throw new IllegalArgumentException("User ID cannot be null or blank for user handle generation.");
        }
        log.info("returning user handle");
        String userHandleString = "n" + user.getId().toString();
        log.info("userHandle: {}", new ByteArray(userHandleString.getBytes(StandardCharsets.UTF_8)));
        return new ByteArray(userHandleString.getBytes(StandardCharsets.UTF_8));
    }

    public static String generateFlowId() {
        log.info("generateFlowId");
        byte[] randomBytes = new byte[32];
        random.nextBytes(randomBytes);
        return new ByteArray(randomBytes).getBase64Url();
    }
}
