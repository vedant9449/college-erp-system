package com.college.gui;

import com.college.dao.CourseDAO;
import com.college.dao.FacultyDAO;
import com.college.dao.FeeDAO;
import com.college.dao.StudentDAO;
import com.college.model.User;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Main Application Framework Window (Dashboard & Navigation Hub).
 * Demonstrates:
 * 1. JFrame application container.
 * 2. Complex layout management (BorderLayout, GridLayout, BoxLayout, FlowLayout).
 * 3. ActionListener interface implementation for menu navigation and module switching.
 * 4. User session management and graceful logout workflow.
 */
public class MainMenuFrame extends JFrame implements ActionListener {

    private final User currentUser;
    private JPanel contentPanel;
    private JLabel statusDateLabel;

    // Navigation Menu Buttons
    private JButton btnDashboard;
    private JButton btnStudents;
    private JButton btnFaculty;
    private JButton btnCourses;
    private JButton btnAttendance;
    private JButton btnExams;
    private JButton btnFees;
    private JButton btnLogout;

    // DAOs for live dashboard stats
    private final StudentDAO studentDAO;
    private final FacultyDAO facultyDAO;
    private final CourseDAO courseDAO;
    private final FeeDAO feeDAO;

    public MainMenuFrame(User user) {
        super("College ERP System - Academic Control Center");
        this.currentUser = (user != null) ? user : new User("Admin", "admin123", "ADMIN");
        this.studentDAO = new StudentDAO();
        this.facultyDAO = new FacultyDAO();
        this.courseDAO = new CourseDAO();
        this.feeDAO = new FeeDAO();

        initializeUI();
    }

