package com.myhotel.ui;

import com.myhotel.app.Session;
import com.myhotel.db.DB;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class EmployeeFrame extends DashboardFrame {
    private JTable reservationsTable;

    public EmployeeFrame(Session session) {
        super(session, "My Hotel - Employee View");
        addScreen("dashboard", "Dashboard", dashboard());
        addScreen("profile", "My Profile", profile());
        addScreen("reservations", "Reservations", reservations());
        addScreen("guests", "Guests", simpleTable("Guests", guestsSql()));
        addScreen("rooms", "Rooms", simpleTable("Rooms", "SELECT Room_number, Status, Type FROM Room ORDER BY Room_number"));
        addScreen("services", "Services", simpleTable("Services", "SELECT Service_id, Name, Price FROM Service ORDER BY Service_id"));
        addScreen("invoices", "Invoices", simpleTable("Invoices", "SELECT Invoice_id, Status, Payment_type, Total_amount, Reservation_num FROM Invoice ORDER BY Invoice_id"));
        addScreen("reservation_services", "Reservation Services", simpleTable("Reservation Services", "SELECT Reservation_num, Service_id FROM Res_Service ORDER BY Reservation_num"));
        addScreen("employees", "Employees", simpleTable("Employees", "SELECT Employee_id, Fname, Lname, E_email, Role FROM Employee ORDER BY Employee_id"));
        addScreen("assignments", "Assignments", simpleTable("Employee Assignments", assignmentsSql()));
        addLogout();
        showScreen("dashboard");
    }

    private JPanel dashboard() {
        JPanel page = page("Hotel Overview");
        JPanel body = new JPanel(new BorderLayout(0, 18));
        body.setOpaque(false);

        JPanel hero = AppTheme.darkPanel();
        hero.setBorder(javax.swing.BorderFactory.createEmptyBorder(22, 24, 22, 24));
        JPanel heroStack = new JPanel(new GridLayout(2, 1, 0, 4));
        heroStack.setOpaque(false);
        JLabel welcome = new JLabel("Employee Operations Center");
        welcome.setForeground(Color.WHITE);
        welcome.setFont(new Font("SansSerif", Font.BOLD, 24));
        JLabel note = new JLabel("Reservations, guests, rooms, invoices, and requested services in one workspace.");
        note.setForeground(new Color(224, 211, 180));
        heroStack.add(welcome);
        heroStack.add(note);
        hero.add(heroStack, BorderLayout.CENTER);
        body.add(hero, BorderLayout.NORTH);

        JPanel metrics = new JPanel(new GridLayout(1, 4, 14, 0));
        metrics.setOpaque(false);
        metrics.add(operationTile("Reservations", scalar("SELECT COUNT(*) FROM Reservation")));
        metrics.add(operationTile("Guests", scalar("SELECT COUNT(*) FROM Guest")));
        metrics.add(operationTile("Rooms", scalar("SELECT COUNT(*) FROM Room")));
        metrics.add(operationTile("Pending Invoices", scalar("SELECT COUNT(*) FROM Invoice WHERE Status = 'Pending'")));

        JPanel tableCard = AppTheme.card();
        tableCard.add(sectionTitle("Reservation Details"), BorderLayout.NORTH);
        tableCard.add(tableFor("SELECT r.Reservation_num, CONCAT(g.Fname, ' ', g.Lname) AS Guest_name, r.Room_number, rm.Type AS Room_type, r.Check_in, r.Check_out, COALESCE(i.Status, 'No invoice') AS Invoice_status FROM Reservation r JOIN Guest g ON r.Guest_id = g.Guest_id LEFT JOIN Room rm ON r.Room_number = rm.Room_number LEFT JOIN Invoice i ON r.Reservation_num = i.Reservation_num ORDER BY r.Check_in DESC LIMIT 12"), BorderLayout.CENTER);

        JPanel content = new JPanel(new BorderLayout(0, 18));
        content.setOpaque(false);
        content.add(metrics, BorderLayout.NORTH);
        content.add(tableCard, BorderLayout.CENTER);
        body.add(content, BorderLayout.CENTER);

        page.add(body, BorderLayout.CENTER);
        return page;
    }

    private String assignmentsSql() {
        return "SELECT r.Reservation_num, "
                + "COALESCE(er.Employee_id, (SELECT Employee_id FROM Employee ORDER BY Employee_id LIMIT 1)) AS Employee_id, "
                + "CONCAT(g.Fname, ' ', g.Lname) AS Guest_name, "
                + "r.Check_in, r.Check_out "
                + "FROM Reservation r "
                + "JOIN Guest g ON r.Guest_id = g.Guest_id "
                + "LEFT JOIN Emp_Reservation er ON r.Reservation_num = er.Reservation_num "
                + "ORDER BY r.Reservation_num DESC";
    }

    private JLabel sectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(AppTheme.TEXT);
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 12, 0));
        return label;
    }

    private JPanel operationTile(String label, String value) {
        JPanel tile = new JPanel(new BorderLayout(0, 4));
        tile.setBackground(AppTheme.SIDEBAR);
        tile.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 16, 15, 16));
        JLabel top = new JLabel(label);
        top.setForeground(new Color(220, 210, 185));
        JLabel bottom = new JLabel(value);
        bottom.setForeground(AppTheme.GOLD);
        bottom.setFont(new Font("SansSerif", Font.BOLD, 20));
        tile.add(top, BorderLayout.NORTH);
        tile.add(bottom, BorderLayout.CENTER);
        return tile;
    }

    private JPanel profile() {
        JPanel page = page("My Employee Profile");
        JPanel card = AppTheme.card();
        JPanel fields = new JPanel(new GridLayout(8, 2, 12, 12));
        fields.setOpaque(false);

        JTextField id = AppTheme.field("Employee ID");
        JTextField first = AppTheme.field("First name");
        JTextField last = AppTheme.field("Last name");
        JTextField address = AppTheme.field("Address");
        JTextField phone = AppTheme.field("Phone");
        JTextField email = AppTheme.field("Email");
        JTextField role = AppTheme.field("Role");
        JTextField salary = AppTheme.field("Salary");
        id.setEditable(false);
        role.setEditable(false);
        salary.setEditable(false);
        loadEmployeeProfile(id, first, last, address, phone, email, role, salary);

        fields.add(new JLabel("Employee ID"));
        fields.add(id);
        fields.add(new JLabel("First Name"));
        fields.add(first);
        fields.add(new JLabel("Last Name"));
        fields.add(last);
        fields.add(new JLabel("Address"));
        fields.add(address);
        fields.add(new JLabel("Phone"));
        fields.add(phone);
        fields.add(new JLabel("Email"));
        fields.add(email);
        fields.add(new JLabel("Role"));
        fields.add(role);
        fields.add(new JLabel("Salary"));
        fields.add(salary);
        card.add(fields, BorderLayout.CENTER);

        JButton save = AppTheme.primaryButton("Update Contact Info");
        save.addActionListener(event -> updateEmployeeProfile(first, last, address, phone, email));
        card.add(save, BorderLayout.SOUTH);
        page.add(card, BorderLayout.CENTER);
        return page;
    }

    private JPanel reservations() {
        JPanel page = page("Reservations");
        JPanel root = new JPanel(new BorderLayout(0, 12));
        root.setOpaque(false);

        root.add(reservationTools(), BorderLayout.NORTH);

        reservationsTable = new JTable();
        loadReservations(null);
        root.add(AppTheme.tableScroll(reservationsTable), BorderLayout.CENTER);

        page.add(root, BorderLayout.CENTER);
        return page;
    }

    private JPanel reservationTools() {
        JPanel tools = new JPanel(new GridLayout(1, 3, 14, 0));
        tools.setOpaque(false);

        JPanel addCard = AppTheme.card();
        JPanel addFields = new JPanel(new GridLayout(7, 2, 10, 8));
        addFields.setOpaque(false);
        JTextField guestId = AppTheme.field("Guest ID");
        JComboBox<String> room = new JComboBox<>();
        loadAvailableRooms(room);
        JTextField reservationDate = AppTheme.field("YYYY-MM-DD");
        reservationDate.setText(LocalDate.now().toString());
        reservationDate.setEditable(false);
        JTextField checkIn = AppTheme.field("YYYY-MM-DD");
        JTextField checkOut = AppTheme.field("YYYY-MM-DD");
        JComboBox<String> paymentType = new JComboBox<>(new String[] {"Credit Card", "Debit Card", "Cash"});
        JTextField price = AppTheme.field("Room price");
        price.setEditable(false);
        updatePricePreview(room, price);
        room.addActionListener(event -> updatePricePreview(room, price));
        addFields.add(new JLabel("Guest ID"));
        addFields.add(guestId);
        addFields.add(new JLabel("Available Room"));
        addFields.add(room);
        addFields.add(new JLabel("Reservation Date"));
        addFields.add(reservationDate);
        addFields.add(new JLabel("Check In"));
        addFields.add(checkIn);
        addFields.add(new JLabel("Check Out"));
        addFields.add(checkOut);
        addFields.add(new JLabel("Payment Type"));
        addFields.add(paymentType);
        addFields.add(new JLabel("Price"));
        addFields.add(price);
        addCard.add(addFields, BorderLayout.CENTER);
        JButton add = AppTheme.primaryButton("Add Reservation");
        add.addActionListener(event -> addReservationForGuest(guestId, room, reservationDate, checkIn, checkOut, paymentType));
        addCard.add(add, BorderLayout.SOUTH);

        JPanel updateCard = AppTheme.card();
        JPanel updateFields = new JPanel(new GridLayout(6, 2, 10, 8));
        updateFields.setOpaque(false);
        JTextField updateReservationNumber = AppTheme.field("Reservation number");
        JComboBox<String> updateRoom = new JComboBox<>();
        loadAvailableRooms(updateRoom);
        JTextField updateCheckIn = AppTheme.field("YYYY-MM-DD");
        JTextField updateCheckOut = AppTheme.field("YYYY-MM-DD");
        JComboBox<String> updatePaymentType = new JComboBox<>(new String[] {"Credit Card", "Debit Card", "Cash"});
        JTextField updatePrice = AppTheme.field("Room price");
        updatePrice.setEditable(false);
        updatePricePreview(updateRoom, updatePrice);
        updateRoom.addActionListener(event -> updatePricePreview(updateRoom, updatePrice));
        updateFields.add(new JLabel("Reservation Number"));
        updateFields.add(updateReservationNumber);
        updateFields.add(new JLabel("Available Room"));
        updateFields.add(updateRoom);
        updateFields.add(new JLabel("Check In"));
        updateFields.add(updateCheckIn);
        updateFields.add(new JLabel("Check Out"));
        updateFields.add(updateCheckOut);
        updateFields.add(new JLabel("Payment Type"));
        updateFields.add(updatePaymentType);
        updateFields.add(new JLabel("Price"));
        updateFields.add(updatePrice);
        updateCard.add(updateFields, BorderLayout.CENTER);
        JButton update = AppTheme.primaryButton("Update Reservation");
        update.addActionListener(event -> updateReservation(updateReservationNumber, updateRoom, updateCheckIn, updateCheckOut, updatePaymentType));
        updateCard.add(update, BorderLayout.SOUTH);

        JPanel deleteCard = AppTheme.card();
        JPanel deleteFields = new JPanel(new GridLayout(2, 2, 10, 8));
        deleteFields.setOpaque(false);
        JTextField reservationNumber = AppTheme.field("Reservation number");
        deleteFields.add(new JLabel("Reservation Number"));
        deleteFields.add(reservationNumber);
        deleteFields.add(new JLabel(""));
        JButton delete = AppTheme.primaryButton("Delete Reservation");
        delete.addActionListener(event -> deleteReservation(reservationNumber));
        deleteFields.add(delete);
        deleteCard.add(deleteFields, BorderLayout.NORTH);

        tools.add(addCard);
        tools.add(updateCard);
        tools.add(deleteCard);
        return tools;
    }

    private JPanel simpleTable(String title, String sql) {
        JPanel page = page(title);
        JPanel card = AppTheme.card();
        card.add(tableFor(sql), BorderLayout.CENTER);
        page.add(card, BorderLayout.CENTER);
        return page;
    }

    private String guestsSql() {
        return "SELECT g.Guest_id, CONCAT(g.Fname, ' ', g.Lname) AS Guest_name, g.G_email, g.Phone_number, "
                + "COALESCE(GROUP_CONCAT(DISTINCT r.Reservation_num ORDER BY r.Reservation_num SEPARATOR ', '), 'No reservation') AS Reservations, "
                + "COALESCE(GROUP_CONCAT(DISTINCT r.Room_number ORDER BY r.Room_number SEPARATOR ', '), 'No room') AS Room_numbers, "
                + "COALESCE(GROUP_CONCAT(DISTINCT rm.Type ORDER BY rm.Type SEPARATOR ', '), 'No room type') AS Room_types, "
                + "COALESCE(GROUP_CONCAT(DISTINCT s.Name ORDER BY s.Name SEPARATOR ', '), 'No services') AS Requested_services, "
                + "COALESCE(GROUP_CONCAT(DISTINCT CONCAT('Invoice ', i.Invoice_id, ': ', i.Status, ' SAR ', i.Total_amount) ORDER BY i.Invoice_id SEPARATOR ' | '), 'No invoices') AS Invoices "
                + "FROM Guest g "
                + "LEFT JOIN Reservation r ON g.Guest_id = r.Guest_id "
                + "LEFT JOIN Room rm ON r.Room_number = rm.Room_number "
                + "LEFT JOIN Res_Service rs ON r.Reservation_num = rs.Reservation_num "
                + "LEFT JOIN Service s ON rs.Service_id = s.Service_id "
                + "LEFT JOIN Invoice i ON r.Reservation_num = i.Reservation_num "
                + "GROUP BY g.Guest_id, g.Fname, g.Lname, g.G_email, g.Phone_number "
                + "ORDER BY g.Guest_id";
    }

    private javax.swing.JScrollPane tableFor(String sql) {
        JTable table = new JTable();
        try (Connection connection = DB.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            table.setModel(TableUtil.modelFrom(resultSet));
        } catch (Exception exception) {
            DialogUtil.error(this, exception);
        }
        return AppTheme.tableScroll(table);
    }

    private void addReservationForGuest(JTextField guestId, JComboBox<String> room, JTextField reservationDate, JTextField checkIn, JTextField checkOut, JComboBox<String> paymentType) {
        Integer guest = positiveNumberFrom(guestId, "Guest ID");
        if (guest == null) {
            return;
        }

        String normalizedReservationDate = LocalDate.now().toString();
        reservationDate.setText(normalizedReservationDate);
        String normalizedCheckIn = normalizeDate(checkIn.getText().trim());
        String normalizedCheckOut = normalizeDate(checkOut.getText().trim());
        if (!hasValidReservationDates(normalizedReservationDate, normalizedCheckIn, normalizedCheckOut)) {
            return;
        }

        String sql = "INSERT INTO Reservation (Reservation_num, Reservation_date, Check_in, Check_out, Room_number, Guest_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DB.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (!guestExists(connection, guest)) {
                DialogUtil.info(this, "Please enter a valid Guest ID.");
                return;
            }

            int reservationNumber = nextReservationNumber(connection);
            int roomNumber = selectedRoomNumber(room);
            statement.setInt(1, reservationNumber);
            statement.setString(2, normalizedReservationDate);
            statement.setString(3, normalizedCheckIn);
            statement.setString(4, normalizedCheckOut);
            statement.setInt(5, roomNumber);
            statement.setInt(6, guest);
            statement.executeUpdate();
            createInvoice(connection, reservationNumber, roomNumber, normalizedCheckIn, normalizedCheckOut, String.valueOf(paymentType.getSelectedItem()));
            assignReservationToCurrentEmployee(connection, reservationNumber);
            DialogUtil.info(this, "Reservation created for guest " + guest + ".");
            loadReservations(null);
        } catch (Exception exception) {
            DialogUtil.error(this, exception);
        }
    }

    private void deleteReservation(JTextField reservationNumberField) {
        Integer reservationNumber = positiveNumberFrom(reservationNumberField, "Reservation number");
        if (reservationNumber == null) {
            return;
        }

        try (Connection connection = DB.getConnection()) {
            if (!reservationExists(connection, reservationNumber)) {
                DialogUtil.info(this, "Reservation number not found.");
                return;
            }

            deleteByReservation(connection, "DELETE FROM Res_Service WHERE Reservation_num = ?", reservationNumber);
            deleteByReservation(connection, "DELETE FROM Emp_Reservation WHERE Reservation_num = ?", reservationNumber);
            deleteByReservation(connection, "DELETE FROM Invoice WHERE Reservation_num = ?", reservationNumber);
            deleteByReservation(connection, "DELETE FROM Reservation WHERE Reservation_num = ?", reservationNumber);
            DialogUtil.info(this, "Reservation deleted.");
            loadReservations(null);
        } catch (Exception exception) {
            DialogUtil.error(this, exception);
        }
    }

    private void updateReservation(JTextField reservationNumberField, JComboBox<String> room, JTextField checkIn, JTextField checkOut, JComboBox<String> paymentType) {
        Integer reservationNumber = positiveNumberFrom(reservationNumberField, "Reservation number");
        if (reservationNumber == null) {
            return;
        }

        String normalizedCheckIn = normalizeDate(checkIn.getText().trim());
        String normalizedCheckOut = normalizeDate(checkOut.getText().trim());
        LocalDate today = LocalDate.now();
        if (!hasValidReservationDates(today.toString(), normalizedCheckIn, normalizedCheckOut)) {
            return;
        }

        String sql = "UPDATE Reservation SET Check_in = ?, Check_out = ?, Room_number = ? WHERE Reservation_num = ?";
        try (Connection connection = DB.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (!reservationExists(connection, reservationNumber)) {
                DialogUtil.info(this, "Reservation number not found.");
                return;
            }

            int roomNumber = selectedRoomNumber(room);
            statement.setString(1, normalizedCheckIn);
            statement.setString(2, normalizedCheckOut);
            statement.setInt(3, roomNumber);
            statement.setInt(4, reservationNumber);
            statement.executeUpdate();
            updateInvoiceForReservation(connection, reservationNumber, roomNumber, normalizedCheckIn, normalizedCheckOut, String.valueOf(paymentType.getSelectedItem()));
            DialogUtil.info(this, "Reservation updated.");
            loadReservations(null);
        } catch (Exception exception) {
            DialogUtil.error(this, exception);
        }
    }

    private void updateInvoiceForReservation(Connection connection, int reservationNumber, int roomNumber, String checkIn, String checkOut, String paymentType) throws Exception {
        String sql = "UPDATE Invoice SET Payment_type = ?, Total_amount = ? WHERE Reservation_num = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, paymentType);
            statement.setDouble(2, invoiceAmountForStay(connection, roomNumber, checkIn, checkOut));
            statement.setInt(3, reservationNumber);
            int updated = statement.executeUpdate();
            if (updated == 0) {
                createInvoice(connection, reservationNumber, roomNumber, checkIn, checkOut, paymentType);
            }
        }
    }

    private void deleteByReservation(Connection connection, String sql, int reservationNumber) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, reservationNumber);
            statement.executeUpdate();
        }
    }

    private boolean guestExists(Connection connection, int guestId) throws Exception {
        String sql = "SELECT COUNT(*) FROM Guest WHERE Guest_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, guestId);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1) > 0;
            }
        }
    }

    private boolean reservationExists(Connection connection, int reservationNumber) throws Exception {
        String sql = "SELECT COUNT(*) FROM Reservation WHERE Reservation_num = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, reservationNumber);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1) > 0;
            }
        }
    }

    private void assignReservationToCurrentEmployee(Connection connection, int reservationNumber) throws Exception {
        String sql = "INSERT INTO Emp_Reservation (Employee_id, Reservation_num) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, session.id());
            statement.setInt(2, reservationNumber);
            statement.executeUpdate();
        }
    }

    private void createInvoice(Connection connection, int reservationNumber, int roomNumber, String checkIn, String checkOut, String paymentType) throws Exception {
        String sql = "INSERT INTO Invoice (Invoice_id, Status, Payment_type, Total_amount, Reservation_num) VALUES (?, 'Pending', ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, nextInvoiceId(connection));
            statement.setString(2, paymentType);
            statement.setDouble(3, invoiceAmountForStay(connection, roomNumber, checkIn, checkOut));
            statement.setInt(4, reservationNumber);
            statement.executeUpdate();
        }
    }

    private int nextReservationNumber(Connection connection) throws Exception {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT COALESCE(MAX(Reservation_num), 1000) + 1 FROM Reservation")) {
            resultSet.next();
            return resultSet.getInt(1);
        }
    }

    private int nextInvoiceId(Connection connection) throws Exception {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT COALESCE(MAX(Invoice_id), 0) + 1 FROM Invoice")) {
            resultSet.next();
            return resultSet.getInt(1);
        }
    }

    private double invoiceAmountForStay(Connection connection, int roomNumber, String checkIn, String checkOut) throws Exception {
        LocalDate in = LocalDate.parse(checkIn);
        LocalDate out = LocalDate.parse(checkOut);
        long nights = Math.max(1, ChronoUnit.DAYS.between(in, out));
        return nightlyRateForRoom(connection, roomNumber) * nights;
    }

    private double nightlyRateForRoom(Connection connection, int roomNumber) throws Exception {
        String sql = "SELECT Type FROM Room WHERE Room_number = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, roomNumber);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return priceForRoomType(resultSet.getString("Type"));
                }
            }
        }
        return 900.00;
    }

    private double priceForRoomType(String type) {
        if ("Suite".equalsIgnoreCase(type)) {
            return 2500.00;
        }
        if ("Double".equalsIgnoreCase(type)) {
            return 1500.00;
        }
        return 900.00;
    }

    private void loadAvailableRooms(JComboBox<String> room) {
        String sql = "SELECT Room_number, Type FROM Room WHERE Status = 'Available' ORDER BY Room_number";
        try (Connection connection = DB.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                int roomNumber = resultSet.getInt("Room_number");
                String type = resultSet.getString("Type");
                room.addItem(roomNumber + " - " + type + " - SAR " + String.format("%.2f", priceForRoomType(type)));
            }
        } catch (Exception exception) {
            DialogUtil.error(this, exception);
        }
    }

    private int selectedRoomNumber(JComboBox<String> room) {
        Object selected = room.getSelectedItem();
        if (selected == null) {
            throw new IllegalArgumentException("Please select an available room.");
        }
        return Integer.parseInt(selected.toString().split(" - ")[0]);
    }

    private void updatePricePreview(JComboBox<String> room, JTextField price) {
        Object selected = room.getSelectedItem();
        if (selected == null) {
            price.setText("");
            return;
        }
        String[] parts = selected.toString().split(" - ");
        price.setText(parts.length >= 3 ? parts[2] + " per night" : "");
    }

    private boolean hasValidReservationDates(String reservationDate, String checkIn, String checkOut) {
        LocalDate reservedOn = parseDateField("Reservation date", reservationDate);
        if (reservedOn == null) {
            return false;
        }

        LocalDate in = parseDateField("Check-in date", checkIn);
        if (in == null) {
            return false;
        }

        LocalDate out = parseDateField("Check-out date", checkOut);
        if (out == null) {
            return false;
        }

        if (reservedOn.isAfter(in)) {
            DialogUtil.info(this, "Reservation date cannot be after check-in date.");
            return false;
        }
        if (!in.isBefore(out)) {
            DialogUtil.info(this, "Check-in date must be before check-out date.");
            return false;
        }
        return true;
    }

    private LocalDate parseDateField(String label, String value) {
        try {
            return LocalDate.parse(value);
        } catch (Exception exception) {
            DialogUtil.info(this, label + " must be a real date in YYYY-MM-DD format.");
            return null;
        }
    }

    private String normalizeDate(String value) {
        if (value.matches("\\d{4}-\\d{1,2}-\\d{1,2}")) {
            String[] parts = value.split("-");
            return String.format("%04d-%02d-%02d", Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
        }
        if (value.matches("\\d{1,2}-\\d{1,2}-\\d{4}")) {
            String[] parts = value.split("-");
            return String.format("%04d-%02d-%02d", Integer.parseInt(parts[2]), Integer.parseInt(parts[1]), Integer.parseInt(parts[0]));
        }
        return value;
    }

    private Integer positiveNumberFrom(JTextField field, String label) {
        try {
            int value = Integer.parseInt(field.getText().trim());
            if (value <= 0) {
                DialogUtil.info(this, label + " must be greater than zero.");
                return null;
            }
            return value;
        } catch (NumberFormatException exception) {
            DialogUtil.info(this, label + " must be a valid number.");
            return null;
        }
    }

    private void loadReservations(String guestIdSearch) {
        String sql = "SELECT r.Reservation_num, r.Reservation_date, r.Check_in, r.Check_out, r.Room_number, rm.Type AS Room_type, r.Guest_id, "
                + "CONCAT(g.Fname, ' ', g.Lname) AS Guest_name, "
                + "COALESCE(GROUP_CONCAT(DISTINCT s.Name ORDER BY s.Name SEPARATOR ', '), 'No services') AS Requested_services, "
                + "COALESCE(CAST(i.Invoice_id AS CHAR), 'No invoice') AS Invoice_id, COALESCE(i.Status, 'No invoice') AS Invoice_status, COALESCE(i.Total_amount, 0) AS Invoice_total "
                + "FROM Reservation r JOIN Guest g ON r.Guest_id = g.Guest_id "
                + "LEFT JOIN Room rm ON r.Room_number = rm.Room_number "
                + "LEFT JOIN Res_Service rs ON r.Reservation_num = rs.Reservation_num "
                + "LEFT JOIN Service s ON rs.Service_id = s.Service_id "
                + "LEFT JOIN Invoice i ON r.Reservation_num = i.Reservation_num "
                + "WHERE (? IS NULL OR r.Guest_id = ?) "
                + "GROUP BY r.Reservation_num, r.Reservation_date, r.Check_in, r.Check_out, r.Room_number, rm.Type, r.Guest_id, g.Fname, g.Lname, i.Invoice_id, i.Status, i.Total_amount "
                + "ORDER BY r.Reservation_date DESC";
        try (Connection connection = DB.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (guestIdSearch == null || guestIdSearch.isBlank()) {
                statement.setNull(1, java.sql.Types.INTEGER);
                statement.setNull(2, java.sql.Types.INTEGER);
            } else {
                int guestId = Integer.parseInt(guestIdSearch);
                statement.setInt(1, guestId);
                statement.setInt(2, guestId);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                reservationsTable.setModel(TableUtil.modelFrom(resultSet));
            }
        } catch (NumberFormatException exception) {
            DialogUtil.info(this, "Guest ID must be a number.");
        } catch (Exception exception) {
            DialogUtil.error(this, exception);
        }
    }

    private void loadEmployeeProfile(JTextField id, JTextField first, JTextField last, JTextField address, JTextField phone, JTextField email, JTextField role, JTextField salary) {
        String sql = "SELECT Employee_id, Fname, Lname, Address, Phone_number, E_email, Role, Salary FROM Employee WHERE Employee_id = ?";
        try (Connection connection = DB.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, session.id());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    id.setText(String.valueOf(resultSet.getInt("Employee_id")));
                    first.setText(resultSet.getString("Fname"));
                    last.setText(resultSet.getString("Lname"));
                    address.setText(resultSet.getString("Address"));
                    phone.setText(resultSet.getString("Phone_number"));
                    email.setText(resultSet.getString("E_email"));
                    role.setText(resultSet.getString("Role"));
                    salary.setText(resultSet.getString("Salary"));
                }
            }
        } catch (Exception exception) {
            DialogUtil.error(this, exception);
        }
    }

    private void updateEmployeeProfile(JTextField first, JTextField last, JTextField address, JTextField phone, JTextField email) {
        String sql = "UPDATE Employee SET Fname = ?, Lname = ?, Address = ?, Phone_number = ?, E_email = ? WHERE Employee_id = ?";
        try (Connection connection = DB.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, first.getText().trim());
            statement.setString(2, last.getText().trim());
            statement.setString(3, address.getText().trim());
            statement.setString(4, phone.getText().trim());
            statement.setString(5, email.getText().trim());
            statement.setInt(6, session.id());
            statement.executeUpdate();
            DialogUtil.info(this, "Profile updated.");
        } catch (Exception exception) {
            DialogUtil.error(this, exception);
        }
    }

    private String scalar(String sql) {
        try (Connection connection = DB.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            return resultSet.next() ? resultSet.getString(1) : "0";
        } catch (Exception exception) {
            return "0";
        }
    }
}
