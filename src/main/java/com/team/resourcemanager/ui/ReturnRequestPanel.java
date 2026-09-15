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

//사용자 화면 - 본인이 대여중인 물품을 반납 신청
public class ReturnRequestPanel extends JPanel {

    private final Connection conn;
    private final int userId;
    private final LoanService loanService;

    private JTable loanTable;
    private DefaultTableModel tableModel;

    private JButton returnRequestButton;
    private JButton refreshButton;

    public ReturnRequestPanel(Connection conn, int userId) {

        this.conn = conn;
        this.userId = userId;
        this.loanService = new LoanService();

        initializeUI();
        loadMyBorrowedLoans();
    }

    private void initializeUI() {

        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("반납 신청");
        add(titleLabel, BorderLayout.NORTH);

        String[] columns = {
                "대여번호",
                "물품번호",
                "대여시작일",
                "반납예정일",
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
        returnRequestButton = new JButton("반납 신청");

        refreshButton.addActionListener(
                e -> loadMyBorrowedLoans());

        returnRequestButton.addActionListener(
                e -> requestReturnForSelectedLoan());

        buttonPanel.add(refreshButton);
        buttonPanel.add(returnRequestButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    //본인이 대여중/연체중인 목록 표시 (LoanService가 userId 기준으로 조회해서 넘겨줌)
    private void loadMyBorrowedLoans() {

        try {
            List<Loan> loans =
                    loanService.getMyBorrowedLoans(conn, userId);

            tableModel.setRowCount(0);

            for (Loan loan : loans) {

                tableModel.addRow(new Object[]{
                        loan.getLoanId(),
                        loan.getItemId(),
                        loan.getStartDate(),
                        loan.getDueDate(),
                        loan.getStatus()
                });
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "대여 목록 조회 중 오류가 발생했습니다."
            );

            e.printStackTrace();
        }
    }

    //선택한 대여 건 반납 신청
    private void requestReturnForSelectedLoan() {

        int selectedRow = loanTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "반납 신청할 항목을 선택해주세요."
            );
            return;
        }

        int loanId =
                (int) tableModel.getValueAt(selectedRow, 0);

        try {
            boolean result =
                    loanService.requestReturn(conn, loanId);

            if (result) {

                JOptionPane.showMessageDialog(
                        this,
                        "반납 신청이 완료되었습니다. 관리자 확인 후 반납 처리됩니다."
                );

                loadMyBorrowedLoans();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "반납 신청에 실패했습니다. 대여중 상태인지 확인해주세요."
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "반납 신청 처리 중 오류가 발생했습니다."
            );

            e.printStackTrace();
        }
    }
}