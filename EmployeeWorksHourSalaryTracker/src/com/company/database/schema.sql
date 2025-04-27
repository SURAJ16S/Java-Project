-- Create the database if it doesn't exist
CREATE DATABASE IF NOT EXISTS EmployeeDB;
USE EmployeeDB;

-- Create user_accounts table
CREATE TABLE IF NOT EXISTS user_accounts (
    user_id VARCHAR(10) NOT NULL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('admin', 'employee', 'developer') NOT NULL
);

-- Create employees table
CREATE TABLE IF NOT EXISTS employees (
    employee_id VARCHAR(20) NOT NULL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    department VARCHAR(50),
    designation VARCHAR(50),
    upi VARCHAR(100),
    gender ENUM('M', 'F', 'Other')
);

-- Create attendance table
CREATE TABLE IF NOT EXISTS attendance (
    attendance_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    employee_id VARCHAR(20) NOT NULL,
    work_date DATE NOT NULL,
    shift_type ENUM('fulltime', 'night', 'extra', 'hourly') NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    working_hours DECIMAL(4,2),
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
);

-- Create job_applications table
CREATE TABLE IF NOT EXISTS job_applications (
    application_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    birthdate DATE NOT NULL,
    work_experience INT,
    profile_pic VARCHAR(255),
    resume VARCHAR(255),
    interested_sector VARCHAR(50),
    email VARCHAR(100) NOT NULL,
    gender ENUM('M', 'F', 'Other'),
    status ENUM('pending', 'approved', 'rejected') DEFAULT 'pending'
);

-- Create salary_payments table
CREATE TABLE IF NOT EXISTS salary_payments (
    payment_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    employee_id VARCHAR(20) NOT NULL,
    payment_date DATE NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id)
);

-- Insert default admin user (password: admin123)
INSERT INTO user_accounts (user_id, username, password, role) 
VALUES ('ADMIN001', 'admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'admin')
ON DUPLICATE KEY UPDATE user_id=user_id;

-- Add the column if it doesn't exist (this will give an error if it already exists, but you can ignore it)
ALTER TABLE job_applications ADD COLUMN status ENUM('pending', 'approved', 'rejected') DEFAULT 'pending'; 