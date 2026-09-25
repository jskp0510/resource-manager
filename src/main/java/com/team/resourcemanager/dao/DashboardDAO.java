package com.team.resourcemanager.dao;

import com.team.resourcemanager.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DashboardDAO {

    // 전체 물품 수
    public int getTotalItemCount() {

        String sql = "SELECT COUNT(*) FROM item";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);

                rs.close();
                pstmt.close();
                conn.close();

                return count;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }


    // 대여 가능한 물품 수
    public int getAvailableItemCount() {

        String sql =
                "SELECT COUNT(*) FROM item WHERE status = 'AVAILABLE'";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);

                rs.close();
                pstmt.close();
                conn.close();

                return count;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }


    // 현재 대여 중인 기록 수
    public int getLoanedCount() {

        String sql =
                "SELECT COUNT(*) FROM loan WHERE status = 'LOANED'";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);

                rs.close();
                pstmt.close();
                conn.close();

                return count;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }


    // 전체 대여 기록 수
    public int getTotalLoanCount() {

        String sql = "SELECT COUNT(*) FROM loan";

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);

                rs.close();
                pstmt.close();
                conn.close();

                return count;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
}