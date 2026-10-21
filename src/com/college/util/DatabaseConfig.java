package com.college.util;

import com.college.db.DBConnection;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Utility class to manage database connection configurations from a local properties file.
 * Enables students to seamlessly change MySQL username/password without modifying source code.
 */
public class DatabaseConfig {

    private static final String CONFIG_FILE = "db.properties";

    public static void loadConfig() {
        File file = new File(CONFIG_FILE);
        if (file.exists()) {
            Properties props = new Properties();
            try (FileInputStream in = new FileInputStream(file)) {
                props.load(in);
                String host = props.getProperty("db.host", "localhost");
                String port = props.getProperty("db.port", "3306");
                String name = props.getProperty("db.name", "college_erp");
                String user = props.getProperty("db.user", "root");
                String pass = props.getProperty("db.password", "");
                
                String url = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC", host, port, name);
                DBConnection.setCredentials(url, user, pass);
            } catch (IOException e) {
                System.err.println("[DatabaseConfig] Warning: Could not read db.properties, using defaults. " + e.getMessage());
            }
        } else {
            createDefaultConfigFile(file);
        }
    }

    private static void createDefaultConfigFile(File file) {
        Properties props = new Properties();
        props.setProperty("db.host", "localhost");
        props.setProperty("db.port", "3306");
        props.setProperty("db.name", "college_erp");
        props.setProperty("db.user", "root");
        props.setProperty("db.password", "");
        
        try (FileOutputStream out = new FileOutputStream(file)) {
            props.store(out, "College ERP Database Configuration");
        } catch (IOException ignored) {}
    }
}
