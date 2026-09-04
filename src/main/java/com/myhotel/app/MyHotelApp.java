package com.myhotel.app;

import com.myhotel.ui.AppTheme;
import com.myhotel.ui.LoginFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class MyHotelApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            AppTheme.install();
            new LoginFrame().setVisible(true);
        });
    }
}
