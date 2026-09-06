package com.team.resourcemanager.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordUtilTest {

    @Test
    void hashesAndMatchesPassword() {
        char[] password = "Password123".toCharArray();
        String hash = PasswordUtil.hash(password);

        assertNotEquals("Password123", hash);
        assertTrue(PasswordUtil.matches("Password123".toCharArray(), hash));
        assertFalse(PasswordUtil.matches("WrongPassword1".toCharArray(), hash));
    }

    @Test
    void rejectsEmptyValuesAndInvalidHash() {
        assertFalse(PasswordUtil.matches(new char[0], "$2a$invalid"));
        assertFalse(PasswordUtil.matches("Password123".toCharArray(), "invalid"));
        assertFalse(PasswordUtil.matches("Password123".toCharArray(), null));
    }
}
