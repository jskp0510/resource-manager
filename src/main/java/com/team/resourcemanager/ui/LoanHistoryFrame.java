package com.team.resourcemanager.ui;

import com.team.resourcemanager.dao.LoanDAO;
import com.team.resourcemanager.model.LoanHistoryResult;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LoanHistoryFrame extends JFrame {

    private JTextField itemNameField;
    private JTextField userNameField;

    private JButton itemSearchButton;
    private JButton userSearchButton;

    private JTable table;
    private DefaultTableModel tableModel;

    public LoanHistoryFrame() {

        setTitle("대여 이력 조회");
        setSize(950, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // =========================
        // 검색 영역
        // =========================

        JPanel searchPanel = new JPanel();

        itemNameField = new JTextField(10);
        userNameField = new JTextField(10);

        itemSearchButton = new JButton("물품별 조회");
        userSearchButton = new JButton("사용자별 조회");

        searchPanel.add(new JLabel("물품명"));
        searchPanel.add(itemNameField);
        searchPanel.add(itemSearchButton);

        searchPanel.add(new JLabel("사용자명"));
        searchPanel.add(userNameField);
        searchPanel.add(userSearchButton);

        JButton overdueButton = new JButton("연체 조회");

        overdueButton.addActionListener(e -> {

            LoanDAO loanDAO = new LoanDAO();

            List<LoanHistoryResult> loans =
                    loanDAO.getOverdueLoans();

            if (loans.isEmpty()) {
                tableModel.setRowCount(0);

                JOptionPane.showMessageDialog(
                        this,
                        "연체된 대여 기록이 없습니다."
                );

                return;
            }

            showResults(loans);
        });

        searchPanel.add(overdueButton);


        // =========================
        // 테이블
        // =========================

        String[] columns = {
                "대여번호",
                "물품명",
                "사용자",
                "대여 시작일",
                "반납 예정일",
                "실제 반납일",
                "목적",
                "상태"
        };

        tableModel = new DefaultTableModel(columns, 0);

        table = new JTable(tableModel);

        JScrollPane scrollPane =
                new JScrollPane(table);


        // =========================
        // 화면 배치
        // =========================

        setLayout(new BorderLayout());

        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);


        // =========================
        // 버튼 이벤트
        // =========================

        itemSearchButton.addActionListener(
                e -> searchByItem()
        );

        userSearchButton.addActionListener(
                e -> searchByUser()
        );

        setVisible(true);
    }


    // =========================
    // 물품명으로 조회
    // =========================

    private void searchByItem() {

        String itemName =
                itemNameField.getText();

        if (itemName.isBlank()) {

            JOptionPane.showMessageDialog(
                    this,
                    "물품명을 입력해주세요."
            );

            return;
        }

        LoanDAO loanDAO =
                new LoanDAO();

        List<LoanHistoryResult> loans =
                loanDAO.getLoansByItemName(itemName);

        showResults(loans);
    }


    // =========================
    // 사용자명으로 조회
    // =========================

    private void searchByUser() {

        String userName =
                userNameField.getText();

        if (userName.isBlank()) {

            JOptionPane.showMessageDialog(
                    this,
                    "사용자명을 입력해주세요."
            );

            return;
        }

        LoanDAO loanDAO =
                new LoanDAO();

        List<LoanHistoryResult> loans =
                loanDAO.getLoansByUserName(userName);

        showResults(loans);
    }


    // =========================
    // 검색 결과 JTable에 표시
    // =========================

    private void showResults(
            List<LoanHistoryResult> loans
    ) {

        tableModel.setRowCount(0);

        if (loans.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "검색 결과가 없습니다."
            );

            return;
        }

        for (LoanHistoryResult loan : loans) {

            tableModel.addRow(
                    new Object[]{
                            loan.getLoanId(),
                            loan.getItemName(),
                            loan.getUserName(),
                            loan.getStartDate(),
                            loan.getDueDate(),
                            loan.getReturnDate(),
                            loan.getPurpose(),
                            loan.getStatus()
                    }
            );
        }
    }


    public static void main(String[] args) {

        new LoanHistoryFrame();

    }
}