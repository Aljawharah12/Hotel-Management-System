# Hotel Management System

Java Swing desktop application for managing hotel reservations, guests, employees, invoices, rooms, and services for Intercontinental Dar Altawhid Makkah.

## Project Overview

This system provides two main dashboards:

- Guest dashboard for creating reservations, requesting services, viewing invoices, and paying eligible invoices.
- Employee dashboard for managing reservations, guests, rooms, services, invoices, employees, and employee assignments.

The application connects to a MySQL database using JDBC and uses Maven for dependency management.

## Technologies

- Java
- Java Swing
- MySQL
- JDBC
- Maven

## Project Structure

```text
src/main/java
|-- Main.java
|-- com/myhotel/app
|   |-- MyHotelApp.java
|   `-- Session.java
|-- com/myhotel/db
|   `-- DB.java
`-- com/myhotel/ui
    |-- AppTheme.java
    |-- DashboardFrame.java
    |-- DialogUtil.java
    |-- EmployeeFrame.java
    |-- GuestFrame.java
    |-- LoginFrame.java
    `-- TableUtil.java
```

## Database Setup

The application expects a MySQL database named:

```text
Intercontinental_Dar_Altawhid_Makkah
```

Update the database connection in:

```text
src/main/java/com/myhotel/db/DB.java
```

```java
private static final String URL = "jdbc:mysql://localhost:3306/Intercontinental_Dar_Altawhid_Makkah?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
private static final String USER = "root";
private static final String PASSWORD = "";
```

## SQL Files

- `sample_data.sql`: sample reservations, invoices, services, and employee assignments.
- `employee_service_inserts.sql`: sample employees and hotel services.
- `add_password_columns.sql`: adds password columns for guest and employee login.
- `src/main/resources/sql/my_hotel_views.sql`: optional SQL views for guest and employee data.

Run the SQL files in MySQL Workbench before starting the application.

## How To Run

From the project folder:

```bash
mvn compile exec:java
```

You can also run the main file from an IDE such as VS Code:

```text
src/main/java/Main.java
```

## Login

The login screen supports two roles:

- Guest: login using `Guest_id` and guest password.
- Employee: login using `Employee_id` and employee password.

New guests can create an account from the login screen.

## Guest Features

- View dashboard summary.
- Update guest profile.
- Create a new reservation.
- View available rooms.
- View available services.
- Request a service for an existing reservation.
- View invoices.
- Pay invoices only when the status is `Pending` or `Unpaid`.
- View requested services.

Guest validation includes:

- Reservation date is locked to the current date.
- Check-in date must be before check-out date.
- Reservation date cannot be after check-in date.
- Invalid service IDs show a clear message.
- Service ID, reservation number, and invoice ID must be positive numbers.
- Paid invoices cannot be paid again.

## Employee Features

- View hotel dashboard summary.
- Update employee profile.
- View all reservations.
- Add a reservation for a guest.
- Update an existing reservation.
- Delete an old reservation.
- View guest details.
- View rooms.
- View services.
- View invoices.
- View reservation services.
- View employees.
- View employee assignments.

Employee reservation management includes:

- Creating a reservation with an invoice.
- Assigning new reservations to the current employee.
- Updating reservation room, check-in date, check-out date, payment type, and invoice amount.
- Deleting linked services, employee assignment, invoice, and reservation records safely.

## Notes

- Build output in `target/` is ignored by Git.
- The local JDK folder is ignored by Git and should not be uploaded to GitHub.
- Make sure MySQL is running before starting the application.
