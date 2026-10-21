package com.college.model;

import java.util.List;

/**
 * Interface defining fee transaction and management contracts.
 * Demonstrates:
 * 1. Interface definition in Java OOP architecture.
 * 2. Contractual decoupling of business operations.
 * 3. Exception propagation via throws FeeException.
 * 4. Collections usage (java.util.List).
 */
public interface FeeOperations {

    /**
     * Records a fee payment for a given student fee record.
     * 
     * @param feeId  target fee ledger identifier
     * @param amount amount being paid
     * @throws FeeException if the amount is invalid, exceeds dues, or fee record is absent
     */
    void payFee(int feeId, double amount) throws FeeException;

    /**
     * Assigns a new fee obligation to a student.
     * 
     * @param fee newly created fee object
     * @return generated fee id
     * @throws FeeException on persistence or validation failure
     */
    int assignFee(Fee fee) throws FeeException;

    /**
     * Retrieves all fees associated with a particular student.
     * 
     * @param studentId the student id
     * @return List of Fee objects
     * @throws FeeException if data retrieval fails
     */
    List<Fee> getFeesByStudentId(int studentId) throws FeeException;

    /**
     * Retrieves all overdue or partially paid fee records.
     * 
     * @return List of pending fees
     * @throws FeeException if data retrieval fails
     */
    List<Fee> getPendingFees() throws FeeException;

    /**
     * Calculates the aggregate pending fee dues across the college.
     * 
     * @return total dues in INR
     * @throws FeeException if computation or query fails
     */
    double calculateTotalDues() throws FeeException;
}
