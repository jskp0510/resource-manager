package com.team.resourcemanager.ui;

import com.team.resourcemanager.dao.DashboardDAO;

import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {

    private JLabel totalItemLabel;
    private JLabel availableItemLabel;
    private JLabel loanedLabel;
    private JLabel totalLoanLabel;

    public DashboardFrame() {

        setTitle("대시보드");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 2, 20, 20));

        totalItemLabel = new JLabel("", SwingConstants.CENTER);
        availableItemLabel = new JLabel("", SwingConstants.CENTER);
        loanedLabel = new JLabel("", SwingConstants.CENTER);
        totalLoanLabel = new JLabel("", SwingConstants.CENTER);

        panel.add(totalItemLabel);
        panel.add(availableItemLabel);
        panel.add(loanedLabel);
        panel.add(totalLoanLabel);


        // 새로고침 버튼
        JButton refreshButton = new JButton("새로고침");

        refreshButton.addActionListener(e -> {
            loadDashboardData();
        });


        // 전체 배치
        JPanel mainPanel = new JPanel(new BorderLayout());

        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.add(refreshButton, BorderLayout.SOUTH);

        add(mainPanel);


        // 처음 화면 열었을 때 한 번 조회
        loadDashboardData();

        setVisible(true);
    }

    private void loadDashboardData() {

        DashboardDAO dao = new DashboardDAO();

        int totalItems = dao.getTotalItemCount();
        int availableItems = dao.getAvailableItemCount();
        int loanedCount = dao.getLoanedCount();
        int totalLoans = dao.getTotalLoanCount();

        totalItemLabel.setText(
                "<html><center>전체 물품<br><h1>"
                        + totalItems
                        + "</h1></center></html>"
        );

        availableItemLabel.setText(
                "<html><center>대여 가능<br><h1>"
                        + availableItems
                        + "</h1></center></html>"
        );

        loanedLabel.setText(
                "<html><center>대여 중<br><h1>"
                        + loanedCount
                        + "</h1></center></html>"
        );

        totalLoanLabel.setText(
                "<html><center>전체 대여 기록<br><h1>"
                        + totalLoans
                        + "</h1></center></html>"
        );
    }

    public static void main(String[] args) {
        new DashboardFrame();
    }
}