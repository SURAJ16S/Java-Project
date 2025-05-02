# Employee Works Hour and Salary Tracker

![Project Logo](upi_qr.png)

A comprehensive Java-based application for managing employee work hours, attendance, and salary processing with UPI payment integration.

## 👥 Developers
- **Suraj Bagul** 
- **Yash Borude**

## 📋 Table of Contents
- [Overview](#-overview)
- [Features](#-features)
- [System Requirements](#-system-requirements)
- [Installation Guide](#-installation-guide)
- [Database Setup](#-database-setup)
- [Running the Application](#-running-the-application)
- [User Guide](#-user-guide)
- [Technical Documentation](#-technical-documentation)
- [Project Structure](#-project-structure)
- [Security Features](#-security-features)
- [Performance Optimization](#-performance-optimization)
- [Troubleshooting](#-troubleshooting)
- [Contributing](#-contributing)
- [License](#-license)
- [Acknowledgments](#-acknowledgments)
- [Future Roadmap](#-future-roadmap)

## 🌟 Overview

The Employee Works Hour and Salary Tracker is a robust desktop application designed to streamline employee management, attendance tracking, and salary processing. Built with modern Java technologies, it features a sleek user interface, secure authentication, and seamless UPI payment integration. The application is designed to handle all aspects of employee management, from hiring to salary disbursement, with a focus on efficiency and user experience.

### Key Benefits
- **Automated Processes**: Reduces manual work in attendance and salary management
- **Real-time Tracking**: Live updates for attendance and work hours
- **Secure Payments**: Integrated UPI payment system with QR code support
- **Comprehensive Reporting**: Detailed analytics and reports for management
- **User-friendly Interface**: Intuitive design for all user roles

## ✨ Features

### User Management
- Role-based access control (Admin, Employee, Developer)
- Secure authentication system with password hashing
- User registration and profile management
- Session management and auto-logout
- Password recovery system
- Activity logging and audit trails

### Employee Management
- Complete employee information tracking
- Department and designation management
- UPI ID and mobile number integration
- QR code generation for payments
- Employee status tracking (Active/Inactive)
- Document management (ID proofs, certificates)
- Performance tracking and reviews

### Attendance System
- Shift tracking (fulltime, night, extra, hourly)
- Working hours calculation
- Time tracking and reporting
- Leave management
- Overtime calculation
- Attendance regularization
- Biometric integration support
- Mobile check-in/out

### Salary Management
- Salary calculations
- Payment processing
- Payment history tracking
- UPI payment integration
- QR code generation for payments
- Tax calculation and deduction
- Bonus and incentive management
- Salary slip generation
- Automated payment scheduling

### Job Application System
- Application submission
- Status tracking
- Document upload (profile picture, resume)
- Application processing
- Interview scheduling
- Candidate evaluation
- Automated email notifications
- Application analytics

### Reporting System
- Employee performance reports
- Attendance summary
- Salary reports
- Department-wise analytics
- Custom report generation
- Export to PDF/Excel
- Dashboard with key metrics

## 💻 System Requirements

### Hardware Requirements
- Processor: 1.6 GHz or higher (2.0 GHz recommended)
- RAM: 4 GB minimum (8 GB recommended)
- Storage: 500 MB free space (1 GB recommended)
- Display: 1366x768 or higher resolution (1920x1080 recommended)
- Network: Stable internet connection for UPI payments

### Software Requirements
- Operating System: 
  - Windows 10/11 (64-bit)
  - Linux (Ubuntu 20.04 or higher)
  - macOS (10.15 or higher)
- Java Development Kit (JDK) 11 or higher
- MySQL Server 8.0 or higher
- Maven 3.6 or higher
- Git (for version control)

### Development Tools
- IDE: IntelliJ IDEA or Eclipse
- Database Management: MySQL Workbench
- Version Control: Git
- Build Tool: Maven
- Testing: JUnit 5

## 📥 Installation Guide

### 1. Install Java Development Kit (JDK)
1. Download JDK 11 from [Oracle's website](https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html)
2. Run the installer and follow the instructions
3. Set JAVA_HOME environment variable:
   ```bash
   # Windows
   setx JAVA_HOME "C:\Program Files\Java\jdk-11"
   setx PATH "%PATH%;%JAVA_HOME%\bin"
   
   # Linux/macOS
   export JAVA_HOME=/usr/lib/jvm/java-11-openjdk
   export PATH=$PATH:$JAVA_HOME/bin
   ```
4. Verify installation:
   ```bash
   java -version
   javac -version
   ```

### 2. Install MySQL Server
1. Download MySQL Server 8.0 from [MySQL website](https://dev.mysql.com/downloads/mysql/)
2. Run the installer
3. During installation:
   - Choose "Developer Default" installation type
   - Set root password (remember this for later)
   - Configure MySQL as a service
   - Complete the installation
4. Install MySQL Workbench for database management
5. Verify installation:
   ```bash
   mysql --version
   ```

### 3. Install Maven
1. Download Maven from [Apache Maven website](https://maven.apache.org/download.cgi)
2. Extract to a directory (e.g., C:\Program Files\Apache\maven)
3. Add Maven to PATH:
   ```bash
   # Windows
   setx PATH "%PATH%;C:\Program Files\Apache\maven\bin"
   
   # Linux/macOS
   export PATH=$PATH:/opt/apache-maven/bin
   ```
4. Verify installation:
   ```bash
   mvn -version
   ```

## 🗄️ Database Setup

### 1. Create Database
1. Open MySQL Command Line Client
2. Log in with root credentials
3. Execute the following commands:
   ```sql
   CREATE DATABASE EmployeeDB;
   USE EmployeeDB;
   ```

### 2. Import Schema
1. Navigate to the project directory
2. Run the schema.sql file:
   ```bash
   mysql -u root -p EmployeeDB < src/com/company/database/schema.sql
   ```

### 3. Configure Database Connection
1. Create a `config.properties` file in the project root:
   ```properties
   # Database Configuration
   db.url=jdbc:mysql://localhost:3306/EmployeeDB
   db.user=root
   db.password=your_password
   
   # Connection Pool Settings
   db.pool.max_size=10
   db.pool.min_size=5
   db.pool.timeout=30000
   
   # Logging Configuration
   logging.level=INFO
   logging.file=database.log
   ```

### 4. Database Optimization
1. Create necessary indexes:
   ```sql
   CREATE INDEX idx_employee_id ON employees(employee_id);
   CREATE INDEX idx_attendance_date ON attendance(work_date);
   CREATE INDEX idx_salary_payment ON salary_payments(payment_date);
   ```

## 🚀 Running the Application

### Method 1: Using Maven
1. Open terminal in project directory
2. Build the project:
   ```bash
   mvn clean package
   ```
3. Run the application:
   ```bash
   java -jar target/EmployeeWorksHourSalaryTracker-1.0-SNAPSHOT.jar
   ```

### Method 2: Using run.bat (Windows)
1. Double-click `run.bat` in the project directory
2. The script contains:
   ```batch
   @echo off
   java -jar target/EmployeeWorksHourSalaryTracker-1.0-SNAPSHOT.jar
   pause
   ```

### Method 3: Using IDE
1. Open project in IntelliJ IDEA or Eclipse
2. Import as Maven project
3. Run `com.company.gui.HomeFrame` class

## 📖 User Guide

### Login
- Default Admin Credentials:
  - Username: admin
  - Password: admin123
- Security Features:
  - Password strength requirements
  - Account lockout after failed attempts
  - Session timeout
  - Activity logging

### Admin Dashboard
1. Employee Management
   - View all employees
   - Add new employees
   - Update employee details
   - Manage UPI IDs and mobile numbers
   - Export employee data
   - Bulk operations

2. Attendance Management
   - Track employee attendance
   - View attendance reports
   - Manage shifts
   - Approve/reject leave requests
   - Generate attendance reports
   - Set attendance policies

3. Salary Processing
   - Calculate salaries
   - Process payments
   - Generate payment QR codes
   - View payment history
   - Manage deductions
   - Process bonuses
   - Generate salary slips

### Employee Dashboard
1. Personal Information
   - View profile
   - Update UPI ID and mobile number
   - Generate payment QR code
   - Update contact information
   - View employment history
   - Access documents

2. Attendance
   - Clock in/out
   - View attendance history
   - Check working hours
   - Apply for leave
   - View shift schedule
   - Check overtime

### Developer Dashboard
1. User Management
   - View all users
   - Manage user roles
   - System monitoring
   - Access logs
   - Performance metrics
   - Error tracking

## 📚 Technical Documentation

### Database Schema
```sql
-- User Accounts
CREATE TABLE user_accounts (
    user_id VARCHAR(10) PRIMARY KEY,
    username VARCHAR(50) UNIQUE,
    password VARCHAR(255),
    role ENUM('admin', 'employee', 'developer'),
    status ENUM('active', 'inactive', 'locked'),
    last_login DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP
);

-- Employees
CREATE TABLE employees (
    employee_id VARCHAR(20) PRIMARY KEY,
    full_name VARCHAR(100),
    email VARCHAR(100),
    department VARCHAR(50),
    designation VARCHAR(50),
    upi VARCHAR(100),
    mobile_number VARCHAR(15),
    gender ENUM('M', 'F', 'Other'),
    joining_date DATE,
    status ENUM('active', 'inactive', 'on_leave'),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP
);

-- Attendance
CREATE TABLE attendance (
    attendance_id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id VARCHAR(20),
    work_date DATE,
    shift_type ENUM('fulltime', 'night', 'extra', 'hourly'),
    start_time TIME,
    end_time TIME,
    working_hours DECIMAL(4,2),
    status ENUM('present', 'absent', 'half_day', 'on_leave'),
    remarks TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
);

-- Job Applications
CREATE TABLE job_applications (
    application_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100),
    birthdate DATE,
    work_experience INT,
    profile_pic VARCHAR(255),
    resume VARCHAR(255),
    interested_sector VARCHAR(50),
    email VARCHAR(100),
    gender ENUM('M', 'F', 'Other'),
    status ENUM('pending', 'approved', 'rejected'),
    interview_date DATETIME,
    interview_notes TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP
);

-- Salary Payments
CREATE TABLE salary_payments (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id VARCHAR(20),
    payment_date DATE,
    amount DECIMAL(10,2),
    payment_method ENUM('upi', 'bank_transfer', 'cash'),
    status ENUM('pending', 'completed', 'failed'),
    transaction_id VARCHAR(100),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
);
```

### Project Structure
```
EmployeeWorksHourSalaryTracker/
├── src/
│   └── com/
│       └── company/
│           ├── database/
│           │   ├── DBConnection.java
│           │   ├── schema.sql
│           │   └── Queries.java
│           ├── gui/
│           │   ├── AdminDashboard.java
│           │   ├── EmployeeDashboard.java
│           │   ├── DeveloperDashboard.java
│           │   ├── HomeFrame.java
│           │   ├── LoginFrame.java
│           │   └── RegisterForm.java
│           ├── utils/
│           │   ├── QRCodeGenerator.java
│           │   ├── SecurityUtils.java
│           │   └── Logger.java
│           └── models/
│               ├── Employee.java
│               ├── User.java
│               └── Attendance.java
├── lib/
│   ├── mysql-connector-java-8.0.33.jar
│   ├── flatlaf-3.2.jar
│   └── zxing-3.5.1.jar
├── uploads/
│   ├── profile_pics/
│   └── resumes/
├── config/
│   └── config.properties
├── logs/
│   ├── database.log
│   └── application.log
├── pom.xml
└── run.bat
```

### Dependencies
- MySQL Connector/J 8.0.33
- FlatLaf 3.2 (UI Theme)
- ZXing 3.5.1 (QR Code Generation)
- JUnit 5 (Testing)
- Log4j 2 (Logging)
- Apache Commons (Utilities)

## 🔒 Security Features

### Authentication
- Password hashing using SHA-256
- Session management
- Role-based access control
- Account lockout policy
- Password expiration
- Two-factor authentication (optional)

### Data Protection
- Input validation
- SQL injection prevention
- XSS protection
- CSRF protection
- Data encryption
- Secure file uploads

### Audit Trail
- User activity logging
- Login attempts tracking
- Critical operation logging
- Error logging
- System event logging

## ⚡ Performance Optimization

### Database Optimization
- Connection pooling
- Query optimization
- Indexing strategy
- Caching mechanism
- Batch processing

### Application Optimization
- Memory management
- Thread pooling
- Resource cleanup
- Lazy loading
- Background processing

## 🛠️ Troubleshooting

### Common Issues

1. Database Connection Error
   - Check MySQL service is running
   - Verify credentials in config.properties
   - Ensure database exists
   - Check network connectivity
   - Verify port availability

2. Java Version Error
   - Verify JDK 11 is installed
   - Check JAVA_HOME environment variable
   - Update PATH if necessary
   - Check for multiple Java versions

3. Maven Build Error
   - Check internet connection
   - Verify Maven installation
   - Clean and rebuild project
   - Check dependency conflicts
   - Verify proxy settings

### Error Logs
- Check `database.log` for database-related issues
- Check `application.log` for runtime errors
- Check system event logs
- Monitor performance metrics

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

### Development Guidelines
- Follow Java coding standards
- Write unit tests
- Document code changes
- Update documentation
- Perform code review

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments

- Thanks to the open-source community for their contributions
- Special thanks to the developers of the libraries used
- Appreciation to all contributors and testers

## 🚀 Future Roadmap

### Planned Features
- Mobile application integration
- Biometric attendance system
- Advanced analytics dashboard
- Automated report generation
- Integration with HR systems
- Enhanced security features
- Multi-language support
- Cloud deployment option

## 📞 Support

For support, please contact:
- Email: support@example.com
- Phone: +1 (555) 123-4567
- Documentation: [Project Wiki](https://github.com/yourusername/EmployeeWorksHourSalaryTracker/wiki)
- Issue Tracker: [GitHub Issues](https://github.com/yourusername/EmployeeWorksHourSalaryTracker/issues)

---

Made with ❤️ by **Suraj Bagul** and **Yash Borude** 