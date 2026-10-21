package com.college.model;

import java.sql.Date;

/**
 * Fee entity representing student tuition and institutional dues.
 * Contains business methods for fee payment processing with custom exception validation.
 */
public class Fee {

    private int feeId;
    private int studentId;
    private String studentRollNumber; // Display / joined field
    private String studentName;       // Display / joined field
    private String feeType;
    private double totalAmount;
    private double paidAmount;
    private double dueAmount;
    private String status;            // "PAID", "PARTIAL", "UNPAID"
    private Date lastPaymentDate;

    public Fee() {
    }

    public Fee(int feeId, int studentId, String feeType, double totalAmount, 
               double paidAmount, double dueAmount, String status, Date lastPaymentDate) {
        this.feeId = feeId;
        this.studentId = studentId;
        this.feeType = feeType;
        this.totalAmount = totalAmount;
        this.paidAmount = paidAmount;
        this.dueAmount = dueAmount;
        this.status = status;
        this.lastPaymentDate = lastPaymentDate;
    }

    public Fee(int studentId, String feeType, double totalAmount) {
        this(0, studentId, feeType, totalAmount, 0.0, totalAmount, "UNPAID", null);
    }

    /**
     * Business logic method demonstrating custom FeeException handling.
     * 
     * @param paymentAmount amount to pay
     * @throws FeeException if payment is non-positive or exceeds outstanding dues
     */
    public void recordPayment(double paymentAmount) throws FeeException {
        if (paymentAmount <= 0) {
            throw new FeeException("Payment amount must be greater than zero.", paymentAmount, this.dueAmount);
        }
        if (paymentAmount > this.dueAmount) {
            throw new FeeException("Payment exceeds current outstanding due balance.", paymentAmount, this.dueAmount);
        }

        this.paidAmount += paymentAmount;
        this.dueAmount -= paymentAmount;
        this.lastPaymentDate = new Date(System.currentTimeMillis());

        if (this.dueAmount == 0.0) {
            this.status = "PAID";
        } else {
            this.status = "PARTIAL";
        }
    }

    // Getters and Setters
    public int getFeeId() {
        return feeId;
    }

    public void setFeeId(int feeId) {
        this.feeId = feeId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentRollNumber() {
        return studentRollNumber;
    }

    public void setStudentRollNumber(String studentRollNumber) {
        this.studentRollNumber = studentRollNumber;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getFeeType() {
        return feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public double getDueAmount() {
        return dueAmount;
    }

    public void setDueAmount(double dueAmount) {
        this.dueAmount = dueAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getLastPaymentDate() {
        return lastPaymentDate;
    }

    public void setLastPaymentDate(Date lastPaymentDate) {
        this.lastPaymentDate = lastPaymentDate;
    }

    @Override
    public String toString() {
        return String.format("Fee #%d - Student ID %d | %s | Due: ₹%.2f | Status: %s",
                feeId, studentId, feeType, dueAmount, status);
    }
}
