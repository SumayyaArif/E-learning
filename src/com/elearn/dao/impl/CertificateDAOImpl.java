package com.elearn.dao.impl;

import com.elearn.dao.CertificateDAO;
import com.elearn.db.DBConnection;
import com.elearn.model.Certificate;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CertificateDAOImpl implements CertificateDAO {
    @Override
    public boolean issueCertificate(int studentId, int courseId) {
        String sql = "INSERT INTO certificates(student_id, course_id, status) VALUES(?,?, 'Issued')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Certificate> findByStudentId(int studentId) {
        List<Certificate> list = new ArrayList<>();
        String sql = "SELECT cert_id, student_id, course_id, issue_date, status FROM certificates WHERE student_id=? ORDER BY issue_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Certificate c = new Certificate();
                    c.setCertId(rs.getInt("cert_id"));
                    c.setStudentId(rs.getInt("student_id"));
                    c.setCourseId(rs.getInt("course_id"));
                    Timestamp ts = rs.getTimestamp("issue_date");
                    if (ts != null) c.setIssueDate(ts.toLocalDateTime());
                    c.setStatus(rs.getString("status"));
                    list.add(c);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}


