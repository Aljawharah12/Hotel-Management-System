package com.myhotel.ui;

import com.myhotel.app.Session;
import com.myhotel.db.DB;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class LoginFrame extends JFrame {
    private final JRadioButton guestRole = new JRadioButton("Guest", true);
    private final JRadioButton employeeRole = new JRadioButton("Employee");
    private final JTextField idField = AppTheme.field("Enter Guest ID or Employee ID");
    private final JPasswordField passwordField = new JPasswordField();

    public LoginFrame() {
        setTitle("My Hotel - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1080, 760);
        setMinimumSize(new Dimension(940, 700));
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(AppTheme.BACKGROUND);

        add(loginCard(), new GridBagConstraints());
    }

    private JPanel loginCard() {
        JPanel card = new JPanel(new BorderLayout(0, 0));
        card.setPreferredSize(new Dimension(620, 610));
        card.setBackground(AppTheme.CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        JPanel visual = new JPanel(new BorderLayout());
        visual.setBackground(AppTheme.SIDEBAR);
        visual.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        visual.add(AppTheme.imageLabel("/images/hotel.png", 620, 185), BorderLayout.CENTER);
        card.add(visual, BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 0));
        body.setBackground(AppTheme.CARD);
        JLabel logo = AppTheme.imageLabel("/images/intercontinental-logo.png", 220, 60);
        logo.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        body.add(logo, BorderLayout.NORTH);
        body.add(formPanel(), BorderLayout.CENTER);
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private JPanel formPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(AppTheme.CARD);
        form.setBorder(BorderFactory.createEmptyBorder(10, 56, 24, 56));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets.set(5, 0, 5, 0);

        JLabel title = AppTheme.title("Sign In");
        title.setHorizontalAlignment(JLabel.LEFT);
        form.add(title, gbc);

        gbc.gridy++;
        form.add(AppTheme.subtitle("Intercontinental Dar Altawhid Makkah"), gbc);

        gbc.gridy++;
        JPanel roles = new JPanel(new GridLayout(1, 2, 10, 0));
        roles.setOpaque(false);
        ButtonGroup group = new ButtonGroup();
        group.add(guestRole);
        group.add(employeeRole);
        prepareRole(guestRole);
        prepareRole(employeeRole);
        guestRole.addActionListener(event -> syncRoleButtons());
        employeeRole.addActionListener(event -> syncRoleButtons());
        roles.add(guestRole);
        roles.add(employeeRole);
        form.add(roles, gbc);
        syncRoleButtons();

        gbc.gridy++;
        form.add(labelWithField("ID", idField), gbc);

        gbc.gridy++;
        passwordField.setPreferredSize(new Dimension(300, 42));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER),
                BorderFactory.createEmptyBorder(9, 12, 9, 12)
        ));
        passwordField.setBackground(AppTheme.CARD);
        form.add(labelWithField("Password", passwordField), gbc);

        gbc.gridy++;
        JButton login = AppTheme.primaryButton("Sign In");
        login.setPreferredSize(new Dimension(300, 46));
        login.addActionListener(event -> login());
        form.add(login, gbc);

        gbc.gridy++;
        JLabel noAccount = new JLabel("Don't have an account? Create one below");
        noAccount.setForeground(AppTheme.MUTED);
        noAccount.setHorizontalAlignment(JLabel.CENTER);
        form.add(noAccount, gbc);

        gbc.gridy++;
        JButton createAccount = AppTheme.ghostButton("Create Guest Account");
        createAccount.addActionListener(event -> registerGuest());
        form.add(createAccount, gbc);
        return form;
    }

    private void prepareRole(JRadioButton role) {
        role.setFocusPainted(false);
        role.setOpaque(true);
        role.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        role.setFont(new Font("SansSerif", Font.BOLD, 13));
    }

    private JPanel labelWithField(String label, java.awt.Component field) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);
        JLabel text = new JLabel(label);
        text.setFont(new Font("SansSerif", Font.BOLD, 13));
        text.setForeground(AppTheme.TEXT);
        panel.add(text, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private void syncRoleButtons() {
        styleRole(guestRole, guestRole.isSelected());
        styleRole(employeeRole, employeeRole.isSelected());
    }

    private void styleRole(JRadioButton role, boolean selected) {
        role.setBackground(selected ? AppTheme.SIDEBAR : AppTheme.ACCENT_SOFT);
        role.setForeground(selected ? Color.WHITE : AppTheme.TEXT);
    }

    private void login() {
        String idText = idField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (idText.isBlank() || password.isBlank()) {
            DialogUtil.info(this, "Please enter ID and password.");
            return;
        }

        try {
            int id = Integer.parseInt(idText.replaceAll("[^0-9]", ""));
            Session session = guestRole.isSelected() ? findGuest(id, password) : findEmployee(id, password);
            if (session == null) {
                DialogUtil.info(this, "Invalid ID or password.");
                return;
            }
            dispose();
            if (session.role() == Session.Role.GUEST) {
                new GuestFrame(session).setVisible(true);
            } else {
                new EmployeeFrame(session).setVisible(true);
            }
        } catch (NumberFormatException exception) {
            DialogUtil.info(this, "ID must contain a number.");
        } catch (Exception exception) {
            DialogUtil.error(this, exception);
        }
    }

    private void registerGuest() {
        JTextField first = AppTheme.field("First name");
        JTextField last = AppTheme.field("Last name");
        JTextField email = AppTheme.field("Email");
        JTextField phone = AppTheme.field("05xxxxxxxx");
        JPasswordField password = new JPasswordField();
        password.setPreferredSize(new Dimension(300, 42));
        password.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER),
                BorderFactory.createEmptyBorder(9, 12, 9, 12)
        ));

        JPanel form = new JPanel(new GridLayout(5, 2, 10, 10));
        form.add(new JLabel("First Name"));
        form.add(first);
        form.add(new JLabel("Last Name"));
        form.add(last);
        form.add(new JLabel("Email"));
        form.add(email);
        form.add(new JLabel("Phone"));
        form.add(phone);
        form.add(new JLabel("Password"));
        form.add(password);

        int choice = JOptionPane.showConfirmDialog(this, form, "Create Guest Account", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        String firstName = first.getText().trim();
        String lastName = last.getText().trim();
        String guestEmail = email.getText().trim();
        String guestPhone = phone.getText().trim();
        String guestPassword = new String(password.getPassword()).trim();
        if (firstName.isBlank() || lastName.isBlank() || guestEmail.isBlank() || guestPhone.isBlank() || guestPassword.isBlank()) {
            DialogUtil.info(this, "Please fill all fields.");
            return;
        }

        String sql = "INSERT INTO Guest (Guest_id, Fname, Lname, G_email, G_password, Phone_number, Password) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DB.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            int newId = nextGuestId(connection);
            statement.setInt(1, newId);
            statement.setString(2, firstName);
            statement.setString(3, lastName);
            statement.setString(4, guestEmail);
            statement.setString(5, guestPassword);
            statement.setString(6, guestPhone);
            statement.setString(7, guestPassword);
            statement.executeUpdate();
            guestRole.setSelected(true);
            syncRoleButtons();
            idField.setText(String.valueOf(newId));
            passwordField.setText(guestPassword);
            DialogUtil.info(this, "Account created. Your Guest ID is " + newId + ".");
        } catch (Exception exception) {
            DialogUtil.error(this, exception);
        }
    }

    private int nextGuestId(Connection connection) throws Exception {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT COALESCE(MAX(Guest_id), 0) + 1 FROM Guest")) {
            resultSet.next();
            return resultSet.getInt(1);
        }
    }

    private Session findGuest(int id, String password) throws Exception {
        String sql = "SELECT Guest_id, Fname, Lname, G_email FROM Guest WHERE Guest_id = ? AND Password = ?";
        try (Connection connection = DB.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.setString(2, password);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String name = resultSet.getString("Fname") + " " + resultSet.getString("Lname");
                    return new Session(Session.Role.GUEST, resultSet.getInt("Guest_id"), name, resultSet.getString("G_email"));
                }
                return null;
            }
        }
    }

    private Session findEmployee(int id, String password) throws Exception {
        String sql = "SELECT Employee_id, Fname, Lname, Role FROM Employee WHERE Employee_id = ? AND Emp_password = ?";
        try (Connection connection = DB.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.setString(2, password);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String name = resultSet.getString("Fname") + " " + resultSet.getString("Lname");
                    return new Session(Session.Role.EMPLOYEE, resultSet.getInt("Employee_id"), name, resultSet.getString("Role"));
                }
                return null;
            }
        }
    }
}
