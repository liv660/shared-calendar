package com.soyeon.sharedcalendar.auth.pkce;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public final class PKCEUtils {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int STATE_LENGTH = 16;
    private static final int VERIFIER_LENGTH = 32;

    public static String generateState() {
        byte[] bytes = new byte[STATE_LENGTH];
        RANDOM.nextBytes(bytes);
        return b64Url(bytes);
    }

    public static String generateCodeVerifier() {
        byte[] bytes = new byte[VERIFIER_LENGTH];
        RANDOM.nextBytes(bytes);
        return b64Url(bytes);
    }

    public static String toCodeChallenge(String codeVerifier) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] digest = sha256.digest(codeVerifier.getBytes(StandardCharsets.US_ASCII));
            return b64Url(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    //URL Base64
    private static String b64Url(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
