package com.elearn.dao.impl;

import com.elearn.dao.AdminDAO;
import com.elearn.db.DBConnection;
import com.elearn.model.Admin;

import java.sql.*;

public class AdminDAOImpl implements AdminDAO {
    @Override
    public Admin findByUsernameAndPassword(String username, String password) {
        String sql = "SELECT admin_id, username, password FROM admin WHERE username=? AND password=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Admin a = new Admin();
                    a.setAdminId(rs.getInt("admin_id"));
                    a.setUsername(rs.getString("username"));
                    a.setPassword(rs.getString("password"));
                    return a;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}


