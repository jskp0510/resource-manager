package com.team.resourcemanager;

import com.team.resourcemanager.ui.LoginFrame;
import com.team.resourcemanager.ui.UIStyle;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        UIStyle.apply();
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
