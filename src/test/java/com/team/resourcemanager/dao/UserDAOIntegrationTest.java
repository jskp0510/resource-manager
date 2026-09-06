package com.team.resourcemanager.dao;

import com.team.resourcemanager.model.User;
import com.team.resourcemanager.util.Constants;
import com.team.resourcemanager.util.PasswordUtil;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserDAOIntegrationTest {

    @Test
    void performsCrudAgainstLocalDatabase() throws Exception {
        Assumptions.assumeTrue(
                System.getenv("RESOURCE_MANAGER_DB_PASSWORD") != null,
                "로컬 DB 비밀번호 환경변수가 없어 통합 테스트를 건너뜁니다."
        );

        UserDAO dao = new UserDAO();
        String loginId = "itest_" + UUID.randomUUID().toString().substring(0, 8);
        User user = new User(
                loginId,
                PasswordUtil.hash("Password123".toCharArray()),
                "통합테스트",
                Constants.ROLE_USER
        );

        try {
            int userId = dao.insert(user);
            assertTrue(userId > 0);
            assertTrue(dao.existsByLoginId(loginId));

            User found = dao.findByLoginId(loginId).orElseThrow();
            assertEquals("통합테스트", found.getName());

            found.setName("수정테스트");
            found.setRole(Constants.ROLE_ADMIN);
            assertTrue(dao.update(found));

            User updated = dao.findById(userId).orElseThrow();
            assertEquals("수정테스트", updated.getName());
            assertEquals(Constants.ROLE_ADMIN, updated.getRole());
            assertFalse(dao.findAll().isEmpty());
        } finally {
            if (user.getUserId() > 0) {
                dao.deleteById(user.getUserId());
            }
        }

        assertFalse(dao.existsByLoginId(loginId));
    }
}
