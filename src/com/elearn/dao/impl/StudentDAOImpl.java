package com.elearn.dao.impl;

import com.elearn.dao.StudentDAO;
import com.elearn.db.DBConnection;
import com.elearn.model.Student;

import java.sql.*;

public class StudentDAOImpl implements StudentDAO {
    @Override
    public Student findByEmailAndPassword(String email, String password) {
        String sql = "SELECT student_id, name, email, password, date_joined FROM students WHERE email=? AND password=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Student s = new Student();
                    s.setStudentId(rs.getInt("student_id"));
                    s.setName(rs.getString("name"));
                    s.setEmail(rs.getString("email"));
                    s.setPassword(rs.getString("password"));
                    Timestamp ts = rs.getTimestamp("date_joined");
                    if (ts != null) s.setDateJoined(ts.toLocalDateTime());
                    return s;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Student findById(int studentId) {
        String sql = "SELECT student_id, name, email, password, date_joined FROM students WHERE student_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Student s = new Student();
                    s.setStudentId(rs.getInt("student_id"));
                    s.setName(rs.getString("name"));
                    s.setEmail(rs.getString("email"));
                    s.setPassword(rs.getString("password"));
                    Timestamp ts = rs.getTimestamp("date_joined");
                    if (ts != null) s.setDateJoined(ts.toLocalDateTime());
                    return s;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean create(Student student) {
        String sql = "INSERT INTO students(name, email, password) VALUES(?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, student.getName());
            ps.setString(2, student.getEmail());
            ps.setString(3, student.getPassword());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        student.setStudentId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            // Check if it's a duplicate email error
            if (e.getSQLState().equals("23000") && e.getMessage().contains("Duplicate entry")) {
                System.err.println("Email already exists: " + student.getEmail());
            } else {
                System.err.println("Database error during student creation: " + e.getMessage());
            }
            e.printStackTrace();
        }
        return false;
    }
}


