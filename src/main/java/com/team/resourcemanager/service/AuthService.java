package com.team.resourcemanager.service;

import com.team.resourcemanager.dao.UserDAO;
import com.team.resourcemanager.model.User;
import com.team.resourcemanager.util.Constants;
import com.team.resourcemanager.util.PasswordUtil;

import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;

public class AuthService {

    private static final Pattern LOGIN_ID_PATTERN =
            Pattern.compile("^[A-Za-z0-9_]{4,20}$");

    private final UserDAO userDAO;

    public AuthService() {
        this(new UserDAO());
    }

    AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User register(String loginId, char[] password,
                         char[] passwordConfirm, String name)
            throws ValidationException, SQLException {

        String normalizedId = normalizeLoginId(loginId);
        String normalizedName = normalizeName(name);

        try {
            validateLoginId(normalizedId);
            validateName(normalizedName);
            validatePassword(password, passwordConfirm);

            if (userDAO.existsByLoginId(normalizedId)) {
                throw new ValidationException("이미 사용 중인 아이디입니다.");
            }

            String passwordHash = PasswordUtil.hash(password);
            User user = new User(
                    normalizedId,
                    passwordHash,
                    normalizedName,
                    Constants.ROLE_USER
            );

            try {
                userDAO.insert(user);
            } catch (SQLIntegrityConstraintViolationException e) {
                throw new ValidationException("이미 사용 중인 아이디입니다.");
            }
            return user.withoutPassword();
        } finally {
            clear(password);
            clear(passwordConfirm);
        }
    }

    public User authenticate(String loginId, char[] password)
            throws ValidationException, AuthenticationException, SQLException {

        String normalizedId = normalizeLoginId(loginId);
        try {
            if (normalizedId.isEmpty() || password == null || password.length == 0) {
                throw new ValidationException("아이디와 비밀번호를 입력해주세요.");
            }

            Optional<User> found = userDAO.findByLoginId(normalizedId);
            if (found.isEmpty()
                    || !PasswordUtil.matches(password, found.get().getPassword())) {
                throw new AuthenticationException();
            }
            return found.get().withoutPassword();
        } finally {
            clear(password);
        }
    }

    private void validateLoginId(String loginId) throws ValidationException {
        if (!LOGIN_ID_PATTERN.matcher(loginId).matches()) {
            throw new ValidationException(
                    "아이디는 영문, 숫자, 밑줄을 사용해 4~20자로 입력해주세요.");
        }
    }

    private void validateName(String name) throws ValidationException {
        if (name.length() < 2 || name.length() > 20) {
            throw new ValidationException("이름은 2~20자로 입력해주세요.");
        }
        if (name.chars().anyMatch(Character::isISOControl)) {
            throw new ValidationException("이름에 사용할 수 없는 문자가 있습니다.");
        }
    }

    private void validatePassword(char[] password, char[] passwordConfirm)
            throws ValidationException {
        if (password == null || passwordConfirm == null
                || password.length == 0 || passwordConfirm.length == 0) {
            throw new ValidationException("비밀번호와 비밀번호 확인을 입력해주세요.");
        }
        if (!Arrays.equals(password, passwordConfirm)) {
            throw new ValidationException("비밀번호가 서로 일치하지 않습니다.");
        }
        if (password.length < PasswordUtil.MIN_LENGTH) {
            throw new ValidationException("비밀번호는 8자 이상이어야 합니다.");
        }
        if (PasswordUtil.utf8Length(password) > PasswordUtil.MAX_BCRYPT_BYTES) {
            throw new ValidationException("비밀번호가 너무 깁니다.");
        }

        boolean hasLetter = false;
        boolean hasDigit = false;
        for (char c : password) {
            hasLetter |= Character.isLetter(c);
            hasDigit |= Character.isDigit(c);
        }
        if (!hasLetter || !hasDigit) {
            throw new ValidationException("비밀번호에는 문자와 숫자가 모두 포함되어야 합니다.");
        }
    }

    private String normalizeLoginId(String loginId) {
        return loginId == null ? "" : loginId.trim();
    }

    private String normalizeName(String name) {
        return name == null ? "" : name.trim().replaceAll("\\s+", " ");
    }

    private void clear(char[] value) {
        if (value != null) {
            Arrays.fill(value, '\0');
        }
    }
}
