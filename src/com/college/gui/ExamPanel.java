package com.college.gui;

import com.college.dao.CourseDAO;
import com.college.dao.ExamDAO;
import com.college.dao.StudentDAO;
import com.college.model.Course;
import com.college.model.Exam;
import com.college.model.Student;

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
import java.time.LocalDate;
import java.util.List;

/**
 * Examination & Marks Grading Module.
 * Demonstrates:
 * 1. Interface for recording marks and automatic grade computation.
 * 2. Assessment history with JTable and DefaultTableModel.
 * 3. Exam CRUD operations.
 */
public class ExamPanel extends JPanel implements ActionListener {

    private final ExamDAO examDAO;
    private final CourseDAO courseDAO;
    private final StudentDAO studentDAO;

    private JTable examTable;
    private DefaultTableModel tableModel;

    private JTextField txtId;
    private JTextField txtExamName;
    private JComboBox<CourseItem> cbCourse;
    private JComboBox<StudentItem> cbStudent;
    private JTextField txtMaxMarks;
    private JTextField txtMarksObtained;
    private JTextField txtGrade;
    private JTextField txtExamDate;

    private JButton btnRecord;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;
    private JButton btnRefresh;

    public static class CourseItem {
        private final int id;
        private final String title;
        public CourseItem(int id, String title) { this.id = id; this.title = title; }
        public int getId() { return id; }
        @Override public String toString() { return title; }
    }

    public static class StudentItem {
        private final int id;
        private final String title;
        public StudentItem(int id, String title) { this.id = id; this.title = title; }
        public int getId() { return id; }
        @Override public String toString() { return title; }
    }

    public ExamPanel() {
        this.examDAO = new ExamDAO();
        this.courseDAO = new CourseDAO();
        this.studentDAO = new StudentDAO();
        initializeUI();
        loadDropdowns();
        loadExamData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.BG_LIGHT);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // 1. Header Bar
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(UITheme.createCardBorder());

        JLabel title = new JLabel("Examinations & Academic Grading Management");
        title.setFont(UITheme.FONT_HEADER);
        title.setForeground(UITheme.PRIMARY);

        btnRefresh = UITheme.createSecondaryButton("Reload Records");
        btnRefresh.addActionListener(this);

