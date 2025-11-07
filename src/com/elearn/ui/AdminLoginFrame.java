package com.elearn.ui;

import com.elearn.dao.AdminDAO;
import com.elearn.dao.impl.AdminDAOImpl;
import com.elearn.model.Admin;
import com.elearn.util.ModernTheme;

import javax.swing.*;
import java.awt.*;

public class AdminLoginFrame extends JFrame {
    private final AdminDAO adminDAO = new AdminDAOImpl();
    private JTextField usernameField;
    private JPasswordField passwordField;

    public AdminLoginFrame() {
        setTitle("Admin Login - E-Learning System");
        setSize(400, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ModernTheme.styleFrame(this);
        
        createUI();
    }
    
    private void createUI() {
        setLayout(new BorderLayout());
        
        // Main panel with card design
        JPanel mainPanel = ModernTheme.createCardPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        
        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        
        JLabel titleLabel = ModernTheme.createHeadingLabel("Admin Portal");
        JLabel subtitleLabel = ModernTheme.createBodyLabel("E-Learning Management System");
        subtitleLabel.setForeground(ModernTheme.TEXT_SECONDARY);
        
        headerPanel.add(Box.createVerticalStrut(20));
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(subtitleLabel);
        
        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Username field
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel usernameLabel = ModernTheme.createBodyLabel("Username:");
        formPanel.add(usernameLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        usernameField = ModernTheme.createModernTextField();
        usernameField.setPreferredSize(new Dimension(250, 40));
        formPanel.add(usernameField, gbc);
        
        // Password field
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        JLabel passwordLabel = ModernTheme.createBodyLabel("Password:");
        formPanel.add(passwordLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        passwordField = new JPasswordField();
        passwordField.setBackground(ModernTheme.SURFACE_COLOR);
        passwordField.setForeground(ModernTheme.TEXT_PRIMARY);
        passwordField.setFont(ModernTheme.BODY_FONT);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ModernTheme.ACCENT_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        passwordField.setPreferredSize(new Dimension(250, 40));
        formPanel.add(passwordField, gbc);
        
        // Login button
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton loginButton = ModernTheme.createModernButton("🔐 Login");
        loginButton.setPreferredSize(new Dimension(250, 45));
        loginButton.addActionListener(e -> onLogin());
        formPanel.add(loginButton, gbc);
        
        // Back to main app button
        gbc.gridx = 0; gbc.gridy = 5;
        JButton backButton = ModernTheme.createSecondaryButton("← Back to Main App");
        backButton.setPreferredSize(new Dimension(250, 40));
        backButton.addActionListener(e -> {
            dispose();
            new com.elearn.app.Main().main(new String[]{});
        });
        formPanel.add(backButton, gbc);
        
        // Add panels to main panel
        mainPanel.add(headerPanel);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(formPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Center the main panel
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(mainPanel);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Set default button
        getRootPane().setDefaultButton(loginButton);
        
        // Focus on username field
        SwingUtilities.invokeLater(() -> usernameField.requestFocus());
    }
    
    private void onLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            Admin admin = adminDAO.findByUsernameAndPassword(username, password);
            if (admin != null) {
                JOptionPane.showMessageDialog(this, "Login successful! Welcome, " + admin.getUsername(), "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new FullyFunctionalAdminDashboard(admin).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
                usernameField.requestFocus();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Login error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
