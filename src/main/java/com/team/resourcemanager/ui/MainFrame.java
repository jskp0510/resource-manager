package com.team.resourcemanager.ui;

import com.team.resourcemanager.model.User;
import com.team.resourcemanager.util.Session;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;

import com.team.resourcemanager.util.DBConnection;

public class MainFrame extends JFrame {

    private final Session session = Session.getInstance();
    private final JLabel contentLabel = new JLabel(
            "메뉴를 선택해주세요.", SwingConstants.CENTER);

    public MainFrame() {
        User user = session.getUser();
        if (user == null) {
            throw new IllegalStateException("로그인 후에만 메인 화면을 열 수 있습니다.");
        }

        setTitle("Resource Manager");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createHeaderPanel(user), BorderLayout.NORTH);
        add(createMenuPanel(user), BorderLayout.WEST);

        contentLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        add(contentLabel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel(User user) {
        JPanel headerPanel = new JPanel(new BorderLayout(15, 0));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel titleLabel = new JLabel("Resource Manager");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));

        String roleText = user.isAdmin() ? "관리자" : "일반 사용자";
        JLabel userLabel = new JLabel(
                user.getName() + "님 | " + roleText, SwingConstants.RIGHT);

        JButton logoutButton = new JButton("로그아웃");
        logoutButton.addActionListener(e -> logout());

        JPanel rightPanel = new JPanel(new BorderLayout(10, 0));
        rightPanel.add(userLabel, BorderLayout.CENTER);
        rightPanel.add(logoutButton, BorderLayout.EAST);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);
        return headerPanel;
    }

    private JPanel createMenuPanel(User user) {
        JPanel menuPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton dashboardButton = new JButton("대시보드");
        dashboardButton.addActionListener(e -> new DashboardFrame().setVisible(true));
        menuPanel.add(dashboardButton);
        if (user.isAdmin()) {
            JButton resourceButton = new JButton("물품 관리");
            resourceButton.addActionListener(e -> openResourceManager(user));
            menuPanel.add(resourceButton);
        }
        JButton itemSearchButton = new JButton("물품 검색");
        itemSearchButton.addActionListener(e -> new ItemSearchFrame().setVisible(true));
        menuPanel.add(itemSearchButton);
        JButton loanButton = new JButton(user.isAdmin() ? "대여 관리" : "대여 신청");
        loanButton.addActionListener(e -> openLoanManager(user));
        menuPanel.add(loanButton);
        JButton returnButton = new JButton(user.isAdmin() ? "반납 관리" : "반납 신청");
        returnButton.addActionListener(e -> openReturnManager(user));
        menuPanel.add(returnButton);
        JButton historyButton = new JButton("이력 조회");
        historyButton.addActionListener(e -> new LoanHistoryFrame(user).setVisible(true));
        menuPanel.add(historyButton);
        JButton scheduleButton = new JButton("일정");
        scheduleButton.addActionListener(e -> new LoanScheduleFrame(user).setVisible(true));
        menuPanel.add(scheduleButton);

        if (user.isAdmin()) {
            JButton memberManagementButton = new JButton("회원 관리");
            memberManagementButton.addActionListener(
                    e -> new MemberManagementFrame().setVisible(true));
            menuPanel.add(memberManagementButton);
        }

        return menuPanel;
    }

    private void openResourceManager(User user) {
        JFrame frame = new JFrame("카테고리 · 물품 관리");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(new ResourceManagerView(user.isAdmin()));
        frame.setSize(850, 520);
        frame.setLocationRelativeTo(this);
        frame.setVisible(true);
    }

    private void openLoanManager(User user) {
        try {
            Connection connection = DBConnection.getConnection();
            JFrame frame = new JFrame(user.isAdmin() ? "대여 신청 관리" : "대여 신청");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setContentPane(user.isAdmin()
                    ? new LoanAdminPanel(connection)
                    : new LoanRequestPanel(connection, user.getUserId()));
            frame.setSize(800, 500);
            frame.setLocationRelativeTo(this);
            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    try {
                        connection.close();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(MainFrame.this,
                                "대여 화면 DB 연결 종료 중 오류: " + ex.getMessage(),
                                "DB 오류", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            frame.setVisible(true);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "대여 화면 DB 연결에 실패했습니다: " + e.getMessage(),
                    "DB 오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openReturnManager(User user) {
        try {
            Connection connection = DBConnection.getConnection();
            JFrame frame = new JFrame(user.isAdmin() ? "반납 신청 관리" : "반납 신청");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setContentPane(user.isAdmin()
                    ? new ReturnAdminPanel(connection)
                    : new ReturnRequestPanel(connection, user.getUserId()));
            frame.setSize(800, 500);
            frame.setLocationRelativeTo(this);
            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e) {
                    try {
                        connection.close();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(MainFrame.this,
                                "반납 화면 DB 연결 종료 중 오류: " + ex.getMessage(),
                                "DB 오류", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            frame.setVisible(true);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "반납 화면 DB 연결에 실패했습니다: " + e.getMessage(),
                    "DB 오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void logout() {
        int result = JOptionPane.showConfirmDialog(
                this,
                "로그아웃하시겠습니까?",
                "로그아웃",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            session.logout();
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
}
