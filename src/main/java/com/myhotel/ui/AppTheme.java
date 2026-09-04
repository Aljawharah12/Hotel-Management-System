package com.myhotel.ui;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.net.URL;

public final class AppTheme {
    public static final Color NAVY = new Color(20, 54, 52);
    public static final Color NAVY_DARK = new Color(10, 31, 32);
    public static final Color GOLD = new Color(196, 153, 76);
    public static final Color GOLD_DARK = new Color(142, 103, 43);
    public static final Color BACKGROUND = new Color(245, 242, 235);
    public static final Color CARD = new Color(255, 254, 251);
    public static final Color TEXT = new Color(25, 34, 38);
    public static final Color MUTED = new Color(107, 104, 96);
    public static final Color BORDER = new Color(219, 210, 191);
    public static final Color SIDEBAR = new Color(13, 38, 40);
    public static final Color ACCENT_SOFT = new Color(243, 234, 216);

    private AppTheme() {
    }

    public static void install() {
        UIManager.put("Label.font", new Font("SansSerif", Font.PLAIN, 14));
        UIManager.put("TabbedPane.font", new Font("SansSerif", Font.BOLD, 13));
        UIManager.put("Button.font", new Font("SansSerif", Font.BOLD, 13));
        UIManager.put("TextField.font", new Font("SansSerif", Font.PLAIN, 14));
        UIManager.put("Table.font", new Font("SansSerif", Font.PLAIN, 13));
        UIManager.put("Table.rowHeight", 34);
        UIManager.put("Panel.background", BACKGROUND);
        UIManager.put("ScrollPane.background", BACKGROUND);
    }

    public static JLabel title(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 30));
        label.setForeground(TEXT);
        return label;
    }

    public static JLabel subtitle(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(MUTED);
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        return label;
    }

    public static JLabel eyebrow(String text) {
        JLabel label = new JLabel(text.toUpperCase());
        label.setForeground(GOLD_DARK);
        label.setFont(new Font("SansSerif", Font.BOLD, 11));
        return label;
    }

    public static JButton primaryButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(GOLD);
        button.setForeground(NAVY_DARK);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 22, 12, 22));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static JButton ghostButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(CARD);
        button.setForeground(GOLD_DARK);
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static JButton navButton(String text) {
        JButton button = new JButton(text);
        button.setForeground(new Color(237, 229, 211));
        button.setBackground(SIDEBAR);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(11, 16, 11, 16));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static JPanel card() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 3, 1, BORDER),
                new EmptyBorder(20, 20, 20, 20)
        ));
        return panel;
    }

    public static JPanel featurePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(250, 248, 242));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 6, 1, 1, GOLD),
                new EmptyBorder(24, 24, 24, 24)
        ));
        return panel;
    }

    public static JPanel darkPanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g2 = (Graphics2D) graphics.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(SIDEBAR);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(23, 64, 61));
                g2.fillRect(0, getHeight() - 7, getWidth(), 7);
                g2.dispose();
                super.paintComponent(graphics);
            }
        };
        panel.setOpaque(false);
        return panel;
    }

    public static JPanel statCard(String title, String value, String icon) {
        JPanel panel = featurePanel();
        JPanel stack = new JPanel(new GridLayout(3, 1, 0, 4));
        stack.setOpaque(false);
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        iconLabel.setForeground(GOLD_DARK);
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        valueLabel.setForeground(TEXT);
        stack.add(iconLabel);
        stack.add(valueLabel);
        stack.add(subtitle(title));
        panel.add(stack, BorderLayout.CENTER);
        return panel;
    }

    public static JTextField field(String placeholder) {
        JTextField field = new JTextField();
        field.setToolTipText(placeholder);
        field.setPreferredSize(new Dimension(300, 42));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(9, 12, 9, 12)
        ));
        field.setBackground(new Color(255, 254, 251));
        field.setForeground(TEXT);
        field.setCaretColor(GOLD_DARK);
        return field;
    }

    public static JScrollPane tableScroll(JTable table) {
        table.setFillsViewportHeight(true);
        table.setGridColor(new Color(235, 229, 216));
        table.setSelectionBackground(ACCENT_SOFT);
        table.setSelectionForeground(TEXT);
        table.setShowVerticalLines(false);
        JTableHeader header = table.getTableHeader();
        header.setBackground(SIDEBAR);
        header.setForeground(new Color(244, 237, 220));
        header.setFont(new Font("SansSerif", Font.BOLD, 13));
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setBorder(new EmptyBorder(0, 10, 0, 10));
        table.setDefaultRenderer(Object.class, renderer);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER));
        scroll.getViewport().setBackground(CARD);
        return scroll;
    }

    public static void pad(JComponent component, int top, int left, int bottom, int right) {
        component.setBorder(new EmptyBorder(top, left, bottom, right));
    }

    public static Component gap(int width, int height) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(width, height));
        return panel;
    }
    public static JLabel imageLabel(String resourcePath, int width, int height) {
        URL resource = AppTheme.class.getResource(resourcePath);
        if (resource == null) {
            return new JLabel();
        }
        Image image = new ImageIcon(resource).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        JLabel label = new JLabel(new ImageIcon(image));
        label.setPreferredSize(new Dimension(width, height));
        return label;
    }
}
