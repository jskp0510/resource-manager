package com.team.resourcemanager.ui;

import com.team.resourcemanager.dao.LoanDAO;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

public class LoanManageFrame extends JFrame {

    // =========================
    // 대여 입력창
    // =========================

    private JTextField userIdField;
    private JTextField loanItemIdField;
    private JTextField purposeField;

    private JDatePickerImpl startDatePicker;
    private JDatePickerImpl dueDatePicker;


    // =========================
    // 반납 입력창
    // =========================

    private JTextField loanIdField;
    private JTextField returnItemIdField;

    private JDatePickerImpl returnDatePicker;


    public LoanManageFrame() {

        setTitle("대여 / 반납 관리");
        setSize(550, 550);

        setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );

        setLocationRelativeTo(null);

        setLayout(
                new GridLayout(2, 1, 10, 10)
        );


        // =========================
        // 대여 영역
        // =========================

        JPanel loanPanel =
                new JPanel(
                        new GridLayout(6, 2, 5, 5)
                );

        loanPanel.setBorder(
                BorderFactory.createTitledBorder(
                        "대여 처리"
                )
        );


        userIdField = new JTextField();
        loanItemIdField = new JTextField();
        purposeField = new JTextField();

        startDatePicker = createDatePicker();
        dueDatePicker = createDatePicker();

        JButton loanButton =
                new JButton("대여하기");


        loanPanel.add(new JLabel("사용자 ID"));
        loanPanel.add(userIdField);

        loanPanel.add(new JLabel("물품 ID"));
        loanPanel.add(loanItemIdField);

        loanPanel.add(new JLabel("대여 시작일"));
        loanPanel.add(startDatePicker);

        loanPanel.add(new JLabel("반납 예정일"));
        loanPanel.add(dueDatePicker);

        loanPanel.add(new JLabel("대여 목적"));
        loanPanel.add(purposeField);

        loanPanel.add(new JLabel(""));
        loanPanel.add(loanButton);


        // =========================
        // 반납 영역
        // =========================

        JPanel returnPanel =
                new JPanel(
                        new GridLayout(4, 2, 5, 5)
                );

        returnPanel.setBorder(
                BorderFactory.createTitledBorder(
                        "반납 처리"
                )
        );


        loanIdField = new JTextField();
        returnItemIdField = new JTextField();

        returnDatePicker = createDatePicker();

        JButton returnButton =
                new JButton("반납하기");


        returnPanel.add(new JLabel("대여 ID"));
        returnPanel.add(loanIdField);

        returnPanel.add(new JLabel("물품 ID"));
        returnPanel.add(returnItemIdField);

        returnPanel.add(new JLabel("반납일"));
        returnPanel.add(returnDatePicker);

        returnPanel.add(new JLabel(""));
        returnPanel.add(returnButton);


        // 화면에 추가
        add(loanPanel);
        add(returnPanel);


        // =========================
        // 버튼 이벤트
        // =========================

        loanButton.addActionListener(
                e -> loanItem()
        );

        returnButton.addActionListener(
                e -> returnItem()
        );


