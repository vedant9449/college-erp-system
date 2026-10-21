package com.college.model;

/**
 * Course entity encapsulating academic syllabus subjects.
 * Demonstrates encapsulation with private fields and standard getters/setters.
 */
public class Course {

    private int courseId;
    private String courseCode;
    private String courseName;
    private int credits;
    private String department;
    private int facultyId;
    private String facultyName; // Display helper for GUI tables

    public Course() {
    }

    public Course(int courseId, String courseCode, String courseName, int credits, 
                  String department, int facultyId, String facultyName) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credits = credits;
        this.department = department;
        this.facultyId = facultyId;
        this.facultyName = facultyName;
    }

    public Course(int courseId, String courseCode, String courseName, int credits, 
                  String department, int facultyId) {
        this(courseId, courseCode, courseName, credits, department, facultyId, "Unassigned");
    }

    public Course(String courseCode, String courseName, int credits, String department, int facultyId) {
        this(0, courseCode, courseName, credits, department, facultyId, "Unassigned");
    }

    // Getters and Setters
    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(int facultyId) {
        this.facultyId = facultyId;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%d Credits)", courseCode, courseName, credits);
    }
}
