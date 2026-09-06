package com.team.resourcemanager.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // DB 접속 정보
    // 각자의 로컬 MySQL 환경에 맞게 수정

    // MySQL DB 접속 주소
    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/resource_manager"
                    + "?useUnicode=true&characterEncoding=UTF-8"
                    + "&connectTimeout=5000&socketTimeout=5000";

    private static final String URL = environmentOrDefault(
            "RESOURCE_MANAGER_DB_URL", DEFAULT_URL);

    // MySQL 사용자 이름
    private static final String USER = environmentOrDefault(
            "RESOURCE_MANAGER_DB_USER", "root");

    // MySQL 비밀번호
    // DB 연결 생성
    public static Connection getConnection() throws SQLException {
        String password = System.getenv("RESOURCE_MANAGER_DB_PASSWORD");
        if (password == null) {
            throw new SQLException(
                    "환경변수 RESOURCE_MANAGER_DB_PASSWORD가 설정되지 않았습니다.");
        }
        return DriverManager.getConnection(URL, USER, password);
    }

    private static String environmentOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? defaultValue : value.trim();
    }

    private DBConnection() {
    }
}
