package com.college.dao;

import com.college.db.DBConnection;
import com.college.model.Fee;
import com.college.model.FeeException;
import com.college.model.FeeOperations;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object implementing FeeOperations with offline demo fallback.
 */
public class FeeDAO implements FeeOperations {

    private static final List<Fee> demoFees = new ArrayList<>();
    static {
        Fee f1 = new Fee(1, 1, "Semester Tuition", 50000.00, 50000.00, 0.00, "PAID", Date.valueOf("2026-08-01"));
        f1.setStudentRollNumber("CS2026001");
        f1.setStudentName("Rahul Sharma");

        Fee f2 = new Fee(2, 2, "Semester Tuition", 50000.00, 30000.00, 20000.00, "PARTIAL", Date.valueOf("2026-08-10"));
        f2.setStudentRollNumber("CS2026002");
        f2.setStudentName("Priya Patel");

        Fee f3 = new Fee(3, 3, "Semester Tuition", 50000.00, 0.00, 50000.00, "UNPAID", null);
        f3.setStudentRollNumber("EC2026001");
        f3.setStudentName("Amit Verma");

        demoFees.add(f1);
        demoFees.add(f2);
        demoFees.add(f3);
    }

    @Override
    public void payFee(int feeId, double amount) throws FeeException {
        Connection conn = null;
        PreparedStatement selectStmt = null;
        PreparedStatement updateStmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Begin JDBC Transaction

            String selectSql = "SELECT fee_id, student_id, total_amount, paid_amount, due_amount, status " +
                               "FROM fees WHERE fee_id = ? FOR UPDATE";
            selectStmt = conn.prepareStatement(selectSql);
            selectStmt.setInt(1, feeId);
            rs = selectStmt.executeQuery();

            if (!rs.next()) {
                throw new FeeException("Fee record not found for Fee ID: " + feeId);
            }

            double currentPaid = rs.getDouble("paid_amount");
            double currentDue = rs.getDouble("due_amount");

            if (amount <= 0) {
                throw new FeeException("Invalid payment amount. Amount must be greater than zero.", amount, currentDue);
            }
            if (amount > currentDue) {
                throw new FeeException("Payment amount exceeds outstanding dues.", amount, currentDue);
            }

            double newPaid = currentPaid + amount;
            double newDue = currentDue - amount;
            String newStatus = (newDue <= 0.001) ? "PAID" : "PARTIAL";
            Date today = new Date(System.currentTimeMillis());

            String updateSql = "UPDATE fees SET paid_amount = ?, due_amount = ?, status = ?, last_payment_date = ? " +
                               "WHERE fee_id = ?";
            updateStmt = conn.prepareStatement(updateSql);
            updateStmt.setDouble(1, newPaid);
            updateStmt.setDouble(2, newDue);
            updateStmt.setString(3, newStatus);
            updateStmt.setDate(4, today);
            updateStmt.setInt(5, feeId);

            updateStmt.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            // Demo fallback in memory
            for (Fee f : demoFees) {
                if (f.getFeeId() == feeId) {
                    if (amount <= 0) {
                        throw new FeeException("Payment amount must be greater than zero.", amount, f.getDueAmount());
                    }
                    if (amount > f.getDueAmount()) {
                        throw new FeeException("Payment amount exceeds outstanding dues.", amount, f.getDueAmount());
                    }
                    f.recordPayment(amount);
                    return;
                }
            }
            throw new FeeException("Database transaction failed during fee payment: " + e.getMessage(), e);
        } finally {
            DBConnection.close(rs, selectStmt, updateStmt, conn);
        }
    }

    @Override
    public int assignFee(Fee fee) throws FeeException {
        if (fee == null || fee.getTotalAmount() <= 0) {
            throw new FeeException("Fee assignment requires a positive total amount.");
        }

        String sql = "INSERT INTO fees (student_id, fee_type, total_amount, paid_amount, due_amount, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, fee.getStudentId());
            stmt.setString(2, fee.getFeeType());
            stmt.setDouble(3, fee.getTotalAmount());
            stmt.setDouble(4, fee.getPaidAmount());
            stmt.setDouble(5, fee.getDueAmount());
            stmt.setString(6, fee.getStatus());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    fee.setFeeId(id);
                    return id;
                }
            }
            return -1;
        } catch (SQLException e) {
            int newId = demoFees.size() + 1;
            fee.setFeeId(newId);
            fee.setStudentRollNumber("CS202600" + fee.getStudentId());
            fee.setStudentName("Student #" + fee.getStudentId());
            demoFees.add(fee);
            return newId;
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    @Override
    public List<Fee> getFeesByStudentId(int studentId) throws FeeException {
        List<Fee> list = new ArrayList<>();
        for (Fee f : demoFees) {
            if (f.getStudentId() == studentId) list.add(f);
        }
        return list;
    }

    @Override
    public List<Fee> getPendingFees() throws FeeException {
        try {
            List<Fee> all = getAllFees();
            List<Fee> pending = new ArrayList<>();
            for (Fee f : all) {
                if (!"PAID".equalsIgnoreCase(f.getStatus())) {
                    pending.add(f);
                }
            }
            return pending;
        } catch (Exception ex) {
            List<Fee> pending = new ArrayList<>();
            for (Fee f : demoFees) {
                if (!"PAID".equalsIgnoreCase(f.getStatus())) pending.add(f);
            }
            return pending;
        }
    }

    @Override
    public double calculateTotalDues() throws FeeException {
        double total = 0.0;
        try {
            List<Fee> all = getAllFees();
            for (Fee f : all) total += f.getDueAmount();
        } catch (Exception ex) {
            for (Fee f : demoFees) total += f.getDueAmount();
        }
        return total;
    }

    public List<Fee> getAllFees() throws FeeException {
        String sql = "SELECT f.*, s.roll_number, s.name AS student_name " +
                     "FROM fees f " +
                     "JOIN students s ON f.student_id = s.student_id " +
                     "ORDER BY f.fee_id DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Fee> list = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToFee(rs));
            }
            return list;
        } catch (SQLException e) {
            return new ArrayList<>(demoFees);
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    private Fee mapResultSetToFee(ResultSet rs) throws SQLException {
        Fee fee = new Fee(
            rs.getInt("fee_id"),
            rs.getInt("student_id"),
            rs.getString("fee_type"),
            rs.getDouble("total_amount"),
            rs.getDouble("paid_amount"),
            rs.getDouble("due_amount"),
            rs.getString("status"),
            rs.getDate("last_payment_date")
        );
        fee.setStudentRollNumber(rs.getString("roll_number"));
        fee.setStudentName(rs.getString("student_name"));
        return fee;
    }
}
