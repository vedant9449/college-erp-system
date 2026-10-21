package com.college.gui;

import com.college.dao.AttendanceDAO;
import com.college.dao.CourseDAO;
import com.college.dao.StudentDAO;
import com.college.model.Attendance;
import com.college.model.Course;
import com.college.model.Student;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Attendance Register & Tracking Module.
 * Demonstrates:
 * 1. Interface for recording daily student presence.
 * 2. Automated attendance percentage calculation.
 * 3. JTable history view.
 */
public class AttendancePanel extends JPanel implements ActionListener {

    private final AttendanceDAO attendanceDAO;
    private final StudentDAO studentDAO;
    private final CourseDAO courseDAO;

    private JTable attendanceTable;
    private DefaultTableModel tableModel;

    // Form inputs
    private JComboBox<StudentComboItem> cbStudents;
    private JComboBox<CourseComboItem> cbCourses;
    private JTextField txtDate;
    private JComboBox<String> cbStatus;
    private JTextField txtRemarks;

    private JButton btnRecord;
    private JButton btnCalcPercentage;
    private JButton btnRefresh;

    // Helper item classes for dropdowns
    public static class StudentComboItem {
        private final int id;
        private final String label;
        public StudentComboItem(int id, String label) { this.id = id; this.label = label; }
        public int getId() { return id; }
        @Override public String toString() { return label; }
    }

    public static class CourseComboItem {
        private final int id;
        private final String label;
        public CourseComboItem(int id, String label) { this.id = id; this.label = label; }
        public int getId() { return id; }
        @Override public String toString() { return label; }
    }

    public AttendancePanel() {
        this.attendanceDAO = new AttendanceDAO();
        this.studentDAO = new StudentDAO();
        this.courseDAO = new CourseDAO();
        initializeUI();
        loadDropdowns();
        loadAttendanceData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.BG_LIGHT);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // 1. Header Bar
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(UITheme.createCardBorder());

        JLabel title = new JLabel("Daily Lecture Attendance Register");
        title.setFont(UITheme.FONT_HEADER);
        title.setForeground(UITheme.PRIMARY);

        btnRefresh = UITheme.createSecondaryButton("Reload Records");
        btnRefresh.addActionListener(this);

