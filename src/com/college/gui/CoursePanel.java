package com.college.gui;

import com.college.dao.CourseDAO;
import com.college.dao.FacultyDAO;
import com.college.model.Course;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Course Management Panel.
 * Demonstrates:
 * 1. JTable presentation of catalog data with faculty relation.
 * 2. Dynamic JComboBox population from relational entities.
 * 3. Course CRUD operations.
 */
public class CoursePanel extends JPanel implements ActionListener {

    private final CourseDAO courseDAO;
    private final FacultyDAO facultyDAO;

    private JTable courseTable;
    private DefaultTableModel tableModel;

    private JTextField txtId;
    private JTextField txtCourseCode;
    private JTextField txtCourseName;
    private JComboBox<Integer> cbCredits;
    private JComboBox<String> cbDepartment;
    private JComboBox<FacultyItem> cbFaculty;

    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;
    private JButton btnRefresh;

    private static final String[] DEPARTMENTS = {
        "Computer Science", "Electronics", "Information Technology", "Mechanical", "Civil"
    };

    // Helper holder class for JComboBox faculty selection
    public static class FacultyItem {
        private final int id;
        private final String name;

        public FacultyItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() { return id; }

        @Override
        public String toString() { return name; }
    }

    public CoursePanel() {
        this.courseDAO = new CourseDAO();
        this.facultyDAO = new FacultyDAO();
        initializeUI();
        loadFacultyDropdown();
        loadCourseData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.BG_LIGHT);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // 1. Header Bar
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(UITheme.createCardBorder());

        JLabel title = new JLabel("Curriculum & Course Catalog Management");
        title.setFont(UITheme.FONT_HEADER);
        title.setForeground(UITheme.PRIMARY);

        btnRefresh = UITheme.createSecondaryButton("Reload Table");
        btnRefresh.addActionListener(this);

