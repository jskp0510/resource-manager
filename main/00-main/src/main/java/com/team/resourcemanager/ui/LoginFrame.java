package com.team.resourcemanager.ui;

import com.team.resourcemanager.model.User;
import com.team.resourcemanager.service.AuthService;
import com.team.resourcemanager.service.AuthenticationException;
import com.team.resourcemanager.service.ValidationException;
import com.team.resourcemanager.util.Session;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class LoginFrame extends JFrame {

    private final AuthService authService = new AuthService();

    private final JTextField loginIdField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JButton loginButton = new JButton("로그인");
    private final JButton signUpButton = new JButton("회원가입");

    public LoginFrame() {
        setTitle("Resource Manager - Login");
        setSize(420, 280);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(35, 35, 15, 35));
        formPanel.add(new JLabel("ID"));
        formPanel.add(loginIdField);
        formPanel.add(new JLabel("Password"));
        formPanel.add(passwordField);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 35, 35, 35));
        buttonPanel.add(signUpButton);
        buttonPanel.add(loginButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(loginButton);
        loginButton.addActionListener(e -> attemptLogin());
        signUpButton.addActionListener(e -> openSignUp());
    }

    private void attemptLogin() {
        String loginId = loginIdField.getText();
        char[] password = passwordField.getPassword();
        setBusy(true);

        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override
            protected User doInBackground() throws Exception {
                return authService.authenticate(loginId, password);
            }

            @Override
            protected void done() {
                try {
                    User user = get();
                    Session.getInstance().login(user);
                    new MainFrame().setVisible(true);
                    dispose();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    showError("로그인 작업이 중단되었습니다.");
                } catch (ExecutionException e) {
                    handleLoginFailure(e.getCause());
                } finally {
                    passwordField.setText("");
                    setBusy(false);
                }
            }
        };
        worker.execute();
    }

    private void openSignUp() {
        setVisible(false);
        new SignUpFrame(this).setVisible(true);
    }

    void returnFromSignUp(String registeredLoginId) {
        if (registeredLoginId != null) {
            loginIdField.setText(registeredLoginId);
        }
        passwordField.setText("");
        setLocationRelativeTo(null);
        setVisible(true);
        passwordField.requestFocusInWindow();
    }

    private void handleLoginFailure(Throwable cause) {
        if (cause instanceof ValidationException
                || cause instanceof AuthenticationException) {
            showError(cause.getMessage());
        } else if (cause instanceof SQLException) {
            showError("데이터베이스에 연결할 수 없습니다. MySQL 실행 상태를 확인해주세요.");
            cause.printStackTrace();
        } else {
            showError("로그인 처리 중 예상하지 못한 오류가 발생했습니다.");
            cause.printStackTrace();
        }
    }

    private void setBusy(boolean busy) {
        loginButton.setEnabled(!busy);
        signUpButton.setEnabled(!busy);
        loginIdField.setEnabled(!busy);
        passwordField.setEnabled(!busy);
        setCursor(Cursor.getPredefinedCursor(
                busy ? Cursor.WAIT_CURSOR : Cursor.DEFAULT_CURSOR));
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this, message, "로그인 실패", JOptionPane.ERROR_MESSAGE);
    }
}
