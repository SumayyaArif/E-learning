package com.elearn.ui;

import com.elearn.model.Course;
import com.elearn.model.Student;
import com.elearn.service.CourseService;
import com.elearn.service.EnrollmentService;
import com.elearn.service.EnrollmentService.EnrollmentProgress;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Enhanced Student Dashboard with progress tracking
 */
public class EnhancedStudentDashboardFrame extends JFrame {
    private final Student student;
    private final CourseService courseService = new CourseService();
    private final EnrollmentService enrollmentService = new EnrollmentService();
    private final DefaultListModel<Course> courseListModel = new DefaultListModel<>();
    private final JList<Course> courseList = new JList<>(courseListModel);
    private final JPanel progressPanel = new JPanel();

    public EnhancedStudentDashboardFrame(Student student) {
        this.student = student;
        setTitle("📚 Welcome, " + student.getName() + " - E-Learning Dashboard");
        setSize(1000, 700);
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
        headerPanel.setBackground(new Color(33, 150, 243));
        headerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel("🎓 Student Learning Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel welcomeLabel = new JLabel("Welcome back, " + student.getName() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        welcomeLabel.setForeground(Color.WHITE);
        
        JPanel headerContent = new JPanel(new BorderLayout());
        headerContent.setOpaque(false);
        headerContent.add(titleLabel, BorderLayout.NORTH);
        headerContent.add(welcomeLabel, BorderLayout.CENTER);
        
        headerPanel.add(headerContent, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Main Content - Split Pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(600);
        
        // Left Panel - Available Courses
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(33, 150, 243), 2),
            "📖 Available Courses",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Arial", Font.BOLD, 14),
            new Color(33, 150, 243)
        ));
        
