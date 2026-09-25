package com.team.resourcemanager.ui;

import com.team.resourcemanager.dao.LoanDAO;
import com.team.resourcemanager.model.LoanScheduleResult;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

public class LoanScheduleFrame extends JFrame {

    private JButton searchButton;

    private JTable table;
    private DefaultTableModel tableModel;

    // 기존 JTextField 대신 날짜 선택기 사용
    private JDatePickerImpl startDatePicker;
    private JDatePickerImpl endDatePicker;

    public LoanScheduleFrame() {

        setTitle("대여 일정 조회");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel searchPanel = new JPanel();


        // =========================
        // 시작일 달력 만들기
        // =========================

        UtilDateModel startModel = new UtilDateModel();

        Properties startProperties = new Properties();

        startProperties.put("text.today", "오늘");
        startProperties.put("text.month", "월");
        startProperties.put("text.year", "년");

        JDatePanelImpl startDatePanel =
                new JDatePanelImpl(
                        startModel,
                        startProperties
                );

        startDatePicker =
                new JDatePickerImpl(
                        startDatePanel,
                        new DateLabelFormatter()
                );


        // =========================
        // 종료일 달력 만들기
        // =========================

        UtilDateModel endModel = new UtilDateModel();

        Properties endProperties = new Properties();

        endProperties.put("text.today", "오늘");
        endProperties.put("text.month", "월");
        endProperties.put("text.year", "년");

        JDatePanelImpl endDatePanel =
                new JDatePanelImpl(
                        endModel,
                        endProperties
                );

        endDatePicker =
                new JDatePickerImpl(
                        endDatePanel,
                        new DateLabelFormatter()
                );


        // =========================
        // 검색 버튼
        // =========================

        searchButton = new JButton("검색");


        searchPanel.add(
                new JLabel("시작일")
        );

        searchPanel.add(
                startDatePicker
        );


        searchPanel.add(
                new JLabel("종료일")
        );

        searchPanel.add(
                endDatePicker
        );


        searchPanel.add(
                searchButton
        );


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

        tableModel =
                new DefaultTableModel(
                        columns,
                        0
                );

        table =
                new JTable(
                        tableModel
                );

        JScrollPane scrollPane =
                new JScrollPane(
                        table
                );


        // =========================
        // 화면 배치
        // =========================

        setLayout(
                new BorderLayout()
        );

        add(
                searchPanel,
                BorderLayout.NORTH
        );

        add(
                scrollPane,
                BorderLayout.CENTER
        );


        // 검색 버튼 클릭
        searchButton.addActionListener(
                e -> searchLoans()
        );


        setVisible(true);
    }


    // =========================
    // 일정 검색
    // =========================

    private void searchLoans() {

        // 선택한 날짜 가져오기
        java.util.Date start =
                (java.util.Date)
                        startDatePicker
                                .getModel()
                                .getValue();

        java.util.Date end =
                (java.util.Date)
                        endDatePicker
                                .getModel()
                                .getValue();


        // 날짜를 선택하지 않은 경우
        if (start == null || end == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "시작일과 종료일을 모두 선택해주세요."
            );

            return;
        }


        // 시작일이 종료일보다 뒤인 경우
        if (start.after(end)) {

            JOptionPane.showMessageDialog(
                    this,
                    "시작일은 종료일보다 늦을 수 없습니다."
            );

            return;
        }


        // DB에 넘기기 위해 yyyy-MM-dd 문자열로 변환
        SimpleDateFormat sdf =
                new SimpleDateFormat(
                        "yyyy-MM-dd"
                );

        String startDate =
                sdf.format(start);

        String endDate =
                sdf.format(end);


        LoanDAO loanDAO =
                new LoanDAO();

        List<LoanScheduleResult> loans =
                loanDAO.getLoansByDateRange(
                        startDate,
                        endDate
                );


        // 기존 테이블 내용 제거
        tableModel.setRowCount(0);


        // 검색 결과 출력
        for (LoanScheduleResult loan : loans) {

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


    // =========================
    // 달력 날짜 표시 형식
    // =========================

    public static class DateLabelFormatter
            extends JFormattedTextField.AbstractFormatter {

        private final String pattern =
                "yyyy-MM-dd";

        private final SimpleDateFormat dateFormatter =
                new SimpleDateFormat(pattern);


        @Override
        public Object stringToValue(String text)
                throws ParseException {

            return dateFormatter.parseObject(text);
        }


        @Override
        public String valueToString(Object value)
                throws ParseException {

            if (value != null) {

                Calendar calendar =
                        (Calendar) value;

                return dateFormatter.format(
                        calendar.getTime()
                );
            }

            return "";
        }
    }


    public static void main(String[] args) {

        new LoanScheduleFrame();

    }
}