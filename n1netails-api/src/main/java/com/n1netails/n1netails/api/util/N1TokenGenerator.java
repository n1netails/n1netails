package com.n1netails.n1netails.api.util;

import com.n1netails.n1netails.api.exception.type.N1neTokenGenerateException;
import lombok.Getter;

import java.security.SecureRandom;
import java.util.Base64;

public class N1TokenGenerator {

    private static final SecureRandom secureRandom = new SecureRandom();

    @Getter
    public static class N1TokenResult {
        private final String tokenPlain;
        private final byte[] tokenHash;

        public N1TokenResult(String tokenPlain, byte[] tokenHash) {
            this.tokenPlain = tokenPlain;
            this.tokenHash = tokenHash;
        }
    }

    public static N1TokenResult generateToken() throws N1neTokenGenerateException {
        byte[] randomBytes = new byte[64];
        secureRandom.nextBytes(randomBytes);

        String tokenBody = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        String tokenPlain = "n1_" + tokenBody;

        byte[] tokenHash = sha256(tokenPlain);

        return new N1TokenResult(tokenPlain, tokenHash);
    }

    public static byte[] sha256(String input) throws N1neTokenGenerateException {
        try {
            return java.security.MessageDigest.getInstance("SHA-256")
                    .digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new N1neTokenGenerateException("SHA-256 algorithm not available", e);
        }
    }
}
