package com.elearn.dao.impl;

import com.elearn.dao.MaterialDAO;
import com.elearn.db.DBConnection;
import com.elearn.model.Material;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MaterialDAOImpl implements MaterialDAO {
    @Override
    public List<Material> findByCourseId(int courseId) {
        List<Material> list = new ArrayList<>();
        String sql = "SELECT material_id, course_id, file_name, file_path, content, type FROM materials WHERE course_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Material m = new Material();
                    m.setMaterialId(rs.getInt("material_id"));
                    m.setCourseId(rs.getInt("course_id"));
                    m.setFileName(rs.getString("file_name"));
                    m.setFilePath(rs.getString("file_path"));
                    m.setContent(rs.getString("content"));
                    m.setType(rs.getString("type"));
                    list.add(m);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Material findById(int materialId) {
        String sql = "SELECT material_id, course_id, file_name, file_path, content, type FROM materials WHERE material_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, materialId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Material m = new Material();
                    m.setMaterialId(rs.getInt("material_id"));
                    m.setCourseId(rs.getInt("course_id"));
                    m.setFileName(rs.getString("file_name"));
                    m.setFilePath(rs.getString("file_path"));
                    m.setContent(rs.getString("content"));
                    m.setType(rs.getString("type"));
                    return m;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean create(Material material) {
        String sql = "INSERT INTO materials(course_id, file_name, file_path, content, type) VALUES(?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, material.getCourseId());
            ps.setString(2, material.getFileName());
            ps.setString(3, material.getFilePath());
            ps.setString(4, material.getContent());
            ps.setString(5, material.getType() != null ? material.getType() : "text");
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) material.setMaterialId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int materialId) {
        String sql = "DELETE FROM materials WHERE material_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, materialId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}


