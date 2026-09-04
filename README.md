# MyHotelJavaViews

مشروع Java Swing مرتب بنفس فكرة مشروع صاحبتك، لكن مبني على جداولك وواجهاتك:

- LoginFrame
- GuestFrame
- EmployeeFrame
- DashboardFrame
- DB connection file with `URL`, `USER`, `PASSWORD`

## أهم ملف للاتصال

افتحي:

```text
src/main/java/com/myhotel/db/DB.java
```

وعدلي:

```java
private static final String URL = "jdbc:mysql://localhost:3306/Intercontinental_Dar_Altawhid_Makkah?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
private static final String USER = "root";
private static final String PASSWORD = "";
```

اسم قاعدة البيانات مضبوط على `Intercontinental_Dar_Altawhid_Makkah` والباسورد فاضي حاليًا.

## التشغيل

من داخل فولدر المشروع:

```bash
mvn compile exec:java
```

أو من VS Code شغلي الملف:

```text
src/main/java/Main.java
```

ملف التشغيل الأساسي موجود هنا:

```text
src/main/java/com/myhotel/app/MyHotelApp.java
```

## تسجيل الدخول

لا يوجد password في جداولك، لذلك الدخول يكون بالـ ID فقط:

- Guest: اكتبي `Guest_id`
- Employee: اكتبي `Employee_id`

## الشاشات

### Guest View

- Dashboard
- My Profile
- Rooms
- Services
- My Invoices
- My Reservation Services

### Employee View

- Dashboard
- Reservations
- Guests
- Rooms
- Services
- Invoices
- Reservation Services
- Employees
- Assignments

## Views SQL

ملف SQL اختياري موجود هنا:

```text
src/main/resources/sql/my_hotel_views.sql
```

يشمل:

- `Guest_View`
- `Employee_View`

تقدرين تشغلينه في MySQL Workbench إذا تبغين views في قاعدة البيانات.

## بيانات الموظفين والخدمات

ملف جاهز للإدخال:

```text
employee_service_inserts.sql
```

## إضافة كلمة مرور

ملف جاهز لإضافة عمود `Password` للضيف والموظف:

```text
add_password_columns.sql
```
