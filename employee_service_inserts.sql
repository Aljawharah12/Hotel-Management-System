USE Intercontinental_Dar_Altawhid_Makkah;

INSERT INTO Service (Service_id, Name, Price)
VALUES
(1, 'Spa', 150.00),
(2, 'Breakfast Buffet', 120.00),
(3, 'Airport Transfer', 250.00),
(4, 'Laundry Service', 90.00);

INSERT INTO Employee (Employee_id, Fname, Lname, Address, Phone_number, E_email, Role, Salary, Branch_id, Emp_password)
VALUES
(1, 'Ahmed', 'Alharbi', 'Makkah - Ajyad', '0501112233', 'ahmed.alharbi@hotel.com', 'Receptionist', 6500.00, 1, 'emp123'),
(2, 'Sara', 'Alotaibi', 'Makkah - Al Aziziyah', '0502223344', 'sara.alotaibi@hotel.com', 'Manager', 8500.00, 1, 'emp123'),
(3, 'Khalid', 'Alzahrani', 'Makkah - Al Shubaikah', '0503334455', 'khalid.alzahrani@hotel.com', 'Receptionist', 7000.00, 1, 'emp123');
