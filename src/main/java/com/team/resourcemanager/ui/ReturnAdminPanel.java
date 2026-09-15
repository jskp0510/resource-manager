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

//관리자 화면 - 반납 신청 목록 확인, 반납 확정 처리, 연체 상태 일괄 갱신
public class ReturnAdminPanel extends JPanel {

    private final Connection conn;
    private final LoanService loanService;

    private JTable loanTable;
    private DefaultTableModel tableModel;

    private JButton confirmReturnButton;
    private JButton overdueCheckButton;
    private JButton refreshButton;

    public ReturnAdminPanel(Connection conn) {

        this.conn = conn;
        this.loanService = new LoanService();

        initializeUI();
        loadReturnRequestedLoans();
    }

    private void initializeUI() {

        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("반납 신청 관리");
        add(titleLabel, BorderLayout.NORTH);

        String[] columns = {
                "신청번호",
                "사용자번호",
                "물품번호",
                "반납예정일",
                "연체일수",
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
        overdueCheckButton = new JButton("연체 상태 갱신");
        confirmReturnButton = new JButton("반납 확인");

        refreshButton.addActionListener(
                e -> loadReturnRequestedLoans());

        overdueCheckButton.addActionListener(
                e -> runOverdueCheck());

        confirmReturnButton.addActionListener(
                e -> confirmSelectedReturn());

        buttonPanel.add(refreshButton);
        buttonPanel.add(overdueCheckButton);
        buttonPanel.add(confirmReturnButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    //반납 신청된 목록 표시 (연체일수는 화면에서 계산해서 같이 보여줌)
    private void loadReturnRequestedLoans() {

        try {
            List<Loan> loans =
                    loanService.getReturnRequestedLoans(conn);

            tableModel.setRowCount(0);

            for (Loan loan : loans) {

                long overdueDays = loanService.calculateOverdueDays(loan);

                tableModel.addRow(new Object[]{
                        loan.getLoanId(),
                        loan.getUserId(),
                        loan.getItemId(),
                        loan.getDueDate(),
                        overdueDays > 0 ? overdueDays + "일 연체" : "정상",
                        loan.getStatus()
                });
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "반납 신청 목록 조회 중 오류가 발생했습니다."
            );

            e.printStackTrace();
        }
    }

    //선택한 반납 신청 건 확정 처리
    private void confirmSelectedReturn() {

        int selectedRow = loanTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "반납 확인할 항목을 선택해주세요."
            );
            return;
        }

        int loanId =
                (int) tableModel.getValueAt(selectedRow, 0);

        try {
            boolean result =
                    loanService.confirmReturn(conn, loanId);

            if (result) {

                JOptionPane.showMessageDialog(
                        this,
                        "반납 확인이 완료되었습니다. 물품 상태가 대여 가능으로 변경되었습니다."
                );

                loadReturnRequestedLoans();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "반납 확인 처리에 실패했습니다."
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "반납 확인 처리 중 오류가 발생했습니다."
            );

            e.printStackTrace();
        }
    }

    //대여중/반납신청중인 전체 건을 훑어서 연체된 것들을 OVERDUE로 갱신
    private void runOverdueCheck() {

        try {
            int updatedCount =
                    loanService.updateOverdueStatuses(conn);

            JOptionPane.showMessageDialog(
                    this,
                    updatedCount + "건이 연체 상태로 갱신되었습니다."
            );

            loadReturnRequestedLoans();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "연체 상태 갱신 중 오류가 발생했습니다."
            );

            e.printStackTrace();
        }
    }
}