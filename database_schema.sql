-- Create Database
CREATE DATABASE IF NOT EXISTS student_management;
USE student_management;

-- Create Students Table
CREATE TABLE IF NOT EXISTS students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    roll_no VARCHAR(20) UNIQUE NOT NULL,
    department VARCHAR(50) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(10),
    marks DOUBLE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Sample Data
INSERT INTO students (name, roll_no, department, email, phone, marks) VALUES
('Rajesh Kumar', 'CS001', 'Computer Science', 'rajesh@email.com', '9876543210', 85.5),
('Priya Singh', 'CS002', 'Computer Science', 'priya@email.com', '9876543211', 92.0),
('Amit Patel', 'EC001', 'Electronics', 'amit@email.com', '9876543212', 78.5),
('Neha Sharma', 'EC002', 'Electronics', 'neha@email.com', '9876543213', 88.0),
('Vikram Reddy', 'ME001', 'Mechanical', 'vikram@email.com', '9876543214', 76.5);
