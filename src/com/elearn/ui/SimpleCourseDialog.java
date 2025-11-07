package com.elearn.ui;

import com.elearn.model.Course;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class SimpleCourseDialog extends JDialog {
    private final JTextField titleField;
    private final JTextArea descriptionArea;
    private final JTextField instructorField;
    // video fields removed
    private final JLabel imagePreviewLabel;
    private final JButton selectImageButton;
    private File selectedImageFile;
    private boolean okPressed = false;

    public SimpleCourseDialog(JFrame parent, String title, Course course) {
        super(parent, title, true);
        setSize(600, 600);
        setLocationRelativeTo(parent);
        setVisible(true); // Make sure dialog is visible
        setAlwaysOnTop(true); // Bring to front
        toFront(); // Ensure it's on top
        
        // Initialize fields
        titleField = new JTextField(20);
        descriptionArea = new JTextArea(4, 20);
        instructorField = new JTextField(20);
    // video fields removed
        imagePreviewLabel = new JLabel();
        selectImageButton = new JButton("📷 Select Course Image");
        
        createUI();
        if (course != null) {
            populateFields(course);
        }
    }
    
    private void createUI() {
        System.out.println("Creating UI components..."); // Debug output
        setLayout(new BorderLayout(10, 10));
        
        // Main panel with scroll
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Image panel
        JPanel imagePanel = createImagePanel();
        mainPanel.add(imagePanel, BorderLayout.NORTH);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Title
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Course Title *:"), gbc);
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
        formPanel.add(new JLabel("Instructor *:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(instructorField, gbc);
        
    // Video inputs removed
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("Save Course");
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
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Scrollable main panel
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Set default button
        getRootPane().setDefaultButton(okButton);
        
        System.out.println("UI creation completed!"); // Debug output
        repaint(); // Force repaint
        validate(); // Validate layout
    }
    
    private JPanel createImagePanel() {
        System.out.println("Creating image panel..."); // Debug output
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Course Image"));
        
        // Image preview
        imagePreviewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imagePreviewLabel.setVerticalAlignment(SwingConstants.CENTER);
        imagePreviewLabel.setBorder(BorderFactory.createDashedBorder(Color.GRAY, 2, 5));
        imagePreviewLabel.setPreferredSize(new Dimension(200, 150));
        imagePreviewLabel.setText("<html><div style='text-align: center; color: gray;'>No image selected<br>Click button to select</div></html>");
        
        // Select image button
        selectImageButton.addActionListener(e -> onSelectImage());
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(selectImageButton);
        
        panel.add(imagePreviewLabel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void onSelectImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);
        
        // Set file filter for images
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) return true;
                String name = f.getName().toLowerCase();
                return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") || 
                       name.endsWith(".gif") || name.endsWith(".bmp");
            }
            
            @Override
            public String getDescription() {
                return "Images (*.jpg, *.jpeg, *.png, *.gif, *.bmp)";
            }
        });
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedImageFile = fileChooser.getSelectedFile();
            updateImagePreview();
        }
    }
    
    private void updateImagePreview() {
        if (selectedImageFile != null) {
            try {
                BufferedImage image = ImageIO.read(selectedImageFile);
                if (image != null) {
                    // Scale image to fit preview
                    Image scaledImage = image.getScaledInstance(200, 150, Image.SCALE_SMOOTH);
                    imagePreviewLabel.setIcon(new ImageIcon(scaledImage));
                    imagePreviewLabel.setText("");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error loading image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
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
    
    public File getSelectedImageFile() {
        return selectedImageFile;
    }
}
