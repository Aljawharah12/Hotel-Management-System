USE Intercontinental_Dar_Altawhid_Makkah;

ALTER TABLE Guest
ADD COLUMN Password VARCHAR(100);

ALTER TABLE Employee
ADD COLUMN Emp_password VARCHAR(100);

UPDATE Guest
SET Password = 'guest123'
WHERE Guest_id IN (1, 2, 3);

UPDATE Employee
SET Emp_password = 'emp123'
WHERE Employee_id IN (1, 2, 3);
