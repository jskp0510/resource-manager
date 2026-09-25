package com.team.resourcemanager.dao;

import com.team.resourcemanager.util.DBConnection;

import com.team.resourcemanager.model.LoanScheduleResult;

import com.team.resourcemanager.model.LoanHistoryResult;

import java.util.ArrayList;
import java.util.List;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoanDAO {

    public void printAllLoans() {

        String sql = """
                SELECT
                    l.loan_id,
                    i.item_name,
                    u.name AS user_name,
                    l.start_date,
                    l.due_date,
                    l.return_date,
                    l.purpose,
                    l.status
                FROM loan l
                JOIN item i ON l.item_id = i.item_id
                JOIN user u ON l.user_id = u.user_id
                """;

        try {
            Connection conn = DBConnection.getConnection();

            PreparedStatement pstmt = conn.prepareStatement(sql);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {

                System.out.println("대여번호: " + rs.getInt("loan_id"));
                System.out.println("물품명: " + rs.getString("item_name"));
                System.out.println("사용자: " + rs.getString("user_name"));
                System.out.println("대여일: " + rs.getDate("start_date"));
                System.out.println("반납예정일: " + rs.getDate("due_date"));
                System.out.println("반납일: " + rs.getDate("return_date"));
                System.out.println("목적: " + rs.getString("purpose"));
                System.out.println("상태: " + rs.getString("status"));

                System.out.println("----------------------");
            }

            rs.close();
            pstmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printLoansByItemId(int itemId) {

        String sql = """
            SELECT
                l.loan_id,
                i.item_name,
                u.name AS user_name,
                l.start_date,
                l.due_date,
                l.return_date,
                l.purpose,
                l.status
            FROM loan l
            JOIN item i ON l.item_id = i.item_id
            JOIN user u ON l.user_id = u.user_id
            WHERE l.item_id = ?
            """;

        try {
            Connection conn = DBConnection.getConnection();

            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, itemId);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {

                System.out.println("대여번호: " + rs.getInt("loan_id"));
                System.out.println("물품명: " + rs.getString("item_name"));
                System.out.println("사용자: " + rs.getString("user_name"));
                System.out.println("대여일: " + rs.getDate("start_date"));
                System.out.println("반납예정일: " + rs.getDate("due_date"));
                System.out.println("반납일: " + rs.getDate("return_date"));
                System.out.println("목적: " + rs.getString("purpose"));
                System.out.println("상태: " + rs.getString("status"));

                System.out.println("----------------------");
            }

            rs.close();
            pstmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void printLoansByUserId(int userId) {

        String sql = """
            SELECT
                l.loan_id,
                i.item_name,
                u.name AS user_name,
                l.start_date,
                l.due_date,
                l.return_date,
                l.purpose,
                l.status
            FROM loan l
            JOIN item i ON l.item_id = i.item_id
            JOIN user u ON l.user_id = u.user_id
            WHERE l.user_id = ?
            """;

        try {
            Connection conn = DBConnection.getConnection();

            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, userId);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {

                System.out.println("대여번호: " + rs.getInt("loan_id"));
                System.out.println("물품명: " + rs.getString("item_name"));
                System.out.println("사용자: " + rs.getString("user_name"));
                System.out.println("대여일: " + rs.getDate("start_date"));
                System.out.println("반납예정일: " + rs.getDate("due_date"));
                System.out.println("반납일: " + rs.getDate("return_date"));
                System.out.println("목적: " + rs.getString("purpose"));
                System.out.println("상태: " + rs.getString("status"));

                System.out.println("----------------------");
            }

            rs.close();
            pstmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void printLoansByDateRange(String startDate, String endDate) {

        String sql = """
            SELECT
                l.loan_id,
                i.item_name,
                u.name AS user_name,
                l.start_date,
                l.due_date,
                l.return_date,
                l.purpose,
                l.status
            FROM loan l
            JOIN item i ON l.item_id = i.item_id
            JOIN user u ON l.user_id = u.user_id
            WHERE l.start_date BETWEEN ? AND ?
               OR l.due_date BETWEEN ? AND ?
            ORDER BY l.start_date
            """;

        try {
            Connection conn = DBConnection.getConnection();

            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, startDate);
            pstmt.setString(2, endDate);
            pstmt.setString(3, startDate);
            pstmt.setString(4, endDate);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {

                System.out.println("대여번호: " + rs.getInt("loan_id"));
                System.out.println("물품명: " + rs.getString("item_name"));
                System.out.println("사용자: " + rs.getString("user_name"));
                System.out.println("대여 시작일: " + rs.getDate("start_date"));
                System.out.println("반납 예정일: " + rs.getDate("due_date"));
                System.out.println("실제 반납일: " + rs.getDate("return_date"));
                System.out.println("목적: " + rs.getString("purpose"));
                System.out.println("상태: " + rs.getString("status"));

                System.out.println("----------------------");
            }

            rs.close();
            pstmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public List<LoanScheduleResult> getLoansByDateRange(
            String startDate,
            String endDate
    ) {

        List<LoanScheduleResult> list = new ArrayList<>();

        String sql = """
            SELECT
                l.loan_id,
                i.item_name,
                u.name AS user_name,
                l.start_date,
                l.due_date,
                l.return_date,
                l.purpose,
                l.status
            FROM loan l
            JOIN item i ON l.item_id = i.item_id
            JOIN user u ON l.user_id = u.user_id
            WHERE l.start_date BETWEEN ? AND ?
               OR l.due_date BETWEEN ? AND ?
            ORDER BY l.start_date
            """;

        try {
            Connection conn = DBConnection.getConnection();

            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, startDate);
            pstmt.setString(2, endDate);
            pstmt.setString(3, startDate);
            pstmt.setString(4, endDate);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {

                LoanScheduleResult loan =
                        new LoanScheduleResult(
                                rs.getInt("loan_id"),
                                rs.getString("item_name"),
                                rs.getString("user_name"),
                                rs.getDate("start_date"),
                                rs.getDate("due_date"),
                                rs.getDate("return_date"),
                                rs.getString("purpose"),
                                rs.getString("status")
                        );

                list.add(loan);
            }

            rs.close();
            pstmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    public List<LoanHistoryResult> getLoansByItemId(int itemId) {

        List<LoanHistoryResult> list = new ArrayList<>();

        String sql = """
            SELECT
                l.loan_id,
                i.item_name,
                u.name AS user_name,
                l.start_date,
                l.due_date,
                l.return_date,
                l.purpose,
                l.status
            FROM loan l
            JOIN item i ON l.item_id = i.item_id
            JOIN user u ON l.user_id = u.user_id
            WHERE l.item_id = ?
            """;

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, itemId);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {

                LoanHistoryResult loan =
                        new LoanHistoryResult(
                                rs.getInt("loan_id"),
                                rs.getString("item_name"),
                                rs.getString("user_name"),
                                rs.getDate("start_date"),
                                rs.getDate("due_date"),
                                rs.getDate("return_date"),
                                rs.getString("purpose"),
                                rs.getString("status")
                        );

                list.add(loan);
            }

            rs.close();
            pstmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    public List<LoanHistoryResult> getLoansByUserId(int userId) {

        List<LoanHistoryResult> list = new ArrayList<>();

        String sql = """
            SELECT
                l.loan_id,
                i.item_name,
                u.name AS user_name,
                l.start_date,
                l.due_date,
                l.return_date,
                l.purpose,
                l.status
            FROM loan l
            JOIN item i ON l.item_id = i.item_id
            JOIN user u ON l.user_id = u.user_id
            WHERE l.user_id = ?
            """;

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, userId);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {

                LoanHistoryResult loan =
                        new LoanHistoryResult(
                                rs.getInt("loan_id"),
                                rs.getString("item_name"),
                                rs.getString("user_name"),
                                rs.getDate("start_date"),
                                rs.getDate("due_date"),
                                rs.getDate("return_date"),
                                rs.getString("purpose"),
                                rs.getString("status")
                        );

                list.add(loan);
            }

            rs.close();
            pstmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    public boolean loanItem(
            int userId,
            int itemId,
            String startDate,
            String dueDate,
            String purpose
    ) {

        String checkSql =
                "SELECT status FROM item WHERE item_id = ?";

        String loanSql = """
            INSERT INTO loan
            (user_id, item_id, start_date, due_date, purpose, status)
            VALUES (?, ?, ?, ?, ?, 'LOANED')
            """;

        String itemSql = """
            UPDATE item
            SET status = 'LOANED'
            WHERE item_id = ?
            """;

        try {
            Connection conn = DBConnection.getConnection();

            // 1. 먼저 물품 상태 확인
            PreparedStatement checkStmt =
                    conn.prepareStatement(checkSql);

            checkStmt.setInt(1, itemId);

            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("존재하지 않는 물품입니다.");

                rs.close();
                checkStmt.close();
                conn.close();

                return false;
            }

            String status = rs.getString("status");

            // 이미 대여 중이면 막기
            if (!status.equals("AVAILABLE")) {

                System.out.println("현재 대여할 수 없는 물품입니다.");

                rs.close();
                checkStmt.close();
                conn.close();

                return false;
            }

            rs.close();
            checkStmt.close();


            // 2. loan 테이블에 대여 기록 추가
            PreparedStatement loanStmt =
                    conn.prepareStatement(loanSql);

            loanStmt.setInt(1, userId);
            loanStmt.setInt(2, itemId);
            loanStmt.setString(3, startDate);
            loanStmt.setString(4, dueDate);
            loanStmt.setString(5, purpose);

            loanStmt.executeUpdate();


            // 3. item 상태 변경
            PreparedStatement itemStmt =
                    conn.prepareStatement(itemSql);

            itemStmt.setInt(1, itemId);

            itemStmt.executeUpdate();


            loanStmt.close();
            itemStmt.close();
            conn.close();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
    public boolean returnItem(
            int loanId,
            int itemId,
            String returnDate
    ) {

        String checkSql = """
            SELECT status
            FROM loan
            WHERE loan_id = ?
              AND item_id = ?
            """;

        String loanSql = """
            UPDATE loan
            SET return_date = ?,
                status = 'RETURNED'
            WHERE loan_id = ?
              AND item_id = ?
            """;

        String itemSql = """
            UPDATE item
            SET status = 'AVAILABLE'
            WHERE item_id = ?
            """;

        try {
            Connection conn = DBConnection.getConnection();

            // 1. 대여 기록 확인
            PreparedStatement checkStmt =
                    conn.prepareStatement(checkSql);

            checkStmt.setInt(1, loanId);
            checkStmt.setInt(2, itemId);

            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {

                rs.close();
                checkStmt.close();
                conn.close();

                return false;
            }

            String status = rs.getString("status");

            // 이미 반납했거나 LOANED 상태가 아니면 막기
            if (!status.equals("LOANED")) {

                rs.close();
                checkStmt.close();
                conn.close();

                return false;
            }

            rs.close();
            checkStmt.close();


            // 2. loan 상태를 RETURNED로 변경
            PreparedStatement loanStmt =
                    conn.prepareStatement(loanSql);

            loanStmt.setString(1, returnDate);
            loanStmt.setInt(2, loanId);
            loanStmt.setInt(3, itemId);

            loanStmt.executeUpdate();


            // 3. item을 다시 AVAILABLE로 변경
            PreparedStatement itemStmt =
                    conn.prepareStatement(itemSql);

            itemStmt.setInt(1, itemId);

            itemStmt.executeUpdate();


            loanStmt.close();
            itemStmt.close();
            conn.close();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
    public List<LoanHistoryResult> getLoansByItemName(String itemName) {

        List<LoanHistoryResult> list = new ArrayList<>();

        String sql = """
            SELECT
                l.loan_id,
                i.item_name,
                u.name AS user_name,
                l.start_date,
                l.due_date,
                l.return_date,
                l.purpose,
                l.status
            FROM loan l
            JOIN item i ON l.item_id = i.item_id
            JOIN user u ON l.user_id = u.user_id
            WHERE i.item_name LIKE ?
            """;

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, "%" + itemName + "%");

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {

                LoanHistoryResult loan =
                        new LoanHistoryResult(
                                rs.getInt("loan_id"),
                                rs.getString("item_name"),
                                rs.getString("user_name"),
                                rs.getDate("start_date"),
                                rs.getDate("due_date"),
                                rs.getDate("return_date"),
                                rs.getString("purpose"),
                                rs.getString("status")
                        );

                list.add(loan);
            }

            rs.close();
            pstmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    public List<LoanHistoryResult> getLoansByUserName(String userName) {

        List<LoanHistoryResult> list = new ArrayList<>();

        String sql = """
            SELECT
                l.loan_id,
                i.item_name,
                u.name AS user_name,
                l.start_date,
                l.due_date,
                l.return_date,
                l.purpose,
                l.status
            FROM loan l
            JOIN item i ON l.item_id = i.item_id
            JOIN user u ON l.user_id = u.user_id
            WHERE u.name LIKE ?
            """;

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, "%" + userName + "%");

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {

                LoanHistoryResult loan =
                        new LoanHistoryResult(
                                rs.getInt("loan_id"),
                                rs.getString("item_name"),
                                rs.getString("user_name"),
                                rs.getDate("start_date"),
                                rs.getDate("due_date"),
                                rs.getDate("return_date"),
                                rs.getString("purpose"),
                                rs.getString("status")
                        );

                list.add(loan);
            }

            rs.close();
            pstmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    public List<LoanHistoryResult> getOverdueLoans() {

        List<LoanHistoryResult> list = new ArrayList<>();

        String sql = """
            SELECT
                l.loan_id,
                i.item_name,
                u.name AS user_name,
                l.start_date,
                l.due_date,
                l.return_date,
                l.purpose,
                l.status
            FROM loan l
            JOIN item i ON l.item_id = i.item_id
            JOIN user u ON l.user_id = u.user_id
            WHERE l.due_date < CURDATE()
              AND l.return_date IS NULL
            ORDER BY l.due_date
            """;

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {

                LoanHistoryResult loan =
                        new LoanHistoryResult(
                                rs.getInt("loan_id"),
                                rs.getString("item_name"),
                                rs.getString("user_name"),
                                rs.getDate("start_date"),
                                rs.getDate("due_date"),
                                rs.getDate("return_date"),
                                rs.getString("purpose"),
                                rs.getString("status")
                        );

                list.add(loan);
            }

            rs.close();
            pstmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
