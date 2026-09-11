package com.team.resourcemanager.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.Connection;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.team.resourcemanager.model.Loan;
import com.team.resourcemanager.service.LoanService;

//관리자 화면
public class LoanAdminPanel extends JPanel {

    private final Connection conn;
    private final LoanService loanService;

    private JTable loanTable;
    private DefaultTableModel tableModel;

    private JButton approveButton;
    private JButton rejectButton;
    private JButton refreshButton;

    public LoanAdminPanel(Connection conn) {

        this.conn = conn;
        this.loanService = new LoanService();

        initializeUI();
        loadRequestedLoans();
    }

    private void initializeUI() {

        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("대여 신청 관리");
        add(titleLabel, BorderLayout.NORTH);

        String[] columns = {
                "신청번호",
                "사용자번호",
                "물품번호",
                "시작일",
                "반납예정일",
                "대여목적",
                "상태"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        loanTable = new JTable(tableModel);

        add(new JScrollPane(loanTable), BorderLayout.CENTER);

        JPanel buttonPanel =
                new JPanel(new FlowLayout(FlowLayout.RIGHT));

        refreshButton = new JButton("새로고침");
        approveButton = new JButton("승인");
        rejectButton = new JButton("거절");

        refreshButton.addActionListener(
                e -> loadRequestedLoans());

        approveButton.addActionListener(
                e -> approveSelectedLoan());

        rejectButton.addActionListener(
                e -> rejectSelectedLoan());

        buttonPanel.add(refreshButton);
        buttonPanel.add(approveButton);
        buttonPanel.add(rejectButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    //신청 목록
    private void loadRequestedLoans() {

        try {
            List<Loan> loans =
                    loanService.getRequestedLoans(conn);

            tableModel.setRowCount(0);

            for (Loan loan : loans) {

                tableModel.addRow(new Object[] {
                        loan.getLoanId(),
                        loan.getUserId(),
                        loan.getItemId(),
                        loan.getStartDate(),
                        loan.getDueDate(),
                        loan.getPurpose(),
                        loan.getStatus()
                });
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "대여 신청 목록을 불러오지 못했습니다."
            );

            e.printStackTrace();
        }
    }
    //신청 승인
    private void approveSelectedLoan() {

        int selectedRow = loanTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "승인할 신청을 선택해주세요."
            );
            return;
        }

        int loanId =
                (int) tableModel.getValueAt(selectedRow, 0);

        try {
            boolean result =
                    loanService.approveLoan(conn, loanId);

            if (result) {
                JOptionPane.showMessageDialog(
                        this,
                        "대여 승인이 완료되었습니다."
                );

                loadRequestedLoans();

            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "대여 승인에 실패했습니다."
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "대여 승인 처리 중 오류가 발생했습니다."
            );

            e.printStackTrace();
        }
    }

    //신청 거절 
    private void rejectSelectedLoan() {

        int selectedRow = loanTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "거절할 신청을 선택해주세요."
            );
            return;
        }

        int loanId =
                (int) tableModel.getValueAt(selectedRow, 0);

        try {
            boolean result =
                    loanService.rejectLoan(conn, loanId);

            if (result) {
                JOptionPane.showMessageDialog(
                        this,
                        "대여 신청이 거절되었습니다."
                );

                loadRequestedLoans();

            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "대여 거절에 실패했습니다."
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "대여 거절 처리 중 오류가 발생했습니다."
            );

            e.printStackTrace();
        }
    }
}