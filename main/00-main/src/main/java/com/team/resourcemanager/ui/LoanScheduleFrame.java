package com.team.resourcemanager.ui;

import com.team.resourcemanager.dao.HistoryDAO;
import com.team.resourcemanager.model.LoanScheduleResult;
import com.team.resourcemanager.model.User;
import com.team.resourcemanager.util.StatusLabels;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

/** 원본 일정 화면처럼 달력으로 기간을 고르는 대여 일정 조회 화면. */
public class LoanScheduleFrame extends JFrame {
    private final User user;
    private final HistoryDAO historyDAO = new HistoryDAO();
    private final JDatePickerImpl startDatePicker;
    private final JDatePickerImpl endDatePicker;
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"대여번호", "물품명", "사용자", "대여 시작일", "반납 예정일", "실제 반납일", "목적", "상태"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };

    public LoanScheduleFrame(User user) {
        this.user = user;
        setTitle("대여 일정 조회");
        setSize(900, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel searchPanel = new JPanel();
        Properties properties = new Properties();
        properties.put("text.today", "오늘");
        properties.put("text.month", "월");
        properties.put("text.year", "년");
        UtilDateModel startModel = new UtilDateModel();
        UtilDateModel endModel = new UtilDateModel();
        startDatePicker = new JDatePickerImpl(new JDatePanelImpl(startModel, properties), new DateLabelFormatter());
        endDatePicker = new JDatePickerImpl(new JDatePanelImpl(endModel, properties), new DateLabelFormatter());

        searchPanel.add(new JLabel("시작일"));
        searchPanel.add(startDatePicker);
        searchPanel.add(new JLabel("종료일"));
        searchPanel.add(endDatePicker);
        JButton searchButton = new JButton("검색");
        searchButton.addActionListener(e -> searchLoans());
        searchPanel.add(searchButton);

        setLayout(new BorderLayout());
        add(searchPanel, BorderLayout.NORTH);
        add(new JScrollPane(new JTable(tableModel)), BorderLayout.CENTER);
        searchLoans();
    }

    private void searchLoans() {
        Object startValue = startDatePicker.getModel().getValue();
        Object endValue = endDatePicker.getModel().getValue();
        LocalDate from = startValue instanceof java.util.Date start
                ? start.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
        LocalDate to = endValue instanceof java.util.Date end
                ? end.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
        if (from != null && to != null && from.isAfter(to)) {
            JOptionPane.showMessageDialog(this, "시작일은 종료일보다 늦을 수 없습니다.");
            return;
        }
        try {
            List<LoanScheduleResult> results = historyDAO.findSchedule(
                    from, to, user.getUserId(), user.isAdmin());
            tableModel.setRowCount(0);
            for (LoanScheduleResult loan : results) {
                tableModel.addRow(new Object[]{loan.getLoanId(), loan.getItemName(), loan.getUserName(),
                        loan.getStartDate(), loan.getDueDate(), loan.getReturnDate(), loan.getPurpose(),
                        StatusLabels.loan(loan.getStatus())});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "일정을 조회하지 못했습니다: " + e.getMessage(), "DB 오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {
        private final String pattern = "yyyy-MM-dd";
        private final SimpleDateFormat dateFormatter = new SimpleDateFormat(pattern);

        @Override
        public Object stringToValue(String text) throws ParseException {
            return dateFormatter.parseObject(text);
        }

        @Override
        public String valueToString(Object value) throws ParseException {
            if (value instanceof Calendar calendar) return dateFormatter.format(calendar.getTime());
            if (value instanceof java.util.Date date) return dateFormatter.format(date);
            return "";
        }
    }
}
