package com.team.resourcemanager.util;

import com.team.resourcemanager.model.User;

public final class Session {

    private static final Session INSTANCE = new Session();

    private User currentUser;

    public static Session getInstance() {
        return INSTANCE;
    }

    public synchronized void login(User user) {
        if (user == null) {
            throw new IllegalArgumentException("로그인 사용자 정보가 없습니다.");
        }
        currentUser = user.withoutPassword();
    }

    public synchronized User getUser() {
        return currentUser == null ? null : currentUser.withoutPassword();
    }

    public synchronized boolean isLoggedIn() {
        return currentUser != null;
    }

    public synchronized boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }

    public synchronized void logout() {
        currentUser = null;
    }

    private Session() {
    }
}
