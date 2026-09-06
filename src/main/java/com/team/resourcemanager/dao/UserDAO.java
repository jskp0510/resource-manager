package com.team.resourcemanager.dao;

import com.team.resourcemanager.model.User;
import com.team.resourcemanager.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * USER 테이블의 CRUD를 담당한다.
 * 모든 SQL은 PreparedStatement와 try-with-resources를 사용한다.
 */
public class UserDAO {

    private static final String SELECT_COLUMNS =
            "user_id, login_id, password, name, role";

    public int insert(User user) throws SQLException {
        String sql = "INSERT INTO `USER` (login_id, password, name, role) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getLoginId());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getName());
            pstmt.setString(4, user.getRole());

            if (pstmt.executeUpdate() != 1) {
                throw new SQLException("사용자 등록 결과가 올바르지 않습니다.");
            }

            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("생성된 사용자 번호를 확인할 수 없습니다.");
                }
                int userId = keys.getInt(1);
                user.setUserId(userId);
                return userId;
            }
        }
    }

    public boolean existsByLoginId(String loginId) throws SQLException {
        String sql = "SELECT 1 FROM `USER` WHERE login_id = ? LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, loginId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public Optional<User> findByLoginId(String loginId) throws SQLException {
        String sql = "SELECT " + SELECT_COLUMNS
                + " FROM `USER` WHERE login_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, loginId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? Optional.of(mapUser(rs)) : Optional.empty();
            }
        }
    }

    public Optional<User> findById(int userId) throws SQLException {
        String sql = "SELECT " + SELECT_COLUMNS
                + " FROM `USER` WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? Optional.of(mapUser(rs)) : Optional.empty();
            }
        }
    }

    public List<User> findAll() throws SQLException {
        String sql = "SELECT " + SELECT_COLUMNS
                + " FROM `USER` ORDER BY user_id";
        List<User> users = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                users.add(mapUser(rs));
            }
        }
        return users;
    }

    public boolean update(User user) throws SQLException {
        String sql = "UPDATE `USER` SET login_id = ?, password = ?, "
                + "name = ?, role = ? WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getLoginId());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getName());
            pstmt.setString(4, user.getRole());
            pstmt.setInt(5, user.getUserId());
            return pstmt.executeUpdate() == 1;
        }
    }

    public boolean updateRole(int userId, String role) throws SQLException {
        String sql = "UPDATE `USER` SET role = ? WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, role);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() == 1;
        }
    }

    public boolean deleteById(int userId) throws SQLException {
        String sql = "DELETE FROM `USER` WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() == 1;
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("user_id"),
                rs.getString("login_id"),
                rs.getString("password"),
                rs.getString("name"),
                rs.getString("role")
        );
    }
}
