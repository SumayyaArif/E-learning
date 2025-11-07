package com.elearn.ui;

import com.elearn.dao.CourseDAO;
import com.elearn.dao.MaterialDAO;
import com.elearn.dao.impl.CourseDAOImpl;
import com.elearn.dao.impl.MaterialDAOImpl;
import com.elearn.model.Course;
import com.elearn.model.Material;
import com.elearn.util.DocumentTextExtractor;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class MaterialUploadFrame extends JFrame {
    private final CourseDAO courseDAO = new CourseDAOImpl();
    private final MaterialDAO materialDAO = new MaterialDAOImpl();
    private final DefaultListModel<Course> coursesModel = new DefaultListModel<>();
    private final DefaultListModel<Material> materialsModel = new DefaultListModel<>();
    private File selectedFile;
    private Course selectedCourse;

    public MaterialUploadFrame() {
        setTitle("Material Upload");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        createUI();
        loadCourses();
    }
    
    private void createUI() {
        setLayout(new BorderLayout(10, 10));
        
        // Top panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addTextBtn = new JButton("📝 Add Text Material");
        JButton deleteMaterialBtn = new JButton("🗑️ Delete Material");
        
        addTextBtn.addActionListener(e -> onAddTextMaterial());
        deleteMaterialBtn.addActionListener(e -> onDeleteMaterial());
        
        topPanel.add(addTextBtn);
        topPanel.add(deleteMaterialBtn);
        
        // Center panel with split layout
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        // Left panel - Courses
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Courses"));
        JList<Course> coursesList = new JList<>(coursesModel);
        coursesList.setCellRenderer(new CourseListRenderer());
        coursesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Course selected = coursesList.getSelectedValue();
                if (selected != null) {
                    selectedCourse = selected;
                    loadMaterials(selected.getCourseId());
                }
            }
        });
        leftPanel.add(new JScrollPane(coursesList), BorderLayout.CENTER);
        
        // Right panel - Materials
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Course Materials"));
        JList<Material> materialsList = new JList<>(materialsModel);
        materialsList.setCellRenderer(new MaterialListRenderer());
        rightPanel.add(new JScrollPane(materialsList), BorderLayout.CENTER);
        
        // File info panel
        JPanel fileInfoPanel = new JPanel(new BorderLayout());
        fileInfoPanel.setBorder(BorderFactory.createTitledBorder("Selected File"));
        JLabel fileLabel = new JLabel("No file selected");
        fileLabel.setForeground(Color.GRAY);
        fileInfoPanel.add(fileLabel, BorderLayout.CENTER);
        
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        splitPane.setDividerLocation(300);
        
        add(topPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        add(fileInfoPanel, BorderLayout.SOUTH);
    }
    
    private void loadCourses() {
        coursesModel.clear();
        try {
            List<Course> courses = courseDAO.findAll();
            for (Course course : courses) {
                coursesModel.addElement(course);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading courses: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadMaterials(int courseId) {
        materialsModel.clear();
        try {
            List<Material> materials = materialDAO.findByCourseId(courseId);
            for (Material material : materials) {
                materialsModel.addElement(material);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading materials: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void onSelectFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);
        
        // Set file filter for common document types
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) return true;
                String name = f.getName().toLowerCase();
                return name.endsWith(".pdf") || name.endsWith(".doc") || name.endsWith(".docx") || 
                       name.endsWith(".txt") || name.endsWith(".ppt") || name.endsWith(".pptx") ||
                       name.endsWith(".xls") || name.endsWith(".xlsx");
            }
            
            @Override
            public String getDescription() {
                return "Documents (*.pdf, *.doc, *.docx, *.txt, *.ppt, *.pptx, *.xls, *.xlsx)";
            }
        });
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            updateFileLabel();
        }
    }
    
    private void updateFileLabel() {
        if (selectedFile != null) {
            JLabel fileLabel = (JLabel) ((JPanel) getContentPane().getComponent(2)).getComponent(0);
            fileLabel.setText("Selected: " + selectedFile.getName() + " (" + formatFileSize(selectedFile.length()) + ")");
            fileLabel.setForeground(Color.BLACK);
        }
    }
    
    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
    }
    
    private void onUpload() {
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(this, "Please select a file first", "No File Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (selectedCourse == null) {
            JOptionPane.showMessageDialog(this, "Please select a course first", "No Course Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // Create materials directory if it doesn't exist
            File materialsDir = new File("materials");
            if (!materialsDir.exists()) {
                materialsDir.mkdirs();
            }
            
            // Generate unique filename to avoid conflicts
            String originalName = selectedFile.getName();
            String uniqueFileName = System.currentTimeMillis() + "_" + originalName;
            File destFile = new File(materialsDir, uniqueFileName);
            Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            
            // Create material record
            Material material = new Material();
            material.setCourseId(selectedCourse.getCourseId());
            material.setFileName(originalName); // Store original name for display
            material.setFilePath("materials/" + uniqueFileName);
            material.setType("file");
            
            // Try to extract text content if it's a document
            String content = DocumentTextExtractor.extractTextFromFile(selectedFile);
            if (content != null && !content.isEmpty()) {
                material.setContent(content);
            }
            
            boolean saved = materialDAO.create(material);
            if (saved) {
                JOptionPane.showMessageDialog(this, "Material uploaded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadMaterials(selectedCourse.getCourseId());
                selectedFile = null;
                updateFileLabel();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save material record", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error uploading material: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void onAddTextMaterial() {
        if (selectedCourse == null) {
            JOptionPane.showMessageDialog(this, "Please select a course first", "No Course Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Create a dialog for text input
        JDialog dialog = new JDialog(this, "Add Text Material", true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout());
        
        // Title input
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.add(new JLabel("Material Title:"));
        JTextField titleField = new JTextField(30);
        titlePanel.add(titleField);
        
        // Content input
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createTitledBorder("Material Content"));
        JTextArea contentArea = new JTextArea(15, 50);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentPanel.add(new JScrollPane(contentArea), BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");
        
        saveBtn.addActionListener(e -> {
            String title = titleField.getText().trim();
            String content = contentArea.getText().trim();
            
            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter a title", "Title Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (content.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter content", "Content Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                Material material = new Material();
                material.setCourseId(selectedCourse.getCourseId());
                material.setFileName(title);
                material.setContent(content);
                material.setType("text");
                
                boolean saved = materialDAO.create(material);
                if (saved) {
                    JOptionPane.showMessageDialog(dialog, "Text material added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadMaterials(selectedCourse.getCourseId());
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to save material", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error saving material: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        
        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void onDeleteMaterial() {
        Material selected = getSelectedMaterial();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a material to delete", "No Material Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this material?", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Delete file from filesystem
                File file = new File(selected.getFilePath());
                if (file.exists()) {
                    file.delete();
                }
                
                // Delete record from database
                boolean deleted = materialDAO.delete(selected.getMaterialId());
                if (deleted) {
                    JOptionPane.showMessageDialog(this, "Material deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadMaterials(selected.getCourseId());
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete material record", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting material: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private Material getSelectedMaterial() {
        // Get the selected material from the materials list
        JList<Material> materialsList = (JList<Material>) ((JScrollPane) ((JPanel) ((JSplitPane) getContentPane().getComponent(1)).getRightComponent()).getComponent(0)).getViewport().getView();
        return materialsList.getSelectedValue();
    }
    
    // Custom renderers
    private static class CourseListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Course) {
                Course course = (Course) value;
                setText("<html><b>" + course.getTitle() + "</b><br>" + 
                       "Instructor: " + course.getInstructor() + "</html>");
            }
            return this;
        }
    }
    
    private static class MaterialListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Material) {
                Material material = (Material) value;
                setText("<html><b>" + material.getFileName() + "</b><br>" + 
                       "<i>" + material.getFilePath() + "</i></html>");
            }
            return this;
        }
    }
}
