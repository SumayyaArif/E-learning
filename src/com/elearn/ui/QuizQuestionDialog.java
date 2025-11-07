package com.elearn.ui;

import com.elearn.model.QuizQuestion;

import javax.swing.*;
import java.awt.*;

public class QuizQuestionDialog extends JDialog {
    private final JTextArea questionArea = new JTextArea(3, 30);
    private final JTextField optionAField = new JTextField(25);
    private final JTextField optionBField = new JTextField(25);
    private final JTextField optionCField = new JTextField(25);
    private final JTextField optionDField = new JTextField(25);
    private final JComboBox<String> correctOptionCombo = new JComboBox<>(new String[]{"A", "B", "C", "D"});
    private boolean okPressed = false;
    private final int courseId;

    public QuizQuestionDialog(JFrame parent, String title, QuizQuestion question, int courseId) {
        super(parent, title, true);
        this.courseId = courseId;
        setSize(500, 500);
        setLocationRelativeTo(parent);
        
        createUI();
        if (question != null) {
            populateFields(question);
        }
    }
    
    private void createUI() {
        setLayout(new BorderLayout(10, 10));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Question
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Question:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
        questionArea.setLineWrap(true);
        questionArea.setWrapStyleWord(true);
        formPanel.add(new JScrollPane(questionArea), gbc);
        
        // Option A
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Option A:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(optionAField, gbc);
        
        // Option B
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Option B:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(optionBField, gbc);
        
        // Option C
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Option C:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(optionCField, gbc);
        
        // Option D
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Option D:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(optionDField, gbc);
        
        // Correct Answer
        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Correct Answer:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(correctOptionCombo, gbc);
        
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
    
    private void populateFields(QuizQuestion question) {
        questionArea.setText(question.getQuestion());
        optionAField.setText(question.getOptionA());
        optionBField.setText(question.getOptionB());
        optionCField.setText(question.getOptionC());
        optionDField.setText(question.getOptionD());
        correctOptionCombo.setSelectedItem(question.getCorrectOption());
    }
    
    private boolean validateInput() {
        if (questionArea.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a question", "Validation Error", JOptionPane.WARNING_MESSAGE);
            questionArea.requestFocus();
            return false;
        }
        if (optionAField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter option A", "Validation Error", JOptionPane.WARNING_MESSAGE);
            optionAField.requestFocus();
            return false;
        }
        if (optionBField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter option B", "Validation Error", JOptionPane.WARNING_MESSAGE);
            optionBField.requestFocus();
            return false;
        }
        if (optionCField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter option C", "Validation Error", JOptionPane.WARNING_MESSAGE);
            optionCField.requestFocus();
            return false;
        }
        if (optionDField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter option D", "Validation Error", JOptionPane.WARNING_MESSAGE);
            optionDField.requestFocus();
            return false;
        }
        if (correctOptionCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select the correct answer", "Validation Error", JOptionPane.WARNING_MESSAGE);
            correctOptionCombo.requestFocus();
            return false;
        }
        return true;
    }
    
    public boolean isOkPressed() {
        return okPressed;
    }
    
    public QuizQuestion getQuestion() {
        QuizQuestion question = new QuizQuestion();
        question.setCourseId(courseId);
        question.setQuestion(questionArea.getText().trim());
        question.setOptionA(optionAField.getText().trim());
        question.setOptionB(optionBField.getText().trim());
        question.setOptionC(optionCField.getText().trim());
        question.setOptionD(optionDField.getText().trim());
        question.setCorrectOption(correctOptionCombo.getSelectedItem().toString());
        return question;
    }
}
