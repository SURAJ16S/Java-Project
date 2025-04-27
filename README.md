# Employee Works Hour & Salary Tracker

A modern, full-featured Java desktop application for managing employee work hours, job applications, attendance, and salary calculations. Designed for organizations to streamline HR processes—from job application approval to work assignment and payroll—with a beautiful, user-friendly interface powered by FlatLaf.

## Table of Contents

- Overview
    
- Features
    
    - Admin Features
        
    - Employee Features
        
    - Job Application Workflow
        
    - Security
        
    - UI/UX
        
- Screenshots
    
- Technology Stack & Dependencies
    
- Database Schema
    
- Setup & Installation
    
- Usage
    
- Project Structure
    
- Contributing
    
- License
    
- Acknowledgements
    

## Overview

Employee Works Hour & Salary Tracker is a robust Java Swing application for HR and payroll management. The application allows admins to manage job applications, approve candidates, assign work, track attendance, and calculate salaries. Employees can log in to view their assignments, attendance, and salary details. It uses a MySQL database for persistent storage and features a modern, responsive user interface.

## Features

### Admin Features

- **Dashboard:** View quick stats such as pending applications, total employees, active assignments, and total salary.
    
- **Job Applications:**
    
    - View, approve, and manage job applications.
        
    - View applicant details, resumes, and profile pictures.
        
    - Assign work to approved candidates.
        
- **Employee Management:**
    
    - View all employees.
        
    - _(Planned)_ Add or edit employee details.
        
- **Attendance Tracking:**
    
    - View attendance records for all employees.
        
- **Salary Management:**
    
    - View salary calculations.
        
    - _(Planned)_ Calculate salaries for the current month.
        

### Employee Features

- **Login:** Secure authentication for employees, admins, and developers.
    
- **Dashboard:** View assigned work, attendance, and salary details.
    
- **Attendance:**
    
    - Clock in/out and view attendance history.
        
- **Salary:** View a detailed salary breakdown.
    

### Job Application Workflow

- Candidates can apply for jobs and upload their resumes and profile pictures.
    
- Admins review, approve, and assign work to candidates.
    
- Once approved, candidates are automatically added to the employee database.
    

### Security

- Passwords are hashed using SHA-256.
    
- Database queries are secured using prepared statements.
    

### UI/UX

- Modern, flat look and feel provided by FlatLaf.
    
- Responsive layouts with styled components.
    
- User-friendly dialogs and robust error handling.
    

## Screenshots

> **Note:** Add screenshots here for each major feature (dashboard, job application, work assignment, etc.)

## Technology Stack & Dependencies

- **Java 8+**
    
- **Swing (GUI)**
    
- **FlatLaf (Modern Look & Feel)**
    
- **MySQL (Database)**
    
- **JDBC (Database Connectivity)**
    
- **Maven (Build & Dependency Management)**
    

### Other Libraries

- `mysql-connector-java` (MySQL JDBC driver)
    
- `protobuf-java` (if used)
    
- `QRCodeGenerator` (if used for employee QR codes)
    

> **Maven Dependencies:** Refer to the `README.md` section for details.

## Database Schema

The application uses a MySQL database named `EmployeeDB`. Key tables include:

- **user_accounts:** `user_id`, `username`, `password`, `role`
    
- **employees:** `employee_id`, `full_name`, `email`, `department`, `designation`, `upi`, `gender`
    
- **job_applications:** `application_id`, `full_name`, `birthdate`, `work_experience`, `profile_pic`, `resume`, `interested_sector`, `email`, `gender`, `status`
    
- **attendance:** `attendance_id`, `employee_id`, `work_date`, `shift_type`, `start_time`, `end_time`, `working_hours`
    
- **work_assignments:** `assignment_id`, `application_id`, `employee_id`, `work_type`, `hours`, `hourly_rate`, `start_date`, `end_date`, `description`, `status`
    
- **salary_calculations:** `calculation_id`, `employee_id`, `month`, `year`, `base_salary`, `night_shift_allowance`, `overtime_pay`, `hourly_pay`, `total_salary`
    

> See `src/com/company/database/schema.sql` for the full schema and sample data.

## Setup & Installation

1. **Clone the Repository** > _Apply instructions in README.md.._
    
2. **Set Up the Database**
    
    - **Install MySQL and Create a Database:** > _Apply instructions in README.md.._
        
    - **Import the Schema:** > _Apply instructions in README.md.._
        
    - _(Optional)_ Update MySQL credentials in `DBConnection.java` if needed.
        
3. **Build the Project** > _Apply instructions in README.md.._
    
4. **Run the Application**
    
    - **Option 1: From Maven** > _Apply instructions in README.md.._
        
    - **Option 2: From JAR** > _Apply instructions in README.md.._
        
    
    > **Note:** On Unix/Mac, use `:` instead of `;` in the classpath.
    

## Usage

- **Login:**
    
    - **Default Admin Credentials:**
        
        - **Username:** admin
            
        - **Password:** admin123
            
- **Admin Dashboard:** Manage job applications, employees, attendance, and salary.
    
- **Job Application:** Candidates can apply using the application form.
    
- **Approve & Assign Work:** Admins can approve job applications and assign work to selected candidates.
    
- **Employee Dashboard:** Employees can view their assignments, attendance, and detailed salary information.
    

## Project Structure

> _Apply instructions in README.md.._

## Contributing

Contributions are welcome! Please open issues or submit pull requests for new features, bug fixes, or improvements.

## License

This project is licensed under the MIT License.

## Acknowledgements

- FlatLaf for the modern Java look and feel.
    
- MySQL for the database.
    
- Thanks to all contributors and open-source libraries used in this project.
    

For any questions or support, please open an issue on GitHub.
