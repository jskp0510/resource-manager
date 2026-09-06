package com.team.resourcemanager.model;

public class User {

    private int userId;
    private String loginId;
    private String password;
    private String name;
    private String role;

    public User() {
    }

    public User(String loginId, String password, String name, String role) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.role = role;
    }

    public User(int userId, String loginId, String password,
                String name, String role) {
        this.userId = userId;
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }
}