        topPanel.add(title, BorderLayout.WEST);
        topPanel.add(btnRefresh, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // 2. Center: JTable
        String[] columnNames = {
            "ID", "Course Code", "Course Name", "Credits", "Department", "Assigned Faculty"
        };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        courseTable = new JTable(tableModel);
        courseTable.setFont(UITheme.FONT_BODY);
        courseTable.setRowHeight(28);
        courseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        courseTable.getTableHeader().setFont(UITheme.FONT_SUBHEADER);
        courseTable.getTableHeader().setBackground(UITheme.PRIMARY);
        courseTable.getTableHeader().setForeground(Color.WHITE);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        courseTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        courseTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        courseTable.getColumnModel().getColumn(1).setPreferredWidth(90);
        courseTable.getColumnModel().getColumn(3).setPreferredWidth(60);
        courseTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        courseTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                populateForm();
            }
        });

        JScrollPane scrollPane = new JScrollPane(courseTable);
        scrollPane.setBorder(new LineBorder(UITheme.BORDER_COLOR, 1));
        add(scrollPane, BorderLayout.CENTER);

        // 3. South: Form
        JPanel formContainer = new JPanel(new BorderLayout(10, 10));
        formContainer.setBackground(Color.WHITE);
        formContainer.setBorder(UITheme.createCardBorder());

        JLabel formTitle = new JLabel("Course Specification Form");
        formTitle.setFont(UITheme.FONT_SUBHEADER);
        formTitle.setForeground(UITheme.TEXT_MAIN);
        formContainer.add(formTitle, BorderLayout.NORTH);

        JPanel gridFields = new JPanel(new GridLayout(2, 3, 14, 10));
        gridFields.setBackground(Color.WHITE);

        txtId = UITheme.createTextField(5);
        txtId.setEditable(false);
        txtId.setBackground(new Color(240, 240, 240));

        txtCourseCode = UITheme.createTextField(10);
        txtCourseName = UITheme.createTextField(20);

        Integer[] creditOptions = {1, 2, 3, 4, 5, 6};
        cbCredits = new JComboBox<>(creditOptions);
        cbCredits.setSelectedItem(4);

        cbDepartment = new JComboBox<>(DEPARTMENTS);
        cbFaculty = new JComboBox<>();

        gridFields.add(createLabeledField("Course Code (e.g. CS401) *", txtCourseCode));
        gridFields.add(createLabeledField("Course Title *", txtCourseName));
        gridFields.add(createLabeledField("Academic Credits", cbCredits));

        gridFields.add(createLabeledField("Department", cbDepartment));
        gridFields.add(createLabeledField("Assigned Instructor", cbFaculty));
        gridFields.add(createLabeledField("Course ID", txtId));

        formContainer.add(gridFields, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonPanel.setBackground(Color.WHITE);

        btnClear = UITheme.createSecondaryButton("Clear Form");
        btnClear.addActionListener(this);

        btnDelete = UITheme.createDangerButton("Delete Course");
        btnDelete.addActionListener(this);

        btnUpdate = UITheme.createSecondaryButton("Update Course");
        btnUpdate.setBackground(new Color(226, 232, 240));
        btnUpdate.addActionListener(this);

        btnAdd = UITheme.createPrimaryButton("Add Course");
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

    private void loadFacultyDropdown() {
        cbFaculty.removeAllItems();
        cbFaculty.addItem(new FacultyItem(0, "-- Unassigned --"));
        try {
            List<Faculty> faculties = facultyDAO.getAllFaculty();
            for (Faculty f : faculties) {
                cbFaculty.addItem(new FacultyItem(f.getId(), f.getName() + " (" + f.getDepartment() + ")"));
            }
        } catch (SQLException ex) {
            System.err.println("[CoursePanel] Error loading faculty list: " + ex.getMessage());
        }
    }

    public void loadCourseData() {
        tableModel.setRowCount(0);
        try {
            List<Course> list = courseDAO.getAllCourses();
            for (Course c : list) {
                tableModel.addRow(new Object[]{
                    c.getCourseId(),
                    c.getCourseCode(),
                    c.getCourseName(),
                    c.getCredits(),
                    c.getDepartment(),
                    c.getFacultyName()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load courses from database:\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateForm() {
        int row = courseTable.getSelectedRow();
        if (row != -1) {
            txtId.setText(tableModel.getValueAt(row, 0).toString());
            txtCourseCode.setText(tableModel.getValueAt(row, 1).toString());
            txtCourseName.setText(tableModel.getValueAt(row, 2).toString());
            cbCredits.setSelectedItem(Integer.parseInt(tableModel.getValueAt(row, 3).toString()));
            cbDepartment.setSelectedItem(tableModel.getValueAt(row, 4).toString());

            String facultyName = tableModel.getValueAt(row, 5).toString();
            for (int i = 0; i < cbFaculty.getItemCount(); i++) {
                if (cbFaculty.getItemAt(i).toString().startsWith(facultyName)) {
                    cbFaculty.setSelectedIndex(i);
                    break;
                }
            }
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
            loadFacultyDropdown();
            loadCourseData();
        }
    }

    private void handleAdd() {
        if (!validateInputs()) return;
        try {
            FacultyItem selectedFaculty = (FacultyItem) cbFaculty.getSelectedItem();
            int facultyId = (selectedFaculty != null) ? selectedFaculty.getId() : 0;

            Course course = new Course(
                txtCourseCode.getText().trim(),
                txtCourseName.getText().trim(),
                (Integer) cbCredits.getSelectedItem(),
                (String) cbDepartment.getSelectedItem(),
                facultyId
            );

            int id = courseDAO.addCourse(course);
            if (id > 0) {
                JOptionPane.showMessageDialog(this, "Course registered successfully! ID: " + id,
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadCourseData();
                clearForm();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error adding course:\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdate() {
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a course from table to update.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validateInputs()) return;

        try {
            int id = Integer.parseInt(txtId.getText().trim());
            FacultyItem selectedFaculty = (FacultyItem) cbFaculty.getSelectedItem();
            int facultyId = (selectedFaculty != null) ? selectedFaculty.getId() : 0;

            Course course = new Course(
                id,
                txtCourseCode.getText().trim(),
                txtCourseName.getText().trim(),
                (Integer) cbCredits.getSelectedItem(),
                (String) cbDepartment.getSelectedItem(),
                facultyId
            );

            if (courseDAO.updateCourse(course)) {
                JOptionPane.showMessageDialog(this, "Course updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadCourseData();
                clearForm();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error updating course:\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete() {
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a course to delete.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = Integer.parseInt(txtId.getText().trim());
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete course ID " + id + "?\nAll attendance and exam records for this course will be deleted.",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (courseDAO.deleteCourse(id)) {
                    JOptionPane.showMessageDialog(this, "Course deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadCourseData();
                    clearForm();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting course:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validateInputs() {
        if (txtCourseCode.getText().trim().isEmpty() || txtCourseName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Course Code and Course Title are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void clearForm() {
        txtId.setText("");
        txtCourseCode.setText("");
        txtCourseName.setText("");
        cbCredits.setSelectedItem(4);
        cbDepartment.setSelectedIndex(0);
        cbFaculty.setSelectedIndex(0);
        courseTable.clearSelection();
    }
}
