package com.college.model;

import java.sql.Date;

/**
 * Concrete Student entity inheriting from Person.
 * Demonstrates:
 * 1. Single Inheritance (extends Person).
 * 2. Constructor Chaining using super().
 * 3. Method Overriding (Polymorphism).
 */
public class Student extends Person {

    private String rollNumber;
    private int semester;
    private Date dob;
    private String address;

    public Student() {
        super();
    }

    /**
     * Full constructor demonstrating constructor chaining to superclass.
     */
    public Student(int id, String rollNumber, String name, String email, String phone, 
                   String department, int semester, Date dob, String address) {
        super(id, name, email, phone, department); // Explicit invocation of Person constructor
        this.rollNumber = rollNumber;
        this.semester = semester;
        this.dob = dob;
        this.address = address;
    }

    /**
     * Constructor without DB ID for student registration.
     */
    public Student(String rollNumber, String name, String email, String phone, 
                   String department, int semester, Date dob, String address) {
        super(name, email, phone, department); // Invocating superclass 4-arg constructor
        this.rollNumber = rollNumber;
        this.semester = semester;
        this.dob = dob;
        this.address = address;
    }

    // Polymorphic implementation
    @Override
    public String getRole() {
        return "Student";
    }

    @Override
    public String getDetailsSummary() {
        return String.format("Student [Roll: %s, Name: %s, Semester: %d, Department: %s, Contact: %s]",
                rollNumber, getName(), semester, getDepartment(), getPhone());
    }

    // Getters and Setters
    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - Sem %d", getName(), rollNumber, semester);
    }
}
