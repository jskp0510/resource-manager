package com.team.resourcemanager.ui;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    // 로그인 입력 필드
    private JTextField loginIdField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginFrame() {
        setTitle("Resource Manager - Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        panel.add(new JLabel("ID"));
        loginIdField = new JTextField();
        panel.add(loginIdField);

        panel.add(new JLabel("Password"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        loginButton = new JButton("로그인");
        panel.add(new JLabel());
        panel.add(loginButton);

        add(panel);

        // 현재는 화면 전환만 테스트하는 임시 동작
        loginButton.addActionListener(e -> {
            new MainFrame().setVisible(true);
            dispose();
        });
    }
}