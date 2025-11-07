package com.elearn.dao.impl;

import com.elearn.dao.EnrollmentDAO;
import com.elearn.db.DBConnection;
import com.elearn.model.Enrollment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAOImpl implements EnrollmentDAO {
    @Override
    public boolean enroll(int studentId, int courseId) {
        String sql = "INSERT INTO enrollments(student_id, course_id) VALUES(?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            // Might be duplicate enrollment due to unique constraint
            return false;
        }
    }

    @Override
    public List<Enrollment> findAll() {
            List<Enrollment> list = new ArrayList<>();
            String sql = "SELECT enroll_id, student_id, course_id, enroll_date, status FROM enrollments";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Enrollment e = new Enrollment();
                    e.setEnrollId(rs.getInt("enroll_id"));
                    e.setStudentId(rs.getInt("student_id"));
                    e.setCourseId(rs.getInt("course_id"));
                    Timestamp ts = rs.getTimestamp("enroll_date");
                    if (ts != null) e.setEnrollDate(ts.toLocalDateTime());
                    e.setStatus(rs.getString("status"));
                    list.add(e);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return list;
        }

        @Override
        public List<Enrollment> findByCourseId(int courseId) {
            List<Enrollment> list = new ArrayList<>();
            String sql = "SELECT enroll_id, student_id, course_id, enroll_date, status FROM enrollments WHERE course_id=?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, courseId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Enrollment e = new Enrollment();
                        e.setEnrollId(rs.getInt("enroll_id"));
                        e.setStudentId(rs.getInt("student_id"));
                        e.setCourseId(rs.getInt("course_id"));
                        Timestamp ts = rs.getTimestamp("enroll_date");
                        if (ts != null) e.setEnrollDate(ts.toLocalDateTime());
                        e.setStatus(rs.getString("status"));
                        list.add(e);
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return list;
        }

        @Override
        public String getEnrollmentStatus(int studentId, int courseId) {
            String sql = "SELECT status FROM enrollments WHERE student_id=? AND course_id=?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, studentId);
                ps.setInt(2, courseId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("status");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return "Not Enrolled";
        }

        @Override
        public double getMaterialCompletionPercentage(int studentId, int courseId) {
            String sql = "SELECT " +
                        "(SELECT COUNT(*) FROM material_completion mc " +
                        "JOIN materials m ON mc.material_id = m.material_id " +
                        "WHERE mc.student_id=? AND m.course_id=?) as completed, " +
                        "(SELECT COUNT(*) FROM materials WHERE course_id=?) as total";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, studentId);
                ps.setInt(2, courseId);
                ps.setInt(3, courseId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int completed = rs.getInt("completed");
                        int total = rs.getInt("total");
                        if (total > 0) {
                            return (completed * 100.0) / total;
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 0.0;
        }

        @Override
        public boolean hasCompletedQuiz(int studentId, int courseId) {
            String sql = "SELECT 1 FROM quiz_results WHERE student_id=? AND course_id=? AND score >= 70";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, studentId);
                ps.setInt(2, courseId);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }
    @Override
    public boolean updateStatus(int studentId, int courseId, String status) {
        String sql = "UPDATE enrollments SET status=? WHERE student_id=? AND course_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, studentId);
            ps.setInt(3, courseId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Enrollment> findByStudentId(int studentId) {
        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT enroll_id, student_id, course_id, enroll_date, status FROM enrollments WHERE student_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Enrollment e = new Enrollment();
                    e.setEnrollId(rs.getInt("enroll_id"));
                    e.setStudentId(rs.getInt("student_id"));
                    e.setCourseId(rs.getInt("course_id"));
                    Timestamp ts = rs.getTimestamp("enroll_date");
                    if (ts != null) e.setEnrollDate(ts.toLocalDateTime());
                    e.setStatus(rs.getString("status"));
                    list.add(e);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean isEnrolled(int studentId, int courseId) {
        String sql = "SELECT 1 FROM enrollments WHERE student_id=? AND course_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}


