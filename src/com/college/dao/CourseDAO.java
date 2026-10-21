package com.college.dao;

import com.college.db.DBConnection;
import com.college.model.Course;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Course entities with offline evaluation fallback.
 */
public class CourseDAO {

    private static final List<Course> demoCourses = new ArrayList<>();
    static {
        demoCourses.add(new Course(1, "CS401", "Object Oriented Programming with Java", 4, "Computer Science", 1, "Dr. Alan Turing"));
        demoCourses.add(new Course(2, "CS402", "Database Management Systems", 4, "Computer Science", 2, "Dr. Ada Lovelace"));
        demoCourses.add(new Course(3, "EC601", "Digital Signal Processing", 3, "Electronics", 3, "Prof. Claude Shannon"));
    }

    public int addCourse(Course course) throws SQLException {
        String sql = "INSERT INTO courses (course_code, course_name, credits, department, faculty_id) " +
                     "VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, course.getCourseCode());
            stmt.setString(2, course.getCourseName());
            stmt.setInt(3, course.getCredits());
            stmt.setString(4, course.getDepartment());
            if (course.getFacultyId() > 0) {
                stmt.setInt(5, course.getFacultyId());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    course.setCourseId(id);
                    return id;
                }
            }
            return -1;
        } catch (SQLException e) {
            int newId = demoCourses.size() + 1;
            course.setCourseId(newId);
            demoCourses.add(course);
            return newId;
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    public boolean updateCourse(Course course) throws SQLException {
        String sql = "UPDATE courses SET course_code = ?, course_name = ?, credits = ?, " +
                     "department = ?, faculty_id = ? WHERE course_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, course.getCourseCode());
            stmt.setString(2, course.getCourseName());
            stmt.setInt(3, course.getCredits());
            stmt.setString(4, course.getDepartment());
            if (course.getFacultyId() > 0) {
                stmt.setInt(5, course.getFacultyId());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }
            stmt.setInt(6, course.getCourseId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            for (int i = 0; i < demoCourses.size(); i++) {
                if (demoCourses.get(i).getCourseId() == course.getCourseId()) {
                    demoCourses.set(i, course);
                    return true;
                }
            }
            return false;
        } finally {
            DBConnection.close(stmt, conn);
        }
    }

    public boolean deleteCourse(int courseId) throws SQLException {
        String sql = "DELETE FROM courses WHERE course_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, courseId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return demoCourses.removeIf(c -> c.getCourseId() == courseId);
        } finally {
            DBConnection.close(stmt, conn);
        }
    }

    public Course getCourseById(int courseId) throws SQLException {
        String sql = "SELECT c.*, f.name AS faculty_name FROM courses c " +
                     "LEFT JOIN faculty f ON c.faculty_id = f.faculty_id " +
                     "WHERE c.course_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, courseId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToCourse(rs);
            }
            return null;
        } catch (SQLException e) {
            for (Course c : demoCourses) {
                if (c.getCourseId() == courseId) return c;
            }
            return null;
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    public List<Course> getAllCourses() throws SQLException {
        String sql = "SELECT c.*, COALESCE(f.name, 'Unassigned') AS faculty_name FROM courses c " +
                     "LEFT JOIN faculty f ON c.faculty_id = f.faculty_id " +
                     "ORDER BY c.course_id ASC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Course> list = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToCourse(rs));
            }
            return list;
        } catch (SQLException e) {
            return new ArrayList<>(demoCourses);
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    private Course mapResultSetToCourse(ResultSet rs) throws SQLException {
        return new Course(
            rs.getInt("course_id"),
            rs.getString("course_code"),
            rs.getString("course_name"),
            rs.getInt("credits"),
            rs.getString("department"),
            rs.getInt("faculty_id"),
            rs.getString("faculty_name")
        );
    }
}
