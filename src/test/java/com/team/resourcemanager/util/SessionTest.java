package com.team.resourcemanager.util;

import com.team.resourcemanager.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SessionTest {

    private final Session session = Session.getInstance();

    @AfterEach
    void tearDown() {
        session.logout();
    }

    @Test
    void storesUserWithoutPasswordAndClearsOnLogout() {
        User user = new User(1, "admin01", "secret-hash", "관리자",
                Constants.ROLE_ADMIN);

        session.login(user);

        assertTrue(session.isLoggedIn());
        assertTrue(session.isAdmin());
        assertNull(session.getUser().getPassword());

        session.logout();
        assertFalse(session.isLoggedIn());
        assertNull(session.getUser());
    }
}
