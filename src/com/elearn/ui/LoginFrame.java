package com.elearn.ui;

import com.elearn.model.Student;
import com.elearn.service.AuthService;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private final JTextField emailOrUserField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JButton studentLoginBtn = new JButton("Student Login");
    private final JButton signupBtn = new JButton("Sign Up");
    private final AuthService authService = new AuthService();

    public LoginFrame() {
        setTitle("E-Learning - Student Login");
        setSize(400, 240);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel fields = new JPanel(new GridLayout(0,1,6,6));
        fields.add(new JLabel("Student Email"));
        fields.add(emailOrUserField);
        fields.add(new JLabel("Password"));
        fields.add(passwordField);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(signupBtn);
        buttons.add(studentLoginBtn);

        add(fields, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        studentLoginBtn.addActionListener(e -> onStudentLogin());
        signupBtn.addActionListener(e -> onSignup());
    }

    private void onStudentLogin() {
        String email = emailOrUserField.getText().trim();
        String pass = new String(passwordField.getPassword());
        
        if (email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both email and password", "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            Student s = authService.loginStudent(email, pass);
            if (s != null) {
                SwingUtilities.invokeLater(() -> {
                    dispose();
                    new EnhancedStudentDashboardFrame(s).setVisible(true);
                });
            } else {
                JOptionPane.showMessageDialog(this, "Invalid email or password. Please check your credentials.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database connection error. Please check if MySQL is running.", "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onSignup() {
        String name = JOptionPane.showInputDialog(this, "Enter your name");
        if (name == null || name.trim().isEmpty()) return;
        
        String email = JOptionPane.showInputDialog(this, "Enter email");
        if (email == null || email.trim().isEmpty()) return;
        
        // Basic email validation
        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address", "Invalid Email", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String pass = JOptionPane.showInputDialog(this, "Enter password");
        if (pass == null || pass.trim().isEmpty()) return;
        
        if (pass.length() < 3) {
            JOptionPane.showMessageDialog(this, "Password must be at least 3 characters long", "Weak Password", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            boolean ok = authService.registerStudent(name.trim(), email.trim(), pass.trim());
            if (ok) {
                JOptionPane.showMessageDialog(this, "Signup successful! You can now login with your credentials.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Signup failed. This email may already be registered. Please try a different email.", "Signup Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database connection error. Please check if MySQL is running.", "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
}


