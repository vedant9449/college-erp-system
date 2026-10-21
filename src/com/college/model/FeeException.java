package com.college.model;

/**
 * Custom checked exception representing business logic errors in fee processing
 * (e.g., negative payment amounts, payments exceeding outstanding dues, student not found).
 * Demonstrates custom exception handling as required by the course syllabus.
 */
public class FeeException extends Exception {

    private double attemptedAmount;
    private double currentDue;

    public FeeException(String message) {
        super(message);
    }

    public FeeException(String message, Throwable cause) {
        super(message, cause);
    }

    public FeeException(String message, double attemptedAmount, double currentDue) {
        super(String.format("%s [Attempted: ₹%.2f, Outstanding Due: ₹%.2f]", message, attemptedAmount, currentDue));
        this.attemptedAmount = attemptedAmount;
        this.currentDue = currentDue;
    }

    public double getAttemptedAmount() {
        return attemptedAmount;
    }

    public double getCurrentDue() {
        return currentDue;
    }
}
