package com.team.resourcemanager.ui;

import com.team.resourcemanager.dao.DashboardDAO;
import com.team.resourcemanager.model.DashboardStats;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class DashboardFrame extends JFrame {
    private final DashboardDAO dashboardDAO = new DashboardDAO();
    private final JLabel totalItemLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel availableItemLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel borrowedLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel totalLoanLabel = new JLabel("", SwingConstants.CENTER);

    public DashboardFrame() {
        setTitle("대시보드");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(700, 400);
        setLocationRelativeTo(null);
        JPanel panel = new JPanel(new GridLayout(2, 2, 20, 20));
        panel.add(totalItemLabel);
        panel.add(availableItemLabel);
        panel.add(borrowedLabel);
        panel.add(totalLoanLabel);

        JButton refresh = new JButton("새로고침");
        refresh.addActionListener(e -> loadSummary());
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.add(refresh, BorderLayout.SOUTH);
        add(mainPanel);
        loadSummary();
    }

    private void loadSummary() {
        try {
            DashboardStats stats = dashboardDAO.getSummary();
            totalItemLabel.setText(cardText("전체 물품", stats.totalItems()));
            availableItemLabel.setText(cardText("대여 가능", stats.availableItems()));
            borrowedLabel.setText(cardText("대여 중", stats.borrowedItems()));
            totalLoanLabel.setText(cardText("전체 대여 기록", stats.totalLoans()));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "대시보드를 불러오지 못했습니다: " + e.getMessage(),
                    "DB 오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String cardText(String title, int value) {
        return "<html><center>" + title + "<br><h1>" + value + "</h1></center></html>";
    }
}
