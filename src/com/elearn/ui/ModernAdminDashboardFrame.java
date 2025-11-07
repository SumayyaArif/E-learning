package com.elearn.ui;

import com.elearn.model.Admin;
import com.elearn.model.Course;
import com.elearn.dao.CourseDAO;
import com.elearn.dao.impl.CourseDAOImpl;
import com.elearn.util.ModernTheme;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class ModernAdminDashboardFrame extends JFrame {
    private final Admin admin;
    private final CourseDAO courseDAO = new CourseDAOImpl();
    private final DefaultListModel<Course> coursesModel = new DefaultListModel<>();
    private JList<Course> coursesList;

    public ModernAdminDashboardFrame(Admin admin) {
        this.admin = admin;
        setTitle("Admin Dashboard - " + admin.getUsername());
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ModernTheme.styleFrame(this);
        
        createUI();
        loadCourses();
    }
    
    private void createUI() {
        setLayout(new BorderLayout(20, 20));
        
        // Header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setOpaque(false);
        
        // Left panel - Course list
        JPanel leftPanel = createCourseListPanel();
        mainPanel.add(leftPanel, BorderLayout.WEST);
        
        // Right panel - Course details and actions
        JPanel rightPanel = createActionPanel();
        mainPanel.add(rightPanel, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Footer panel
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        // Title section
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setOpaque(false);
        
        JLabel welcomeLabel = ModernTheme.createHeadingLabel("Welcome, " + admin.getUsername());
        JLabel roleLabel = ModernTheme.createBodyLabel("Administrator");
        roleLabel.setForeground(ModernTheme.TEXT_SECONDARY);
        
        titlePanel.add(welcomeLabel);
        titlePanel.add(Box.createHorizontalStrut(20));
        titlePanel.add(roleLabel);
        
        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        
        JButton addCourseBtn = ModernTheme.createModernButton("➕ Add Course");
        JButton manageQuizBtn = ModernTheme.createSecondaryButton("📝 Manage Quiz");
        JButton uploadMaterialBtn = ModernTheme.createSecondaryButton("📁 Upload Material");
        JButton viewStudentsBtn = ModernTheme.createSecondaryButton("👨‍🎓 View Students");
        JButton reportsBtn = ModernTheme.createSecondaryButton("📊 Reports");
        JButton logoutBtn = ModernTheme.createSecondaryButton("🚪 Logout");
        
        addCourseBtn.addActionListener(e -> onAddCourse());
        manageQuizBtn.addActionListener(e -> onManageQuiz());
        uploadMaterialBtn.addActionListener(e -> onUploadMaterial());
        viewStudentsBtn.addActionListener(e -> onViewStudents());
        reportsBtn.addActionListener(e -> onViewReports());
        logoutBtn.addActionListener(e -> onLogout());
        
        buttonPanel.add(addCourseBtn);
        buttonPanel.add(manageQuizBtn);
        buttonPanel.add(uploadMaterialBtn);
        buttonPanel.add(viewStudentsBtn);
        buttonPanel.add(reportsBtn);
        buttonPanel.add(logoutBtn);
        
        panel.add(titlePanel, BorderLayout.WEST);
        panel.add(buttonPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createCourseListPanel() {
        JPanel panel = ModernTheme.createCardPanel();
        panel.setPreferredSize(new Dimension(400, 0));
        panel.setLayout(new BorderLayout());
        
        JLabel titleLabel = ModernTheme.createSubheadingLabel("📚 Course Management");
        panel.add(titleLabel, BorderLayout.NORTH);
        
        coursesList = new JList<>(coursesModel);
        coursesList.setCellRenderer(new ModernCourseListRenderer());
        coursesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        coursesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Course selected = coursesList.getSelectedValue();
                if (selected != null) {
                    onCourseSelected(selected);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(coursesList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createActionPanel() {
        JPanel panel = ModernTheme.createCardPanel();
        panel.setLayout(new BorderLayout());
        
        JLabel titleLabel = ModernTheme.createSubheadingLabel("🎯 Course Actions");
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Action buttons panel
        JPanel actionPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        actionPanel.setOpaque(false);
        actionPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        JButton editCourseBtn = ModernTheme.createSecondaryButton("✏️ Edit Course");
        JButton deleteCourseBtn = ModernTheme.createSecondaryButton("🗑️ Delete Course");
        JButton viewCourseBtn = ModernTheme.createSecondaryButton("👁️ View Course");
        JButton refreshBtn = ModernTheme.createSecondaryButton("🔄 Refresh");
        
        editCourseBtn.addActionListener(e -> onEditCourse());
        deleteCourseBtn.addActionListener(e -> onDeleteCourse());
        viewCourseBtn.addActionListener(e -> onViewCourse());
        refreshBtn.addActionListener(e -> loadCourses());
        
        actionPanel.add(editCourseBtn);
        actionPanel.add(deleteCourseBtn);
        actionPanel.add(viewCourseBtn);
        actionPanel.add(refreshBtn);
        
        panel.add(actionPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);
        
        JLabel footerLabel = ModernTheme.createBodyLabel("E-Learning Management System - Admin Panel");
        footerLabel.setForeground(ModernTheme.TEXT_SECONDARY);
        panel.add(footerLabel);
        
        return panel;
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
    
    private void onAddCourse() {
        System.out.println("Add Course button clicked!"); // Debug output
        
        // Ensure directories exist
        com.elearn.util.FileUtils.ensureDirectoriesExist();
        
        BasicCourseDialog dialog = new BasicCourseDialog(this, "Add New Course", null);
        System.out.println("Dialog created, showing..."); // Debug output
        if (dialog.isOkPressed()) {
            Course course = dialog.getCourse();
            try {
                boolean saved = courseDAO.create(course);
                if (saved) {
                    JOptionPane.showMessageDialog(this, "Course added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadCourses();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add course", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding course: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void onEditCourse() {
        Course course = coursesList.getSelectedValue();
        if (course == null) {
            JOptionPane.showMessageDialog(this, "Please select a course to edit", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Ensure directories exist
        com.elearn.util.FileUtils.ensureDirectoriesExist();
        
        BasicCourseDialog dialog = new BasicCourseDialog(this, "Edit Course", course);
        if (dialog.isOkPressed()) {
            Course updatedCourse = dialog.getCourse();
            updatedCourse.setCourseId(course.getCourseId());
            try {
                boolean updated = courseDAO.update(updatedCourse);
                if (updated) {
                    JOptionPane.showMessageDialog(this, "Course updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadCourses();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update course", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error updating course: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void onDeleteCourse() {
        Course course = coursesList.getSelectedValue();
        if (course == null) {
            JOptionPane.showMessageDialog(this, "Please select a course to delete", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete the course: " + course.getTitle() + "?\n" +
                "This will also delete all materials and enrollments for this course.",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean deleted = courseDAO.delete(course.getCourseId());
                if (deleted) {
                    JOptionPane.showMessageDialog(this, "Course deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadCourses();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete course", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting course: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void onViewCourse() {
        Course course = coursesList.getSelectedValue();
        if (course == null) {
            JOptionPane.showMessageDialog(this, "Please select a course to view", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        new CourseDetailsFrame(course).setVisible(true);
    }
    
    private void onCourseSelected(Course course) {
        // Handle course selection if needed
    }
    
    private void onManageQuiz() {
        com.elearn.util.FileUtils.ensureDirectoriesExist();
        QuizManagementFrame quizFrame = new QuizManagementFrame();
        quizFrame.setVisible(true);
    }
    
    private void onUploadMaterial() {
        com.elearn.util.FileUtils.ensureDirectoriesExist();
        new MaterialUploadFrame().setVisible(true);
    }
    
    private void onViewStudents() {
        new StudentManagementFrame().setVisible(true);
    }
    
    private void onViewReports() {
        // Use the admin view constructor we created
        new StudentProgressFrame(true).setVisible(true);
    }
    
    private void onLogout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new AdminLoginFrame().setVisible(true);
        }
    }
    
    // Modern course list renderer
    private static class ModernCourseListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Course) {
                Course course = (Course) value;
                setText("<html><div style='padding: 8px;'>" +
                       "<b style='color: " + String.format("#%06x", ModernTheme.PRIMARY_COLOR.getRGB() & 0xFFFFFF) + ";'>" + 
                       course.getTitle() + "</b><br>" +
                       "<span style='color: " + String.format("#%06x", ModernTheme.TEXT_SECONDARY.getRGB() & 0xFFFFFF) + ";'>" +
                       "👨‍🏫 " + course.getInstructor() + "</span><br>" +
                       "<span style='color: " + String.format("#%06x", ModernTheme.TEXT_SECONDARY.getRGB() & 0xFFFFFF) + "; font-size: 11px;'>" +
                       course.getDescription() + "</span></div></html>");
            }
            return this;
        }
    }
}
