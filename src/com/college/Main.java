package com.college;

import com.college.gui.LoginFrame;
import com.college.gui.UITheme;
import com.college.util.DatabaseConfig;

import javax.swing.*;

/**
 * Main application bootstrap class for the College ERP System.
 * Ensures syllabus compliance:
 * - Pure Java Swing/AWT
 * - Native JDBC connection initialization
 * - Event Dispatch Thread (EDT) safety
 */
public class Main {

    public static void main(String[] args) {
        // 1. Initialize Database Configuration (reads db.properties if present)
        DatabaseConfig.loadConfig();

        // 2. Apply Clean Nimbus / System Swing Look & Feel
        UITheme.applyLookAndFeel();

        // 3. Launch GUI safely on the Swing Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
