package com.college.gui;

import com.college.dao.FacultyDAO;
import com.college.model.Faculty;

import javax.swing.*;
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
 * Faculty Management Panel.
 * Demonstrates:
 * 1. JTable data rendering.
 * 2. CRUD operation forms (Add, Edit, Remove).
 * 3. Exception-safe JDBC DAO integration.
 */
public class FacultyPanel extends JPanel implements ActionListener {

    private final FacultyDAO facultyDAO;

    private JTable facultyTable;
    private DefaultTableModel tableModel;

    private JTextField txtId;
    private JTextField txtName;
    private JTextField txtEmail;
    private JTextField txtPhone;
    private JComboBox<String> cbDepartment;
    private JComboBox<String> cbDesignation;
    private JTextField txtSalary;
    private JTextField txtHireDate;

    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;
    private JButton btnRefresh;

    private static final String[] DEPARTMENTS = {
        "Computer Science", "Electronics", "Information Technology", "Mechanical", "Civil"
    };

    private static final String[] DESIGNATIONS = {
        "Professor", "Associate Professor", "Assistant Professor", "Lecturer", "Head of Department"
    };

    public FacultyPanel() {
        this.facultyDAO = new FacultyDAO();
        initializeUI();
        loadFacultyData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.BG_LIGHT);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // 1. Header Bar
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(UITheme.createCardBorder());

        JLabel title = new JLabel("Faculty Directory & Staff Management");
        title.setFont(UITheme.FONT_HEADER);
        title.setForeground(UITheme.PRIMARY);

        btnRefresh = UITheme.createSecondaryButton("Reload Table");
        btnRefresh.addActionListener(this);

