package com.college.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection provides native JDBC connections using java.sql.DriverManager.
 * Strictly adheres to academic syllabus constraints:
 * - Pure native JDBC (no ORM / Spring Boot / Hibernate)
 * - Thread-safe connection factory
 * - Standardized exception handling and resource disposal
 */
public class DBConnection {

    // Default configuration (can be customized via system properties or DatabaseConfig)
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/college_erp?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";

    private static String url = System.getProperty("db.url", DEFAULT_URL);
    private static String user = System.getProperty("db.user", DEFAULT_USER);
    private static String password = System.getProperty("db.password", DEFAULT_PASSWORD);

    // Static block to explicitly load MySQL JDBC Driver
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("[DBConnection] MySQL JDBC Driver not found in classpath. " +
                               "Please ensure mysql-connector-j jar is present in the lib folder.");
        }
    }

    // Private constructor to enforce utility pattern
    private DBConnection() {}

    /**
     * Establishes and returns a new native JDBC Connection.
     * 
     * @return active java.sql.Connection
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Configure runtime credentials if different from default root / blank password.
     */
    public static void setCredentials(String newUrl, String newUser, String newPassword) {
        if (newUrl != null && !newUrl.trim().isEmpty()) {
            url = newUrl.trim();
        }
        if (newUser != null) {
            user = newUser.trim();
        }
        if (newPassword != null) {
            password = newPassword;
        }
    }

    /**
     * Quick connection verification utility.
     * 
     * @return true if database is reachable and credentials are valid
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("[DBConnection] Connection test failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Safe closure helper for AutoCloseable JDBC resources (Connection, Statement, ResultSet).
     */
    public static void close(AutoCloseable... resources) {
        for (AutoCloseable res : resources) {
            if (res != null) {
                try {
                    res.close();
                } catch (Exception ignored) {
                    // Suppressed to prevent masking primary exceptions
                }
            }
        }
    }

    public static String getUrl() {
        return url;
    }

    public static String getUser() {
        return user;
    }
}
