package com.team.resourcemanager.service;

import com.team.resourcemanager.dao.UserDAO;
import com.team.resourcemanager.model.User;
import com.team.resourcemanager.util.Constants;
import com.team.resourcemanager.util.PasswordUtil;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthServiceTest {

    @Test
    void registersAsUserWithHashedPassword() throws Exception {
        FakeUserDAO dao = new FakeUserDAO();
        AuthService service = new AuthService(dao);
        char[] password = "Password123".toCharArray();
        char[] confirm = "Password123".toCharArray();

        User registered = service.register("user_01", password, confirm, "홍길동");

        User stored = dao.findByLoginId("user_01").orElseThrow();
        assertEquals(Constants.ROLE_USER, registered.getRole());
        assertEquals(Constants.ROLE_USER, stored.getRole());
        assertNull(registered.getPassword());
        assertTrue(PasswordUtil.matches("Password123".toCharArray(), stored.getPassword()));
        assertTrue(allCleared(password));
        assertTrue(allCleared(confirm));
    }

    @Test
    void rejectsInvalidAndDuplicateRegistration() throws Exception {
        FakeUserDAO dao = new FakeUserDAO();
        AuthService service = new AuthService(dao);
        service.register("user_01", "Password123".toCharArray(),
                "Password123".toCharArray(), "홍길동");

        ValidationException duplicate = assertThrows(
                ValidationException.class,
                () -> service.register("user_01", "Password123".toCharArray(),
                        "Password123".toCharArray(), "다른사용자")
        );
        assertEquals("이미 사용 중인 아이디입니다.", duplicate.getMessage());

        assertThrows(ValidationException.class,
                () -> service.register("ab", "Password123".toCharArray(),
                        "Password123".toCharArray(), "홍길동"));
        assertThrows(ValidationException.class,
                () -> service.register("user_02", "password".toCharArray(),
                        "password".toCharArray(), "홍길동"));
        assertThrows(ValidationException.class,
                () -> service.register("user_02", "Password123".toCharArray(),
                        "Different123".toCharArray(), "홍길동"));
    }

    @Test
    void authenticatesAndUsesGenericFailureMessage() throws Exception {
        FakeUserDAO dao = new FakeUserDAO();
        AuthService service = new AuthService(dao);
        service.register("user_01", "Password123".toCharArray(),
                "Password123".toCharArray(), "홍길동");

        User user = service.authenticate(
                "user_01", "Password123".toCharArray());
        assertEquals("user_01", user.getLoginId());
        assertNull(user.getPassword());

        AuthenticationException wrongPassword = assertThrows(
                AuthenticationException.class,
                () -> service.authenticate(
                        "user_01", "WrongPassword1".toCharArray())
        );
        AuthenticationException missingUser = assertThrows(
                AuthenticationException.class,
                () -> service.authenticate(
                        "missing", "WrongPassword1".toCharArray())
        );
        assertEquals(wrongPassword.getMessage(), missingUser.getMessage());
    }

    private boolean allCleared(char[] value) {
        for (char c : value) {
            if (c != '\0') {
                return false;
            }
        }
        return true;
    }

    private static class FakeUserDAO extends UserDAO {
        private final Map<String, User> users = new HashMap<>();
        private int nextId = 1;

        @Override
        public int insert(User user) {
            user.setUserId(nextId++);
            users.put(user.getLoginId(), user);
            return user.getUserId();
        }

        @Override
        public boolean existsByLoginId(String loginId) {
            return users.containsKey(loginId);
        }

        @Override
        public Optional<User> findByLoginId(String loginId) throws SQLException {
            return Optional.ofNullable(users.get(loginId));
        }
    }
}
