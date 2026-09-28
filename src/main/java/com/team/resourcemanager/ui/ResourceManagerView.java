package com.team.resourcemanager.ui;

import com.team.resourcemanager.util.DBConnection;
import com.team.resourcemanager.util.StatusLabels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** 통합 스키마에 연결된 카테고리 및 물품 관리 화면. */
public class ResourceManagerView extends JPanel {
    private final JTable itemTable;
    private final DefaultTableModel tableModel;
    private final JComboBox<String> filterCategoryCombo;
    private final JComboBox<String> filterStatusCombo;
    private final List<Integer> categoryIds = new ArrayList<>();

    public ResourceManagerView(boolean canManageItems) {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout(4, 4));
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JButton addCategoryButton = new JButton("카테고리 추가");
        addCategoryButton.addActionListener(e -> addCategoryDialog());
        filterCategoryCombo = new JComboBox<>();
        filterStatusCombo = new JComboBox<>(new String[]{"전체", "대여 가능", "대여 중", "대여 불가"});
        UIStyle.styleComboBox(filterCategoryCombo);
        UIStyle.styleComboBox(filterStatusCombo);
        filterCategoryCombo.addActionListener(e -> loadItems());
        filterStatusCombo.addActionListener(e -> loadItems());
        if (canManageItems) filterRow.add(addCategoryButton);
        filterRow.add(new JLabel("카테고리:"));
        filterRow.add(filterCategoryCombo);
        filterRow.add(new JLabel("상태:"));
        filterRow.add(filterStatusCombo);
        topPanel.add(filterRow, BorderLayout.CENTER);

