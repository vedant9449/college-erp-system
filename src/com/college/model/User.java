package com.college.model;

/**
 * User entity encapsulating credentials and role authorization for the GUI login framework.
 */
public class User {

    private int userId;
    private String username;
    private String password;
    private String role; // "ADMIN", "FACULTY", "STUDENT"

    public User() {
    }

    public User(int userId, String username, String password, String role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public User(String username, String password, String role) {
        this(0, username, password, role);
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return String.format("User: %s [%s]", username, role);
    }
}