        topPanel.add(title, BorderLayout.WEST);
        topPanel.add(btnRefresh, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // 2. Center: JTable
        String[] columnNames = {
            "ID", "Full Name", "Email", "Phone", "Department", "Designation", "Salary (INR)", "Hire Date"
        };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        facultyTable = new JTable(tableModel);
        facultyTable.setFont(UITheme.FONT_BODY);
        facultyTable.setRowHeight(28);
        facultyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        facultyTable.getTableHeader().setFont(UITheme.FONT_SUBHEADER);
        facultyTable.getTableHeader().setBackground(UITheme.PRIMARY);
        facultyTable.getTableHeader().setForeground(Color.WHITE);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        facultyTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        facultyTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

        facultyTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                populateForm();
            }
        });

        JScrollPane scrollPane = new JScrollPane(facultyTable);
        scrollPane.setBorder(new LineBorder(UITheme.BORDER_COLOR, 1));
        add(scrollPane, BorderLayout.CENTER);

        // 3. South: Form
        JPanel formContainer = new JPanel(new BorderLayout(10, 10));
        formContainer.setBackground(Color.WHITE);
        formContainer.setBorder(UITheme.createCardBorder());

        JLabel formTitle = new JLabel("Faculty Details Form");
        formTitle.setFont(UITheme.FONT_SUBHEADER);
        formTitle.setForeground(UITheme.TEXT_MAIN);
        formContainer.add(formTitle, BorderLayout.NORTH);

        JPanel gridFields = new JPanel(new GridLayout(2, 4, 12, 10));
        gridFields.setBackground(Color.WHITE);

        txtId = UITheme.createTextField(5);
        txtId.setEditable(false);
        txtId.setBackground(new Color(240, 240, 240));

        txtName = UITheme.createTextField(15);
        txtEmail = UITheme.createTextField(15);
        txtPhone = UITheme.createTextField(10);
        cbDepartment = new JComboBox<>(DEPARTMENTS);
        cbDepartment.setFont(UITheme.FONT_BODY);
        cbDesignation = new JComboBox<>(DESIGNATIONS);
        cbDesignation.setFont(UITheme.FONT_BODY);
        txtSalary = UITheme.createTextField(10);
        txtHireDate = UITheme.createTextField(10);
        txtHireDate.setToolTipText("YYYY-MM-DD");

        gridFields.add(createLabeledField("Faculty Full Name *", txtName));
        gridFields.add(createLabeledField("Email Address *", txtEmail));
        gridFields.add(createLabeledField("Contact Phone *", txtPhone));
        gridFields.add(createLabeledField("Academic Department", cbDepartment));

        gridFields.add(createLabeledField("Academic Designation", cbDesignation));
        gridFields.add(createLabeledField("Monthly Salary (INR) *", txtSalary));
        gridFields.add(createLabeledField("Date of Joining (YYYY-MM-DD) *", txtHireDate));
        gridFields.add(createLabeledField("Faculty Record ID", txtId));

        formContainer.add(gridFields, BorderLayout.CENTER);

        // Action Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonPanel.setBackground(Color.WHITE);

        btnClear = UITheme.createSecondaryButton("Clear Form");
        btnClear.addActionListener(this);

        btnDelete = UITheme.createDangerButton("Delete Faculty");
        btnDelete.addActionListener(this);

        btnUpdate = UITheme.createSecondaryButton("Update Faculty");
        btnUpdate.setBackground(new Color(226, 232, 240));
        btnUpdate.addActionListener(this);

        btnAdd = UITheme.createPrimaryButton("Add Faculty");
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

    public void loadFacultyData() {
        tableModel.setRowCount(0);
        try {
            List<Faculty> list = facultyDAO.getAllFaculty();
            for (Faculty f : list) {
                tableModel.addRow(new Object[]{
                    f.getId(),
                    f.getName(),
                    f.getEmail(),
                    f.getPhone(),
                    f.getDepartment(),
                    f.getDesignation(),
                    String.format("%.2f", f.getSalary()),
                    f.getHireDate()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load faculty from database:\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateForm() {
        int row = facultyTable.getSelectedRow();
        if (row != -1) {
            txtId.setText(tableModel.getValueAt(row, 0).toString());
            txtName.setText(tableModel.getValueAt(row, 1).toString());
            txtEmail.setText(tableModel.getValueAt(row, 2).toString());
            txtPhone.setText(tableModel.getValueAt(row, 3).toString());
            cbDepartment.setSelectedItem(tableModel.getValueAt(row, 4).toString());
            cbDesignation.setSelectedItem(tableModel.getValueAt(row, 5).toString());
            txtSalary.setText(tableModel.getValueAt(row, 6).toString());
            txtHireDate.setText(tableModel.getValueAt(row, 7).toString());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == btnAdd) {
            handleAdd();
        } else if (src == btnUpdate) {
            handleUpdate();
        } else if (src == btnDelete) {
            handleDelete();
        } else if (src == btnClear) {
            clearForm();
        } else if (src == btnRefresh) {
            loadFacultyData();
        }
    }

    private void handleAdd() {
        if (!validateInputs()) return;
        try {
            double salary = Double.parseDouble(txtSalary.getText().trim());
            Date hireDate = Date.valueOf(txtHireDate.getText().trim());

            Faculty f = new Faculty(
                txtName.getText().trim(),
                txtEmail.getText().trim(),
                txtPhone.getText().trim(),
                (String) cbDepartment.getSelectedItem(),
                (String) cbDesignation.getSelectedItem(),
                salary,
                hireDate
            );

            int id = facultyDAO.addFaculty(f);
            if (id > 0) {
                JOptionPane.showMessageDialog(this, "Faculty member added successfully! ID: " + id,
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadFacultyData();
                clearForm();
            }
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Salary must be a valid numeric amount.", "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException dateEx) {
            JOptionPane.showMessageDialog(this, "Hire Date must follow YYYY-MM-DD format.", "Invalid Date", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error adding faculty:\n" + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdate() {
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a faculty record to update.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validateInputs()) return;
        try {
            int id = Integer.parseInt(txtId.getText().trim());
            double salary = Double.parseDouble(txtSalary.getText().trim());
            Date hireDate = Date.valueOf(txtHireDate.getText().trim());

            Faculty f = new Faculty(
                id,
                txtName.getText().trim(),
                txtEmail.getText().trim(),
                txtPhone.getText().trim(),
                (String) cbDepartment.getSelectedItem(),
                (String) cbDesignation.getSelectedItem(),
                salary,
                hireDate
            );

            if (facultyDAO.updateFaculty(f)) {
                JOptionPane.showMessageDialog(this, "Faculty record updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadFacultyData();
                clearForm();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating faculty: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete() {
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a faculty record to delete.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = Integer.parseInt(txtId.getText().trim());
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete faculty ID " + id + "?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (facultyDAO.deleteFaculty(id)) {
                    JOptionPane.showMessageDialog(this, "Faculty deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadFacultyData();
                    clearForm();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting faculty:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validateInputs() {
        if (txtName.getText().trim().isEmpty() || txtEmail.getText().trim().isEmpty() ||
            txtPhone.getText().trim().isEmpty() || txtSalary.getText().trim().isEmpty() ||
            txtHireDate.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields marked with * are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void clearForm() {
        txtId.setText("");
        txtName.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        cbDepartment.setSelectedIndex(0);
        cbDesignation.setSelectedIndex(0);
        txtSalary.setText("");
        txtHireDate.setText("");
        facultyTable.clearSelection();
    }
}
