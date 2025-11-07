package com.elearn.app;

import com.elearn.ui.LoginFrame;
import com.elearn.db.DBConnection;
import com.elearn.util.ModernTheme;


import javax.swing.SwingUtilities;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Apply modern theme
            ModernTheme.applyModernTheme();
            
            // quick connectivity check (non-fatal): show alert if DB is unreachable
            try (Connection ignored = DBConnection.getConnection()) {
                // ok
            } catch (SQLException ex) {
                javax.swing.JOptionPane.showMessageDialog(null,
                        "Database connection failed. Please check DBConfig and MySQL server.\n" + ex.getMessage(),
                        "DB Connection Error",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
            }
            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        });
    }
}


