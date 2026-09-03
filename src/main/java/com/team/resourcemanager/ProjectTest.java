package com.team.resourcemanager;

import com.team.resourcemanager.ui.LoginFrame;
import com.team.resourcemanager.util.Constants;
import com.team.resourcemanager.util.DBConnection;
import com.team.resourcemanager.util.DateUtil;

import javax.swing.SwingUtilities;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class ProjectTest {

    public static void main(String[] args) {

        // DB 연결 테스트1
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT 1");
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                System.out.println("DB 연결 성공!");
                System.out.println("SELECT 1 결과: " + rs.getInt(1));
            }

        } catch (Exception e) {
            System.out.println("DB 연결 실패!");
            e.printStackTrace();
        }

        // DB 연결 테스트2
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM USER");
            ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                System.out.println("DB 연결 성공!");
                System.out.println("USER 테이블 조회 성공!");
                System.out.println("현재 USER 데이터 수: " + rs.getInt(1));
            }

        } catch (Exception e) {
            System.out.println("DB 연결 실패!");
            e.printStackTrace();
        }

        // 2. Constants 테스트
        System.out.println("권한: " + Constants.ROLE_USER);
        System.out.println("권한: " + Constants.ROLE_ADMIN);

        System.out.println("ITEM: " + Constants.ITEM_AVAILABLE);
        System.out.println("ITEM: " + Constants.ITEM_MAINTENANCE);
        System.out.println("ITEM: " + Constants.ITEM_BORROWED);

        System.out.println("LOAN: " + Constants.LOAN_REQUESTED);
        System.out.println("LOAN: " + Constants.LOAN_BORROWED);
        System.out.println("LOAN: " + Constants.LOAN_REJECTED);
        System.out.println("LOAN: " + Constants.LOAN_RETURNED);
        System.out.println("LOAN: " + Constants.LOAN_OVERDUE);

        // 3. DateUtil 테스트
        LocalDate today = DateUtil.today();
        LocalDate dueDate = DateUtil.parse("2026-09-10");

        System.out.println("오늘: " + DateUtil.format(today));
        System.out.println("반납 예정일: " + DateUtil.format(dueDate));
        System.out.println("날짜 차이: "
                + DateUtil.daysBetween(today, dueDate) + "일");

        // 4. GUI 실행 테스트
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}