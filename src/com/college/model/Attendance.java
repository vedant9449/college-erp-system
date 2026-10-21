package com.college.model;

import java.sql.Date;

/**
 * Attendance entity representing daily lecture attendance records.
 * Demonstrates encapsulation with private fields and standard accessors/mutators.
 */
public class Attendance {

    private int attendanceId;
    private int studentId;
    private String studentRollNumber;
    private String studentName;
    private int courseId;
    private String courseName;
    private Date attendanceDate;
    private String status; // "PRESENT", "ABSENT", "LATE"
    private String remarks;

    public Attendance() {
    }

    public Attendance(int attendanceId, int studentId, String studentRollNumber, String studentName,
                      int courseId, String courseName, Date attendanceDate, String status, String remarks) {
        this.attendanceId = attendanceId;
        this.studentId = studentId;
        this.studentRollNumber = studentRollNumber;
        this.studentName = studentName;
        this.courseId = courseId;
        this.courseName = courseName;
        this.attendanceDate = attendanceDate;
        this.status = status;
        this.remarks = remarks;
    }

    public Attendance(int studentId, int courseId, Date attendanceDate, String status, String remarks) {
        this(0, studentId, null, null, courseId, null, attendanceDate, status, remarks);
    }

    // Getters and Setters
    public int getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(int attendanceId) {
        this.attendanceId = attendanceId;
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

    public Date getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(Date attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return String.format("Attendance #%d: Student #%d | Course #%d | Date: %s | Status: %s",
                attendanceId, studentId, courseId, attendanceDate, status);
    }
}
