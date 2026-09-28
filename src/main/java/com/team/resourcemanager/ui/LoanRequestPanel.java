package com.team.resourcemanager.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import com.team.resourcemanager.service.LoanService;

//사용자 화면 
public class LoanRequestPanel extends JPanel {

    private final Connection conn;
    private final int userId;
    private final LoanService loanService;

    private JComboBox<String> itemComboBox;
    private Map<Integer, String> requestableItems;

    private JDatePickerImpl startDatePicker;
    private JDatePickerImpl dueDatePicker;
    private JTextField purposeField;

    private JButton requestButton;

    public LoanRequestPanel(Connection conn, int userId) {

        this.conn = conn;
        this.userId = userId;
        this.loanService = new LoanService();

        initializeUI();
        loadItems();
    }

    private void initializeUI() {

        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("대여 신청");
        add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel =
                new JPanel(new GridLayout(4, 2, 10, 10));

        formPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        20, 20, 20, 20));

        itemComboBox = new JComboBox<>();
        startDatePicker = createDatePicker();
        dueDatePicker = createDatePicker();
        purposeField = new JTextField();

        formPanel.add(new JLabel("물품 선택"));
        formPanel.add(itemComboBox);

        formPanel.add(new JLabel("대여 시작일"));
        formPanel.add(startDatePicker);

        formPanel.add(new JLabel("반납 예정일"));
        formPanel.add(dueDatePicker);

        formPanel.add(new JLabel("대여 목적"));
        formPanel.add(purposeField);

        add(formPanel, BorderLayout.CENTER);

        requestButton = new JButton("대여 신청");

        requestButton.addActionListener(e -> requestLoan());

        add(requestButton, BorderLayout.SOUTH);
    }

    private JDatePickerImpl createDatePicker() {
        Properties properties = new Properties();
        properties.put("text.today", "오늘");
        properties.put("text.month", "월");
        properties.put("text.year", "년");
        return new JDatePickerImpl(
                new JDatePanelImpl(new UtilDateModel(), properties),
                new LoanScheduleFrame.DateLabelFormatter());
    }

    //신청 가능 물품 표시
    private void loadItems() {

        try {
            requestableItems =
                    loanService.getRequestableItems(conn);

            itemComboBox.removeAllItems();

            for (String displayName : requestableItems.values()) {
                itemComboBox.addItem(displayName);
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "대여 가능한 물품 목록을 불러오지 못했습니다."
            );

            e.printStackTrace();
        }
    }
    //조회
    private Integer getSelectedItemId() {

        String selectedItem =
                (String) itemComboBox.getSelectedItem();

        if (selectedItem == null) {
            return null;
        }

        for (Map.Entry<Integer, String> entry
                : requestableItems.entrySet()) {

            if (entry.getValue().equals(selectedItem)) {
                return entry.getKey();
            }
        }

        return null;
    }
    
    //대여 신청 처리
    private void requestLoan() {

        try {
            Integer itemId = getSelectedItemId();

            if (itemId == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "대여할 물품을 선택해주세요."
                );
                return;
            }

            LocalDate startDate = getSelectedDate(startDatePicker);
            LocalDate dueDate = getSelectedDate(dueDatePicker);
            if (startDate == null || dueDate == null) {
                JOptionPane.showMessageDialog(this, "대여 시작일과 반납 예정일을 모두 선택해주세요.");
                return;
            }

            String purpose =
                    purposeField.getText().trim();

            boolean result = loanService.requestLoan(
                    conn,
                    userId,
                    itemId,
                    startDate,
                    dueDate,
                    purpose
            );

            if (result) {

                JOptionPane.showMessageDialog(
                        this,
                        "대여 신청이 완료되었습니다."
                );

                clearFields();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "현재 대여할 수 없는 물품입니다."
                );
            }

        } catch (IllegalArgumentException e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage()
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "대여 신청 처리 중 오류가 발생했습니다."
            );

            e.printStackTrace();
        }
    }

    private LocalDate getSelectedDate(JDatePickerImpl datePicker) {
        Object value = datePicker.getModel().getValue();
        if (!(value instanceof java.util.Date date)) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
    
    //초기화
    private void clearFields() {

        startDatePicker.getModel().setValue(null);
        dueDatePicker.getModel().setValue(null);
        purposeField.setText("");

        loadItems();
    }
}
