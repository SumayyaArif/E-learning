package com.elearn.app;

import com.elearn.ui.AdminLoginFrame;
import com.elearn.util.ModernTheme;

import javax.swing.SwingUtilities;

public class AdminLauncher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Apply modern theme
            ModernTheme.applyModernTheme();
            
            // Create and show admin login frame
            AdminLoginFrame frame = new AdminLoginFrame();
            frame.setVisible(true);
        });
    }
}
