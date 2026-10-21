-- =====================================================================
-- College ERP System Database Schema
-- Strict Compliance: MySQL Native JDBC, Normalized Relational Schema
-- =====================================================================

CREATE DATABASE IF NOT EXISTS college_erp;
USE college_erp;

-- 1. Authentication / Users Table (Used for GUI Login Verification)
DROP TABLE IF EXISTS users;
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role ENUM('ADMIN', 'FACULTY', 'STUDENT') NOT NULL DEFAULT 'ADMIN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Faculty Table (Person Subclass Entity)
DROP TABLE IF EXISTS faculty;
CREATE TABLE faculty (
    faculty_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    department VARCHAR(50) NOT NULL,
    designation VARCHAR(50) NOT NULL,
    salary DECIMAL(10, 2) NOT NULL,
    hire_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. Students Table (Person Subclass Entity)
DROP TABLE IF EXISTS students;
CREATE TABLE students (
    student_id INT AUTO_INCREMENT PRIMARY KEY,
    roll_number VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    department VARCHAR(50) NOT NULL,
    semester INT NOT NULL,
    dob DATE NOT NULL,
    address VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. Courses Table
DROP TABLE IF EXISTS courses;
CREATE TABLE courses (
    course_id INT AUTO_INCREMENT PRIMARY KEY,
    course_code VARCHAR(20) NOT NULL UNIQUE,
    course_name VARCHAR(100) NOT NULL,
    credits INT NOT NULL,
    department VARCHAR(50) NOT NULL,
    faculty_id INT,
    FOREIGN KEY (faculty_id) REFERENCES faculty(faculty_id) ON DELETE SET NULL
);

-- 5. Attendance Table
DROP TABLE IF EXISTS attendance;
CREATE TABLE attendance (
    attendance_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    course_id INT NOT NULL,
    attendance_date DATE NOT NULL,
    status ENUM('PRESENT', 'ABSENT', 'LATE') NOT NULL,
    remarks VARCHAR(255),
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE
);

-- 6. Exams Table
DROP TABLE IF EXISTS exams;
CREATE TABLE exams (
    exam_id INT AUTO_INCREMENT PRIMARY KEY,
    exam_name VARCHAR(100) NOT NULL,
    course_id INT NOT NULL,
    student_id INT NOT NULL,
    max_marks DOUBLE NOT NULL DEFAULT 100.0,
    marks_obtained DOUBLE NOT NULL,
    grade VARCHAR(5),
    exam_date DATE NOT NULL,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);

-- 7. Fees Table (Supports FeeOperations & Custom FeeException)
DROP TABLE IF EXISTS fees;
CREATE TABLE fees (
    fee_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    fee_type VARCHAR(50) NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    paid_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    due_amount DECIMAL(10, 2) NOT NULL,
    status ENUM('PAID', 'PARTIAL', 'UNPAID') NOT NULL DEFAULT 'UNPAID',
    last_payment_date DATE,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);

-- =====================================================================
-- Initial Seed Data for Testing & Demonstration
-- =====================================================================

-- Default Credentials: username 'admin', password 'admin123'
INSERT INTO users (username, password, role) VALUES 
('admin', 'admin123', 'ADMIN'),
('faculty1', 'faculty123', 'FACULTY'),
('student1', 'student123', 'STUDENT');

-- Initial Faculty
INSERT INTO faculty (name, email, phone, department, designation, salary, hire_date) VALUES
('Dr. Alan Turing', 'alan.turing@college.edu', '9876543210', 'Computer Science', 'Professor', 95000.00, '2020-08-15'),
('Dr. Ada Lovelace', 'ada.lovelace@college.edu', '9876543211', 'Computer Science', 'Associate Professor', 85000.00, '2021-01-10'),
('Prof. Claude Shannon', 'shannon@college.edu', '9876543212', 'Electronics', 'Professor', 92000.00, '2019-07-01');

-- Initial Students
INSERT INTO students (roll_number, name, email, phone, department, semester, dob, address) VALUES
('CS2026001', 'Rahul Sharma', 'rahul.sharma@college.edu', '9123456780', 'Computer Science', 4, '2004-05-14', '123 Tech Park Road, Bangalore'),
('CS2026002', 'Priya Patel', 'priya.patel@college.edu', '9123456781', 'Computer Science', 4, '2004-09-22', '45 Green Valley Ave, Mumbai'),
('EC2026001', 'Amit Verma', 'amit.verma@college.edu', '9123456782', 'Electronics', 6, '2003-11-03', '78 Silicon Lane, Hyderabad');

-- Initial Courses
INSERT INTO courses (course_code, course_name, credits, department, faculty_id) VALUES
('CS401', 'Object Oriented Programming with Java', 4, 'Computer Science', 1),
('CS402', 'Database Management Systems', 4, 'Computer Science', 2),
('EC601', 'Digital Signal Processing', 3, 'Electronics', 3);

-- Initial Attendance
INSERT INTO attendance (student_id, course_id, attendance_date, status, remarks) VALUES
(1, 1, '2026-10-15', 'PRESENT', 'On time'),
(2, 1, '2026-10-15', 'PRESENT', 'On time'),
(3, 3, '2026-10-15', 'ABSENT', 'Medical leave');

-- Initial Exams
INSERT INTO exams (exam_name, course_id, student_id, max_marks, marks_obtained, grade, exam_date) VALUES
('Midterm Exam', 1, 1, 100.0, 92.0, 'A+', '2026-09-20'),
('Midterm Exam', 1, 2, 100.0, 85.5, 'A', '2026-09-20'),
('Midterm Exam', 3, 3, 100.0, 78.0, 'B+', '2026-09-22');

-- Initial Fees
INSERT INTO fees (student_id, fee_type, total_amount, paid_amount, due_amount, status, last_payment_date) VALUES
(1, 'Semester Tuition', 50000.00, 50000.00, 0.00, 'PAID', '2026-08-01'),
(2, 'Semester Tuition', 50000.00, 30000.00, 20000.00, 'PARTIAL', '2026-08-10'),
(3, 'Semester Tuition', 50000.00, 0.00, 50000.00, 'UNPAID', NULL);
