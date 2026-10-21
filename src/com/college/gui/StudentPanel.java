package com.college.gui;

import com.college.dao.StudentDAO;
import com.college.model.Student;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

/**
 * Student Management Panel.
 * Demonstrates:
 * 1. JTable data presentation with DefaultTableModel.
 * 2. Form controls for Add, Update, Delete, and Clear operations.
 * 3. Exception-safe JDBC DAO integration with JOptionPane error dialogs.
 */
public class StudentPanel extends JPanel implements ActionListener {

    private final StudentDAO studentDAO;

    // Table components
    private JTable studentTable;
    private DefaultTableModel tableModel;

    // Form input fields
    private JTextField txtId;
    private JTextField txtRollNo;
    private JTextField txtName;
    private JTextField txtEmail;
    private JTextField txtPhone;
    private JComboBox<String> cbDepartment;
    private JComboBox<Integer> cbSemester;
    private JTextField txtDob;
    private JTextField txtAddress;

    // Action buttons
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;
    private JButton btnRefresh;

    private static final String[] DEPARTMENTS = {
        "Computer Science", "Electronics", "Information Technology", "Mechanical", "Civil"
    };

    public StudentPanel() {
        this.studentDAO = new StudentDAO();
        initializeUI();
        loadStudentData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.BG_LIGHT);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // 1. Top Control / Title Bar
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(UITheme.createCardBorder());

        JLabel title = new JLabel("Student Information Management");
        title.setFont(UITheme.FONT_HEADER);
        title.setForeground(UITheme.PRIMARY);

        btnRefresh = UITheme.createSecondaryButton("Reload Table");
        btnRefresh.addActionListener(this);