        topPanel.add(title, BorderLayout.WEST);
        topPanel.add(btnRefresh, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // 2. Center: JTable
        String[] columnNames = {
            "ID", "Exam Title", "Course", "Roll Number", "Student Name", "Max Marks", "Marks Scored", "Grade", "Date"
        };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        examTable = new JTable(tableModel);
        examTable.setFont(UITheme.FONT_BODY);
        examTable.setRowHeight(28);
        examTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        examTable.getTableHeader().setFont(UITheme.FONT_SUBHEADER);
        examTable.getTableHeader().setBackground(UITheme.PRIMARY);
        examTable.getTableHeader().setForeground(Color.WHITE);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        examTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        examTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        examTable.getColumnModel().getColumn(5).setPreferredWidth(70);
        examTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        examTable.getColumnModel().getColumn(6).setPreferredWidth(80);
        examTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
        examTable.getColumnModel().getColumn(7).setPreferredWidth(50);
        examTable.getColumnModel().getColumn(7).setCellRenderer(centerRenderer);

        examTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                populateForm();
            }
        });

        JScrollPane scrollPane = new JScrollPane(examTable);
        scrollPane.setBorder(new LineBorder(UITheme.BORDER_COLOR, 1));
        add(scrollPane, BorderLayout.CENTER);

        // 3. South: Form
        JPanel formContainer = new JPanel(new BorderLayout(10, 10));
        formContainer.setBackground(Color.WHITE);
        formContainer.setBorder(UITheme.createCardBorder());

        JLabel formTitle = new JLabel("Exam Evaluation Entry Form");
        formTitle.setFont(UITheme.FONT_SUBHEADER);
        formTitle.setForeground(UITheme.TEXT_MAIN);
        formContainer.add(formTitle, BorderLayout.NORTH);

        JPanel gridFields = new JPanel(new GridLayout(2, 4, 12, 10));
        gridFields.setBackground(Color.WHITE);

        txtId = UITheme.createTextField(5);
        txtId.setEditable(false);
        txtId.setBackground(new Color(240, 240, 240));

        txtExamName = UITheme.createTextField(15);
        txtExamName.setText("Midterm Assessment");

        cbCourse = new JComboBox<>();
        cbStudent = new JComboBox<>();

        txtMaxMarks = UITheme.createTextField(8);
        txtMaxMarks.setText("100.0");

        txtMarksObtained = UITheme.createTextField(8);

        txtGrade = UITheme.createTextField(5);
        txtGrade.setEditable(false);
        txtGrade.setBackground(new Color(240, 240, 240));

        txtExamDate = UITheme.createTextField(10);
        txtExamDate.setText(LocalDate.now().toString());

        gridFields.add(createLabeledField("Exam Title (e.g. Midterm) *", txtExamName));
        gridFields.add(createLabeledField("Select Course *", cbCourse));
        gridFields.add(createLabeledField("Select Student *", cbStudent));
        gridFields.add(createLabeledField("Max Marks *", txtMaxMarks));

        gridFields.add(createLabeledField("Marks Obtained *", txtMarksObtained));
        gridFields.add(createLabeledField("Computed Letter Grade", txtGrade));
        gridFields.add(createLabeledField("Exam Date (YYYY-MM-DD) *", txtExamDate));
        gridFields.add(createLabeledField("Exam Record ID", txtId));

        formContainer.add(gridFields, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonPanel.setBackground(Color.WHITE);

        btnClear = UITheme.createSecondaryButton("Clear Form");
        btnClear.addActionListener(this);

        btnDelete = UITheme.createDangerButton("Delete Exam");
        btnDelete.addActionListener(this);

        btnUpdate = UITheme.createSecondaryButton("Update Marks");
        btnUpdate.setBackground(new Color(226, 232, 240));
        btnUpdate.addActionListener(this);

        btnRecord = UITheme.createPrimaryButton("Record Marks");
        btnRecord.addActionListener(this);

        buttonPanel.add(btnClear);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnUpdate);
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
        cbCourse.removeAllItems();
        cbStudent.removeAllItems();
        try {
            List<Course> courses = courseDAO.getAllCourses();
            for (Course c : courses) {
                cbCourse.addItem(new CourseItem(c.getCourseId(), c.getCourseCode() + " - " + c.getCourseName()));
            }

            List<Student> students = studentDAO.getAllStudents();
            for (Student s : students) {
                cbStudent.addItem(new StudentItem(s.getId(), s.getRollNumber() + " - " + s.getName()));
            }
        } catch (SQLException ex) {
            System.err.println("[ExamPanel] Error loading dropdown options: " + ex.getMessage());
        }
    }

    public void loadExamData() {
        tableModel.setRowCount(0);
        try {
            List<Exam> exams = examDAO.getAllExams();
            for (Exam e : exams) {
                tableModel.addRow(new Object[]{
                    e.getExamId(),
                    e.getExamName(),
                    e.getCourseName(),
                    e.getStudentRollNumber(),
                    e.getStudentName(),
                    e.getMaxMarks(),
                    e.getMarksObtained(),
                    e.getGrade(),
                    e.getExamDate()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load exam records from database:\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateForm() {
        int row = examTable.getSelectedRow();
        if (row != -1) {
            txtId.setText(tableModel.getValueAt(row, 0).toString());
            txtExamName.setText(tableModel.getValueAt(row, 1).toString());
            txtMaxMarks.setText(tableModel.getValueAt(row, 5).toString());
            txtMarksObtained.setText(tableModel.getValueAt(row, 6).toString());
            txtGrade.setText(tableModel.getValueAt(row, 7).toString());
            txtExamDate.setText(tableModel.getValueAt(row, 8).toString());

            String courseName = tableModel.getValueAt(row, 2).toString();
            for (int i = 0; i < cbCourse.getItemCount(); i++) {
                if (cbCourse.getItemAt(i).toString().contains(courseName)) {
                    cbCourse.setSelectedIndex(i);
                    break;
                }
            }

            String rollNo = tableModel.getValueAt(row, 3).toString();
            for (int i = 0; i < cbStudent.getItemCount(); i++) {
                if (cbStudent.getItemAt(i).toString().contains(rollNo)) {
                    cbStudent.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == btnRecord) {
            handleRecordExam();
        } else if (src == btnUpdate) {
            handleUpdateExam();
        } else if (src == btnDelete) {
            handleDeleteExam();
        } else if (src == btnClear) {
            clearForm();
        } else if (src == btnRefresh) {
            loadDropdowns();
            loadExamData();
        }
    }

    private void handleRecordExam() {
        if (!validateInputs()) return;

        try {
            CourseItem course = (CourseItem) cbCourse.getSelectedItem();
            StudentItem student = (StudentItem) cbStudent.getSelectedItem();
            double maxMarks = Double.parseDouble(txtMaxMarks.getText().trim());
            double marksObtained = Double.parseDouble(txtMarksObtained.getText().trim());

            if (marksObtained < 0 || marksObtained > maxMarks) {
                JOptionPane.showMessageDialog(this, "Marks obtained must be between 0 and Max Marks (" + maxMarks + ").",
                        "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String grade = Exam.computeGrade(marksObtained, maxMarks);
            txtGrade.setText(grade);
            Date examDate = Date.valueOf(txtExamDate.getText().trim());

            Exam exam = new Exam(
                txtExamName.getText().trim(),
                course.getId(),
                student.getId(),
                maxMarks,
                marksObtained,
                examDate
            );
            exam.setGrade(grade);

            int id = examDAO.recordExam(exam);
            if (id > 0) {
                JOptionPane.showMessageDialog(this,
                        String.format("Exam marks recorded! Generated Grade: %s (ID: %d)", grade, id),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadExamData();
                clearForm();
            }
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Marks must be numeric values.", "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException dateEx) {
            JOptionPane.showMessageDialog(this, "Date must follow YYYY-MM-DD format.", "Date Error", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error recording exam:\n" + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdateExam() {
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select an exam record from the table to update.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validateInputs()) return;

        try {
            int id = Integer.parseInt(txtId.getText().trim());
            CourseItem course = (CourseItem) cbCourse.getSelectedItem();
            StudentItem student = (StudentItem) cbStudent.getSelectedItem();
            double maxMarks = Double.parseDouble(txtMaxMarks.getText().trim());
            double marksObtained = Double.parseDouble(txtMarksObtained.getText().trim());

            String grade = Exam.computeGrade(marksObtained, maxMarks);
            txtGrade.setText(grade);
            Date examDate = Date.valueOf(txtExamDate.getText().trim());

            Exam exam = new Exam(
                id,
                txtExamName.getText().trim(),
                course.getId(),
                null,
                student.getId(),
                null,
                null,
                maxMarks,
                marksObtained,
                grade,
                examDate
            );

            if (examDAO.updateExam(exam)) {
                JOptionPane.showMessageDialog(this, "Marks updated successfully! Recalculated Grade: " + grade, "Success", JOptionPane.INFORMATION_MESSAGE);
                loadExamData();
                clearForm();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating exam: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteExam() {
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select an exam record to delete.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = Integer.parseInt(txtId.getText().trim());
        int confirm = JOptionPane.showConfirmDialog(this, "Delete exam record ID " + id + "?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (examDAO.deleteExam(id)) {
                    JOptionPane.showMessageDialog(this, "Exam record deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadExamData();
                    clearForm();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting exam:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validateInputs() {
        if (txtExamName.getText().trim().isEmpty() || cbCourse.getSelectedItem() == null ||
            cbStudent.getSelectedItem() == null || txtMaxMarks.getText().trim().isEmpty() ||
            txtMarksObtained.getText().trim().isEmpty() || txtExamDate.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required to submit an evaluation.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void clearForm() {
        txtId.setText("");
        txtExamName.setText("Midterm Assessment");
        if (cbCourse.getItemCount() > 0) cbCourse.setSelectedIndex(0);
        if (cbStudent.getItemCount() > 0) cbStudent.setSelectedIndex(0);
        txtMaxMarks.setText("100.0");
        txtMarksObtained.setText("");
        txtGrade.setText("");
        txtExamDate.setText(LocalDate.now().toString());
        examTable.clearSelection();
    }
}
