package com.college.gui;

import com.college.dao.UserDAO;
import com.college.db.DBConnection;
import com.college.model.User;
import com.college.util.DatabaseConfig;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

/**
 * Login Window for the College ERP System.
 * Demonstrates:
 * 1. JFrame window creation.
 * 2. Layout managers: BorderLayout, GridLayout, FlowLayout.
 * 3. ActionListener interface implementation for button click event handling.
 * 4. Exception Handling (try-catch) with user-friendly GUI notifications.
 */
public class LoginFrame extends JFrame implements ActionListener {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton dbSettingsButton;
    private JButton clearButton;
    private JLabel statusLabel;

    private UserDAO userDAO;

    public LoginFrame() {
        super("College ERP System - Portal Login");
        this.userDAO = new UserDAO();
        initializeUI();
    }

    private void initializeUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 500);
        setLocationRelativeTo(null); // Center on screen
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BG_LIGHT);

        // 1. Top Header Banner (BorderLayout.NORTH)
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(UITheme.PRIMARY);
        headerPanel.setBorder(new EmptyBorder(25, 20, 25, 20));

        JLabel titleLabel = new JLabel("COLLEGE ERP SYSTEM");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subTitleLabel = new JLabel("Academic Administration & Student Portal");
        subTitleLabel.setFont(UITheme.FONT_SUBHEADER);
        subTitleLabel.setForeground(new Color(226, 232, 240));
        subTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        headerPanel.add(subTitleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // 2. Center Login Form Panel (BorderLayout.CENTER)
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(UITheme.BG_LIGHT);

        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(UITheme.CARD_BG);
        cardPanel.setBorder(UITheme.createCardBorder());
        cardPanel.setPreferredSize(new Dimension(420, 270));

        JLabel formHeader = new JLabel("Account Login");
        formHeader.setFont(UITheme.FONT_HEADER);
        formHeader.setForeground(UITheme.TEXT_MAIN);
        formHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Form Fields (GridLayout inside container)
        JPanel formFields = new JPanel(new GridLayout(4, 1, 6, 6));
        formFields.setBackground(UITheme.CARD_BG);
        formFields.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel userLabel = new JLabel("Username / ID:");
        userLabel.setFont(UITheme.FONT_SUBHEADER);
        userLabel.setForeground(UITheme.TEXT_MAIN);
        usernameField = UITheme.createTextField(20);
        usernameField.setText("admin"); // Default seed user

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(UITheme.FONT_SUBHEADER);
        passLabel.setForeground(UITheme.TEXT_MAIN);
        passwordField = UITheme.createPasswordField(20);
        passwordField.setText("admin123"); // Default seed password

        formFields.add(userLabel);
        formFields.add(usernameField);
        formFields.add(passLabel);
        formFields.add(passwordField);

        // Buttons Panel (FlowLayout)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(UITheme.CARD_BG);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        clearButton = UITheme.createSecondaryButton("Clear");
        clearButton.addActionListener(this);

        loginButton = UITheme.createPrimaryButton("Sign In");
        loginButton.addActionListener(this);

        buttonPanel.add(clearButton);
        buttonPanel.add(loginButton);

        // Status Label for Feedback
        statusLabel = new JLabel("Default: admin / admin123");
        statusLabel.setFont(UITheme.FONT_SMALL);
        statusLabel.setForeground(UITheme.TEXT_MUTED);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        cardPanel.add(formHeader);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        cardPanel.add(formFields);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        cardPanel.add(buttonPanel);
        cardPanel.add(statusLabel);

        centerWrapper.add(cardPanel);
        add(centerWrapper, BorderLayout.CENTER);

        // 3. Bottom Footer with Database Configuration Option (BorderLayout.SOUTH)
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(UITheme.BG_LIGHT);
        footerPanel.setBorder(new EmptyBorder(10, 20, 15, 20));

        dbSettingsButton = new JButton("⚙ Database Settings");
        dbSettingsButton.setFont(UITheme.FONT_SMALL);
        dbSettingsButton.setForeground(UITheme.PRIMARY);
        dbSettingsButton.setContentAreaFilled(false);
        dbSettingsButton.setBorderPainted(false);
        dbSettingsButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        dbSettingsButton.addActionListener(this);

        JLabel copyright = new JLabel("Strict Syllabus Compliance - Java Swing + Native JDBC");
        copyright.setFont(UITheme.FONT_SMALL);
        copyright.setForeground(UITheme.TEXT_MUTED);

        footerPanel.add(dbSettingsButton, BorderLayout.WEST);
        footerPanel.add(copyright, BorderLayout.EAST);
        add(footerPanel, BorderLayout.SOUTH);

        // Pressing Enter key triggers login
        getRootPane().setDefaultButton(loginButton);
    }

    /**
     * ActionListener implementation for button click event handling.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == loginButton) {
            handleLogin();
        } else if (source == clearButton) {
            usernameField.setText("");
            passwordField.setText("");
            statusLabel.setText("Fields cleared.");
            statusLabel.setForeground(UITheme.TEXT_MUTED);
        } else if (source == dbSettingsButton) {
            openDatabaseSettingsDialog();
        }
    }

    /**
     * Core business authentication logic.
     * Demonstrates Exception Handling (try-catch) and PreparedStatement authentication.
     */
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter both username and password.");
            statusLabel.setForeground(UITheme.DANGER);
            JOptionPane.showMessageDialog(this,
                    "Please provide both Username and Password to proceed.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            statusLabel.setText("Authenticating credentials...");
            statusLabel.setForeground(UITheme.PRIMARY);

            User authenticatedUser = userDAO.authenticate(username, password);

            if (authenticatedUser != null) {
                boolean isDbLive = DBConnection.testConnection();
                String welcomeMsg = "Login Successful! Welcome, " + authenticatedUser.getUsername() + " (" + authenticatedUser.getRole() + ")";
                if (!isDbLive) {
                    welcomeMsg += "\n\n[Notice]: Local MySQL is offline. Running in Demo Mode with built-in sample data so you can test all features!";
                }

                JOptionPane.showMessageDialog(this,
                        welcomeMsg,
                        "Authentication Success",
                        JOptionPane.INFORMATION_MESSAGE);

                MainMenuFrame mainMenu = new MainMenuFrame(authenticatedUser);
                mainMenu.setVisible(true);
                this.dispose(); // Close login window
            } else {
                statusLabel.setText("Invalid username or password.");
                statusLabel.setForeground(UITheme.DANGER);
                JOptionPane.showMessageDialog(this,
                        "Invalid username or password.\n\nPlease use demo credentials:\n- Admin: admin / admin123\n- Faculty: faculty1 / faculty123\n- Student: student1 / student123",
                        "Authentication Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            // If DB threw an error and wasn't caught by demo credentials, allow user to continue in Demo Mode
            int choice = JOptionPane.showConfirmDialog(this,
                    "MySQL server connection was not detected on localhost:3306.\n\n" +
                    "Would you like to enter in 'Offline Demo Mode' to evaluate all screens?\n" +
                    "(You will be able to test Students, Faculty, Courses, Attendance, Exams, and Fees)",
                    "MySQL Offline - Enter Demo Mode?",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {
                User demoUser = new User(1, username.isEmpty() ? "admin" : username, "password", "ADMIN");
                MainMenuFrame mainMenu = new MainMenuFrame(demoUser);
                mainMenu.setVisible(true);
                this.dispose();
            } else {
                statusLabel.setText("Database Connection Error.");
                statusLabel.setForeground(UITheme.DANGER);
            }
        }
    }

    /**
     * Helper dialog to configure MySQL Host, Port, Username, and Password without touching code.
     */
    private void openDatabaseSettingsDialog() {
        JTextField hostField = new JTextField("localhost", 15);
        JTextField portField = new JTextField("3306", 6);
        JTextField dbField = new JTextField("college_erp", 15);
        JTextField userField = new JTextField("root", 15);
        JPasswordField passField = new JPasswordField(15);

        JPanel panel = new JPanel(new GridLayout(5, 2, 8, 8));
        panel.add(new JLabel("Host:"));
        panel.add(hostField);
        panel.add(new JLabel("Port:"));
        panel.add(portField);
        panel.add(new JLabel("Database Name:"));
        panel.add(dbField);
        panel.add(new JLabel("MySQL User:"));
        panel.add(userField);
        panel.add(new JLabel("MySQL Password:"));
        panel.add(passField);

        int result = JOptionPane.showConfirmDialog(this, panel, "MySQL Connection Settings",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String url = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                    hostField.getText().trim(), portField.getText().trim(), dbField.getText().trim());
            String user = userField.getText().trim();
            String pass = new String(passField.getPassword());

            DBConnection.setCredentials(url, user, pass);

            boolean connected = DBConnection.testConnection();
            if (connected) {
                JOptionPane.showMessageDialog(this,
                        "Connection Successful to MySQL server!",
                        "Database Connected", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Could not connect to MySQL with provided settings.\nPlease verify that MySQL service is running.",
                        "Connection Failed", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}
