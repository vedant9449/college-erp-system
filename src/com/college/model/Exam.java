package com.college.model;

import java.sql.Date;

/**
 * Exam entity encapsulating assessment records, marks, and grading.
 * Demonstrates encapsulation and domain logic (automated grade calculation).
 */
public class Exam {

    private int examId;
    private String examName;
    private int courseId;
    private String courseName;
    private int studentId;
    private String studentRollNumber;
    private String studentName;
    private double maxMarks;
    private double marksObtained;
    private String grade;
    private Date examDate;

    public Exam() {
        this.maxMarks = 100.0;
    }

    public Exam(int examId, String examName, int courseId, String courseName, 
                int studentId, String studentRollNumber, String studentName,
                double maxMarks, double marksObtained, String grade, Date examDate) {
        this.examId = examId;
        this.examName = examName;
        this.courseId = courseId;
        this.courseName = courseName;
        this.studentId = studentId;
        this.studentRollNumber = studentRollNumber;
        this.studentName = studentName;
        this.maxMarks = maxMarks;
        this.marksObtained = marksObtained;
        this.grade = (grade == null || grade.trim().isEmpty()) ? computeGrade(marksObtained, maxMarks) : grade;
        this.examDate = examDate;
    }

    public Exam(String examName, int courseId, int studentId, double maxMarks, double marksObtained, Date examDate) {
        this(0, examName, courseId, null, studentId, null, null, maxMarks, marksObtained, null, examDate);
    }

    /**
     * Domain logic helper to determine academic letter grade based on percentage.
     */
    public static String computeGrade(double marksObtained, double maxMarks) {
        if (maxMarks <= 0) return "N/A";
        double percentage = (marksObtained / maxMarks) * 100.0;
        if (percentage >= 90.0) return "A+";
        if (percentage >= 80.0) return "A";
        if (percentage >= 70.0) return "B+";
        if (percentage >= 60.0) return "B";
        if (percentage >= 50.0) return "C";
        if (percentage >= 40.0) return "D";
        return "F";
    }

    // Getters and Setters
    public int getExamId() {
        return examId;
    }

    public void setExamId(int examId) {
        this.examId = examId;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
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

    public double getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(double maxMarks) {
        this.maxMarks = maxMarks;
        this.grade = computeGrade(this.marksObtained, this.maxMarks);
    }

    public double getMarksObtained() {
        return marksObtained;
    }

    public void setMarksObtained(double marksObtained) {
        this.marksObtained = marksObtained;
        this.grade = computeGrade(this.marksObtained, this.maxMarks);
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public Date getExamDate() {
        return examDate;
    }

    public void setExamDate(Date examDate) {
        this.examDate = examDate;
    }

    @Override
    public String toString() {
        return String.format("%s - Marks: %.1f/%.1f (Grade: %s)", examName, marksObtained, maxMarks, grade);
    }
}