        tableModel = new DefaultTableModel(new String[]{"ID", "카테고리", "물품명", "일련번호", "상태", "설명"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        itemTable = new JTable(tableModel);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton addItemButton = new JButton("물품 등록");
        JButton deleteItemButton = new JButton("삭제");
        addItemButton.addActionListener(e -> addItemDialog());
        deleteItemButton.addActionListener(e -> deleteSelectedItem());
        if (canManageItems) {
            bottomPanel.add(addItemButton);
            bottomPanel.add(deleteItemButton);
        }

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(itemTable), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        loadCategories();
        loadItems();
    }

    private void loadCategories() {
        filterCategoryCombo.removeAllItems();
        categoryIds.clear();
        filterCategoryCombo.addItem("전체");
        categoryIds.add(null);
        String sql = "SELECT category_id, name FROM CATEGORY ORDER BY category_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                categoryIds.add(rs.getInt("category_id"));
                filterCategoryCombo.addItem(rs.getString("name"));
            }
        } catch (SQLException e) {
            showDatabaseError("카테고리 조회", e);
        }
    }

    private void loadItems() {
        if (filterCategoryCombo.getItemCount() == 0 || tableModel == null) return;
        tableModel.setRowCount(0);
        int selectedIndex = filterCategoryCombo.getSelectedIndex();
        Integer categoryId = selectedIndex > 0 && selectedIndex < categoryIds.size()
                ? categoryIds.get(selectedIndex) : null;
        String statusLabel = (String) filterStatusCombo.getSelectedItem();
        String status = "전체".equals(statusLabel) ? null : StatusLabels.itemCode(statusLabel);

        StringBuilder sql = new StringBuilder(
                "SELECT i.item_id, c.name AS category_name, i.item_name, i.serial_no, i.status, i.description " +
                "FROM ITEM i LEFT JOIN CATEGORY c ON i.category_id = c.category_id WHERE 1=1 ");
        if (categoryId != null) sql.append("AND i.category_id = ? ");
        if (status != null && !"ALL".equals(status)) sql.append("AND i.status = ? ");
        sql.append("ORDER BY i.item_id");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (categoryId != null) stmt.setInt(paramIndex++, categoryId);
            if (status != null && !"ALL".equals(status)) stmt.setString(paramIndex, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tableModel.addRow(new Object[]{rs.getInt("item_id"), rs.getString("category_name"),
                            rs.getString("item_name"), rs.getString("serial_no"), StatusLabels.item(rs.getString("status")),
                            rs.getString("description")});
                }
            }
        } catch (SQLException e) {
            showDatabaseError("물품 조회", e);
        }
    }

    private void addCategoryDialog() {
        String categoryName = JOptionPane.showInputDialog(this, "새 카테고리 이름:");
        if (categoryName == null || categoryName.trim().isEmpty()) return;
        String sql = "INSERT INTO CATEGORY (name) VALUES (?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, categoryName.trim());
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "카테고리가 추가되었습니다.");
            loadCategories();
        } catch (SQLException e) {
            if (isDuplicateKey(e)) {
                JOptionPane.showMessageDialog(this, "이미 등록된 카테고리명입니다.",
                        "중복 카테고리", JOptionPane.WARNING_MESSAGE);
            } else {
                showDatabaseError("카테고리 추가", e);
            }
        }
    }

    private void addItemDialog() {
        if (filterCategoryCombo.getItemCount() <= 1) {
            JOptionPane.showMessageDialog(this, "카테고리를 먼저 추가해 주세요.");
            return;
        }
        JTextField nameField = new JTextField();
        JTextField serialField = new JTextField();
        JTextField descriptionField = new JTextField();
        JComboBox<String> categoryCombo = new JComboBox<>();
        UIStyle.styleComboBox(categoryCombo);
        for (int i = 1; i < filterCategoryCombo.getItemCount(); i++) categoryCombo.addItem(filterCategoryCombo.getItemAt(i));

        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.add(new JLabel("카테고리:")); panel.add(categoryCombo);
        panel.add(new JLabel("물품명:")); panel.add(nameField);
        panel.add(new JLabel("일련번호:")); panel.add(serialField);
        panel.add(new JLabel("설명:")); panel.add(descriptionField);
        if (JOptionPane.showConfirmDialog(this, panel, "물품 등록", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        if (nameField.getText().trim().isEmpty() || serialField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "물품명과 일련번호는 필수 입력 항목입니다.", "입력 확인", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int categoryId = categoryIds.get(categoryCombo.getSelectedIndex() + 1);
        String sql = "INSERT INTO ITEM (category_id, item_name, serial_no, description, status) VALUES (?, ?, ?, ?, 'AVAILABLE')";
        String serialNo = serialField.getText().trim();
        try (Connection conn = DBConnection.getConnection()) {
            if (serialNumberExists(conn, serialNo)) {
                JOptionPane.showMessageDialog(this, "이미 사용 중인 일련번호입니다. 다른 일련번호를 입력해 주세요.",
                        "중복 일련번호", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, categoryId);
                stmt.setString(2, nameField.getText().trim());
                stmt.setString(3, serialNo);
                stmt.setString(4, descriptionField.getText().trim());
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "물품이 등록되었습니다.");
                loadItems();
            }
        } catch (SQLException e) {
            if (isDuplicateKey(e)) {
                JOptionPane.showMessageDialog(this, "이미 사용 중인 일련번호입니다. 다른 일련번호를 입력해 주세요.",
                        "중복 일련번호", JOptionPane.WARNING_MESSAGE);
            } else {
                showDatabaseError("물품 등록", e);
            }
        }
    }

    private boolean serialNumberExists(Connection conn, String serialNo) throws SQLException {
        String sql = "SELECT 1 FROM ITEM WHERE serial_no = ? LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, serialNo);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private void deleteSelectedItem() {
        int row = itemTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "삭제할 물품을 선택해 주세요.");
            return;
        }
        int modelRow = itemTable.convertRowIndexToModel(row);
        int itemId = (int) tableModel.getValueAt(modelRow, 0);
        if (JOptionPane.showConfirmDialog(this, "선택한 물품을 삭제하시겠습니까?", "삭제 확인",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM ITEM WHERE item_id = ?")) {
            stmt.setInt(1, itemId);
            if (stmt.executeUpdate() == 0) {
                JOptionPane.showMessageDialog(this, "물품이 이미 삭제되었습니다.");
            } else {
                JOptionPane.showMessageDialog(this, "삭제되었습니다.");
            }
            loadItems();
        } catch (SQLException e) {
            if (isForeignKeyReference(e)) {
                JOptionPane.showMessageDialog(this,
                        "대여 기록이 연결된 물품은 삭제할 수 없습니다. 대여·반납 이력을 확인해 주세요.",
                        "물품 삭제 불가", JOptionPane.WARNING_MESSAGE);
            } else {
                showDatabaseError("물품 삭제", e);
            }
        }
    }

    private boolean isDuplicateKey(SQLException e) {
        return e.getErrorCode() == 1062
                || ("23000".equals(e.getSQLState())
                && e.getMessage() != null
                && e.getMessage().contains("Duplicate entry"));
    }

    private boolean isForeignKeyReference(SQLException e) {
        return e.getErrorCode() == 1451
                || ("23000".equals(e.getSQLState())
                && e.getMessage() != null
                && e.getMessage().contains("foreign key constraint fails"));
    }

    private void showDatabaseError(String action, SQLException e) {
        JOptionPane.showMessageDialog(this, action + " 실패: " + e.getMessage(), "DB 오류", JOptionPane.ERROR_MESSAGE);
    }
}
