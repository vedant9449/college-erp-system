package com.college.gui;

import com.college.dao.FeeDAO;
import com.college.dao.StudentDAO;
import com.college.model.Fee;
import com.college.model.FeeException;
import com.college.model.FeeOperations;
import com.college.model.Student;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

/**
 * Fee Operations & Student Financial Ledger Panel.
 * Demonstrates:
 * 1. Interface polymorphism (FeeOperations reference).
 * 2. Custom Exception Handling (FeeException) with graphical error diagnostics.
 * 3. Financial ledger tracking using JTable.
 */
public class FeePanel extends JPanel implements ActionListener {

    // Using the interface reference (Polymorphism)
    private final FeeOperations feeOperations;
    private final FeeDAO feeDAO;
    private final StudentDAO studentDAO;

    private JTable feeTable;
    private DefaultTableModel tableModel;

    private JComboBox<StudentOption> cbStudents;
    private JComboBox<String> cbFeeType;
    private JTextField txtTotalAmount;
    private JButton btnAssignFee;
    private JButton btnPayFee;
    private JButton btnFilterPending;
    private JButton btnShowAll;
    private JLabel lblTotalDuesSummary;

    private boolean showingPendingOnly = false;

    public static class StudentOption {
        private final int id;
        private final String display;
        public StudentOption(int id, String display) { this.id = id; this.display = display; }
        public int getId() { return id; }
        @Override public String toString() { return display; }
    }

