package com.elearn.ui;

import com.elearn.model.Course;
import com.elearn.util.FileUtils;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class BasicCourseDialog extends JDialog {
    private final JTextField titleField;
    private final JTextArea descriptionArea;
    private final JTextField instructorField;
    private final JButton selectImageButton;
    private final JLabel imagePreviewLabel;
    private File selectedImageFile;
    private boolean okPressed = false;
    private Course existingCourse;

    public BasicCourseDialog(JFrame parent, String title, Course course) {
        super(parent, title, true);
        System.out.println("BasicCourseDialog constructor called");
        
        this.existingCourse = course;
        
        // Initialize fields
        titleField = new JTextField(20);
        descriptionArea = new JTextArea(4, 20);
        instructorField = new JTextField(20);
    selectImageButton = new JButton("Select Image");
    imagePreviewLabel = new JLabel("No image selected");
        
        // If editing existing course, populate fields
        if (course != null) {
            titleField.setText(course.getTitle());
            descriptionArea.setText(course.getDescription());
            instructorField.setText(course.getInstructor());
            
            if (course.getImagePath() != null && !course.getImagePath().isEmpty()) {
                imagePreviewLabel.setText(course.getImagePath());
            }
            
            // video fields removed
        }
        
        createUI();
        
        setSize(600, 500);
        setLocationRelativeTo(parent);
        setVisible(true);
        setAlwaysOnTop(true);
        toFront();
    }
    
    private void createUI() {
        System.out.println("Creating basic UI...");
        
        setLayout(new BorderLayout());
        
        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title field
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.add(new JLabel("Course Title:"));
        titlePanel.add(titleField);
        mainPanel.add(titlePanel);
        
        // Description field
        JPanel descPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        descPanel.add(new JLabel("Description:"));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setPreferredSize(new Dimension(300, 100));
        descPanel.add(scrollPane);
        mainPanel.add(descPanel);
        
        // Instructor field
        JPanel instructorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        instructorPanel.add(new JLabel("Instructor:"));
        instructorPanel.add(instructorField);
        mainPanel.add(instructorPanel);
        
        // Image section
        JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        imagePanel.add(new JLabel("Course Image:"));
        imagePanel.add(selectImageButton);
        imagePanel.add(imagePreviewLabel);
        mainPanel.add(imagePanel);
        
    // Video sections removed
        
        // Setup file choosers
        selectImageButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif"));
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedImageFile = fileChooser.getSelectedFile();
                imagePreviewLabel.setText(selectedImageFile.getName());
            }
        });
        
        // video selection removed
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton okButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        okButton.addActionListener(e -> {
            if (validateInputs()) {
                okPressed = true;
                dispose();
            }
        });
        
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        System.out.println("Basic UI creation completed!");
    }
    
    private boolean validateInputs() {
        if (titleField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Course title cannot be empty", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    
    public boolean isOkPressed() {
        return okPressed;
    }
    
    public Course getCourse() {
        Course course = existingCourse != null ? existingCourse : new Course();
        course.setTitle(titleField.getText().trim());
        course.setDescription(descriptionArea.getText().trim());
        course.setInstructor(instructorField.getText().trim());
        
        // Handle image file
        if (selectedImageFile != null) {
            try {
                String imagePath = saveImageFile(selectedImageFile);
                course.setImagePath(imagePath);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error saving image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        // video handling removed
        
        return course;
    }
    
    private String saveImageFile(File file) throws Exception {
        // Create course_images directory if it doesn't exist
        Path courseImagesDir = Paths.get(System.getProperty("user.dir"), "course_images");
        if (!Files.exists(courseImagesDir)) {
            Files.createDirectories(courseImagesDir);
        }
        
        // Generate unique filename
        String uniqueFileName = "course_" + System.currentTimeMillis() + "." + FileUtils.getExtension(file.getName());
        Path targetPath = courseImagesDir.resolve(uniqueFileName);
        
        // Copy file
        Files.copy(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        
        return "course_images/" + uniqueFileName;
    }
    
    public File getSelectedImageFile() {
        return selectedImageFile;
    }
}