        courseList.setCellRenderer(new EnhancedCourseRenderer());
        courseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        courseList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateProgressPanel();
            }
        });
        
        JScrollPane courseScrollPane = new JScrollPane(courseList);
        courseScrollPane.setBorder(BorderFactory.createEmptyBorder());
        leftPanel.add(courseScrollPane, BorderLayout.CENTER);
        
        // Buttons for course actions
        JPanel courseButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        JButton enrollBtn = new JButton("✅ Enroll in Course");
        
        enrollBtn.addActionListener(e -> onEnroll());
        
        courseButtonPanel.add(enrollBtn);
        leftPanel.add(courseButtonPanel, BorderLayout.SOUTH);
        
        // Right Panel - Progress and Actions
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        
        // Progress Display
        progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));
        progressPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(76, 175, 80), 2),
            "📊 Course Progress",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Arial", Font.BOLD, 14),
            new Color(76, 175, 80)
        ));
        updateProgressPanel();
        
        JScrollPane progressScrollPane = new JScrollPane(progressPanel);
        progressScrollPane.setBorder(BorderFactory.createEmptyBorder());
        rightPanel.add(progressScrollPane, BorderLayout.CENTER);
        
        // Action Buttons
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new GridLayout(5, 1, 10, 10));
        actionPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JButton materialsBtn = createActionButton("📖 Reading Materials", new Color(33, 150, 243));
        JButton quizBtn = createActionButton("📝 Take Quiz", new Color(255, 152, 0));
        JButton certificatesBtn = createActionButton("🏆 My Certificates", new Color(255, 193, 7));
        JButton achievementsBtn = createActionButton("⭐ Achievements", new Color(156, 39, 176));
        JButton logoutBtn = createActionButton("🚪 Logout", new Color(244, 67, 54));
        
        materialsBtn.addActionListener(e -> onMaterials());
        quizBtn.addActionListener(e -> onQuiz());
        certificatesBtn.addActionListener(e -> onCertificates());
        achievementsBtn.addActionListener(e -> onAchievements());
        logoutBtn.addActionListener(e -> onLogout());
        
        actionPanel.add(materialsBtn);
        actionPanel.add(quizBtn);
        actionPanel.add(certificatesBtn);
        actionPanel.add(achievementsBtn);
        actionPanel.add(logoutBtn);
        
        rightPanel.add(actionPanel, BorderLayout.SOUTH);
        
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        
        add(splitPane, BorderLayout.CENTER);
    }

    private JButton createActionButton(String text, Color color) {
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

    private void updateProgressPanel() {
        progressPanel.removeAll();
        
        Course selectedCourse = courseList.getSelectedValue();
        if (selectedCourse == null) {
            JLabel noSelectionLabel = new JLabel("Select a course to view progress");
            noSelectionLabel.setFont(new Font("Arial", Font.ITALIC, 13));
            noSelectionLabel.setForeground(Color.GRAY);
            noSelectionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            progressPanel.add(Box.createVerticalStrut(20));
            progressPanel.add(noSelectionLabel);
        } else {
            try {
                EnrollmentProgress progress = enrollmentService.getProgress(
                    student.getStudentId(), 
                    selectedCourse.getCourseId()
                );
                
                // Course Title
                JLabel titleLabel = new JLabel(selectedCourse.getTitle());
                titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
                titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                progressPanel.add(titleLabel);
                progressPanel.add(Box.createVerticalStrut(10));
                
                // Overall Progress
                progressPanel.add(createProgressSection(
                    "Overall Progress",
                    progress.getProgressPercentage(),
                    new Color(76, 175, 80)
                ));
                
                progressPanel.add(Box.createVerticalStrut(15));
                
                // Materials Progress
                int materialsPercent = progress.getTotalMaterials() > 0 
                    ? (progress.getCompletedMaterials() * 100) / progress.getTotalMaterials()
                    : 0;
                    
                progressPanel.add(createProgressSection(
                    String.format("Reading Materials (%d/%d)", 
                        progress.getCompletedMaterials(), 
                        progress.getTotalMaterials()),
                    materialsPercent,
                    new Color(33, 150, 243)
                ));
                
                progressPanel.add(Box.createVerticalStrut(15));
                
                // Quiz Status
                JPanel quizStatusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                quizStatusPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                quizStatusPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
                
                JLabel quizLabel = new JLabel("Quiz Status: ");
                quizLabel.setFont(new Font("Arial", Font.BOLD, 13));
                
                JLabel quizStatus = new JLabel(
                    progress.isQuizPassed() ? "✅ Passed" : "⏳ Not Completed"
                );
                quizStatus.setFont(new Font("Arial", Font.PLAIN, 13));
                quizStatus.setForeground(progress.isQuizPassed() 
                    ? new Color(76, 175, 80) 
                    : new Color(255, 152, 0));
                
                quizStatusPanel.add(quizLabel);
                quizStatusPanel.add(quizStatus);
                progressPanel.add(quizStatusPanel);
                
                progressPanel.add(Box.createVerticalStrut(15));
                
                // Completion Status
                if (progress.isCompleted()) {
                    JPanel completionPanel = new JPanel();
                    completionPanel.setBackground(new Color(76, 175, 80));
                    completionPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
                    completionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    completionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
                    
                    JLabel completionLabel = new JLabel("🎉 Course Completed! Certificate Issued!");
                    completionLabel.setFont(new Font("Arial", Font.BOLD, 14));
                    completionLabel.setForeground(Color.WHITE);
                    completionPanel.add(completionLabel);
                    
                    progressPanel.add(completionPanel);
                }
                
            } catch (Exception e) {
                JLabel errorLabel = new JLabel("Error loading progress: " + e.getMessage());
                errorLabel.setForeground(Color.RED);
                progressPanel.add(errorLabel);
            }
        }
        
        progressPanel.revalidate();
        progressPanel.repaint();
    }

    private JPanel createProgressSection(String title, int percentage, Color color) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 13));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.add(titleLabel);
        
        section.add(Box.createVerticalStrut(5));
        
        JPanel progressBarPanel = new JPanel(new BorderLayout());
        progressBarPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        progressBarPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(percentage);
        progressBar.setStringPainted(true);
        progressBar.setString(percentage + "%");
        progressBar.setForeground(color);
        progressBar.setFont(new Font("Arial", Font.BOLD, 11));
        
        progressBarPanel.add(progressBar, BorderLayout.CENTER);
        section.add(progressBarPanel);
        
        return section;
    }

    private void loadCourses() {
        courseListModel.clear();
        List<Course> courses = courseService.getAllCourses();
        for (Course c : courses) {
            courseListModel.addElement(c);
        }
    }

    private void onEnroll() {
        Course c = courseList.getSelectedValue();
        if (c == null) {
            JOptionPane.showMessageDialog(this, "Please select a course first", "No Course Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        boolean success = enrollmentService.enrollStudent(student.getStudentId(), c.getCourseId());
        if (success) {
            JOptionPane.showMessageDialog(this, "Successfully enrolled in " + c.getTitle() + "!", "Enrollment Success", JOptionPane.INFORMATION_MESSAGE);
            updateProgressPanel();
        } else {
            JOptionPane.showMessageDialog(this, "Already enrolled or enrollment failed", "Enrollment Failed", JOptionPane.WARNING_MESSAGE);
        }
    }


    private void onQuiz() {
        Course c = courseList.getSelectedValue();
        if (c == null) {
            JOptionPane.showMessageDialog(this, "Please select a course first", "No Course Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Check if student can take quiz
        if (!enrollmentService.canTakeQuiz(student.getStudentId(), c.getCourseId())) {
            JOptionPane.showMessageDialog(this, 
                "You must complete all reading materials before taking the quiz.\nPlease visit 'Reading Materials' to complete them.", 
                "Materials Not Completed", 
                JOptionPane.WARNING_MESSAGE);
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
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to logout?", 
            "Confirm Logout", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }

    // Custom Renderer for Course List with Image Support
    private class EnhancedCourseRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof Course) {
                Course course = (Course) value;
                
                // Get progress
                EnrollmentProgress progress = null;
                try {
                    progress = enrollmentService.getProgress(student.getStudentId(), course.getCourseId());
                } catch (Exception e) {
                    // Ignore
                }
                
                String status = "";
                if (progress != null && progress.isCompleted()) {
                    status = " ✅";
                } else if (progress != null && progress.getProgressPercentage() > 0) {
                    status = " 🔄 " + progress.getProgressPercentage() + "%";
                }
                
                // Try to load thumbnail if image exists
                if (course.getImagePath() != null && !course.getImagePath().isEmpty()) {
                    try {
                        java.io.File imageFile = new java.io.File(course.getImagePath());
                        if (imageFile.exists()) {
                            java.awt.image.BufferedImage img = javax.imageio.ImageIO.read(imageFile);
                            if (img != null) {
                                Image thumbnail = img.getScaledInstance(60, 40, Image.SCALE_SMOOTH);
                                setIcon(new ImageIcon(thumbnail));
                            }
                        }
                    } catch (Exception e) {
                        // Ignore image loading errors
                    }
                }
                
                setText(String.format("<html><b>%s</b>%s<br><i>%s</i></html>", 
                    course.getTitle(), 
                    status,
                    course.getInstructor()));
                
                setBorder(new EmptyBorder(8, 8, 8, 8));
            }
            
            return this;
        }
    }
}
