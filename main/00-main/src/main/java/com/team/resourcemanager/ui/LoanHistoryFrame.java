package com.team.resourcemanager.ui;

import com.team.resourcemanager.dao.HistoryDAO;
import com.team.resourcemanager.model.LoanHistoryResult;
import com.team.resourcemanager.model.User;
import com.team.resourcemanager.util.StatusLabels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/** 이력 PR 원본과 같이 물품·사용자별 전체 대여 기록을 검색한다. */
public class LoanHistoryFrame extends JFrame {
    private final User user;
    private final HistoryDAO historyDAO = new HistoryDAO();
    private final JTextField itemNameField = new JTextField(12);
    private final JTextField userNameField = new JTextField(12);
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"대여번호", "물품명", "사용자", "대여 시작일", "반납 예정일", "실제 반납일", "목적", "상태"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };

    public LoanHistoryFrame(User user) {
        this.user = user;
        setTitle("대여 이력 조회");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(950, 500);
        setLocationRelativeTo(null);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton searchButton = new JButton("조회");
        searchButton.addActionListener(e -> searchHistory());
        searchPanel.add(new JLabel("물품명"));
        searchPanel.add(itemNameField);
        if (user.isAdmin()) {
            searchPanel.add(new JLabel("사용자명"));
            searchPanel.add(userNameField);
        }
        searchPanel.add(searchButton);
        JButton overdue = new JButton("연체 조회");
        overdue.addActionListener(e -> {
            if (runSearch(() -> historyDAO.findOverdue(user.getUserId(), user.isAdmin()))
                    && tableModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "연체된 대여 기록이 없습니다.");
            }
        });
        searchPanel.add(overdue);

        add(searchPanel, BorderLayout.NORTH);
        add(new JScrollPane(new JTable(tableModel)), BorderLayout.CENTER);
        searchHistory();
    }

    private void searchHistory() {
        runSearch(() -> historyDAO.searchHistory(
                itemNameField.getText(), userNameField.getText(), user.getUserId(), user.isAdmin()));
    }

    private boolean runSearch(SqlSupplier<List<LoanHistoryResult>> query) {
        try {
            tableModel.setRowCount(0);
            for (LoanHistoryResult loan : query.get()) {
                tableModel.addRow(new Object[]{loan.getLoanId(), loan.getItemName(), loan.getUserName(),
                        loan.getStartDate(), loan.getDueDate(), loan.getReturnDate(), loan.getPurpose(),
                        StatusLabels.loan(loan.getStatus())});
            }
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "이력을 조회하지 못했습니다: " + e.getMessage(), "DB 오류", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    @FunctionalInterface private interface SqlSupplier<T> { T get() throws SQLException; }
}
