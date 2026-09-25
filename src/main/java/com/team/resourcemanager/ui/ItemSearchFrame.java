package com.team.resourcemanager.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import com.team.resourcemanager.dao.ItemDAO;
import com.team.resourcemanager.model.ItemSearchResult;

import java.util.List;

public class ItemSearchFrame extends JFrame {

    private JTextField nameField;
    private JComboBox<String> categoryCombo;
    private JComboBox<String> statusCombo;

    private JButton searchButton;

    private JTable table;
    private DefaultTableModel tableModel;

    public ItemSearchFrame() {

        setTitle("물품 검색");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // 위쪽 검색 영역
        JPanel searchPanel = new JPanel();

        nameField = new JTextField(10);
        categoryCombo = new JComboBox<>(new String[]{
                "",
                "전자기기"
        });

        statusCombo = new JComboBox<>(new String[]{
                "",
                "AVAILABLE",
                "LOANED"
        });

        searchButton = new JButton("검색");
        searchButton.addActionListener(e -> searchItems());

        searchPanel.add(new JLabel("물품명"));
        searchPanel.add(nameField);

        searchPanel.add(new JLabel("카테고리"));
        searchPanel.add(categoryCombo);

        searchPanel.add(new JLabel("상태"));
        searchPanel.add(statusCombo);

        searchPanel.add(searchButton);

        // 테이블
        String[] columns = {
                "물품번호",
                "물품명",
                "일련번호",
                "카테고리",
                "상태"
        };

        tableModel = new DefaultTableModel(columns, 0);

        table = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);

        // 배치
        setLayout(new BorderLayout());

        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    public static void main(String[] args) {
        new ItemSearchFrame();
    }

    private void searchItems() {

        String itemName = nameField.getText();
        String categoryName =
                categoryCombo.getSelectedItem().toString();

        String status =
                statusCombo.getSelectedItem().toString();

        ItemDAO itemDAO = new ItemDAO();

        List<ItemSearchResult> items =
                itemDAO.searchItems(
                        itemName,
                        "",
                        categoryName,
                        status
                );

        // 기존 표 내용 지우기
        tableModel.setRowCount(0);

        // 검색 결과를 표에 넣기
        for (ItemSearchResult item : items) {

            tableModel.addRow(new Object[]{
                    item.getItemId(),
                    item.getItemName(),
                    item.getSerialNo(),
                    item.getCategoryName(),
                    item.getStatus()
            });
        }
    }
}