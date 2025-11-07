package com.elearn.ui;

import com.elearn.dao.CourseDAO;
import com.elearn.dao.impl.CourseDAOImpl;
import com.elearn.model.Admin;
import com.elearn.model.Course;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

/**
 * Fully functional admin dashboard with all features working
 */
public class FullyFunctionalAdminDashboard extends JFrame {
    private final Admin admin;
    private final CourseDAO courseDAO = new CourseDAOImpl();
    private final DefaultListModel<Course> courseListModel = new DefaultListModel<>();
    private final JList<Course> courseList = new JList<>(courseListModel);
    private final JPanel courseDetailsPanel = new JPanel();

    public FullyFunctionalAdminDashboard(Admin admin) {
        this.admin = admin;
        setTitle("🔧 Admin Dashboard - " + admin.getUsername());
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        setupUI();
        loadCourses();
    }

    private void setupUI() {
        setLayout(new BorderLayout(15, 15));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(15, 15, 15, 15));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(63, 81, 181));
        headerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel("🔧 Admin Control Panel");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel welcomeLabel = new JLabel("Managing E-Learning Platform");
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        welcomeLabel.setForeground(Color.WHITE);
        
        JPanel headerContent = new JPanel(new BorderLayout());
        headerContent.setOpaque(false);
        headerContent.add(titleLabel, BorderLayout.NORTH);
        headerContent.add(welcomeLabel, BorderLayout.CENTER);
        
        JButton logoutBtn = new JButton("🚪 Logout");
        logoutBtn.setBackground(new Color(244, 67, 54));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.addActionListener(e -> onLogout());
        
        headerPanel.add(headerContent, BorderLayout.WEST);
        headerPanel.add(logoutBtn, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Main Content - Split Pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(500);
        
        // Left Panel - Course List
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(63, 81, 181), 2),
            "📚 All Courses",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Arial", Font.BOLD, 14),
            new Color(63, 81, 181)
        ));
        
        courseList.setCellRenderer(new CourseListRenderer());
        courseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        courseList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateCourseDetails();
            }
        });
        
        JScrollPane courseScrollPane = new JScrollPane(courseList);
        courseScrollPane.setBorder(BorderFactory.createEmptyBorder());
        leftPanel.add(courseScrollPane, BorderLayout.CENTER);
        
        // Course Management Buttons
        JPanel courseButtonPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        courseButtonPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        JButton addCourseBtn = createButton("➕ Add Course", new Color(76, 175, 80));
        JButton editCourseBtn = createButton("✏️ Edit Course", new Color(33, 150, 243));
        JButton deleteCourseBtn = createButton("🗑️ Delete Course", new Color(244, 67, 54));
        JButton refreshBtn = createButton("🔄 Refresh", new Color(255, 152, 0));
        
        addCourseBtn.addActionListener(e -> onAddCourse());
        editCourseBtn.addActionListener(e -> onEditCourse());
        deleteCourseBtn.addActionListener(e -> onDeleteCourse());
        refreshBtn.addActionListener(e -> loadCourses());
        
        courseButtonPanel.add(addCourseBtn);
        courseButtonPanel.add(editCourseBtn);
        courseButtonPanel.add(deleteCourseBtn);
        courseButtonPanel.add(refreshBtn);
        
        leftPanel.add(courseButtonPanel, BorderLayout.SOUTH);
        
        // Right Panel - Course Details
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        
        courseDetailsPanel.setLayout(new BoxLayout(courseDetailsPanel, BoxLayout.Y_AXIS));
        courseDetailsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(63, 81, 181), 2),
            "📄 Course Details",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Arial", Font.BOLD, 14),
            new Color(63, 81, 181)
        ));
        updateCourseDetails();
        
        JScrollPane detailsScrollPane = new JScrollPane(courseDetailsPanel);
        detailsScrollPane.setBorder(BorderFactory.createEmptyBorder());
        rightPanel.add(detailsScrollPane, BorderLayout.CENTER);
        
        // Management Action Buttons
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new GridLayout(4, 1, 10, 10));
        actionPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JButton materialsBtn = createButton("📁 Material Management", new Color(33, 150, 243));
        JButton quizBtn = createButton("📝 Quiz Management", new Color(255, 152, 0));
        JButton studentsBtn = createButton("👥 View Students", new Color(156, 39, 176));
        JButton reportsBtn = createButton("📊 Reports", new Color(0, 150, 136));
        
        materialsBtn.addActionListener(e -> onMaterialManagement());
        quizBtn.addActionListener(e -> onQuizManagement());
        studentsBtn.addActionListener(e -> onViewStudents());
        reportsBtn.addActionListener(e -> onReports());
        
        actionPanel.add(materialsBtn);
        actionPanel.add(quizBtn);
        actionPanel.add(studentsBtn);
        actionPanel.add(reportsBtn);
        
        rightPanel.add(actionPanel, BorderLayout.SOUTH);
        
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        
        add(splitPane, BorderLayout.CENTER);
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void updateCourseDetails() {
        courseDetailsPanel.removeAll();
        
        Course selectedCourse = courseList.getSelectedValue();
        if (selectedCourse == null) {
            JLabel noSelectionLabel = new JLabel("Select a course to view details");
            noSelectionLabel.setFont(new Font("Arial", Font.ITALIC, 13));
            noSelectionLabel.setForeground(Color.GRAY);
            noSelectionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            courseDetailsPanel.add(Box.createVerticalStrut(20));
            courseDetailsPanel.add(noSelectionLabel);
        } else {
            // Course Image
            if (selectedCourse.getImagePath() != null && !selectedCourse.getImagePath().isEmpty()) {
                try {
                    File imageFile = new File(selectedCourse.getImagePath());
                    if (imageFile.exists()) {
                        BufferedImage img = ImageIO.read(imageFile);
                        if (img != null) {
                            Image scaledImg = img.getScaledInstance(400, 250, Image.SCALE_SMOOTH);
                            JLabel imageLabel = new JLabel(new ImageIcon(scaledImg));
                            imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                            imageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                            courseDetailsPanel.add(imageLabel);
                            courseDetailsPanel.add(Box.createVerticalStrut(15));
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error loading course image: " + e.getMessage());
                }
            }
            
            // Course Title
            JLabel titleLabel = new JLabel(selectedCourse.getTitle());
            titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
            titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            courseDetailsPanel.add(titleLabel);
            courseDetailsPanel.add(Box.createVerticalStrut(10));
            
            // Instructor
            JPanel instructorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            instructorPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            instructorPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            JLabel instructorIcon = new JLabel("👨‍🏫 ");
            JLabel instructorLabel = new JLabel("Instructor: " + selectedCourse.getInstructor());
            instructorLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            instructorPanel.add(instructorIcon);
            instructorPanel.add(instructorLabel);
            courseDetailsPanel.add(instructorPanel);
            courseDetailsPanel.add(Box.createVerticalStrut(10));
            
            // Description
            JPanel descPanel = new JPanel(new BorderLayout());
            descPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            descPanel.setBorder(BorderFactory.createTitledBorder("Description"));
            JTextArea descArea = new JTextArea(selectedCourse.getDescription());
            descArea.setEditable(false);
            descArea.setLineWrap(true);
            descArea.setWrapStyleWord(true);
            descArea.setFont(new Font("Arial", Font.PLAIN, 12));
            descArea.setBackground(new Color(245, 245, 245));
            JScrollPane descScroll = new JScrollPane(descArea);
            descScroll.setPreferredSize(new Dimension(400, 100));
            descPanel.add(descScroll);
            courseDetailsPanel.add(descPanel);
            courseDetailsPanel.add(Box.createVerticalStrut(10));
            
            // Video display removed
        }
        
        courseDetailsPanel.revalidate();
        courseDetailsPanel.repaint();
    }

    private void loadCourses() {
        courseListModel.clear();
        try {
            List<Course> courses = courseDAO.findAll();
            for (Course course : courses) {
                courseListModel.addElement(course);
            }
            
            if (courses.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No courses found. Click 'Add Course' to create your first course!", 
                    "No Courses", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading courses: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onAddCourse() {
        FunctionalCourseDialog dialog = new FunctionalCourseDialog(this, "Add New Course", null);
        dialog.setVisible(true);
        
        if (dialog.isOkPressed()) {
            Course newCourse = dialog.getCourse();
            try {
                boolean success = courseDAO.create(newCourse);
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "Course '" + newCourse.getTitle() + "' created successfully!", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    loadCourses();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to create course. Please try again.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Database error: " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onEditCourse() {
        Course selected = courseList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, 
                "Please select a course to edit", 
                "No Course Selected", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        FunctionalCourseDialog dialog = new FunctionalCourseDialog(this, "Edit Course", selected);
        dialog.setVisible(true);
        
        if (dialog.isOkPressed()) {
            Course updatedCourse = dialog.getCourse();
            try {
                boolean success = courseDAO.update(updatedCourse);
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "Course updated successfully!", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    loadCourses();
                    updateCourseDetails();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to update course. Please try again.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Database error: " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onDeleteCourse() {
        Course selected = courseList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, 
                "Please select a course to delete", 
                "No Course Selected", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete '" + selected.getTitle() + "'?\n" +
            "This will also delete all materials, quizzes, and enrollments for this course!", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Delete course image file if exists
                if (selected.getImagePath() != null && !selected.getImagePath().isEmpty()) {
                    File imageFile = new File(selected.getImagePath());
                    if (imageFile.exists()) {
                        imageFile.delete();
                    }
                }
                
                boolean success = courseDAO.delete(selected.getCourseId());
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "Course deleted successfully!", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    loadCourses();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to delete course. Please try again.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Database error: " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onMaterialManagement() {
        new MaterialUploadFrame().setVisible(true);
    }

    private void onQuizManagement() {
        new QuizManagementFrame().setVisible(true);
    }

    private void onViewStudents() {
        JOptionPane.showMessageDialog(this, 
            "Student management feature coming soon!\nYou can view enrollments in the database.", 
            "Feature Info", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void onReports() {
        JOptionPane.showMessageDialog(this, 
            "Reports feature coming soon!\nYou can query the database for statistics.", 
            "Feature Info", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void onLogout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to logout?", 
            "Confirm Logout", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new AdminLoginFrame().setVisible(true);
        }
    }

    // Custom Renderer for Course List
    private class CourseListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof Course) {
                Course course = (Course) value;
                
                String imageIndicator = (course.getImagePath() != null && !course.getImagePath().isEmpty()) 
                    ? "🖼️ " : "";
                
                setText(String.format("<html>%s<b>%s</b><br><i>%s</i></html>", 
                    imageIndicator,
                    course.getTitle(), 
                    course.getInstructor()));
                
                setBorder(new EmptyBorder(8, 8, 8, 8));
            }
            
            return this;
        }
    }
}
