package com.myhotel.ui;

import com.myhotel.app.Session;
import com.myhotel.db.DB;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JComboBox;
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

public class GuestFrame extends DashboardFrame {
    public GuestFrame(Session session) {
        super(session, "My Hotel - Guest View");
        addScreen("dashboard", "Dashboard", dashboard());
        addScreen("profile", "My Profile", profile());
        addScreen("new_reservation", "New Reservation", newReservation());
        addScreen("rooms", "Available Rooms", simpleTable("Available Rooms", "SELECT Room_number, Type, CASE Type WHEN 'Single' THEN 900.00 WHEN 'Double' THEN 1500.00 WHEN 'Suite' THEN 2500.00 ELSE 900.00 END AS Price FROM Room WHERE Status = 'Available' ORDER BY Room_number"));
        addScreen("services", "Services", simpleTable("Services", "SELECT Service_id, Name, Price FROM Service ORDER BY Service_id"));
        addScreen("request_service", "Request Service", requestService());
        addScreen("invoices", "My Invoices", guestTable("My Invoices", invoicesSql()));
        addScreen("pay_invoice", "Pay Invoice", payInvoice());
        addScreen("reservation_services", "My Services", guestTable("My Reservation Services", servicesSql()));
        addLogout();
        showScreen("dashboard");
    }

    private JPanel dashboard() {
        JPanel page = page("Stay Overview");
        JPanel body = new JPanel(new BorderLayout(0, 18));
        body.setOpaque(false);

        JPanel hero = AppTheme.darkPanel();
        hero.setBorder(javax.swing.BorderFactory.createEmptyBorder(22, 24, 22, 24));
        JPanel heroStack = new JPanel(new GridLayout(2, 1, 0, 4));
        heroStack.setOpaque(false);
        JLabel welcome = new JLabel("Welcome, " + session.name());
        welcome.setForeground(Color.WHITE);
        welcome.setFont(new Font("SansSerif", Font.BOLD, 24));
        JLabel note = new JLabel("Your reservations, invoices, and requested hotel services are shown below.");
        note.setForeground(new Color(224, 211, 180));
        heroStack.add(welcome);
        heroStack.add(note);
        hero.add(heroStack, BorderLayout.CENTER);
        body.add(hero, BorderLayout.NORTH);

        JPanel metrics = new JPanel(new GridLayout(1, 5, 14, 0));
        metrics.setOpaque(false);
        metrics.add(summaryTile("Reservations", scalar("SELECT COUNT(*) FROM Reservation WHERE Guest_id = ?", "0")));
        metrics.add(summaryTile("Invoices", scalar("SELECT COUNT(*) FROM Invoice i JOIN Reservation r ON i.Reservation_num = r.Reservation_num WHERE r.Guest_id = ?", "0")));
        metrics.add(summaryTile("Total", "SAR " + scalar("SELECT COALESCE(SUM(i.Total_amount), 0) FROM Invoice i JOIN Reservation r ON i.Reservation_num = r.Reservation_num WHERE r.Guest_id = ?", "0")));
        metrics.add(summaryTile("Services", scalar("SELECT COUNT(*) FROM Res_Service rs JOIN Reservation r ON rs.Reservation_num = r.Reservation_num WHERE r.Guest_id = ?", "0")));
        metrics.add(summaryTile("Access", "Private"));

        JPanel timeline = AppTheme.card();
        timeline.add(sectionTitle("My Reservation Details"), BorderLayout.NORTH);
        timeline.add(tableFor(reservationsSql(), true), BorderLayout.CENTER);

        JPanel content = new JPanel(new BorderLayout(0, 18));
        content.setOpaque(false);
        content.add(metrics, BorderLayout.NORTH);
        content.add(timeline, BorderLayout.CENTER);
        body.add(content, BorderLayout.CENTER);

        page.add(body, BorderLayout.CENTER);
        return page;
    }

