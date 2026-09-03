package com.team.resourcemanager.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // DB 접속 정보
    // 각자의 로컬 MySQL 환경에 맞게 수정

    // MySQL DB 접속 주소
    private static final String URL = "jdbc:mysql://localhost:3306/resource_manager";

    // MySQL 사용자 이름
    private static final String USER = "root";

    // MySQL 비밀번호
    private static final String PASSWORD = "";

    // DB 연결 생성
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}