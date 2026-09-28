package com.team.resourcemanager.service;

public class AuthenticationException extends Exception {

    public AuthenticationException() {
        super("아이디 또는 비밀번호가 올바르지 않습니다.");
    }
}
