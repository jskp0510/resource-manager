package com.team.resourcemanager.ui;

import com.team.resourcemanager.dao.UserDAO;
import com.team.resourcemanager.model.User;
import com.team.resourcemanager.util.Constants;
import com.team.resourcemanager.util.Session;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/** 기존 UserDAO CRUD를 관리자 회원 관리 화면에 연결한다. */
public class MemberManagementFrame extends JFrame {

    private final UserDAO userDAO = new UserDAO();
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"사용자 번호", "아이디", "이름", "권한"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable userTable = new JTable(tableModel);

    public MemberManagementFrame() {
        if (!Session.getInstance().isAdmin()) {
            throw new IllegalStateException("관리자만 회원 관리 화면을 열 수 있습니다.");
        }

        setTitle("회원 관리");
        setSize(700, 450);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        add(new JScrollPane(userTable), BorderLayout.CENTER);

        JButton refreshButton = new JButton("새로고침");
        JButton roleButton = new JButton("권한 변경");
        JButton deleteButton = new JButton("회원 삭제");
        refreshButton.addActionListener(e -> loadUsers());
        roleButton.addActionListener(e -> changeSelectedUserRole());
        deleteButton.addActionListener(e -> deleteSelectedUser());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(refreshButton);
        buttonPanel.add(roleButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        loadUsers();
    }

    private void loadUsers() {
        try {
            List<User> users = userDAO.findAll();
            tableModel.setRowCount(0);
            for (User user : users) {
                tableModel.addRow(new Object[]{
                        user.getUserId(), user.getLoginId(), user.getName(), user.getRole()
                });
            }
        } catch (SQLException e) {
            showDatabaseError("회원 목록을 불러오지 못했습니다.", e);
        }
    }

    private void changeSelectedUserRole() {
        int modelRow = selectedModelRow();
        if (modelRow < 0) {
            showSelectionMessage();
            return;
        }

        int userId = (int) tableModel.getValueAt(modelRow, 0);
        String currentRole = (String) tableModel.getValueAt(modelRow, 3);
        Object selectedRole = JOptionPane.showInputDialog(
                this,
                "변경할 권한을 선택해주세요.",
                "회원 권한 변경",
                JOptionPane.PLAIN_MESSAGE,
                null,
                new String[]{Constants.ROLE_USER, Constants.ROLE_ADMIN},
                currentRole
        );
        if (!(selectedRole instanceof String role) || role.equals(currentRole)) {
            return;
        }

        User currentUser = Session.getInstance().getUser();
        if (currentUser != null && currentUser.getUserId() == userId
                && Constants.ROLE_USER.equals(role)) {
            JOptionPane.showMessageDialog(this,
                    "현재 로그인한 관리자 권한은 이 화면에서 해제할 수 없습니다.",
                    "권한 변경 불가", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (userDAO.updateRole(userId, role)) {
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this, "권한을 변경하지 못했습니다.");
            }
        } catch (SQLException e) {
            showDatabaseError("회원 권한 변경에 실패했습니다.", e);
        }
    }

    private void deleteSelectedUser() {
        int modelRow = selectedModelRow();
        if (modelRow < 0) {
            showSelectionMessage();
            return;
        }

        int userId = (int) tableModel.getValueAt(modelRow, 0);
        String loginId = (String) tableModel.getValueAt(modelRow, 1);
        User currentUser = Session.getInstance().getUser();
        if (currentUser != null && currentUser.getUserId() == userId) {
            JOptionPane.showMessageDialog(this,
                    "현재 로그인한 계정은 삭제할 수 없습니다.",
                    "회원 삭제 불가", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "회원 '" + loginId + "'을(를) 삭제하시겠습니까?",
                "회원 삭제 확인",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            if (userDAO.deleteById(userId)) {
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this, "회원을 삭제하지 못했습니다.");
            }
        } catch (SQLException e) {
            showDatabaseError("회원 삭제에 실패했습니다. 대여 기록의 연결 여부를 확인해주세요.", e);
        }
    }

    private int selectedModelRow() {
        int selectedRow = userTable.getSelectedRow();
        return selectedRow < 0 ? -1 : userTable.convertRowIndexToModel(selectedRow);
    }

    private void showSelectionMessage() {
        JOptionPane.showMessageDialog(this, "회원을 먼저 선택해주세요.");
    }

    private void showDatabaseError(String message, SQLException error) {
        JOptionPane.showMessageDialog(this,
                message + "\n" + error.getMessage(),
                "DB 오류", JOptionPane.ERROR_MESSAGE);
    }
}
