package com.team.resourcemanager.ui;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;

/** 공통 Swing 글꼴·버튼·표 기본 스타일. */
public final class UIStyle {
    private UIStyle() {
    }

    public static void apply() {
        Font baseFont = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
        Font tableHeaderFont = new Font(Font.SANS_SERIF, Font.BOLD, 14);

        UIManager.put("Label.font", baseFont);
        UIManager.put("Button.font", baseFont);
        UIManager.put("Button.margin", new Insets(6, 12, 6, 12));
        UIManager.put("TextField.font", baseFont);
        UIManager.put("ComboBox.font", baseFont);
        UIManager.put("Table.font", baseFont);
        UIManager.put("TableHeader.font", tableHeaderFont);
        UIManager.put("Table.rowHeight", 26);
        UIManager.put("OptionPane.messageFont", baseFont);
        UIManager.put("OptionPane.buttonFont", baseFont);
    }

    public static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        ListCellRenderer<Object> renderer = new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                label.setVerticalAlignment(SwingConstants.BOTTOM);
                Border padding = BorderFactory.createEmptyBorder(3, 6, 1, 6);
                label.setBorder(BorderFactory.createCompoundBorder(label.getBorder(), padding));
                return label;
            }
        };
        comboBox.setRenderer(renderer);
    }
}
