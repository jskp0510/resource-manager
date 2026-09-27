package com.team.resourcemanager.dao;

import com.team.resourcemanager.model.LoanHistoryResult;
import com.team.resourcemanager.model.LoanScheduleResult;
import com.team.resourcemanager.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** 대여·물품 정보를 조회만 하는 통합 DAO. */
public class HistoryDAO {
    public List<LoanHistoryResult> searchHistory(String itemName, String userName,
            Integer userId, boolean admin) throws SQLException {
        String sql = """
                SELECT l.loan_id, i.item_name, u.name AS user_name, l.start_date, l.due_date,
                       l.return_date, l.purpose, l.status
                FROM LOAN l JOIN ITEM i ON i.item_id = l.item_id
                JOIN USER u ON u.user_id = l.user_id
                WHERE (? = TRUE OR l.user_id = ?)
                  AND (? = '' OR i.item_name LIKE ?)
                  AND (? = '' OR u.name LIKE ?)
                ORDER BY l.start_date DESC, l.loan_id DESC
                """;
        String itemFilter = itemName == null ? "" : itemName.trim();
        String userFilter = userName == null ? "" : userName.trim();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, admin);
            stmt.setInt(2, userId == null ? -1 : userId);
            stmt.setString(3, itemFilter);
            stmt.setString(4, "%" + itemFilter + "%");
            stmt.setString(5, userFilter);
            stmt.setString(6, "%" + userFilter + "%");
            return readHistory(stmt);
        }
    }

    public List<LoanHistoryResult> findHistoryByItemName(String itemName, Integer userId, boolean admin)
            throws SQLException {
        String sql = """
                SELECT l.loan_id, i.item_name, u.name AS user_name, l.start_date, l.due_date,
                       l.return_date, l.purpose, l.status
                FROM LOAN l JOIN ITEM i ON i.item_id = l.item_id
                JOIN USER u ON u.user_id = l.user_id
                WHERE (? = TRUE OR l.user_id = ?)
                  AND i.item_name LIKE ?
                ORDER BY l.start_date DESC, l.loan_id DESC
                """;
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, admin);
            stmt.setInt(2, userId == null ? -1 : userId);
            stmt.setString(3, "%" + itemName.trim() + "%");
            return readHistory(stmt);
        }
    }

    public List<LoanHistoryResult> findHistoryByUser(String nameFilter, Integer userId, boolean admin)
            throws SQLException {
        String sql = """
                SELECT l.loan_id, i.item_name, u.name AS user_name, l.start_date, l.due_date,
                       l.return_date, l.purpose, l.status
                FROM LOAN l JOIN ITEM i ON i.item_id = l.item_id
                JOIN USER u ON u.user_id = l.user_id
                WHERE (? = TRUE OR l.user_id = ?)
                  AND u.name LIKE ?
                ORDER BY l.start_date DESC, l.loan_id DESC
                """;
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, admin);
            stmt.setInt(2, userId == null ? -1 : userId);
            stmt.setString(3, "%" + nameFilter.trim() + "%");
            return readHistory(stmt);
        }
    }

    public List<LoanHistoryResult> findOverdue(Integer userId, boolean admin) throws SQLException {
        String sql = """
                SELECT l.loan_id, i.item_name, u.name AS user_name, l.start_date, l.due_date,
                       l.return_date, l.purpose, l.status
                FROM LOAN l JOIN ITEM i ON i.item_id = l.item_id
                JOIN USER u ON u.user_id = l.user_id
                WHERE (? = TRUE OR l.user_id = ?)
                  AND l.due_date < CURDATE()
                  AND l.return_date IS NULL
                ORDER BY l.due_date, l.loan_id
                """;
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, admin);
            stmt.setInt(2, userId == null ? -1 : userId);
            return readHistory(stmt);
        }
    }

    public List<LoanScheduleResult> findSchedule(LocalDate from, LocalDate to, Integer userId, boolean admin)
            throws SQLException {
        StringBuilder sql = new StringBuilder("""
                SELECT l.loan_id, i.item_name, u.name AS user_name, l.start_date, l.due_date,
                       l.return_date, l.purpose, l.status
                FROM LOAN l JOIN ITEM i ON i.item_id = l.item_id
                JOIN USER u ON u.user_id = l.user_id
                WHERE (? = TRUE OR l.user_id = ?)
                """);
        if (from != null || to != null) {
            sql.append("AND (");
            appendDateRangeCondition(sql, "l.start_date", from, to);
            sql.append(" OR ");
            appendDateRangeCondition(sql, "l.due_date", from, to);
            sql.append(") ");
        }
        sql.append("ORDER BY l.start_date, l.due_date, l.loan_id");
        List<LoanScheduleResult> rows = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            stmt.setBoolean(1, admin);
            stmt.setInt(2, userId == null ? -1 : userId);
            int parameterIndex = 3;
            if (from != null || to != null) {
                for (int range = 0; range < 2; range++) {
                    if (from != null) {
                        stmt.setDate(parameterIndex++, Date.valueOf(from));
                    }
                    if (to != null) {
                        stmt.setDate(parameterIndex++, Date.valueOf(to));
                    }
                }
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rows.add(new LoanScheduleResult(rs.getInt("loan_id"), rs.getString("item_name"),
                            rs.getString("user_name"), rs.getDate("start_date"), rs.getDate("due_date"),
                            rs.getDate("return_date"), rs.getString("purpose"), rs.getString("status")));
                }
            }
        }
        return rows;
    }

    private void appendDateRangeCondition(StringBuilder sql, String column, LocalDate from, LocalDate to) {
        sql.append("(");
        if (from != null) {
            sql.append(column).append(" >= ?");
        }
        if (to != null) {
            if (from != null) {
                sql.append(" AND ");
            }
            sql.append(column).append(" <= ?");
        }
        sql.append(")");
    }

    private List<LoanHistoryResult> readHistory(PreparedStatement stmt) throws SQLException {
        List<LoanHistoryResult> rows = new ArrayList<>();
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                rows.add(new LoanHistoryResult(rs.getInt("loan_id"), rs.getString("item_name"),
                        rs.getString("user_name"), rs.getDate("start_date"), rs.getDate("due_date"),
                        rs.getDate("return_date"), rs.getString("purpose"), rs.getString("status")));
            }
        }
        return rows;
    }
}
