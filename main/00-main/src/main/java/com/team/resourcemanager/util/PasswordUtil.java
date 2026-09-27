package com.team.resourcemanager.util;

import org.mindrot.jbcrypt.BCrypt;

import java.nio.charset.StandardCharsets;

public final class PasswordUtil {

    public static final int MIN_LENGTH = 8;
    public static final int MAX_BCRYPT_BYTES = 72;
    private static final int LOG_ROUNDS = 12;

    public static String hash(char[] rawPassword) {
        requireUsablePassword(rawPassword);
        return BCrypt.hashpw(new String(rawPassword), BCrypt.gensalt(LOG_ROUNDS));
    }

    public static boolean matches(char[] rawPassword, String passwordHash) {
        if (rawPassword == null || rawPassword.length == 0
                || passwordHash == null || passwordHash.isBlank()) {
            return false;
        }
        try {
            return BCrypt.checkpw(new String(rawPassword), passwordHash);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static int utf8Length(char[] password) {
        if (password == null) {
            return 0;
        }
        return new String(password).getBytes(StandardCharsets.UTF_8).length;
    }

    private static void requireUsablePassword(char[] password) {
        if (password == null || password.length == 0) {
            throw new IllegalArgumentException("비밀번호가 비어 있습니다.");
        }
        if (utf8Length(password) > MAX_BCRYPT_BYTES) {
            throw new IllegalArgumentException("비밀번호가 너무 깁니다.");
        }
    }

    private PasswordUtil() {
    }
}
