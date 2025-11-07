package com.elearn.ui;

import com.elearn.model.Course;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Fully functional course creation/editing dialog with image upload
 */
public class FunctionalCourseDialog extends JDialog {
    private final JTextField titleField = new JTextField(30);
    private final JTextArea descriptionArea = new JTextArea(5, 30);
    private final JTextField instructorField = new JTextField(30);
    private final JLabel imagePreviewLabel = new JLabel();
    private final JButton selectImageBtn = new JButton("📁 Select Course Image");
    private final JButton removeImageBtn = new JButton("✖ Remove Image");
    
    private Course course;
    private boolean okPressed = false;
    private File selectedImageFile = null;
    private String existingImagePath = null;

    public FunctionalCourseDialog(Frame parent, String title, Course existingCourse) {
        super(parent, title, true);
        this.course = existingCourse;
        
        if (existingCourse != null) {
            titleField.setText(existingCourse.getTitle());
            descriptionArea.setText(existingCourse.getDescription());
            instructorField.setText(existingCourse.getInstructor());
            // video fields removed
            existingImagePath = existingCourse.getImagePath();
            
            // Load existing image if available
            if (existingImagePath != null && !existingImagePath.isEmpty()) {
                loadImagePreview(new File(existingImagePath));
            }
        }
        
        initUI();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(15, 15, 15, 15));

        // Main panel with form fields
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Title
        mainPanel.add(createFieldPanel("Course Title:", titleField));
        mainPanel.add(Box.createVerticalStrut(10));

        // Description
        JPanel descPanel = new JPanel(new BorderLayout(5, 5));
        descPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(new Font("Arial", Font.BOLD, 12));
        descPanel.add(descLabel, BorderLayout.NORTH);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setPreferredSize(new Dimension(400, 100));
        descPanel.add(descScroll, BorderLayout.CENTER);
        mainPanel.add(descPanel);
        mainPanel.add(Box.createVerticalStrut(10));

        // Instructor
        mainPanel.add(createFieldPanel("Instructor Name:", instructorField));
        mainPanel.add(Box.createVerticalStrut(10));

    // Video functionality removed

        // Image Upload Section
        JPanel imagePanel = new JPanel(new BorderLayout(10, 10));
        imagePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        imagePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(33, 150, 243), 2),
            "Course Image",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Arial", Font.BOLD, 12),
            new Color(33, 150, 243)
        ));

        // Image preview
        imagePreviewLabel.setPreferredSize(new Dimension(200, 150));
        imagePreviewLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        imagePreviewLabel.setHorizontalAlignment(JLabel.CENTER);
        imagePreviewLabel.setVerticalAlignment(JLabel.CENTER);
        imagePreviewLabel.setText("No image selected");
        imagePreviewLabel.setBackground(Color.WHITE);
        imagePreviewLabel.setOpaque(true);

        // Image buttons
        JPanel imageButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectImageBtn.addActionListener(e -> selectImage());
        removeImageBtn.addActionListener(e -> removeImage());
        removeImageBtn.setEnabled(false);
        imageButtonPanel.add(selectImageBtn);
        imageButtonPanel.add(removeImageBtn);

        imagePanel.add(imagePreviewLabel, BorderLayout.CENTER);
        imagePanel.add(imageButtonPanel, BorderLayout.SOUTH);
        mainPanel.add(imagePanel);

        add(mainPanel, BorderLayout.CENTER);

        // Bottom buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("✅ Save Course");
        JButton cancelButton = new JButton("❌ Cancel");

        okButton.setPreferredSize(new Dimension(140, 35));
        cancelButton.setPreferredSize(new Dimension(120, 35));

        okButton.setBackground(new Color(76, 175, 80));
        okButton.setForeground(Color.WHITE);
        okButton.setFocusPainted(false);

        cancelButton.setBackground(new Color(244, 67, 54));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);

        okButton.addActionListener(e -> onOk());
        cancelButton.addActionListener(e -> onCancel());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createFieldPanel(String labelText, JTextField textField) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setPreferredSize(new Dimension(150, 25));
        
        panel.add(label, BorderLayout.WEST);
        panel.add(textField, BorderLayout.CENTER);
        return panel;
    }

    private void selectImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Course Image");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        // Image file filter
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) return true;
                String name = f.getName().toLowerCase();
                return name.endsWith(".jpg") || name.endsWith(".jpeg") || 
                       name.endsWith(".png") || name.endsWith(".gif");
            }
            
            @Override
            public String getDescription() {
                return "Image Files (*.jpg, *.jpeg, *.png, *.gif)";
            }
        });

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedImageFile = fileChooser.getSelectedFile();
            loadImagePreview(selectedImageFile);
            removeImageBtn.setEnabled(true);
        }
    }

    private void loadImagePreview(File imageFile) {
        try {
            BufferedImage img = ImageIO.read(imageFile);
            if (img != null) {
                // Scale image to fit preview
                Image scaledImg = img.getScaledInstance(200, 150, Image.SCALE_SMOOTH);
                imagePreviewLabel.setIcon(new ImageIcon(scaledImg));
                imagePreviewLabel.setText("");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Failed to load image: " + e.getMessage(), 
                "Image Load Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeImage() {
        selectedImageFile = null;
        existingImagePath = null;
        imagePreviewLabel.setIcon(null);
        imagePreviewLabel.setText("No image selected");
        removeImageBtn.setEnabled(false);
    }

    private void onOk() {
        // Validation
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        String instructor = instructorField.getText().trim();

        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a course title", "Validation Error", JOptionPane.WARNING_MESSAGE);
            titleField.requestFocus();
            return;
        }

        if (description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a course description", "Validation Error", JOptionPane.WARNING_MESSAGE);
            descriptionArea.requestFocus();
            return;
        }

        if (instructor.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the instructor name", "Validation Error", JOptionPane.WARNING_MESSAGE);
            instructorField.requestFocus();
            return;
        }

        // Create/update course object
        if (course == null) {
            course = new Course();
        }
        
    course.setTitle(title);
    course.setDescription(description);
    course.setInstructor(instructor);

        // Handle image file
        if (selectedImageFile != null) {
            try {
                // Create course_images directory if it doesn't exist
                Path imagesDir = Paths.get("course_images");
                if (!Files.exists(imagesDir)) {
                    Files.createDirectories(imagesDir);
                }

                // Generate unique filename
                String extension = getFileExtension(selectedImageFile.getName());
                String filename = "course_" + System.currentTimeMillis() + extension;
                Path destination = imagesDir.resolve(filename);

                // Copy image file
                Files.copy(selectedImageFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
                course.setImagePath(destination.toString());
                
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                    "Failed to save image: " + e.getMessage(), 
                    "Image Save Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else if (existingImagePath != null) {
            // Keep existing image
            course.setImagePath(existingImagePath);
        }

        okPressed = true;
        dispose();
    }

    private void onCancel() {
        okPressed = false;
        dispose();
    }

    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot > 0 && lastDot < filename.length() - 1) {
            return filename.substring(lastDot);
        }
        return ".jpg";
    }

    public boolean isOkPressed() {
        return okPressed;
    }

    public Course getCourse() {
        return course;
    }
}
