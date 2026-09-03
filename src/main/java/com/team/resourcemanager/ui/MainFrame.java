package com.team.resourcemanager.ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Resource Manager");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 상단
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Resource Manager");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));

        JButton logoutButton = new JButton("로그아웃");

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);

        // 왼쪽 메뉴
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(7, 1, 5, 5));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        menuPanel.add(new JButton("대시보드"));
        menuPanel.add(new JButton("물품 관리"));
        menuPanel.add(new JButton("대여 관리"));
        menuPanel.add(new JButton("반납 관리"));
        menuPanel.add(new JButton("이력 조회"));
        menuPanel.add(new JButton("일정"));
        menuPanel.add(new JButton("회원 관리"));

        // 가운데 내용 영역
        JLabel contentLabel = new JLabel(
                "화면 내용이 표시되는 영역입니다.",
                SwingConstants.CENTER
        );
        contentLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));

        add(headerPanel, BorderLayout.NORTH);
        add(menuPanel, BorderLayout.WEST);
        add(contentLabel, BorderLayout.CENTER);
    }
}