    private void initializeUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 720);
        setMinimumSize(new Dimension(950, 600));
        setLocationRelativeTo(null); // Center on screen
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BG_LIGHT);

        // 1. Top Navigation Bar (BorderLayout.NORTH)
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UITheme.PRIMARY);
        topBar.setBorder(new EmptyBorder(14, 24, 14, 24));

        JLabel brandTitle = new JLabel("COLLEGE ERP SYSTEM");
        brandTitle.setFont(UITheme.FONT_TITLE);
        brandTitle.setForeground(Color.WHITE);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        userPanel.setOpaque(false);

        JLabel userBadge = new JLabel(String.format("Logged in: %s [%s]", 
                currentUser.getUsername(), currentUser.getRole()));
        userBadge.setFont(UITheme.FONT_SUBHEADER);
        userBadge.setForeground(Color.WHITE);

        btnLogout = UITheme.createSecondaryButton("Sign Out");
        btnLogout.setBackground(new Color(237, 242, 247));
        btnLogout.addActionListener(this);

        userPanel.add(userBadge);
        userPanel.add(btnLogout);

        topBar.add(brandTitle, BorderLayout.WEST);
        topBar.add(userPanel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // 2. Left Sidebar Navigation Menu (BorderLayout.WEST)
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Color.WHITE);
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBorder(new CompoundBorder(
            new LineBorder(UITheme.BORDER_COLOR, 1),
            new EmptyBorder(16, 12, 16, 12)
        ));

        JLabel navHeading = new JLabel("ACADEMIC MODULES");
        navHeading.setFont(UITheme.FONT_SMALL);
        navHeading.setForeground(UITheme.TEXT_MUTED);
        navHeading.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(navHeading);
        sidebar.add(Box.createRigidArea(new Dimension(0, 12)));

        btnDashboard = createNavButton("•  Dashboard");
        btnStudents = createNavButton("•  Student Records");
        btnFaculty = createNavButton("•  Faculty Directory");
        btnCourses = createNavButton("•  Course Catalog");
        btnAttendance = createNavButton("•  Attendance Register");
        btnExams = createNavButton("•  Examinations & Marks");
        btnFees = createNavButton("•  Fee Ledger");

        sidebar.add(btnDashboard);
        sidebar.add(Box.createRigidArea(new Dimension(0, 6)));
        sidebar.add(btnStudents);
        sidebar.add(Box.createRigidArea(new Dimension(0, 6)));
        sidebar.add(btnFaculty);
        sidebar.add(Box.createRigidArea(new Dimension(0, 6)));
        sidebar.add(btnCourses);
        sidebar.add(Box.createRigidArea(new Dimension(0, 6)));
        sidebar.add(btnAttendance);
        sidebar.add(Box.createRigidArea(new Dimension(0, 6)));
        sidebar.add(btnExams);
        sidebar.add(Box.createRigidArea(new Dimension(0, 6)));
        sidebar.add(btnFees);
        sidebar.add(Box.createVerticalGlue());

        add(sidebar, BorderLayout.WEST);

        // 3. Center Content Panel (BorderLayout.CENTER)
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(UITheme.BG_LIGHT);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Display initial Overview Dashboard
        showDashboardPanel();
        add(contentPanel, BorderLayout.CENTER);

        // 4. Bottom Status Bar (BorderLayout.SOUTH)
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(Color.WHITE);
        statusBar.setBorder(new CompoundBorder(
            new LineBorder(UITheme.BORDER_COLOR, 1),
            new EmptyBorder(6, 18, 6, 18)
        ));

        JLabel systemInfo = new JLabel("System: Connected | Session Active | Java Swing Desktop Engine");
        systemInfo.setFont(UITheme.FONT_SMALL);
        systemInfo.setForeground(UITheme.TEXT_MUTED);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy");
        statusDateLabel = new JLabel(LocalDateTime.now().format(dtf));
        statusDateLabel.setFont(UITheme.FONT_SMALL);
        statusDateLabel.setForeground(UITheme.TEXT_MUTED);

        statusBar.add(systemInfo, BorderLayout.WEST);
        statusBar.add(statusDateLabel, BorderLayout.EAST);
        add(statusBar, BorderLayout.SOUTH);
    }

    /**
     * Factory method to create uniform navigation sidebar buttons.
     */
    private JButton createNavButton(String title) {
        JButton btn = new JButton(title);
        btn.setFont(UITheme.FONT_SUBHEADER);
        btn.setForeground(UITheme.TEXT_MAIN);
        btn.setBackground(Color.WHITE);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setBorder(new CompoundBorder(
            new LineBorder(UITheme.BORDER_COLOR, 1, true),
            new EmptyBorder(10, 14, 10, 14)
        ));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(this);
        return btn;
    }

    /**
     * Renders the home dashboard with dynamic statistics.
     */
    public void showDashboardPanel() {
        contentPanel.removeAll();

        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(UITheme.BG_LIGHT);

        // Welcome Header Banner
        JPanel banner = new JPanel(new BorderLayout());
        banner.setBackground(UITheme.CARD_BG);
        banner.setBorder(UITheme.createCardBorder());

        JLabel welcomeTitle = new JLabel("Academic Administration Overview");
        welcomeTitle.setFont(UITheme.FONT_TITLE);
        welcomeTitle.setForeground(UITheme.PRIMARY);

        JLabel welcomeSub = new JLabel("Manage student enrollments, faculty profiles, curriculum, attendance, and finances.");
        welcomeSub.setFont(UITheme.FONT_BODY);
        welcomeSub.setForeground(UITheme.TEXT_MUTED);

        banner.add(welcomeTitle, BorderLayout.NORTH);
        banner.add(welcomeSub, BorderLayout.SOUTH);
        panel.add(banner, BorderLayout.NORTH);

        // Metric Statistics Cards (GridLayout 2x2)
        JPanel statsGrid = new JPanel(new GridLayout(2, 2, 16, 16));
        statsGrid.setBackground(UITheme.BG_LIGHT);

        int totalStudents = 0;
        int totalFaculty = 0;
        int totalCourses = 0;
        double totalDues = 0.0;

        try {
            totalStudents = studentDAO.getAllStudents().size();
            totalFaculty = facultyDAO.getAllFaculty().size();
            totalCourses = courseDAO.getAllCourses().size();
            totalDues = feeDAO.calculateTotalDues();
        } catch (Exception ex) {
            System.err.println("[Dashboard] Error querying live metrics: " + ex.getMessage());
        }

        statsGrid.add(createMetricCard("Total Students Enrolled", String.valueOf(totalStudents), "Active in Current Semester", UITheme.PRIMARY));
        statsGrid.add(createMetricCard("Faculty Members", String.valueOf(totalFaculty), "Full-time Academic Staff", UITheme.ACCENT));
        statsGrid.add(createMetricCard("Active Courses", String.valueOf(totalCourses), "Curriculum Catalog", UITheme.WARNING));
        statsGrid.add(createMetricCard("Outstanding Fee Dues", String.format("₹%,.2f", totalDues), "Total Unpaid Student Dues", UITheme.DANGER));

        panel.add(statsGrid, BorderLayout.CENTER);

        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /**
     * Creates an individual metric summary card.
     */
    private JPanel createMetricCard(String title, String value, String subtext, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(UITheme.CARD_BG);
        card.setBorder(new CompoundBorder(
            new LineBorder(accentColor, 2, true),
            new EmptyBorder(16, 18, 16, 18)
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.FONT_SUBHEADER);
        titleLbl.setForeground(UITheme.TEXT_MUTED);

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLbl.setForeground(accentColor);

        JLabel subLbl = new JLabel(subtext);
        subLbl.setFont(UITheme.FONT_SMALL);
        subLbl.setForeground(UITheme.TEXT_MUTED);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLbl, BorderLayout.CENTER);
        card.add(subLbl, BorderLayout.SOUTH);
        return card;
    }

    /**
     * Swaps out the main central display area for a new module panel.
     */
    public void setContentModule(JPanel modulePanel) {
        contentPanel.removeAll();
        contentPanel.add(modulePanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /**
     * ActionListener handling sidebar navigation and logout.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == btnLogout) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to sign out?",
                    "Confirm Sign Out",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose();
                new LoginFrame().setVisible(true);
            }
        } else if (src == btnDashboard) {
            showDashboardPanel();
        } else {
            // Placeholder hooks for Phase 5 Business Modules
            handleModuleSwitch(src);
        }
    }

    /**
     * Modular routing hook for Phase 5 business panels.
     */
    private void handleModuleSwitch(Object src) {
        if (src == btnStudents) {
            setContentModule(new StudentPanel());
        } else if (src == btnFaculty) {
            setContentModule(new FacultyPanel());
        } else if (src == btnCourses) {
            setContentModule(new CoursePanel());
        } else if (src == btnAttendance) {
            setContentModule(new AttendancePanel());
        } else if (src == btnExams) {
            setContentModule(new ExamPanel());
        } else if (src == btnFees) {
            setContentModule(new FeePanel());
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }
}
