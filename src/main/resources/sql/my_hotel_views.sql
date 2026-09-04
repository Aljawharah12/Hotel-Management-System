CREATE OR REPLACE VIEW Guest_View AS
SELECT
    g.Guest_id,
    g.Fname,
    g.Lname,
    g.G_email,
    g.Phone_number,
    r.Reservation_num,
    r.Reservation_date,
    r.Check_in,
    r.Check_out,
    r.Room_number,
    rm.Status AS Room_status,
    rm.Type AS Room_type,
    i.Invoice_id,
    i.Status AS Invoice_status,
    i.Payment_type,
    i.Total_amount,
    s.Service_id,
    s.Name AS Service_name,
    s.Price AS Service_price
FROM Guest g
LEFT JOIN Reservation r ON g.Guest_id = r.Guest_id
LEFT JOIN Room rm ON r.Room_number = rm.Room_number
LEFT JOIN Invoice i ON r.Reservation_num = i.Reservation_num
LEFT JOIN Res_Service rs ON r.Reservation_num = rs.Reservation_num
LEFT JOIN Service s ON rs.Service_id = s.Service_id;

CREATE OR REPLACE VIEW Employee_View AS
SELECT
    e.Employee_id,
    e.Fname,
    e.Lname,
    e.E_email,
    e.Role,
    e.Branch_id,
    er.Reservation_num,
    r.Reservation_date,
    r.Check_in,
    r.Check_out,
    r.Room_number,
    r.Guest_id,
    i.Invoice_id,
    i.Status AS Invoice_status,
    i.Payment_type,
    i.Total_amount
FROM Employee e
LEFT JOIN Emp_Reservation er ON e.Employee_id = er.Employee_id
LEFT JOIN Reservation r ON er.Reservation_num = r.Reservation_num
LEFT JOIN Invoice i ON r.Reservation_num = i.Reservation_num;