        topPanel.add(title, BorderLayout.WEST);
        topPanel.add(btnRefresh, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // 2. Center: JTable displaying Student records
        String[] columnNames = {
            "ID", "Roll Number", "Full Name", "Email", "Phone", "Department", "Semester", "DOB", "Address"
        };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only cells; edited via form below
            }
        };

        studentTable = new JTable(tableModel);
        studentTable.setFont(UITheme.FONT_BODY);
        studentTable.setRowHeight(28);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.getTableHeader().setFont(UITheme.FONT_SUBHEADER);
        studentTable.getTableHeader().setBackground(UITheme.PRIMARY);
        studentTable.getTableHeader().setForeground(Color.WHITE);

        // Center align ID, Roll No, and Semester
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        studentTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        studentTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        studentTable.getColumnModel().getColumn(1).setPreferredWidth(90);
        studentTable.getColumnModel().getColumn(6).setPreferredWidth(60);
        studentTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);

        studentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                populateFormFromSelectedRow();
            }
        });

        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(new LineBorder(UITheme.BORDER_COLOR, 1));
        add(scrollPane, BorderLayout.CENTER);

        // 3. South: Form to Add/Edit/Delete
        JPanel formContainer = new JPanel(new BorderLayout(10, 10));
        formContainer.setBackground(Color.WHITE);
        formContainer.setBorder(UITheme.createCardBorder());

        JLabel formTitle = new JLabel("Student Record Form (Select a row to edit or fill fields to add)");
        formTitle.setFont(UITheme.FONT_SUBHEADER);
        formTitle.setForeground(UITheme.TEXT_MAIN);
        formContainer.add(formTitle, BorderLayout.NORTH);

        // Form fields grid (2 rows x 4 columns)
        JPanel gridFields = new JPanel(new GridLayout(2, 4, 12, 10));
        gridFields.setBackground(Color.WHITE);

        txtId = UITheme.createTextField(5);
        txtId.setEditable(false);
        txtId.setBackground(new Color(240, 240, 240));

        txtRollNo = UITheme.createTextField(10);
        txtName = UITheme.createTextField(15);
        txtEmail = UITheme.createTextField(15);
        txtPhone = UITheme.createTextField(10);
        cbDepartment = new JComboBox<>(DEPARTMENTS);
        cbDepartment.setFont(UITheme.FONT_BODY);

        Integer[] semesters = {1, 2, 3, 4, 5, 6, 7, 8};
        cbSemester = new JComboBox<>(semesters);
        cbSemester.setFont(UITheme.FONT_BODY);

        txtDob = UITheme.createTextField(10);
        txtDob.setToolTipText("YYYY-MM-DD");
        txtAddress = UITheme.createTextField(20);

        gridFields.add(createLabeledField("Roll Number *", txtRollNo));
        gridFields.add(createLabeledField("Full Name *", txtName));
        gridFields.add(createLabeledField("Email Address *", txtEmail));
        gridFields.add(createLabeledField("Phone *", txtPhone));

        gridFields.add(createLabeledField("Department", cbDepartment));
        gridFields.add(createLabeledField("Semester", cbSemester));
        gridFields.add(createLabeledField("Date of Birth (YYYY-MM-DD)", txtDob));
        gridFields.add(createLabeledField("Residential Address", txtAddress));

        formContainer.add(gridFields, BorderLayout.CENTER);

        // Buttons Bar
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonPanel.setBackground(Color.WHITE);

        btnClear = UITheme.createSecondaryButton("Clear Form");
        btnClear.addActionListener(this);

        btnDelete = UITheme.createDangerButton("Delete Student");
        btnDelete.addActionListener(this);

        btnUpdate = UITheme.createSecondaryButton("Update Student");
        btnUpdate.setBackground(new Color(226, 232, 240));
        btnUpdate.addActionListener(this);

        btnAdd = UITheme.createPrimaryButton("Add Student");
        btnAdd.addActionListener(this);

        buttonPanel.add(btnClear);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnAdd);

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

    /**
     * Loads student records from database into JTable using StudentDAO.
     */
    public void loadStudentData() {
        tableModel.setRowCount(0);
        try {
            List<Student> students = studentDAO.getAllStudents();
            for (Student s : students) {
                tableModel.addRow(new Object[]{
                    s.getId(),
                    s.getRollNumber(),
                    s.getName(),
                    s.getEmail(),
                    s.getPhone(),
                    s.getDepartment(),
                    s.getSemester(),
                    s.getDob(),
                    s.getAddress()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load students from database:\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateFormFromSelectedRow() {
        int row = studentTable.getSelectedRow();
        if (row != -1) {
            txtId.setText(tableModel.getValueAt(row, 0).toString());
            txtRollNo.setText(tableModel.getValueAt(row, 1).toString());
            txtName.setText(tableModel.getValueAt(row, 2).toString());
            txtEmail.setText(tableModel.getValueAt(row, 3).toString());
            txtPhone.setText(tableModel.getValueAt(row, 4).toString());
            cbDepartment.setSelectedItem(tableModel.getValueAt(row, 5).toString());
            cbSemester.setSelectedItem(Integer.parseInt(tableModel.getValueAt(row, 6).toString()));
            txtDob.setText(tableModel.getValueAt(row, 7) != null ? tableModel.getValueAt(row, 7).toString() : "");
            txtAddress.setText(tableModel.getValueAt(row, 8) != null ? tableModel.getValueAt(row, 8).toString() : "");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == btnAdd) {
            handleAddStudent();
        } else if (src == btnUpdate) {
            handleUpdateStudent();
        } else if (src == btnDelete) {
            handleDeleteStudent();
        } else if (src == btnClear) {
            clearForm();
        } else if (src == btnRefresh) {
            loadStudentData();
        }
    }

    private void handleAddStudent() {
        if (!validateInputs()) return;

        try {
            Date dob = Date.valueOf(txtDob.getText().trim());
            Student student = new Student(
                txtRollNo.getText().trim(),
                txtName.getText().trim(),
                txtEmail.getText().trim(),
                txtPhone.getText().trim(),
                (String) cbDepartment.getSelectedItem(),
                (Integer) cbSemester.getSelectedItem(),
                dob,
                txtAddress.getText().trim()
            );

            int generatedId = studentDAO.addStudent(student);
            if (generatedId > 0) {
                JOptionPane.showMessageDialog(this, "Student registered successfully! Generated ID: " + generatedId,
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadStudentData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Could not insert student record.", "Insertion Error", JOptionPane.WARNING_MESSAGE);
            }
        } catch (IllegalArgumentException dateEx) {
            JOptionPane.showMessageDialog(this, "Date of Birth must be formatted as YYYY-MM-DD (e.g. 2004-05-14).",
                    "Invalid Date Format", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error adding student:\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdateStudent() {
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a student from the table to update.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validateInputs()) return;

        try {
            int id = Integer.parseInt(txtId.getText().trim());
            Date dob = Date.valueOf(txtDob.getText().trim());
            Student student = new Student(
                id,
                txtRollNo.getText().trim(),
                txtName.getText().trim(),
                txtEmail.getText().trim(),
                txtPhone.getText().trim(),
                (String) cbDepartment.getSelectedItem(),
                (Integer) cbSemester.getSelectedItem(),
                dob,
                txtAddress.getText().trim()
            );

            boolean updated = studentDAO.updateStudent(student);
            if (updated) {
                JOptionPane.showMessageDialog(this, "Student record updated successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadStudentData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Record not found or no changes made.", "Update Notice", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IllegalArgumentException dateEx) {
            JOptionPane.showMessageDialog(this, "Date of Birth must be formatted as YYYY-MM-DD.",
                    "Invalid Date Format", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error updating student:\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteStudent() {
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a student from the table to delete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(txtId.getText().trim());
        String name = txtName.getText().trim();
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete student: " + name + " (ID: " + id + ")?\n" +
                "All associated attendance, exams, and fees will also be removed.",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean deleted = studentDAO.deleteStudent(id);
                if (deleted) {
                    JOptionPane.showMessageDialog(this, "Student deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadStudentData();
                    clearForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete student.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error deleting student:\n" + ex.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validateInputs() {
        if (txtRollNo.getText().trim().isEmpty() || txtName.getText().trim().isEmpty() ||
            txtEmail.getText().trim().isEmpty() || txtPhone.getText().trim().isEmpty() ||
            txtDob.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Roll Number, Name, Email, Phone, and DOB are required fields.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void clearForm() {
        txtId.setText("");
        txtRollNo.setText("");
        txtName.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        cbDepartment.setSelectedIndex(0);
        cbSemester.setSelectedIndex(0);
        txtDob.setText("");
        txtAddress.setText("");
        studentTable.clearSelection();
    }
}
