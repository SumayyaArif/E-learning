package com.elearn.ui;

import com.elearn.model.Course;
import com.elearn.util.ModernTheme;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ModernCourseDialog extends JDialog {
    private final JTextField titleField;
    private final JTextArea descriptionArea;
    private final JTextField instructorField;
    private final JLabel imagePreviewLabel;
    private final JButton selectImageButton;
    private File selectedImageFile;
    private boolean okPressed = false;

    public ModernCourseDialog(JFrame parent, String title, Course course) {
        super(parent, title, true);
        System.out.println("ModernCourseDialog constructor called"); // Debug output
        setSize(600, 700);
        setLocationRelativeTo(parent);
        ModernTheme.styleDialog(this);
        
        // Initialize fields
        titleField = ModernTheme.createModernTextField();
        descriptionArea = ModernTheme.createModernTextArea();
        instructorField = ModernTheme.createModernTextField();
    // video fields removed
        imagePreviewLabel = new JLabel();
        selectImageButton = ModernTheme.createSecondaryButton("📷 Select Course Image");
        
        createUI();
        if (course != null) {
            populateFields(course);
        }
    }
    
    private void createUI() {
        System.out.println("Creating UI for ModernCourseDialog"); // Debug output
        setLayout(new BorderLayout(20, 20));
        
        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setOpaque(false);
        JLabel headerLabel = ModernTheme.createSubheadingLabel("Course Information");
        headerPanel.add(headerLabel);
        
        // Main form panel
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        
        // Image selection panel
        JPanel imagePanel = createImagePanel();
        formPanel.add(imagePanel);
        formPanel.add(Box.createVerticalStrut(20));
        
        // Form fields panel
        JPanel fieldsPanel = createFieldsPanel();
        formPanel.add(fieldsPanel);
        
        // Scrollable form
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // Buttons panel
        JPanel buttonPanel = createButtonPanel();
        
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createImagePanel() {
        JPanel panel = ModernTheme.createCardPanel();
        panel.setLayout(new BorderLayout());
        
        JLabel titleLabel = ModernTheme.createBodyLabel("Course Image");
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Image preview and selection
        JPanel imageContainer = new JPanel(new BorderLayout(10, 10));
        imageContainer.setOpaque(false);
        
        // Image preview
        imagePreviewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imagePreviewLabel.setVerticalAlignment(SwingConstants.CENTER);
        imagePreviewLabel.setBorder(BorderFactory.createDashedBorder(ModernTheme.ACCENT_COLOR, 2, 5));
        imagePreviewLabel.setPreferredSize(new Dimension(200, 150));
        imagePreviewLabel.setText("<html><div style='text-align: center; color: " + 
            String.format("#%06x", ModernTheme.TEXT_SECONDARY.getRGB() & 0xFFFFFF) + 
            ";'>No image selected<br>Click button to select</div></html>");
        
        // Select image button
        selectImageButton.addActionListener(e -> onSelectImage());
        
        imageContainer.add(imagePreviewLabel, BorderLayout.CENTER);
        imageContainer.add(selectImageButton, BorderLayout.SOUTH);
        
        panel.add(imageContainer, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFieldsPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Title
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel titleLabel = ModernTheme.createBodyLabel("Course Title *");
        panel.add(titleLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        titleField.setPreferredSize(new Dimension(400, 35));
        panel.add(titleField, gbc);
        
        // Description
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        JLabel descLabel = ModernTheme.createBodyLabel("Description");
        panel.add(descLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.BOTH;
        descriptionArea.setPreferredSize(new Dimension(400, 100));
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setBorder(descriptionArea.getBorder());
        descriptionArea.setBorder(BorderFactory.createEmptyBorder());
        panel.add(descScroll, gbc);
        
        // Instructor
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE;
        JLabel instructorLabel = ModernTheme.createBodyLabel("Instructor *");
        panel.add(instructorLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.HORIZONTAL;
        instructorField.setPreferredSize(new Dimension(400, 35));
        panel.add(instructorField, gbc);
        
    // Video fields removed
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        JButton okButton = ModernTheme.createModernButton("✅ Save Course");
        JButton cancelButton = ModernTheme.createSecondaryButton("❌ Cancel");
        
        okButton.addActionListener(e -> {
            if (validateInput()) {
                okPressed = true;
                dispose();
            }
        });
        
        cancelButton.addActionListener(e -> dispose());
        
        panel.add(cancelButton);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(okButton);
        
        // Set default button
        getRootPane().setDefaultButton(okButton);
        
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
            } catch (IOException e) {
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
