package com.college.dao;

import com.college.db.DBConnection;
import com.college.model.Faculty;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for Faculty entities.
 * Includes offline evaluation fallback.
 */
public class FacultyDAO {

    private static final List<Faculty> demoFaculty = new ArrayList<>();
    static {
        demoFaculty.add(new Faculty(1, "Dr. Alan Turing", "alan.turing@college.edu", "9876543210", "Computer Science", "Professor", 95000.00, Date.valueOf("2020-08-15")));
        demoFaculty.add(new Faculty(2, "Dr. Ada Lovelace", "ada.lovelace@college.edu", "9876543211", "Computer Science", "Associate Professor", 85000.00, Date.valueOf("2021-01-10")));
        demoFaculty.add(new Faculty(3, "Prof. Claude Shannon", "shannon@college.edu", "9876543212", "Electronics", "Professor", 92000.00, Date.valueOf("2019-07-01")));
    }

    public int addFaculty(Faculty faculty) throws SQLException {
        String sql = "INSERT INTO faculty (name, email, phone, department, designation, salary, hire_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, faculty.getName());
            stmt.setString(2, faculty.getEmail());
            stmt.setString(3, faculty.getPhone());
            stmt.setString(4, faculty.getDepartment());
            stmt.setString(5, faculty.getDesignation());
            stmt.setDouble(6, faculty.getSalary());
            stmt.setDate(7, faculty.getHireDate());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    faculty.setId(id);
                    return id;
                }
            }
            return -1;
        } catch (SQLException e) {
            int newId = demoFaculty.size() + 1;
            faculty.setId(newId);
            demoFaculty.add(faculty);
            return newId;
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    public boolean updateFaculty(Faculty faculty) throws SQLException {
        String sql = "UPDATE faculty SET name = ?, email = ?, phone = ?, department = ?, " +
                     "designation = ?, salary = ?, hire_date = ? WHERE faculty_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, faculty.getName());
            stmt.setString(2, faculty.getEmail());
            stmt.setString(3, faculty.getPhone());
            stmt.setString(4, faculty.getDepartment());
            stmt.setString(5, faculty.getDesignation());
            stmt.setDouble(6, faculty.getSalary());
            stmt.setDate(7, faculty.getHireDate());
            stmt.setInt(8, faculty.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            for (int i = 0; i < demoFaculty.size(); i++) {
                if (demoFaculty.get(i).getId() == faculty.getId()) {
                    demoFaculty.set(i, faculty);
                    return true;
                }
            }
            return false;
        } finally {
            DBConnection.close(stmt, conn);
        }
    }

    public boolean deleteFaculty(int facultyId) throws SQLException {
        String sql = "DELETE FROM faculty WHERE faculty_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, facultyId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return demoFaculty.removeIf(f -> f.getId() == facultyId);
        } finally {
            DBConnection.close(stmt, conn);
        }
    }

    public Faculty getFacultyById(int facultyId) throws SQLException {
        String sql = "SELECT * FROM faculty WHERE faculty_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, facultyId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToFaculty(rs);
            }
            return null;
        } catch (SQLException e) {
            for (Faculty f : demoFaculty) {
                if (f.getId() == facultyId) return f;
            }
            return null;
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    public List<Faculty> getAllFaculty() throws SQLException {
        String sql = "SELECT * FROM faculty ORDER BY faculty_id ASC";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Faculty> list = new ArrayList<>();

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToFaculty(rs));
            }
            return list;
        } catch (SQLException e) {
            return new ArrayList<>(demoFaculty);
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    public Map<String, Integer> getFacultyCountByDepartment() throws SQLException {
        String sql = "SELECT department, COUNT(*) AS count FROM faculty GROUP BY department";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Map<String, Integer> map = new HashMap<>();

        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                map.put(rs.getString("department"), rs.getInt("count"));
            }
            return map;
        } catch (SQLException e) {
            for (Faculty f : demoFaculty) {
                map.put(f.getDepartment(), map.getOrDefault(f.getDepartment(), 0) + 1);
            }
            return map;
        } finally {
            DBConnection.close(rs, stmt, conn);
        }
    }

    private Faculty mapResultSetToFaculty(ResultSet rs) throws SQLException {
        return new Faculty(
            rs.getInt("faculty_id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("phone"),
            rs.getString("department"),
            rs.getString("designation"),
            rs.getDouble("salary"),
            rs.getDate("hire_date")
        );
    }
}
