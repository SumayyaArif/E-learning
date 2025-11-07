package com.elearn.ui;

import com.elearn.model.Admin;
import com.elearn.model.Course;
import com.elearn.dao.CourseDAO;
import com.elearn.dao.impl.CourseDAOImpl;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AdminDashboardFrame extends JFrame {
    private final CourseDAO courseDAO = new CourseDAOImpl();
    private final DefaultListModel<Course> coursesModel = new DefaultListModel<>();

    public AdminDashboardFrame(Admin admin) {
        setTitle("Admin Dashboard - " + admin.getUsername());
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        createUI();
        loadCourses();
    }
    
    private void createUI() {
        setLayout(new BorderLayout(10, 10));
        
        // Top panel with buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addCourseBtn = new JButton("➕ Add Course");
        JButton manageQuizBtn = new JButton("📝 Manage Quiz");
        JButton uploadMaterialBtn = new JButton("📁 Upload Material");
        JButton viewStudentsBtn = new JButton("👨‍🎓 View Students");
        JButton viewReportsBtn = new JButton("📊 Reports");
        JButton logoutBtn = new JButton("🚪 Logout");
        
        addCourseBtn.addActionListener(e -> onAddCourse());
        manageQuizBtn.addActionListener(e -> onManageQuiz());
        uploadMaterialBtn.addActionListener(e -> onUploadMaterial());
        viewStudentsBtn.addActionListener(e -> onViewStudents());
        viewReportsBtn.addActionListener(e -> onViewReports());
        logoutBtn.addActionListener(e -> onLogout());
        
        topPanel.add(addCourseBtn);
        topPanel.add(manageQuizBtn);
        topPanel.add(uploadMaterialBtn);
        topPanel.add(viewStudentsBtn);
        topPanel.add(viewReportsBtn);
        topPanel.add(logoutBtn);
        
        // Center panel with course list
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Courses"));
        
        JList<Course> coursesList = new JList<>(coursesModel);
        coursesList.setCellRenderer(new CourseListRenderer());
        coursesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Course selected = coursesList.getSelectedValue();
                if (selected != null) {
                    onCourseSelected(selected);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(coursesList);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Bottom panel with course actions
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton editCourseBtn = new JButton("✏️ Edit Course");
        JButton deleteCourseBtn = new JButton("🗑️ Delete Course");
        JButton viewCourseBtn = new JButton("👁️ View Course");
        
        editCourseBtn.addActionListener(e -> onEditCourse(coursesList.getSelectedValue()));
        deleteCourseBtn.addActionListener(e -> onDeleteCourse(coursesList.getSelectedValue()));
        viewCourseBtn.addActionListener(e -> onViewCourse(coursesList.getSelectedValue()));
        
        bottomPanel.add(editCourseBtn);
        bottomPanel.add(deleteCourseBtn);
        bottomPanel.add(viewCourseBtn);
        
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
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
        CourseDialog dialog = new CourseDialog(this, "Add New Course", null);
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
                JOptionPane.showMessageDialog(this, "Error adding course: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void onEditCourse(Course course) {
        if (course == null) {
            JOptionPane.showMessageDialog(this, "Please select a course to edit", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        CourseDialog dialog = new CourseDialog(this, "Edit Course", course);
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
                JOptionPane.showMessageDialog(this, "Error updating course: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void onDeleteCourse(Course course) {
        if (course == null) {
            JOptionPane.showMessageDialog(this, "Please select a course to delete", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete the course: " + course.getTitle() + "?", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
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
                JOptionPane.showMessageDialog(this, "Error deleting course: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void onViewCourse(Course course) {
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
        new QuizManagementFrame().setVisible(true);
    }
    
    private void onUploadMaterial() {
        new MaterialUploadFrame().setVisible(true);
    }
    
    private void onLogout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
    
    private void onViewStudents() {
        // Open student management showing only enrolled students by default for admins
        new StudentManagementFrame(true).setVisible(true);
    }
    
    private void onViewReports() {
        new ReportsFrame().setVisible(true);
    }
    
    // Custom renderer for course list
    private static class CourseListRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Course) {
                Course course = (Course) value;
                setText("<html><b>" + course.getTitle() + "</b><br>" + 
                       "Instructor: " + course.getInstructor() + "<br>" +
                       "<i>" + course.getDescription() + "</i></html>");
            }
            return this;
        }
    }
}


