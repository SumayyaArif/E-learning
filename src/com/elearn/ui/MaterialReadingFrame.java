package com.elearn.ui;

import com.elearn.dao.CourseDAO;
import com.elearn.dao.MaterialDAO;
import com.elearn.dao.MaterialCompletionDAO;
import com.elearn.dao.EnrollmentDAO;
import com.elearn.dao.impl.CourseDAOImpl;
import com.elearn.dao.impl.MaterialDAOImpl;
import com.elearn.dao.impl.MaterialCompletionDAOImpl;
import com.elearn.dao.impl.EnrollmentDAOImpl;
import com.elearn.model.Course;
import com.elearn.model.Material;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class MaterialReadingFrame extends JFrame {
    private final int studentId;
    private final CourseDAO courseDAO = new CourseDAOImpl();
    private final MaterialDAO materialDAO = new MaterialDAOImpl();
    private final MaterialCompletionDAO completionDAO = new MaterialCompletionDAOImpl();
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAOImpl();
    private final DefaultListModel<Course> coursesModel = new DefaultListModel<>();
    private final DefaultListModel<Material> materialsModel = new DefaultListModel<>();
    private Course selectedCourse;
    private Material selectedMaterial;

    public MaterialReadingFrame(int studentId) {
        this.studentId = studentId;
        setTitle("Reading Materials");
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        createUI();
        loadCourses();
    }
    
    private void createUI() {
        setLayout(new BorderLayout(10, 10));
        
        // Top panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton markCompletedBtn = new JButton("✅ Mark as Completed");
        JButton takeQuizBtn = new JButton("📝 Take Quiz");
        
        markCompletedBtn.addActionListener(e -> onMarkCompleted());
        takeQuizBtn.addActionListener(e -> onTakeQuiz());
        
        topPanel.add(markCompletedBtn);
        topPanel.add(takeQuizBtn);
        
        // Center panel with split layout
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        // Left panel - Courses and Materials
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Courses & Materials"));
        
        // Courses list
        JPanel coursesPanel = new JPanel(new BorderLayout());
        coursesPanel.setBorder(BorderFactory.createTitledBorder("Courses"));
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
        coursesPanel.add(new JScrollPane(coursesList), BorderLayout.CENTER);
        
        // Materials list
        JPanel materialsPanel = new JPanel(new BorderLayout());
        materialsPanel.setBorder(BorderFactory.createTitledBorder("Materials"));
        JList<Material> materialsList = new JList<>(materialsModel);
        materialsList.setCellRenderer(new MaterialListRenderer());
        materialsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Material selected = materialsList.getSelectedValue();
                if (selected != null) {
                    selectedMaterial = selected;
                    displayMaterial(selected);
                }
            }
        });
        materialsPanel.add(new JScrollPane(materialsList), BorderLayout.CENTER);
        
        // Split courses and materials
        JSplitPane leftSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        leftSplit.setTopComponent(coursesPanel);
        leftSplit.setBottomComponent(materialsPanel);
        leftSplit.setDividerLocation(200);
        
        leftPanel.add(leftSplit, BorderLayout.CENTER);
        
        // Right panel - Material content
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Material Content"));
        JTextArea contentArea = new JTextArea();
        contentArea.setEditable(false);
        contentArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        rightPanel.add(new JScrollPane(contentArea), BorderLayout.CENTER);
        
        // Store reference to content area for later use
        this.contentArea = contentArea;
        
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        splitPane.setDividerLocation(300);
        
        add(topPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }
    
    private JTextArea contentArea;
    
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
            // Check if student is enrolled
            if (!enrollmentDAO.isEnrolled(studentId, courseId)) {
                JOptionPane.showMessageDialog(this, 
                    "You must be enrolled in this course to view its materials.", 
                    "Enrollment Required", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            List<Material> materials = materialDAO.findByCourseId(courseId);
            for (Material m : materials) {
                materialsModel.addElement(m);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading materials: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void displayMaterial(Material material) {
        if (material.getType() != null && material.getType().equals("text") && material.getContent() != null) {
            contentArea.setText(material.getContent());
        } else {
            contentArea.setText("This material is a file. Please download it from the course details.\n\nFile: " + material.getFileName());
        }
        contentArea.setCaretPosition(0);
    }
    
    private void onMarkCompleted() {
        if (selectedMaterial == null) {
            JOptionPane.showMessageDialog(this, "Please select a material first", "No Material Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            boolean completed = completionDAO.markAsCompleted(studentId, selectedMaterial.getMaterialId());
            if (completed) {
                JOptionPane.showMessageDialog(this, "Material marked as completed!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to mark material as completed", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error marking material as completed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void onTakeQuiz() {
        if (selectedCourse == null) {
            JOptionPane.showMessageDialog(this, "Please select a course first", "No Course Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Check if all materials are completed
        try {
            List<Material> materials = materialDAO.findByCourseId(selectedCourse.getCourseId());
            
            if (materials.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No materials available for this course. Please add materials first.", "No Materials", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            boolean allCompleted = true;
            int totalMaterials = materials.size();
            int completedCount = 0;
            
            for (Material material : materials) {
                if (completionDAO.isCompleted(studentId, material.getMaterialId())) {
                    completedCount++;
                } else {
                    allCompleted = false;
                }
            }
            
            if (!allCompleted) {
                JOptionPane.showMessageDialog(this, 
                    String.format("You must complete all materials before taking the quiz.\n\nProgress: %d/%d materials completed", 
                    completedCount, totalMaterials), 
                    "Materials Not Completed", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Open quiz frame
            QuizFrame quizFrame = new QuizFrame(studentId, selectedCourse);
            quizFrame.setVisible(true);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error checking material completion: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Custom renderers
    private class CourseListRenderer extends DefaultListCellRenderer {
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
    
    private class MaterialListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Material) {
                Material material = (Material) value;
                boolean isCompleted = false;
                try {
                    isCompleted = completionDAO.isCompleted(studentId, material.getMaterialId());
                } catch (Exception e) {
                    // Ignore error
                }
                
                String status = isCompleted ? "✅" : "⏳";
                String type = material.getType() != null && material.getType().equals("text") ? "📖" : "📄";
                
                setText("<html>" + status + " " + type + " <b>" + material.getFileName() + "</b></html>");
            }
            return this;
        }
    }
}
