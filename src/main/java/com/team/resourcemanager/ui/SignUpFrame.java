package com.team.resourcemanager.ui;

import com.team.resourcemanager.model.User;
import com.team.resourcemanager.service.AuthService;
import com.team.resourcemanager.service.ValidationException;

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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class SignUpFrame extends JFrame {

    private final LoginFrame loginFrame;
    private final AuthService authService = new AuthService();

    private final JTextField loginIdField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JPasswordField passwordConfirmField = new JPasswordField();
    private final JTextField nameField = new JTextField();
    private final JButton cancelButton = new JButton("취소");
    private final JButton registerButton = new JButton("가입");

    private boolean returning;

    public SignUpFrame(LoginFrame loginFrame) {
        this.loginFrame = loginFrame;

        setTitle("Resource Manager - 회원가입");
        setSize(460, 350);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(loginFrame);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(35, 35, 15, 35));
        formPanel.add(new JLabel("아이디"));
        formPanel.add(loginIdField);
        formPanel.add(new JLabel("비밀번호"));
        formPanel.add(passwordField);
        formPanel.add(new JLabel("비밀번호 확인"));
        formPanel.add(passwordConfirmField);
        formPanel.add(new JLabel("이름"));
        formPanel.add(nameField);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 35, 35, 35));
        buttonPanel.add(cancelButton);
        buttonPanel.add(registerButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(registerButton);
        registerButton.addActionListener(e -> attemptRegister());
        cancelButton.addActionListener(e -> returnToLogin(null));
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                returnToLogin(null);
            }
        });
    }

    private void attemptRegister() {
        String loginId = loginIdField.getText();
        char[] password = passwordField.getPassword();
        char[] passwordConfirm = passwordConfirmField.getPassword();
        String name = nameField.getText();
        setBusy(true);

        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override
            protected User doInBackground() throws Exception {
                return authService.register(
                        loginId, password, passwordConfirm, name);
            }

            @Override
            protected void done() {
                try {
                    User user = get();
                    JOptionPane.showMessageDialog(
                            SignUpFrame.this,
                            "회원가입이 완료되었습니다.",
                            "회원가입 성공",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    returnToLogin(user.getLoginId());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    showError("회원가입 작업이 중단되었습니다.");
                } catch (ExecutionException e) {
                    handleRegisterFailure(e.getCause());
                } finally {
                    passwordField.setText("");
                    passwordConfirmField.setText("");
                    setBusy(false);
                }
            }
        };
        worker.execute();
    }

    private void handleRegisterFailure(Throwable cause) {
        if (cause instanceof ValidationException) {
            showError(cause.getMessage());
        } else if (cause instanceof SQLException) {
            showError("데이터베이스에 연결할 수 없습니다. MySQL 실행 상태를 확인해주세요.");
            cause.printStackTrace();
        } else {
            showError("회원가입 처리 중 예상하지 못한 오류가 발생했습니다.");
            cause.printStackTrace();
        }
    }

    private void returnToLogin(String registeredLoginId) {
        if (returning) {
            return;
        }
        returning = true;
        dispose();
        loginFrame.returnFromSignUp(registeredLoginId);
    }

    private void setBusy(boolean busy) {
        registerButton.setEnabled(!busy);
        cancelButton.setEnabled(!busy);
        loginIdField.setEnabled(!busy);
        passwordField.setEnabled(!busy);
        passwordConfirmField.setEnabled(!busy);
        nameField.setEnabled(!busy);
        setCursor(Cursor.getPredefinedCursor(
                busy ? Cursor.WAIT_CURSOR : Cursor.DEFAULT_CURSOR));
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this, message, "회원가입 실패", JOptionPane.ERROR_MESSAGE);
    }
}
