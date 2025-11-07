package com.elearn.dao.impl;

import com.elearn.dao.MaterialCompletionDAO;
import com.elearn.db.DBConnection;
import com.elearn.model.MaterialCompletion;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MaterialCompletionDAOImpl implements MaterialCompletionDAO {
    
    @Override
    public boolean markAsCompleted(int studentId, int materialId) {
        String sql = "INSERT INTO material_completions (student_id, material_id, completed_date) VALUES (?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE completed_date = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, materialId);
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isCompleted(int studentId, int materialId) {
        String sql = "SELECT completion_id FROM material_completions WHERE student_id = ? AND material_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, materialId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<MaterialCompletion> findByStudentId(int studentId) {
        List<MaterialCompletion> completions = new ArrayList<>();
        String sql = "SELECT * FROM material_completions WHERE student_id = ? ORDER BY completed_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MaterialCompletion completion = new MaterialCompletion();
                    completion.setCompletionId(rs.getInt("completion_id"));
                    completion.setStudentId(rs.getInt("student_id"));
                    completion.setMaterialId(rs.getInt("material_id"));
                    Timestamp ts = rs.getTimestamp("completed_date");
                    if (ts != null) {
                        completion.setCompletedDate(ts.toLocalDateTime());
                    }
                    completions.add(completion);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return completions;
    }

    @Override
    public List<MaterialCompletion> findByMaterialId(int materialId) {
        List<MaterialCompletion> completions = new ArrayList<>();
        String sql = "SELECT * FROM material_completions WHERE material_id = ? ORDER BY completed_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, materialId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MaterialCompletion completion = new MaterialCompletion();
                    completion.setCompletionId(rs.getInt("completion_id"));
                    completion.setStudentId(rs.getInt("student_id"));
                    completion.setMaterialId(rs.getInt("material_id"));
                    Timestamp ts = rs.getTimestamp("completed_date");
                    if (ts != null) {
                        completion.setCompletedDate(ts.toLocalDateTime());
                    }
                    completions.add(completion);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return completions;
    }
}
