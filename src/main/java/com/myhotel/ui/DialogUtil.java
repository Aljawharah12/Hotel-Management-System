package com.myhotel.ui;

import javax.swing.JOptionPane;

public final class DialogUtil {
    private DialogUtil() {
    }

    public static void info(Object parent, String message) {
        JOptionPane.showMessageDialog(null, message, "My Hotel System", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void error(Object parent, Exception exception) {
        String message = exception.getMessage();
        if (message == null || message.isBlank()) {
            message = exception.getClass().getSimpleName();
        }
        JOptionPane.showMessageDialog(null, message, "My Hotel System", JOptionPane.ERROR_MESSAGE);
    }
}
