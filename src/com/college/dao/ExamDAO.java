package com.college.dao;

import com.college.db.DBConnection;
import com.college.model.Exam;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Exam evaluation records with offline demo fallback.
 */
public class ExamDAO {

    private static final List<Exam> demoExams = new ArrayList<>();
    static {
        demoExams.add(new Exam(1, "Midterm Exam", 1, "Object Oriented Programming with Java", 1, "CS2026001", "Rahul Sharma", 100.0, 92.0, "A+", Date.valueOf("2026-09-20")));
        demoExams.add(new Exam(2, "Midterm Exam", 1, "Object Oriented Programming with Java", 2, "CS2026002", "Priya Patel", 100.0, 85.5, "A", Date.valueOf("2026-09-20")));
        demoExams.add(new Exam(3, "Midterm Exam", 3, "Digital Signal Processing", 3, "EC2026001", "Amit Verma", 100.0, 78.0, "B+", Date.valueOf("2026-09-22")));
    }

    public int recordExam(Exam exam) throws SQLException {
        String sql = "INSERT INTO exams (exam_name, course_id, student_id, max_marks, marks_obtained, grade, exam_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, exam.getExamName());
            stmt.setInt(2, exam.getCourseId());
            stmt.setInt(3, exam.getStudentId());
            stmt.setDouble(4, exam.getMaxMarks());
            stmt.setDouble(5, exam.getMarksObtained());
            stmt.setString(6, exam.getGrade());
            stmt.setDate(7, exam.getExamDate());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    exam.setExamId(id);
                    return id;
                }
            }
            return -1;
        } catch (SQLException e) {
            int newId = demoExams.size() + 1;
            exam.setExamId(newId);
            exam.setStudentRollNumber("CS202600" + exam.getStudentId());
            exam.setStudentName("Student #" + exam.getStudentId());
            exam.setCourseName("Course #" + exam.getCourseId());
            demoExams.add(0, exam);
            return newId;
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    public boolean updateExam(Exam exam) throws SQLException {
        String sql = "UPDATE exams SET exam_name = ?, course_id = ?, student_id = ?, " +
                     "max_marks = ?, marks_obtained = ?, grade = ?, exam_date = ? WHERE exam_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, exam.getExamName());
            stmt.setInt(2, exam.getCourseId());
            stmt.setInt(3, exam.getStudentId());
            stmt.setDouble(4, exam.getMaxMarks());
            stmt.setDouble(5, exam.getMarksObtained());
            stmt.setString(6, exam.getGrade());
            stmt.setDate(7, exam.getExamDate());
            stmt.setInt(8, exam.getExamId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            for (int i = 0; i < demoExams.size(); i++) {
                if (demoExams.get(i).getExamId() == exam.getExamId()) {
                    demoExams.set(i, exam);
                    return true;
                }
            }
            return false;
        } finally {
            DBConnection.close(stmt, conn);
        }
    }

    public boolean deleteExam(int examId) throws SQLException {
        String sql = "DELETE FROM exams WHERE exam_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, examId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return demoExams.removeIf(ex -> ex.getExamId() == examId);
        } finally {
            DBConnection.close(stmt, conn);
        }
    }

    public List<Exam> getAllExams() throws SQLException {
        String sql = "SELECT e.*, c.course_name, s.roll_number, s.name AS student_name " +
                     "FROM exams e " +
                     "JOIN courses c ON e.course_id = c.course_id " +
                     "JOIN students s ON e.student_id = s.student_id " +
                     "ORDER BY e.exam_date DESC, e.exam_id DESC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Exam> list = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToExam(rs));
            }
            return list;
        } catch (SQLException e) {
            return new ArrayList<>(demoExams);
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    private Exam mapResultSetToExam(ResultSet rs) throws SQLException {
        return new Exam(
            rs.getInt("exam_id"),
            rs.getString("exam_name"),
            rs.getInt("course_id"),
            rs.getString("course_name"),
            rs.getInt("student_id"),
            rs.getString("roll_number"),
            rs.getString("student_name"),
            rs.getDouble("max_marks"),
            rs.getDouble("marks_obtained"),
            rs.getString("grade"),
            rs.getDate("exam_date")
        );
    }
}
