package com.college.model;

/**
 * Abstract base class representing a generic Person in the College ERP System.
 * Demonstrates:
 * 1. Abstraction (cannot be directly instantiated).
 * 2. Encapsulation (private fields exposed via standard accessors/mutators).
 * 3. Polymorphism (abstract methods implemented uniquely by subclasses).
 */
public abstract class Person {

    private int id;
    private String name;
    private String email;
    private String phone;
    private String department;

    // Default constructor
    public Person() {
    }

    // Parameterized constructor
    public Person(int id, String name, String email, String phone, String department) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.department = department;
    }

    // Parameterized constructor without ID (for newly created entities before DB persistence)
    public Person(String name, String email, String phone, String department) {
        this(0, name, email, phone, department);
    }

    // Abstract methods demonstrating Polymorphism
    public abstract String getRole();
    public abstract String getDetailsSummary();

    // Standard Getters and Setters (Encapsulation)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return String.format("[%s] ID: %d | Name: %s | Dept: %s | Email: %s", 
                getRole(), id, name, department, email);
    }
}
