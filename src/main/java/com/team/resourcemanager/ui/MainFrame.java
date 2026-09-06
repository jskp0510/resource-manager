package com.team.resourcemanager.ui;

import com.team.resourcemanager.model.User;
import com.team.resourcemanager.util.Constants;
import com.team.resourcemanager.util.Session;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

public class MainFrame extends JFrame {

    private final Session session = Session.getInstance();
    private final JLabel contentLabel = new JLabel(
            "화면 내용이 표시되는 영역입니다.", SwingConstants.CENTER);

    public MainFrame() {
        User user = session.getUser();
        if (user == null) {
            throw new IllegalStateException("로그인 후에만 메인 화면을 열 수 있습니다.");
        }

        setTitle("Resource Manager");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel headerPanel = createHeaderPanel(user);
        JPanel menuPanel = createMenuPanel();

        contentLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));

        add(headerPanel, BorderLayout.NORTH);
        add(menuPanel, BorderLayout.WEST);
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

    private JPanel createMenuPanel() {
        JPanel menuPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        addMenuButton(menuPanel, "대시보드", null);
        addMenuButton(menuPanel, "물품 관리", Constants.ROLE_ADMIN);
        addMenuButton(menuPanel, "대여 관리", null);
        addMenuButton(menuPanel, "반납 관리", null);
        addMenuButton(menuPanel, "이력 조회", null);
        addMenuButton(menuPanel, "일정", null);
        addMenuButton(menuPanel, "회원 관리", Constants.ROLE_ADMIN);

        return menuPanel;
    }

    private void addMenuButton(JPanel panel, String text, String requiredRole) {
        if (Constants.ROLE_ADMIN.equals(requiredRole) && !session.isAdmin()) {
            return;
        }

        JButton button = new JButton(text);
        button.addActionListener(e -> {
            if (Constants.ROLE_ADMIN.equals(requiredRole) && !session.isAdmin()) {
                JOptionPane.showMessageDialog(
                        this,
                        "관리자만 사용할 수 있는 기능입니다.",
                        "접근 권한 없음",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            contentLabel.setText(text + " 화면입니다.");
        });
        panel.add(button);
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
