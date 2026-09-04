INSERT INTO Reservation (Reservation_num, Reservation_date, Check_in, Check_out, Room_number, Guest_id)
VALUES
(1001, '2026-05-13', '2026-06-01', '2026-06-05', 101, 1),
(1002, '2026-05-14', '2026-07-10', '2026-07-15', 201, 2),
(1003, '2026-05-15', '2026-08-01', '2026-08-03', 102, 3);

INSERT INTO Invoice (Invoice_id, Status, Payment_type, Total_amount, Reservation_num)
VALUES
(1, 'Paid', 'Credit Card', 1500.00, 1001),
(2, 'Pending', 'Cash', 2500.00, 1002),
(3, 'Unpaid', 'Debit Card', 900.00, 1003);

INSERT INTO Res_Service (Reservation_num, Service_id)
VALUES
(1001, 1),
(1001, 3),
(1002, 2),
(1002, 4),
(1003, 1);

INSERT INTO Emp_Reservation (Employee_id, Reservation_num)
VALUES
(1, 1001),
(2, 1002),
(3, 1003);
