package com.team.resourcemanager.ui;

import com.team.resourcemanager.util.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * [Resource Manager] 카테고리 및 물품 관리 화면 패널
 * - 팀 메인 프레임의 우측 콘텐츠 영역(Content Panel)에 탑재되는 JPanel 모듈
 * - DBConnection 유틸리티를 활용한 MySQL 데이터베이스 연동
 */
public class ResourceManagerView extends JPanel {

    // GUI 주요 컴포넌트 선언
    private JTable itemTable; // 물품 목록 표시 테이블
    private DefaultTableModel tableModel; // 테이블 데이터 관리 모델
    private JComboBox<String> filterCategoryCombo; // 카테고리 필터 드롭다운
    private JComboBox<String> filterStatusCombo; // 물품 상태 필터 드롭다운

    // DB 카테고리 ID 매핑 리스트
    private List<Integer> categoryIds = new ArrayList<>();

    // 생성자: 화면 패널 레이아웃 구성 및 데이터 로드
    public ResourceManagerView() {
        setLayout(new BorderLayout());

        initUI(); // UI 컴포넌트 배치
        loadCategories(); // 카테고리 목록 DB 조회
        loadItems(); // 물품 목록 DB 조회
    }

    // -----------------------------------------------------------------
    // [1. 화면 레이아웃 구성] 상단 필터, 중앙 테이블, 하단 버튼 패널
    // -----------------------------------------------------------------
    private void initUI() {
        // [상단 패널] 카테고리 추가 및 필터링 제어바
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        JButton manageCategoryBtn = new JButton("카테고리 추가");
        manageCategoryBtn.addActionListener(e -> addCategoryDialog());

        filterCategoryCombo = new JComboBox<>();
        // 팀 공통 상태 Enum 규격 적용
        filterStatusCombo = new JComboBox<>(new String[]{"ALL", "AVAILABLE", "MAINTENANCE", "BORROWED"});

        // 필터 선택 변경 시 자동으로 테이블 새로고침
        filterCategoryCombo.addActionListener(e -> loadItems());
        filterStatusCombo.addActionListener(e -> loadItems());

        topPanel.add(manageCategoryBtn);
        topPanel.add(new JLabel(" | 카테고리 필터:"));
        topPanel.add(filterCategoryCombo);
        topPanel.add(new JLabel("상태 필터:"));
        topPanel.add(filterStatusCombo);

        // [중앙 테이블] 셀 수정 불가(Read-Only) 설정
        String[] columns = {"ID", "카테고리", "물품명", "일련번호", "상태", "설명"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        itemTable = new JTable(tableModel);

        // [하단 패널] 등록/삭제 작업 버튼
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton addBtn = new JButton("물품 등록");
        JButton deleteBtn = new JButton("삭제");

        addBtn.addActionListener(e -> addItemDialog());
        deleteBtn.addActionListener(e -> deleteSelectedItem());

        bottomPanel.add(addBtn);
        bottomPanel.add(deleteBtn);

        // JPanel 본체에 각각 배치
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(itemTable), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // -----------------------------------------------------------------
    // [2. 카테고리 로드] DB에서 카테고리 목록을 가져와 필터에 동적 바인딩
    // -----------------------------------------------------------------
    private void loadCategories() {
        filterCategoryCombo.removeAllItems();
        categoryIds.clear();

        filterCategoryCombo.addItem("전체");
        categoryIds.add(null);

        String sql = "SELECT category_id, name FROM category ORDER BY category_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                categoryIds.add(rs.getInt("category_id"));
                filterCategoryCombo.addItem(rs.getString("name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "카테고리 로드 오류: " + e.getMessage(), "DB 오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    // -----------------------------------------------------------------
    // [3. 물품 목록 조회] 조건별 필터링(JOIN 쿼리) 결과를 테이블에 출력
    // -----------------------------------------------------------------
    private void loadItems() {
        if (filterCategoryCombo.getItemCount() == 0) return;

        tableModel.setRowCount(0); // 기존 테이블 행 초기화

        int selectedCatIdx = filterCategoryCombo.getSelectedIndex();
        Integer selectedCatId = (selectedCatIdx > 0 && selectedCatIdx < categoryIds.size()) 
                ? categoryIds.get(selectedCatIdx) : null;
        String selectedStatus = (String) filterStatusCombo.getSelectedItem();

        StringBuilder sql = new StringBuilder(
            "SELECT i.item_id, c.name AS category_name, i.name, i.serial_number, i.status, i.description " +
            "FROM item i LEFT JOIN category c ON i.category_id = c.category_id WHERE 1=1 "
        );

        if (selectedCatId != null) sql.append("AND i.category_id = ? ");
        if (selectedStatus != null && !selectedStatus.equals("ALL")) sql.append("AND i.status = ? ");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int paramIdx = 1;
            if (selectedCatId != null) pstmt.setInt(paramIdx++, selectedCatId);
            if (selectedStatus != null && !selectedStatus.equals("ALL")) pstmt.setString(paramIdx, selectedStatus);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                        rs.getInt("item_id"),
                        rs.getString("category_name"),
                        rs.getString("name"),
                        rs.getString("serial_number"),
                        rs.getString("status"),
                        rs.getString("description")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // -----------------------------------------------------------------
    // [4. 새 카테고리 등록]
    // -----------------------------------------------------------------
    private void addCategoryDialog() {
        String catName = JOptionPane.showInputDialog(this, "새 카테고리 이름:");
        if (catName != null && !catName.trim().isEmpty()) {
            String sql = "INSERT INTO category (name) VALUES (?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, catName.trim());
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "카테고리가 추가되었습니다.");
                loadCategories();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "카테고리 추가 실패: " + e.getMessage(), "DB 오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // -----------------------------------------------------------------
    // [5. 신규 물품 등록 입력 폼]
    // -----------------------------------------------------------------
    private void addItemDialog() {
        if (filterCategoryCombo.getItemCount() <= 1) {
            JOptionPane.showMessageDialog(this, "카테고리를 먼저 추가해 주세요.");
            return;
        }

        JTextField nameField = new JTextField();
        JTextField serialField = new JTextField();
        JTextField descField = new JTextField();

        JComboBox<String> catCombo = new JComboBox<>();
        for (int i = 1; i < filterCategoryCombo.getItemCount(); i++) {
            catCombo.addItem(filterCategoryCombo.getItemAt(i));
        }

        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.add(new JLabel("카테고리:")); panel.add(catCombo);
        panel.add(new JLabel("물품명:")); panel.add(nameField);
        panel.add(new JLabel("일련번호:")); panel.add(serialField);
        panel.add(new JLabel("설명:")); panel.add(descField);

        int result = JOptionPane.showConfirmDialog(this, panel, "물품 등록", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            if (nameField.getText().trim().isEmpty() || serialField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "물품명과 일련번호는 필수 입력 항목입니다.", "경고", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int selectedCatId = categoryIds.get(catCombo.getSelectedIndex() + 1);

            String sql = "INSERT INTO item (category_id, name, serial_number, description) VALUES (?, ?, ?, ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, selectedCatId);
                pstmt.setString(2, nameField.getText().trim());
                pstmt.setString(3, serialField.getText().trim());
                pstmt.setString(4, descField.getText().trim());
                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "물품이 등록되었습니다.");
                loadItems();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "물품 등록 실패: " + e.getMessage(), "DB 오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // -----------------------------------------------------------------
    // [6. 선택 물품 삭제]
    // -----------------------------------------------------------------
    private void deleteSelectedItem() {
        int row = itemTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "삭제할 물품을 선택해주세요.");
            return;
        }
        int itemId = (int) tableModel.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "선택한 물품을 삭제하시겠습니까?", "삭제 확인", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM item WHERE item_id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, itemId);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "삭제되었습니다.");
                loadItems();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "삭제 실패: " + e.getMessage(), "DB 오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

/*
 ====================================================================================
 [팀원 메인 프레임(MainFrame) 연동 안내]
 ====================================================================================
 
 1. 모듈 특징:
    - 본 클래스(ResourceManagerView)는 JPanel을 상속받아 독립적으로 작동하는 모듈입니다.
    - 생성자(Constructor) 호출 시 DB 연결, 초기 카테고리/물품 데이터 로딩 및 UI 배치가 
      자동으로 수행됩니다.

 2. 메인 화면(MainFrame) 연결 방법:
    - 메인 사이드바의 '자원/물품 관리' 버튼 클릭 이벤트(ActionListener) 발생 시,
      우측 메인 콘텐츠 패널(contentPanel)의 화면을 아래와 같이 교체해 주세요.

    [연동 예시 코드]
    resourceManageBtn.addActionListener(e -> {
        contentPanel.removeAll(); // 1. 기존 화면 제거
        contentPanel.add(new ResourceManagerView()); // 2. 자원 관리 화면 추가
        contentPanel.revalidate(); // 3. 레이아웃 재계산
        contentPanel.repaint(); // 4. 화면 갱신
    });

 ====================================================================================
*/
