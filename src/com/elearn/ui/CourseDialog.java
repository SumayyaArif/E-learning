package com.elearn.ui;

import com.elearn.model.Course;

import javax.swing.*;
import java.awt.*;

public class CourseDialog extends JDialog {
    private final JTextField titleField = new JTextField(20);
    private final JTextArea descriptionArea = new JTextArea(4, 20);
    private final JTextField instructorField = new JTextField(20);
    // video inputs removed
    private boolean okPressed = false;

    public CourseDialog(JFrame parent, String title, Course course) {
        super(parent, title, true);
        setSize(500, 400);
        setLocationRelativeTo(parent);
        
        createUI();
        if (course != null) {
            populateFields(course);
        }
    }
    
    private void createUI() {
        setLayout(new BorderLayout(10, 10));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Title
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Course Title:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(titleField, gbc);
        
        // Description
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        formPanel.add(new JScrollPane(descriptionArea), gbc);
        
        // Instructor
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Instructor:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(instructorField, gbc);
        
    // Video fields removed
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        
        okButton.addActionListener(e -> {
            if (validateInput()) {
                okPressed = true;
                dispose();
            }
        });
        
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Set default button
        getRootPane().setDefaultButton(okButton);
    }
    
    private void populateFields(Course course) {
        titleField.setText(course.getTitle());
        descriptionArea.setText(course.getDescription());
        instructorField.setText(course.getInstructor());
    // videos removed
    }
    
    private boolean validateInput() {
        if (titleField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a course title", "Validation Error", JOptionPane.WARNING_MESSAGE);
            titleField.requestFocus();
            return false;
        }
        if (instructorField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an instructor name", "Validation Error", JOptionPane.WARNING_MESSAGE);
            instructorField.requestFocus();
            return false;
        }
        return true;
    }
    
    public boolean isOkPressed() {
        return okPressed;
    }
    
    public Course getCourse() {
        Course course = new Course();
        course.setTitle(titleField.getText().trim());
        course.setDescription(descriptionArea.getText().trim());
        course.setInstructor(instructorField.getText().trim());
        // video fields removed
        return course;
    }
}
