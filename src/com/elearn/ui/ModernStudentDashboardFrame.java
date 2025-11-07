package com.elearn.ui;

import com.elearn.model.Course;
import com.elearn.model.Student;
import com.elearn.service.CourseService;
import com.elearn.util.ModernTheme;

import javax.swing.*;
import java.awt.*;

public class ModernStudentDashboardFrame extends JFrame {
    private final Student student;
    private final CourseService courseService = new CourseService();
    private final DefaultListModel<Course> courseListModel = new DefaultListModel<>();
    private JList<Course> courseList;

    public ModernStudentDashboardFrame(Student student) {
        this.student = student;
        setTitle("Student Dashboard - " + student.getName());
        setSize(1000, 700);
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
        
        // Right panel - Actions
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
        
        JLabel welcomeLabel = ModernTheme.createHeadingLabel("Welcome, " + student.getName());
        JLabel roleLabel = ModernTheme.createBodyLabel("Student");
        roleLabel.setForeground(ModernTheme.TEXT_SECONDARY);
        
        titlePanel.add(welcomeLabel);
        titlePanel.add(Box.createHorizontalStrut(20));
        titlePanel.add(roleLabel);
        
        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        
        JButton materialsBtn = ModernTheme.createModernButton("📖 Reading Materials");
        JButton certificatesBtn = ModernTheme.createSecondaryButton("🏆 My Certificates");
        JButton achievementsBtn = ModernTheme.createSecondaryButton("🎯 Achievements");
        JButton logoutBtn = ModernTheme.createSecondaryButton("🚪 Logout");
        
        materialsBtn.addActionListener(e -> onMaterials());
        certificatesBtn.addActionListener(e -> onCertificates());
        achievementsBtn.addActionListener(e -> onAchievements());
        logoutBtn.addActionListener(e -> onLogout());
        
        buttonPanel.add(materialsBtn);
        buttonPanel.add(certificatesBtn);
        buttonPanel.add(achievementsBtn);
        buttonPanel.add(logoutBtn);
        
        panel.add(titlePanel, BorderLayout.WEST);
        panel.add(buttonPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createCourseListPanel() {
        JPanel panel = ModernTheme.createCardPanel();
        panel.setPreferredSize(new Dimension(400, 0));
        panel.setLayout(new BorderLayout());
        
        JLabel titleLabel = ModernTheme.createSubheadingLabel("📚 Available Courses");
        panel.add(titleLabel, BorderLayout.NORTH);
        
        courseList = new JList<>(courseListModel);
        courseList.setCellRenderer(new ModernCourseListRenderer());
        courseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(courseList);
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
        
        JButton enrollBtn = ModernTheme.createModernButton("📝 Enroll in Course");
        JButton quizBtn = ModernTheme.createSecondaryButton("📋 Take Quiz");
        JButton refreshBtn = ModernTheme.createSecondaryButton("🔄 Refresh");
        
        enrollBtn.addActionListener(e -> onEnroll());
        quizBtn.addActionListener(e -> onQuiz());
        refreshBtn.addActionListener(e -> loadCourses());
        
        actionPanel.add(enrollBtn);
        actionPanel.add(quizBtn);
        actionPanel.add(refreshBtn);
        
        panel.add(actionPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);
        
        JLabel footerLabel = ModernTheme.createBodyLabel("E-Learning Management System - Student Portal");
        footerLabel.setForeground(ModernTheme.TEXT_SECONDARY);
        panel.add(footerLabel);
        
        return panel;
    }
    
    private void loadCourses() {
        courseListModel.clear();
        try {
            java.util.List<Course> courses = courseService.getAllCourses();
            for (Course c : courses) {
                courseListModel.addElement(c);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading courses: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void onEnroll() {
        Course c = courseList.getSelectedValue();
        if (c == null) {
            JOptionPane.showMessageDialog(this, "Please select a course first", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            boolean ok = courseService.enroll(student.getStudentId(), c.getCourseId());
            JOptionPane.showMessageDialog(this, ok ? "Enrolled successfully!" : "Already enrolled or failed", 
                ok ? "Success" : "Info", ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error enrolling: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void onQuiz() {
        Course c = courseList.getSelectedValue();
        if (c == null) {
            JOptionPane.showMessageDialog(this, "Please select a course first", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        new QuizFrame(student.getStudentId(), c).setVisible(true);
    }
    
    
    private void onMaterials() {
        new MaterialReadingFrame(student.getStudentId()).setVisible(true);
    }
    
    private void onCertificates() {
        new CertificateFrame(student.getStudentId()).setVisible(true);
    }
    
    private void onAchievements() {
        new AchievementsFrame(student.getStudentId()).setVisible(true);
    }
    
    private void onLogout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
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
