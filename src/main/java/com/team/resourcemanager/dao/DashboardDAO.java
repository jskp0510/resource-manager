package com.team.resourcemanager.dao;

import com.team.resourcemanager.model.DashboardStats;
import com.team.resourcemanager.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** 대시보드 수치를 한 번의 조회로 읽는다. */
public class DashboardDAO {
    private static final String SUMMARY_SQL = """
            SELECT
              (SELECT COUNT(*) FROM ITEM) AS total_items,
              (SELECT COUNT(*) FROM ITEM WHERE status = 'AVAILABLE') AS available_items,
              (SELECT COUNT(*) FROM LOAN WHERE status = 'BORROWED') AS borrowed_loans,
              (SELECT COUNT(*) FROM LOAN) AS total_loans
            """;

    public DashboardStats getSummary() throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SUMMARY_SQL);
             ResultSet rs = stmt.executeQuery()) {
            if (!rs.next()) throw new SQLException("대시보드 요약 결과가 비어 있습니다.");
            return new DashboardStats(rs.getInt("total_items"), rs.getInt("available_items"),
                    rs.getInt("borrowed_loans"), rs.getInt("total_loans"));
        }
    }
}