    private JLabel sectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(AppTheme.TEXT);
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 12, 0));
        return label;
    }

    private JPanel summaryTile(String label, String value) {
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
        JPanel page = page("My Profile");
        JPanel card = AppTheme.card();
        JPanel fields = new JPanel(new GridLayout(5, 2, 12, 12));
        fields.setOpaque(false);

        JTextField id = AppTheme.field("Guest ID");
        JTextField first = AppTheme.field("First name");
        JTextField last = AppTheme.field("Last name");
        JTextField email = AppTheme.field("Email");
        JTextField phone = AppTheme.field("Phone");
        id.setEditable(false);
        loadProfile(id, first, last, email, phone);

        fields.add(new JLabel("Guest ID"));
        fields.add(id);
        fields.add(new JLabel("First Name"));
        fields.add(first);
        fields.add(new JLabel("Last Name"));
        fields.add(last);
        fields.add(new JLabel("Email"));
        fields.add(email);
        fields.add(new JLabel("Phone"));
        fields.add(phone);
        card.add(fields, BorderLayout.CENTER);

        javax.swing.JButton save = AppTheme.primaryButton("Update Profile");
        save.addActionListener(event -> updateProfile(first, last, email, phone));
        card.add(save, BorderLayout.SOUTH);
        page.add(card, BorderLayout.CENTER);
        return page;
    }

    private JPanel newReservation() {
        JPanel page = page("Make Reservation");
        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setOpaque(false);

        JPanel formCard = AppTheme.card();
        JPanel fields = new JPanel(new GridLayout(6, 2, 12, 12));
        fields.setOpaque(false);
        JComboBox<String> room = new JComboBox<>();
        loadAvailableRooms(room);
        JTextField reservationDate = AppTheme.field("YYYY-MM-DD");
        JTextField checkIn = AppTheme.field("YYYY-MM-DD or DD-MM-YYYY");
        JTextField checkOut = AppTheme.field("YYYY-MM-DD or DD-MM-YYYY");
        JComboBox<String> paymentType = new JComboBox<>(new String[] {"Credit Card", "Debit Card", "Cash"});
        JTextField price = AppTheme.field("Room price");
        price.setEditable(false);
        reservationDate.setText(LocalDate.now().toString());
        reservationDate.setEditable(false);
        updatePricePreview(room, price);
        room.addActionListener(event -> updatePricePreview(room, price));

        fields.add(new JLabel("Available Room"));
        fields.add(room);
        fields.add(new JLabel("Reservation Date"));
        fields.add(reservationDate);
        fields.add(new JLabel("Check In (YYYY-MM-DD)"));
        fields.add(checkIn);
        fields.add(new JLabel("Check Out (YYYY-MM-DD)"));
        fields.add(checkOut);
        fields.add(new JLabel("Payment Type"));
        fields.add(paymentType);
        fields.add(new JLabel("Price"));
        fields.add(price);
        formCard.add(fields, BorderLayout.CENTER);

        JButton save = AppTheme.primaryButton("Create Reservation + Invoice");
        save.addActionListener(event -> createReservation(room, reservationDate, checkIn, checkOut, paymentType));
        formCard.add(save, BorderLayout.SOUTH);

        JPanel roomsCard = AppTheme.card();
        roomsCard.add(new JLabel("Available Rooms and Prices"), BorderLayout.NORTH);
        roomsCard.add(tableFor("SELECT Room_number, Type, CASE Type WHEN 'Single' THEN 900.00 WHEN 'Double' THEN 1500.00 WHEN 'Suite' THEN 2500.00 ELSE 900.00 END AS Price FROM Room WHERE Status = 'Available' ORDER BY Room_number", false), BorderLayout.CENTER);

        root.add(formCard, BorderLayout.NORTH);
        root.add(roomsCard, BorderLayout.CENTER);
        page.add(root, BorderLayout.CENTER);
        return page;
    }

    private JPanel payInvoice() {
        JPanel page = page("Pay Invoice");
        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setOpaque(false);

        JPanel formCard = AppTheme.card();
        JPanel fields = new JPanel(new GridLayout(2, 2, 12, 12));
        fields.setOpaque(false);
        JTextField invoiceId = AppTheme.field("Invoice ID");
        JComboBox<String> paymentType = new JComboBox<>(new String[] {"Credit Card", "Debit Card", "Cash"});
        fields.add(new JLabel("Invoice ID"));
        fields.add(invoiceId);
        fields.add(new JLabel("Payment Type"));
        fields.add(paymentType);
        formCard.add(fields, BorderLayout.CENTER);

        JButton pay = AppTheme.primaryButton("Pay Invoice");
        pay.addActionListener(event -> payInvoice(invoiceId, paymentType));
        formCard.add(pay, BorderLayout.SOUTH);

        JPanel invoicesCard = AppTheme.card();
        invoicesCard.add(new JLabel("My Pending Invoices"), BorderLayout.NORTH);
        invoicesCard.add(tableFor("SELECT i.Invoice_id, i.Status, i.Payment_type, i.Total_amount, i.Reservation_num FROM Invoice i JOIN Reservation r ON i.Reservation_num = r.Reservation_num WHERE r.Guest_id = ? AND i.Status IN ('Pending', 'Unpaid') ORDER BY i.Invoice_id DESC", true), BorderLayout.CENTER);

        root.add(formCard, BorderLayout.NORTH);
        root.add(invoicesCard, BorderLayout.CENTER);
        page.add(root, BorderLayout.CENTER);
        return page;
    }

    private JPanel requestService() {
        JPanel page = page("Request Service");
        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setOpaque(false);

        JPanel formCard = AppTheme.card();
        JPanel fields = new JPanel(new GridLayout(2, 2, 12, 12));
        fields.setOpaque(false);
        JTextField reservation = AppTheme.field("Reservation number");
        JTextField service = AppTheme.field("Service ID");
        fields.add(new JLabel("Reservation Number"));
        fields.add(reservation);
        fields.add(new JLabel("Service ID"));
        fields.add(service);
        formCard.add(fields, BorderLayout.CENTER);

        JButton save = AppTheme.primaryButton("Request Service");
        save.addActionListener(event -> addServiceToReservation(reservation, service));
        formCard.add(save, BorderLayout.SOUTH);

        JPanel listCard = AppTheme.card();
        listCard.add(new JLabel("Available Services"), BorderLayout.NORTH);
        listCard.add(tableFor("SELECT Service_id, Name, Price FROM Service ORDER BY Service_id", false), BorderLayout.CENTER);

        root.add(formCard, BorderLayout.NORTH);
        root.add(listCard, BorderLayout.CENTER);
        page.add(root, BorderLayout.CENTER);
        return page;
    }

    private JPanel guestTable(String title, String sql) {
        JPanel page = page(title);
        JPanel card = AppTheme.card();
        card.add(tableFor(sql, true), BorderLayout.CENTER);
        page.add(card, BorderLayout.CENTER);
        return page;
    }

    private JPanel simpleTable(String title, String sql) {
        JPanel page = page(title);
        JPanel card = AppTheme.card();
        card.add(tableFor(sql, false), BorderLayout.CENTER);
        page.add(card, BorderLayout.CENTER);
        return page;
    }

    private javax.swing.JScrollPane tableFor(String sql, boolean useGuestId) {
        JTable table = new JTable();
        try (Connection connection = DB.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (useGuestId) {
                statement.setInt(1, session.id());
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                table.setModel(TableUtil.modelFrom(resultSet));
            }
        } catch (Exception exception) {
            DialogUtil.error(this, exception);
        }
        return AppTheme.tableScroll(table);
    }

    private void loadProfile(JTextField id, JTextField first, JTextField last, JTextField email, JTextField phone) {
        String sql = "SELECT Guest_id, Fname, Lname, G_email, Phone_number FROM Guest WHERE Guest_id = ?";
        try (Connection connection = DB.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, session.id());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    id.setText(String.valueOf(resultSet.getInt("Guest_id")));
                    first.setText(resultSet.getString("Fname"));
                    last.setText(resultSet.getString("Lname"));
                    email.setText(resultSet.getString("G_email"));
                    phone.setText(resultSet.getString("Phone_number"));
                }
            }
        } catch (Exception exception) {
            DialogUtil.error(this, exception);
        }
    }

    private void updateProfile(JTextField first, JTextField last, JTextField email, JTextField phone) {
        String sql = "UPDATE Guest SET Fname = ?, Lname = ?, G_email = ?, Phone_number = ? WHERE Guest_id = ?";
        try (Connection connection = DB.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, first.getText().trim());
            statement.setString(2, last.getText().trim());
            statement.setString(3, email.getText().trim());
            statement.setString(4, phone.getText().trim());
            statement.setInt(5, session.id());
            statement.executeUpdate();
            DialogUtil.info(this, "Profile updated.");
        } catch (Exception exception) {
            DialogUtil.error(this, exception);
        }
    }

    private void createReservation(JComboBox<String> room, JTextField reservationDate, JTextField checkIn, JTextField checkOut, JComboBox<String> paymentType) {
        String sql = "INSERT INTO Reservation (Reservation_num, Reservation_date, Check_in, Check_out, Room_number, Guest_id) VALUES (?, ?, ?, ?, ?, ?)";
        String normalizedReservationDate = LocalDate.now().toString();
        reservationDate.setText(normalizedReservationDate);
        String normalizedCheckIn = normalizeDate(checkIn.getText().trim());
        String normalizedCheckOut = normalizeDate(checkOut.getText().trim());
        if (!hasValidReservationDates(normalizedReservationDate, normalizedCheckIn, normalizedCheckOut)) {
            return;
        }

        try (Connection connection = DB.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            int reservationNumber = nextReservationNumber(connection);
            int roomNumber = selectedRoomNumber(room);
            statement.setInt(1, reservationNumber);
            statement.setString(2, normalizedReservationDate);
            statement.setString(3, normalizedCheckIn);
            statement.setString(4, normalizedCheckOut);
            statement.setInt(5, roomNumber);
            statement.setInt(6, session.id());
            statement.executeUpdate();
            createInvoice(connection, reservationNumber, roomNumber, normalizedCheckIn, normalizedCheckOut, String.valueOf(paymentType.getSelectedItem()));
            assignReservationToEmployee(connection, reservationNumber);
            DialogUtil.info(this, "Reservation and invoice created.");
            dispose();
            new GuestFrame(session).setVisible(true);
        } catch (Exception exception) {
            DialogUtil.error(this, exception);
        }
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

    private void assignReservationToEmployee(Connection connection, int reservationNumber) throws Exception {
        int employeeId = nextEmployeeForAssignment(connection);
        String sql = "INSERT INTO Emp_Reservation (Employee_id, Reservation_num) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, employeeId);
            statement.setInt(2, reservationNumber);
            statement.executeUpdate();
        }
    }

    private int nextEmployeeForAssignment(Connection connection) throws Exception {
        String sql = "SELECT e.Employee_id "
                + "FROM Employee e "
                + "LEFT JOIN Emp_Reservation er ON e.Employee_id = er.Employee_id "
                + "GROUP BY e.Employee_id "
                + "ORDER BY COUNT(er.Reservation_num), e.Employee_id "
                + "LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt("Employee_id");
            }
        }
        throw new IllegalStateException("No employee is available to assign this reservation.");
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

    private int nextReservationNumber(Connection connection) throws Exception {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT COALESCE(MAX(Reservation_num), 1000) + 1 FROM Reservation")) {
            resultSet.next();
            return resultSet.getInt(1);
        }
    }

    private void addServiceToReservation(JTextField reservation, JTextField service) {
        String sql = "INSERT INTO Res_Service (Reservation_num, Service_id) VALUES (?, ?)";
        Integer reservationNumber = positiveNumberFrom(reservation, "Reservation number");
        if (reservationNumber == null) {
            return;
        }

        Integer serviceId = positiveNumberFrom(service, "Service ID");
        if (serviceId == null) {
            return;
        }

        try (Connection connection = DB.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (!serviceExists(connection, serviceId)) {
                DialogUtil.info(this, "Please enter a valid Service ID from the available services list.");
                return;
            }

            statement.setInt(1, reservationNumber);
            statement.setInt(2, serviceId);
            statement.executeUpdate();
            DialogUtil.info(this, "Service requested.");
            dispose();
            new GuestFrame(session).setVisible(true);
        } catch (Exception exception) {
            DialogUtil.error(this, exception);
        }
    }

    private boolean serviceExists(Connection connection, int serviceId) throws Exception {
        String sql = "SELECT COUNT(*) FROM Service WHERE Service_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, serviceId);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1) > 0;
            }
        }
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

    private void payInvoice(JTextField invoiceId, JComboBox<String> paymentType) {
        Integer invoice = positiveNumberFrom(invoiceId, "Invoice ID");
        if (invoice == null) {
            return;
        }

        String sql = "UPDATE Invoice i JOIN Reservation r ON i.Reservation_num = r.Reservation_num SET i.Status = 'Paid', i.Payment_type = ? WHERE i.Invoice_id = ? AND r.Guest_id = ? AND i.Status IN ('Pending', 'Unpaid')";
        try (Connection connection = DB.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, String.valueOf(paymentType.getSelectedItem()));
            statement.setInt(2, invoice);
            statement.setInt(3, session.id());
            int updated = statement.executeUpdate();
            if (updated == 0) {
                DialogUtil.info(this, "Please enter a valid pending or unpaid invoice ID from your invoices list.");
                return;
            }
            DialogUtil.info(this, "Invoice paid.");
            dispose();
            new GuestFrame(session).setVisible(true);
        } catch (Exception exception) {
            DialogUtil.error(this, exception);
        }
    }

    private String scalar(String sql, String fallback) {
        try (Connection connection = DB.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, session.id());
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getString(1) : fallback;
            }
        } catch (Exception exception) {
            return fallback;
        }
    }

    private String reservationsSql() {
        return "SELECT r.Reservation_num, r.Reservation_date, r.Check_in, r.Check_out, r.Room_number, rm.Type AS Room_type, "
                + "COALESCE(GROUP_CONCAT(DISTINCT s.Name ORDER BY s.Name SEPARATOR ', '), 'No services') AS Requested_services, "
                + "COALESCE(CAST(i.Invoice_id AS CHAR), 'No invoice') AS Invoice_id, COALESCE(i.Status, 'No invoice') AS Invoice_status, COALESCE(i.Total_amount, 0) AS Invoice_total "
                + "FROM Reservation r "
                + "LEFT JOIN Room rm ON r.Room_number = rm.Room_number "
                + "LEFT JOIN Res_Service rs ON r.Reservation_num = rs.Reservation_num "
                + "LEFT JOIN Service s ON rs.Service_id = s.Service_id "
                + "LEFT JOIN Invoice i ON r.Reservation_num = i.Reservation_num "
                + "WHERE r.Guest_id = ? "
                + "GROUP BY r.Reservation_num, r.Reservation_date, r.Check_in, r.Check_out, r.Room_number, rm.Type, i.Invoice_id, i.Status, i.Total_amount "
                + "ORDER BY r.Reservation_date DESC";
    }

    private String invoicesSql() {
        return "SELECT i.Invoice_id, i.Status, i.Payment_type, i.Total_amount, i.Reservation_num "
                + "FROM Invoice i JOIN Reservation r ON i.Reservation_num = r.Reservation_num "
                + "WHERE r.Guest_id = ? ORDER BY i.Invoice_id DESC";
    }

    private String servicesSql() {
        return "SELECT rs.Reservation_num, s.Service_id, s.Name, s.Price "
                + "FROM Res_Service rs JOIN Service s ON rs.Service_id = s.Service_id "
                + "JOIN Reservation r ON rs.Reservation_num = r.Reservation_num "
                + "WHERE r.Guest_id = ? ORDER BY rs.Reservation_num DESC";
    }
}
