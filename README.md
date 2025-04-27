Employee Works Hour & Salary Tracker
A modern, full-featured Java desktop application for managing employee work hours, job applications, attendance, and salary calculations. Designed for organizations to streamline HR processes, from job application approval to work assignment and payroll, with a beautiful, user-friendly interface powered by FlatLaf.
Table of Contents
Overview
Features
Screenshots
Technology Stack & Dependencies
Database Schema
Setup & Installation
Usage
Project Structure
Contributing
License
Acknowledgements
Overview
Employee Works Hour & Salary Tracker is a robust Java Swing application for HR and payroll management. It allows admins to manage job applications, approve candidates, assign work, track attendance, and calculate salaries. Employees can log in, view their assignments, and track their attendance and salary. The application uses a MySQL database for persistent storage and features a modern, responsive UI.
Features
Admin Features
Dashboard: View quick stats (pending applications, total employees, active assignments, total salary).
Job Applications:
View, approve, and manage job applications.
View applicant details, resumes, and profile pictures.
Assign work to approved candidates.
Employee Management:
View all employees.
(Planned) Add/edit employee details.
Attendance Tracking:
View attendance records for all employees.
Salary Management:
View salary calculations.
(Planned) Calculate salaries for the current month.
Employee Features
Login: Secure authentication for employees, admins, and developers.
Dashboard: View assigned work, attendance, and salary details.
Attendance: Clock in/out, view attendance history.
Salary: View detailed salary breakdown.
Job Application Workflow
Candidates can apply for jobs, upload resumes and profile pictures.
Admins review, approve, and assign work to candidates.
Approved candidates are automatically added to the employee database.
Security
Passwords are hashed using SHA-256.
Secure database queries using prepared statements.
UI/UX
Modern, flat look and feel with FlatLaf.
Responsive layouts and styled components.
User-friendly dialogs and error handling.
Screenshots
> Add screenshots here for each major feature (dashboard, job application, work assignment, etc.)
Technology Stack & Dependencies
Java 8+
Swing (GUI)
FlatLaf (Modern Look & Feel)
MySQL (Database)
JDBC (Database connectivity)
Maven (Build & dependency management)
Other Libraries:
mysql-connector-java (MySQL JDBC driver)
protobuf-java (if used)
QRCodeGenerator (if used for employee QR codes)
Maven Dependencies
Apply to schema.sql
Database Schema
The application uses a MySQL database named EmployeeDB. Key tables include:
user_accounts (user_id, username, password, role)
employees (employee_id, full_name, email, department, designation, upi, gender)
job_applications (application_id, full_name, birthdate, work_experience, profile_pic, resume, interested_sector, email, gender, status)
attendance (attendance_id, employee_id, work_date, shift_type, start_time, end_time, working_hours)
work_assignments (assignment_id, application_id, employee_id, work_type, hours, hourly_rate, start_date, end_date, description, status)
salary_calculations (calculation_id, employee_id, month, year, base_salary, night_shift_allowance, overtime_pay, hourly_pay, total_salary)
> See `src/com/company/database/schema.sql` for full schema and sample data.
Setup & Installation
1. Clone the Repository
Apply to schema.sql
Run
2. Set Up the Database
Install MySQL and create a database:
Apply to schema.sql
Import the schema:
Apply to schema.sql
Run
(Optional) Update MySQL credentials in DBConnection.java if needed.
3. Build the Project
Apply to schema.sql
Run
4. Run the Application
Option 1: From Maven
Apply to schema.sql
Run
Option 2: From JAR
Apply to schema.sql
Run
> On Unix/Mac, use : instead of ; in the classpath.
Usage
Login:
Default admin credentials:
Username: admin
Password: admin123
Admin Dashboard:
Manage job applications, employees, attendance, and salary.
Job Application:
Candidates can apply via the application form.
Approve & Assign Work:
Admins approve applications and assign work.
Employee Dashboard:
Employees can view assignments, attendance, and salary.
Project Structure
Apply to schema.sql
Contributing
Contributions are welcome! Please open issues or submit pull requests for new features, bug fixes, or improvements.
License
This project is licensed under the MIT License.
Acknowledgements
FlatLaf for the modern Java look and feel.
MySQL for the database.
All contributors and open-source libraries used.
For any questions or support, please open an issue on GitHub.