        setVisible(true);
    }


    // =========================
    // 대여 처리
    // =========================

    private void loanItem() {

        // 문자 입력칸 검사
        if (userIdField.getText().isBlank()
                || loanItemIdField.getText().isBlank()
                || purposeField.getText().isBlank()) {

            JOptionPane.showMessageDialog(
                    this,
                    "대여 정보를 모두 입력해주세요."
            );

            return;
        }


        // 달력에서 선택한 날짜 가져오기
        Date startDateValue =
                (Date) startDatePicker
                        .getModel()
                        .getValue();

        Date dueDateValue =
                (Date) dueDatePicker
                        .getModel()
                        .getValue();


        // 날짜를 선택하지 않았을 경우
        if (startDateValue == null
                || dueDateValue == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "대여 시작일과 반납 예정일을 모두 선택해주세요."
            );

            return;
        }


        // 시작일보다 반납 예정일이 빠른 경우
        if (dueDateValue.before(startDateValue)) {

            JOptionPane.showMessageDialog(
                    this,
                    "반납 예정일은 대여 시작일보다 빠를 수 없습니다."
            );

            return;
        }


        try {

            int userId =
                    Integer.parseInt(
                            userIdField.getText()
                    );

            int itemId =
                    Integer.parseInt(
                            loanItemIdField.getText()
                    );

            String purpose =
                    purposeField.getText();


            // Date → yyyy-MM-dd 문자열 변환
            SimpleDateFormat sdf =
                    new SimpleDateFormat(
                            "yyyy-MM-dd"
                    );

            String startDate =
                    sdf.format(startDateValue);

            String dueDate =
                    sdf.format(dueDateValue);


            LoanDAO loanDAO =
                    new LoanDAO();


            boolean success =
                    loanDAO.loanItem(
                            userId,
                            itemId,
                            startDate,
                            dueDate,
                            purpose
                    );


            if (success) {

                JOptionPane.showMessageDialog(
                        this,
                        "대여 처리가 완료되었습니다."
                );


                // 입력칸 초기화
                userIdField.setText("");
                loanItemIdField.setText("");
                purposeField.setText("");

                startDatePicker
                        .getModel()
                        .setValue(null);

                dueDatePicker
                        .getModel()
                        .setValue(null);

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "대여할 수 없는 물품입니다."
                );
            }


        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "사용자 ID와 물품 ID는 숫자로 입력해주세요."
            );

        } catch (Exception e) {

            e.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "대여 처리 중 오류가 발생했습니다."
            );
        }
    }


    // =========================
    // 반납 처리
    // =========================

    private void returnItem() {

        // 문자 입력칸 검사
        if (loanIdField.getText().isBlank()
                || returnItemIdField.getText().isBlank()) {

            JOptionPane.showMessageDialog(
                    this,
                    "반납 정보를 모두 입력해주세요."
            );

            return;
        }


        // 달력에서 선택한 반납일
        Date returnDateValue =
                (Date) returnDatePicker
                        .getModel()
                        .getValue();


        if (returnDateValue == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "반납일을 선택해주세요."
            );

            return;
        }


        try {

            int loanId =
                    Integer.parseInt(
                            loanIdField.getText()
                    );

            int itemId =
                    Integer.parseInt(
                            returnItemIdField.getText()
                    );


            SimpleDateFormat sdf =
                    new SimpleDateFormat(
                            "yyyy-MM-dd"
                    );

            String returnDate =
                    sdf.format(returnDateValue);


            LoanDAO loanDAO =
                    new LoanDAO();


            boolean success =
                    loanDAO.returnItem(
                            loanId,
                            itemId,
                            returnDate
                    );


            if (success) {

                JOptionPane.showMessageDialog(
                        this,
                        "반납 처리가 완료되었습니다."
                );


                // 입력칸 초기화
                loanIdField.setText("");
                returnItemIdField.setText("");

                returnDatePicker
                        .getModel()
                        .setValue(null);

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "반납할 수 없는 대여 기록입니다."
                );
            }


        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "대여 ID와 물품 ID는 숫자로 입력해주세요."
            );

        } catch (Exception e) {

            e.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "반납 처리 중 오류가 발생했습니다."
            );
        }
    }


    // =========================
    // 달력 생성
    // =========================

    private JDatePickerImpl createDatePicker() {

        UtilDateModel model =
                new UtilDateModel();

        Properties properties =
                new Properties();

        properties.put(
                "text.today",
                "오늘"
        );

        properties.put(
                "text.month",
                "월"
        );

        properties.put(
                "text.year",
                "년"
        );


        JDatePanelImpl datePanel =
                new JDatePanelImpl(
                        model,
                        properties
                );


        return new JDatePickerImpl(
                datePanel,
                new DateLabelFormatter()
        );
    }


    // =========================
    // 날짜 표시 형식
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

        new LoanManageFrame();

    }
}