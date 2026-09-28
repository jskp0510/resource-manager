package com.team.resourcemanager.ui;

import com.team.resourcemanager.dao.ItemSearchDAO;
import com.team.resourcemanager.model.ItemSearchResult;
import com.team.resourcemanager.util.StatusLabels;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.util.List;

/** 팀원 5 원본의 물품명·카테고리·상태 검색 화면. */
public class ItemSearchFrame extends JFrame {
    private final ItemSearchDAO itemSearchDAO = new ItemSearchDAO();
    private final JTextField itemNameField = new JTextField(12);
    private final JComboBox<String> categoryCombo = new JComboBox<>(new String[]{"전체"});
    private final JComboBox<String> statusCombo = new JComboBox<>(new String[]{
            "전체", "대여 가능", "대여 중", "대여 불가"
    });
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"물품번호", "물품명", "일련번호", "카테고리", "상태"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };

    public ItemSearchFrame() {
        setTitle("물품 검색");
        setSize(800, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        UIStyle.styleComboBox(categoryCombo);
        UIStyle.styleComboBox(statusCombo);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("물품명"));
        searchPanel.add(itemNameField);
        searchPanel.add(new JLabel("카테고리"));
        searchPanel.add(categoryCombo);
        searchPanel.add(new JLabel("상태"));
        searchPanel.add(statusCombo);
        JButton searchButton = new JButton("검색");
        searchButton.addActionListener(e -> searchItems());
        searchPanel.add(searchButton);

        setLayout(new BorderLayout());
        add(searchPanel, BorderLayout.NORTH);
        add(new JScrollPane(new JTable(tableModel)), BorderLayout.CENTER);
        loadCategories();
    }

    private void loadCategories() {
        try {
            for (String categoryName : itemSearchDAO.findCategoryNames()) {
                categoryCombo.addItem(categoryName);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "카테고리를 불러오지 못했습니다: " + e.getMessage(),
                    "DB 오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchItems() {
        try {
            String selectedStatus = (String) statusCombo.getSelectedItem();
            List<ItemSearchResult> items = itemSearchDAO.search(
                    itemNameField.getText(),
                    "전체".equals(categoryCombo.getSelectedItem()) ? "" : (String) categoryCombo.getSelectedItem(),
                    "전체".equals(selectedStatus) ? "" : StatusLabels.itemCode(selectedStatus));
            tableModel.setRowCount(0);
            for (ItemSearchResult item : items) {
                tableModel.addRow(new Object[]{item.getItemId(), item.getItemName(), item.getSerialNo(),
                        item.getCategoryName(), StatusLabels.item(item.getStatus())});
            }
            if (items.isEmpty()) {
                JOptionPane.showMessageDialog(this, "검색 결과가 없습니다.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "물품을 검색하지 못했습니다: " + e.getMessage(),
                    "DB 오류", JOptionPane.ERROR_MESSAGE);
        }
    }
}