        topPanel.add(title, BorderLayout.WEST);
        topPanel.add(btnRefresh, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // 2. Center: JTable
        String[] columnNames = {
            "ID", "Date", "Student Roll", "Student Name", "Course", "Status", "Remarks"
        };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        attendanceTable = new JTable(tableModel);
        attendanceTable.setFont(UITheme.FONT_BODY);
        attendanceTable.setRowHeight(28);
        attendanceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        attendanceTable.getTableHeader().setFont(UITheme.FONT_SUBHEADER);
        attendanceTable.getTableHeader().setBackground(UITheme.PRIMARY);
        attendanceTable.getTableHeader().setForeground(Color.WHITE);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        attendanceTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        attendanceTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        attendanceTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        attendanceTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        attendanceTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        attendanceTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(attendanceTable);
        scrollPane.setBorder(new LineBorder(UITheme.BORDER_COLOR, 1));
        add(scrollPane, BorderLayout.CENTER);

        // 3. South: Form
        JPanel formContainer = new JPanel(new BorderLayout(10, 10));
        formContainer.setBackground(Color.WHITE);
        formContainer.setBorder(UITheme.createCardBorder());

        JLabel formTitle = new JLabel("Mark Lecture Attendance");
        formTitle.setFont(UITheme.FONT_SUBHEADER);
        formTitle.setForeground(UITheme.TEXT_MAIN);
        formContainer.add(formTitle, BorderLayout.NORTH);

        JPanel gridFields = new JPanel(new GridLayout(2, 3, 12, 10));
        gridFields.setBackground(Color.WHITE);

        cbStudents = new JComboBox<>();
        cbCourses = new JComboBox<>();
        txtDate = UITheme.createTextField(10);
        txtDate.setText(LocalDate.now().toString()); // Today's date

        String[] statuses = {"PRESENT", "ABSENT", "LATE"};
        cbStatus = new JComboBox<>(statuses);

        txtRemarks = UITheme.createTextField(20);
        txtRemarks.setText("Regular class");

        gridFields.add(createLabeledField("Select Student *", cbStudents));
        gridFields.add(createLabeledField("Select Course *", cbCourses));
        gridFields.add(createLabeledField("Date (YYYY-MM-DD) *", txtDate));

        gridFields.add(createLabeledField("Attendance Status *", cbStatus));
        gridFields.add(createLabeledField("Session Remarks", txtRemarks));

        formContainer.add(gridFields, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonPanel.setBackground(Color.WHITE);

        btnCalcPercentage = UITheme.createSecondaryButton("Check Student %");
        btnCalcPercentage.addActionListener(this);

        btnRecord = UITheme.createPrimaryButton("Record Attendance");
        btnRecord.addActionListener(this);

        buttonPanel.add(btnCalcPercentage);
        buttonPanel.add(btnRecord);

        formContainer.add(buttonPanel, BorderLayout.SOUTH);
        add(formContainer, BorderLayout.SOUTH);
    }

    private JPanel createLabeledField(String labelText, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(3, 3));
        p.setBackground(Color.WHITE);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(UITheme.FONT_SMALL);
        lbl.setForeground(UITheme.TEXT_MUTED);
        p.add(lbl, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private void loadDropdowns() {
        cbStudents.removeAllItems();
        cbCourses.removeAllItems();

        try {
            List<Student> students = studentDAO.getAllStudents();
            for (Student s : students) {
                cbStudents.addItem(new StudentComboItem(s.getId(), s.getRollNumber() + " - " + s.getName()));
            }

            List<Course> courses = courseDAO.getAllCourses();
            for (Course c : courses) {
                cbCourses.addItem(new CourseComboItem(c.getCourseId(), c.getCourseCode() + " - " + c.getCourseName()));
            }
        } catch (SQLException ex) {
            System.err.println("[AttendancePanel] Error loading dropdown options: " + ex.getMessage());
        }
    }

    public void loadAttendanceData() {
        tableModel.setRowCount(0);
        try {
            List<Attendance> list = attendanceDAO.getAllAttendance();
            for (Attendance a : list) {
                tableModel.addRow(new Object[]{
                    a.getAttendanceId(),
                    a.getAttendanceDate(),
                    a.getStudentRollNumber(),
                    a.getStudentName(),
                    a.getCourseName(),
                    a.getStatus(),
                    a.getRemarks()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load attendance records:\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == btnRecord) {
            handleRecordAttendance();
        } else if (src == btnCalcPercentage) {
            handleCalculatePercentage();
        } else if (src == btnRefresh) {
            loadDropdowns();
            loadAttendanceData();
        }
    }

    private void handleRecordAttendance() {
        StudentComboItem student = (StudentComboItem) cbStudents.getSelectedItem();
        CourseComboItem course = (CourseComboItem) cbCourses.getSelectedItem();

        if (student == null || course == null) {
            JOptionPane.showMessageDialog(this, "Please select both a student and a course.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Date date = Date.valueOf(txtDate.getText().trim());
            Attendance att = new Attendance(
                student.getId(),
                course.getId(),
                date,
                (String) cbStatus.getSelectedItem(),
                txtRemarks.getText().trim()
            );

            int id = attendanceDAO.recordAttendance(att);
            if (id > 0) {
                JOptionPane.showMessageDialog(this,
                        "Attendance recorded successfully for " + student.toString() + "!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAttendanceData();
            }
        } catch (IllegalArgumentException dateEx) {
            JOptionPane.showMessageDialog(this, "Invalid Date. Format must be YYYY-MM-DD.", "Date Error", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error recording attendance:\n" + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleCalculatePercentage() {
        StudentComboItem student = (StudentComboItem) cbStudents.getSelectedItem();
        CourseComboItem course = (CourseComboItem) cbCourses.getSelectedItem();

        if (student == null || course == null) {
            JOptionPane.showMessageDialog(this, "Select a student and course to inspect attendance.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double pct = attendanceDAO.getAttendancePercentage(student.getId(), course.getId());
            String eligibility = (pct >= 75.0) ? "Eligible for End-Semester Exams (>=75%)" : "WARNING: Low Attendance (<75%)";
            Color color = (pct >= 75.0) ? UITheme.SUCCESS : UITheme.DANGER;

            JOptionPane.showMessageDialog(this,
                    String.format("Student: %s\nCourse: %s\nAttendance Rate: %.1f%%\n\nAcademic Status: %s",
                            student.toString(), course.toString(), pct, eligibility),
                    "Attendance Audit Report",
                    (pct >= 75.0) ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error computing attendance percentage: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