    public FeePanel() {
        this.feeDAO = new FeeDAO();
        this.feeOperations = feeDAO; // Polymorphic assignment to interface
        this.studentDAO = new StudentDAO();

        initializeUI();
        loadStudentsDropdown();
        loadFeeData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(UITheme.BG_LIGHT);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // 1. Top Header & Summary KPI
        JPanel topPanel = new JPanel(new BorderLayout(15, 10));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(UITheme.createCardBorder());

        JPanel titleGrp = new JPanel(new GridLayout(2, 1));
        titleGrp.setBackground(Color.WHITE);
        JLabel title = new JLabel("Institutional Fee Ledger & Dues Management");
        title.setFont(UITheme.FONT_HEADER);
        title.setForeground(UITheme.PRIMARY);

        lblTotalDuesSummary = new JLabel("Aggregate Pending Dues: Calculating...");
        lblTotalDuesSummary.setFont(UITheme.FONT_SUBHEADER);
        lblTotalDuesSummary.setForeground(UITheme.DANGER);

        titleGrp.add(title);
        titleGrp.add(lblTotalDuesSummary);

        JPanel filterGrp = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterGrp.setBackground(Color.WHITE);

        btnFilterPending = UITheme.createSecondaryButton("Show Overdue / Pending Only");
        btnFilterPending.addActionListener(this);

        btnShowAll = UITheme.createSecondaryButton("Show All Ledgers");
        btnShowAll.addActionListener(this);

        filterGrp.add(btnFilterPending);
        filterGrp.add(btnShowAll);

        topPanel.add(titleGrp, BorderLayout.WEST);
        topPanel.add(filterGrp, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // 2. Center: JTable
        String[] columns = {
            "Fee ID", "Roll Number", "Student Name", "Fee Type", 
            "Total Amount (INR)", "Paid (INR)", "Outstanding Due (INR)", "Status", "Last Payment Date"
        };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        feeTable = new JTable(tableModel);
        feeTable.setFont(UITheme.FONT_BODY);
        feeTable.setRowHeight(28);
        feeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        feeTable.getTableHeader().setFont(UITheme.FONT_SUBHEADER);
        feeTable.getTableHeader().setBackground(UITheme.PRIMARY);
        feeTable.getTableHeader().setForeground(Color.WHITE);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        feeTable.getColumnModel().getColumn(0).setPreferredWidth(45);
        feeTable.getColumnModel().getColumn(0).setCellRenderer(center);
        feeTable.getColumnModel().getColumn(1).setPreferredWidth(85);
        feeTable.getColumnModel().getColumn(7).setPreferredWidth(70);
        feeTable.getColumnModel().getColumn(7).setCellRenderer(center);

        JScrollPane scrollPane = new JScrollPane(feeTable);
        scrollPane.setBorder(new LineBorder(UITheme.BORDER_COLOR, 1));
        add(scrollPane, BorderLayout.CENTER);

        // 3. South: Operations Form
        JPanel formContainer = new JPanel(new BorderLayout(10, 10));
        formContainer.setBackground(Color.WHITE);
        formContainer.setBorder(UITheme.createCardBorder());

        JLabel formTitle = new JLabel("Fee Assignment & Payment Processing");
        formTitle.setFont(UITheme.FONT_SUBHEADER);
        formTitle.setForeground(UITheme.TEXT_MAIN);
        formContainer.add(formTitle, BorderLayout.NORTH);

        JPanel gridFields = new JPanel(new GridLayout(1, 3, 14, 10));
        gridFields.setBackground(Color.WHITE);

        cbStudents = new JComboBox<>();
        String[] types = {"Semester Tuition", "Hostel & Mess", "Laboratory Fee", "Examination Fee", "Library Due"};
        cbFeeType = new JComboBox<>(types);
        txtTotalAmount = UITheme.createTextField(10);
        txtTotalAmount.setText("50000.00");

        gridFields.add(createLabeledField("Assign To Student *", cbStudents));
        gridFields.add(createLabeledField("Fee Category *", cbFeeType));
        gridFields.add(createLabeledField("Total Amount (INR) *", txtTotalAmount));

        formContainer.add(gridFields, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 5));
        buttonPanel.setBackground(Color.WHITE);

        btnAssignFee = UITheme.createSecondaryButton("Assign New Fee");
        btnAssignFee.addActionListener(this);

        btnPayFee = UITheme.createPrimaryButton("Process Payment on Selected Ledger");
        btnPayFee.addActionListener(this);

        buttonPanel.add(btnAssignFee);
        buttonPanel.add(btnPayFee);

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

    private void loadStudentsDropdown() {
        cbStudents.removeAllItems();
        try {
            List<Student> list = studentDAO.getAllStudents();
            for (Student s : list) {
                cbStudents.addItem(new StudentOption(s.getId(), s.getRollNumber() + " - " + s.getName()));
            }
        } catch (SQLException ex) {
            System.err.println("[FeePanel] Error loading students: " + ex.getMessage());
        }
    }

    public void loadFeeData() {
        tableModel.setRowCount(0);
        try {
            List<Fee> fees = showingPendingOnly ? feeOperations.getPendingFees() : feeDAO.getAllFees();
            for (Fee f : fees) {
                tableModel.addRow(new Object[]{
                    f.getFeeId(),
                    f.getStudentRollNumber(),
                    f.getStudentName(),
                    f.getFeeType(),
                    String.format("%.2f", f.getTotalAmount()),
                    String.format("%.2f", f.getPaidAmount()),
                    String.format("%.2f", f.getDueAmount()),
                    f.getStatus(),
                    f.getLastPaymentDate() != null ? f.getLastPaymentDate() : "N/A"
                });
            }

            double totalDues = feeOperations.calculateTotalDues();
            lblTotalDuesSummary.setText(String.format("Aggregate Pending Dues: ₹%,.2f", totalDues));
        } catch (FeeException ex) {
            JOptionPane.showMessageDialog(this, "Fee retrieval error:\n" + ex.getMessage(), "Fee Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == btnAssignFee) {
            handleAssignFee();
        } else if (src == btnPayFee) {
            handleProcessPayment();
        } else if (src == btnFilterPending) {
            showingPendingOnly = true;
            btnFilterPending.setBackground(UITheme.PRIMARY);
            btnFilterPending.setForeground(Color.WHITE);
            btnShowAll.setBackground(new Color(237, 242, 247));
            btnShowAll.setForeground(UITheme.TEXT_MAIN);
            loadFeeData();
        } else if (src == btnShowAll) {
            showingPendingOnly = false;
            btnShowAll.setBackground(UITheme.PRIMARY);
            btnShowAll.setForeground(Color.WHITE);
            btnFilterPending.setBackground(new Color(237, 242, 247));
            btnFilterPending.setForeground(UITheme.TEXT_MAIN);
            loadFeeData();
        }
    }

    private void handleAssignFee() {
        StudentOption student = (StudentOption) cbStudents.getSelectedItem();
        if (student == null) {
            JOptionPane.showMessageDialog(this, "Please select a student to assign fee.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double total = Double.parseDouble(txtTotalAmount.getText().trim());
            Fee fee = new Fee(student.getId(), (String) cbFeeType.getSelectedItem(), total);

            int id = feeOperations.assignFee(fee);
            if (id > 0) {
                JOptionPane.showMessageDialog(this,
                        String.format("Fee assigned to %s successfully! Ledger ID: %d", student.toString(), id),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadFeeData();
            }
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Total amount must be a valid number.", "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (FeeException ex) {
            // Demonstrates custom FeeException handling in GUI
            JOptionPane.showMessageDialog(this,
                    "Business Rule Violation:\n" + ex.getMessage(),
                    "Fee Assignment Rejected", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleProcessPayment() {
        int selectedRow = feeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a fee ledger row from the table to record a payment.",
                    "No Ledger Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int feeId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        String studentName = tableModel.getValueAt(selectedRow, 2).toString();
        double currentDue = Double.parseDouble(tableModel.getValueAt(selectedRow, 6).toString());

        if (currentDue <= 0.0) {
            JOptionPane.showMessageDialog(this,
                    "This fee ledger is already marked as fully PAID. Outstanding due is 0.00.",
                    "No Dues Outstanding", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String input = JOptionPane.showInputDialog(this,
                String.format("Processing Payment for: %s (Fee ID: %d)\nOutstanding Due Balance: ₹%.2f\n\nEnter payment amount (INR):",
                        studentName, feeId, currentDue),
                String.valueOf(currentDue));

        if (input != null && !input.trim().isEmpty()) {
            try {
                double paymentAmount = Double.parseDouble(input.trim());

                // Invoking FeeOperations contract method
                feeOperations.payFee(feeId, paymentAmount);

                JOptionPane.showMessageDialog(this,
                        String.format("Payment of ₹%.2f successfully recorded for Fee ID #%d!", paymentAmount, feeId),
                        "Payment Success", JOptionPane.INFORMATION_MESSAGE);
                loadFeeData();
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Please enter a valid numeric payment amount.",
                        "Input Error", JOptionPane.WARNING_MESSAGE);
            } catch (FeeException feeEx) {
                // Catching custom FeeException and displaying user-friendly explanation
                JOptionPane.showMessageDialog(this,
                        "Fee Transaction Failed:\n" + feeEx.getMessage() +
                        "\n\nPlease verify that the payment does not exceed outstanding dues.",
                        "Fee Business Rule Exception", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
