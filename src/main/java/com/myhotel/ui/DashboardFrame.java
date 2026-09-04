package com.myhotel.ui;

import com.myhotel.app.Session;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class DashboardFrame extends JFrame {
    protected final Session session;
    protected final JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
    private final Map<String, Integer> tabIndexes = new LinkedHashMap<>();

    protected DashboardFrame(Session session, String title) {
        this.session = session;
        setTitle(title);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1240, 760);
        setMinimumSize(new Dimension(1080, 660));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(AppTheme.BACKGROUND);
        root.add(topBar(), BorderLayout.NORTH);
        root.add(tabs, BorderLayout.CENTER);
        setContentPane(root);
    }

    private JPanel topBar() {
        JPanel shell = new JPanel(new BorderLayout());
        shell.setBackground(AppTheme.SIDEBAR);
        shell.setBorder(BorderFactory.createEmptyBorder(18, 28, 16, 28));

        JPanel brand = new JPanel(new BorderLayout(16, 0));
        brand.setOpaque(false);
        brand.add(AppTheme.imageLabel("/images/intercontinental-logo.png", 190, 56), BorderLayout.WEST);
        JPanel nameStack = new JPanel(new BorderLayout(0, 3));
        nameStack.setOpaque(false);
        JLabel hotel = new JLabel("Intercontinental Dar Altawhid Makkah");
        hotel.setForeground(Color.WHITE);
        hotel.setFont(new Font("SansSerif", Font.BOLD, 22));
        JLabel branch = new JLabel("Branch: Makkah - Dar Al Tawhid");
        branch.setForeground(new Color(224, 211, 180));
        branch.setFont(new Font("SansSerif", Font.PLAIN, 12));
        nameStack.add(hotel, BorderLayout.NORTH);
        nameStack.add(branch, BorderLayout.SOUTH);
        brand.add(nameStack, BorderLayout.CENTER);
        shell.add(brand, BorderLayout.WEST);

        JPanel userBox = new JPanel(new BorderLayout(12, 0));
        userBox.setOpaque(false);
        JLabel user = new JLabel(session.name());
        user.setForeground(AppTheme.NAVY_DARK);
        user.setOpaque(true);
        user.setBackground(AppTheme.GOLD);
        user.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        JButton logout = AppTheme.ghostButton("Logout");
        logout.addActionListener(event -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        userBox.add(user, BorderLayout.CENTER);
        userBox.add(logout, BorderLayout.EAST);
        shell.add(userBox, BorderLayout.EAST);
        return shell;
    }

    protected void addScreen(String name, String label, JPanel panel) {
        tabs.addTab(label, panel);
        tabIndexes.put(name, tabs.getTabCount() - 1);
    }

    protected void addLogout() {
        // Logout is part of the top bar in this layout.
    }

    protected void showScreen(String name) {
        Integer index = tabIndexes.get(name);
        if (index != null) {
            tabs.setSelectedIndex(index);
        }
    }

    protected JPanel page(String heading) {
        JPanel page = new JPanel(new BorderLayout(0, 18));
        page.setBackground(AppTheme.BACKGROUND);
        AppTheme.pad(page, 24, 28, 26, 28);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = AppTheme.title(heading);
        header.add(title, BorderLayout.WEST);
        page.add(header, BorderLayout.NORTH);
        return page;
    }
}
