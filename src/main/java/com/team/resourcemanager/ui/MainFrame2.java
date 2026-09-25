package com.team.resourcemanager.ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame2 extends JFrame {

    public MainFrame2() {

        setTitle("자원 관리 시스템");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();

        panel.setLayout(
                new GridLayout(6, 1, 10, 10)
        );

        JButton itemSearchButton =
                new JButton("물품 검색");

        JButton scheduleButton =
                new JButton("대여 일정 조회");

        JButton dashboardButton =
                new JButton("대시보드");

        JButton loanHistoryButton =
                new JButton("대여 이력 조회");

        JButton loanManageButton =
                new JButton("대여 / 반납 관리");

        JButton exitButton =
                new JButton("종료");

        // 물품 검색 버튼
        itemSearchButton.addActionListener(e -> {

            new ItemSearchFrame();

        });


        // 대여 일정 조회 버튼
        scheduleButton.addActionListener(e -> {

            new LoanScheduleFrame();

        });


        // 대시보드 버튼
        dashboardButton.addActionListener(e -> {

            new DashboardFrame();

        });


        // 종료 버튼
        exitButton.addActionListener(e -> {

            System.exit(0);

        });

        loanHistoryButton.addActionListener(e -> {

            new LoanHistoryFrame();

        });

        loanManageButton.addActionListener(e -> {

            new LoanManageFrame();

        });


        panel.add(itemSearchButton);
        panel.add(scheduleButton);
        panel.add(dashboardButton);
        panel.add(loanHistoryButton);
        panel.add(loanManageButton);
        panel.add(exitButton);

        add(panel);

        setVisible(true);
    }


    public static void main(String[] args) {

        new MainFrame2();

    }
}