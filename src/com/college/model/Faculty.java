package com.college.model;

import java.sql.Date;

/**
 * Concrete Faculty entity inheriting from Person.
 * Demonstrates:
 * 1. Inheritance (extends Person).
 * 2. Constructor chaining via super().
 * 3. Polymorphism through overridden getRole() and getDetailsSummary().
 */
public class Faculty extends Person {

    private String designation;
    private double salary;
    private Date hireDate;

    public Faculty() {
        super();
    }

    /**
     * Full constructor demonstrating constructor chaining to superclass.
     */
    public Faculty(int id, String name, String email, String phone, String department,
                   String designation, double salary, Date hireDate) {
        super(id, name, email, phone, department); // Invoking Person constructor
        this.designation = designation;
        this.salary = salary;
        this.hireDate = hireDate;
    }

    /**
     * Constructor without DB ID for new faculty recruitment.
     */
    public Faculty(String name, String email, String phone, String department,
                   String designation, double salary, Date hireDate) {
        super(name, email, phone, department); // Invoking superclass constructor
        this.designation = designation;
        this.salary = salary;
        this.hireDate = hireDate;
    }

    // Polymorphic implementation
    @Override
    public String getRole() {
        return "Faculty";
    }

    @Override
    public String getDetailsSummary() {
        return String.format("Faculty [%s %s, Dept: %s, Salary: ₹%.2f, Email: %s]",
                designation, getName(), getDepartment(), salary, getEmail());
    }

    // Getters and Setters
    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public Date getHireDate() {
        return hireDate;
    }

    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%s)", getName(), designation, getDepartment());
    }
}
