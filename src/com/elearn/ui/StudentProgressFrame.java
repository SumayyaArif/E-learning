package com.elearn.ui;

import com.elearn.dao.StudentDAO;
import com.elearn.dao.EnrollmentDAO;
import com.elearn.dao.QuizDAO;
import com.elearn.dao.MaterialDAO;
import com.elearn.dao.impl.StudentDAOImpl;
import com.elearn.dao.impl.EnrollmentDAOImpl;
import com.elearn.dao.impl.QuizDAOImpl;
import com.elearn.dao.impl.MaterialDAOImpl;
import com.elearn.model.Student;
import com.elearn.model.Enrollment;
import com.elearn.model.Course;
import com.elearn.model.QuizResult;
import com.elearn.model.MaterialCompletion;
import com.elearn.util.ModernTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentProgressFrame extends JFrame {
    private int studentId; // Changed from final to allow admin view
    private final StudentDAO studentDAO = new StudentDAOImpl();
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAOImpl();
    private final QuizDAO quizDAO = new QuizDAOImpl();
    private final MaterialDAO materialDAO = new MaterialDAOImpl();
    
    private JTable coursesTable;
    private DefaultTableModel coursesTableModel;
    private JTable quizTable;
    private DefaultTableModel quizTableModel;
    private JProgressBar overallProgressBar;
    private JLabel studentNameLabel;
    private boolean isAdminView = false;
    private JComboBox<Student> studentSelector;

    // Constructor for admin view - shows all students
    public StudentProgressFrame() {
        this.isAdminView = true;
        setTitle("Student Progress Reports - Admin View");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ModernTheme.styleFrame(this);
        
        createUI();
        loadAllStudents();
    }

    // Constructor for specific student view
    public StudentProgressFrame(int studentId) {
        this.studentId = studentId;
        setTitle("Student Progress");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ModernTheme.styleFrame(this);
        
        createUI();
        loadStudentData();
    }
    
    private void createUI() {
        setLayout(new BorderLayout(10, 10));
        
        // Header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // For admin view, add student selector
        if (isAdminView) {
            JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            selectorPanel.setOpaque(false);
            selectorPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            selectorPanel.add(new JLabel("Select Student:"));
            studentSelector = new JComboBox<>();
            studentSelector.setPreferredSize(new Dimension(250, 30));
            studentSelector.addActionListener(e -> {
                if (studentSelector.getSelectedItem() != null) {
                    Student selected = (Student) studentSelector.getSelectedItem();
                    studentId = selected.getStudentId();
                    loadStudentData();
                }
            });
            selectorPanel.add(studentSelector);
            
            add(selectorPanel, BorderLayout.NORTH);
            // Move header panel below selector
            add(headerPanel, BorderLayout.CENTER);
        } else {
            add(headerPanel, BorderLayout.NORTH);
        }
        
        // Main content panel with tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Courses tab
        JPanel coursesPanel = createCoursesPanel();
        tabbedPane.addTab("Enrolled Courses", coursesPanel);
        
        // Quiz results tab
        JPanel quizPanel = createQuizPanel();
        tabbedPane.addTab("Quiz Results", quizPanel);
        
        // Progress summary tab
        JPanel summaryPanel = createSummaryPanel();
        tabbedPane.addTab("Progress Summary", summaryPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Footer panel
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    private void loadAllStudents() {
        try {
            List<Student> students = studentDAO.findAll();
            if (students.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No students found in the system.", "No Data", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Populate student selector
            studentSelector.removeAllItems();
            for (Student student : students) {
                studentSelector.addItem(student);
            }
            
            // Select first student by default
            if (studentSelector.getItemCount() > 0) {
                studentSelector.setSelectedIndex(0);
                Student selected = (Student) studentSelector.getSelectedItem();
                studentId = selected.getStudentId();
                loadStudentData();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading students: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        
        studentNameLabel = ModernTheme.createHeadingLabel("Student Progress");
        
        JPanel progressPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        progressPanel.setOpaque(false);
        
        JLabel progressLabel = new JLabel("Overall Progress:");
        overallProgressBar = new JProgressBar(0, 100);
        overallProgressBar.setStringPainted(true);
        overallProgressBar.setPreferredSize(new Dimension(200, 20));
        
        progressPanel.add(progressLabel);
        progressPanel.add(overallProgressBar);
        
        panel.add(studentNameLabel, BorderLayout.WEST);
        panel.add(progressPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createCoursesPanel() {
        JPanel panel = ModernTheme.createCardPanel();
        panel.setLayout(new BorderLayout(10, 10));
        
        // Create table model with columns
        String[] columns = {"Course ID", "Course Title", "Enrollment Date", "Progress", "Quiz Score", "Status"};
        coursesTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        coursesTable = new JTable(coursesTableModel);
        coursesTable.setRowHeight(30);
        coursesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(coursesTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createQuizPanel() {
        JPanel panel = ModernTheme.createCardPanel();
        panel.setLayout(new BorderLayout(10, 10));
        
        // Create table model with columns
        String[] columns = {"Quiz ID", "Course", "Attempt Date", "Score", "Pass/Fail", "Certificate"};
        quizTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        quizTable = new JTable(quizTableModel);
        quizTable.setRowHeight(30);
        quizTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(quizTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createSummaryPanel() {
        JPanel panel = ModernTheme.createCardPanel();
        panel.setLayout(new BorderLayout(10, 10));
        
        JPanel statsPanel = new JPanel(new GridLayout(4, 2, 20, 20));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        statsPanel.add(ModernTheme.createBodyLabel("Total Courses Enrolled:"));
        statsPanel.add(ModernTheme.createValueLabel("0"));
        
        statsPanel.add(ModernTheme.createBodyLabel("Courses Completed:"));
        statsPanel.add(ModernTheme.createValueLabel("0"));
        
        statsPanel.add(ModernTheme.createBodyLabel("Quizzes Passed:"));
        statsPanel.add(ModernTheme.createValueLabel("0"));
        
        statsPanel.add(ModernTheme.createBodyLabel("Certificates Earned:"));
        statsPanel.add(ModernTheme.createValueLabel("0"));
        
        panel.add(statsPanel, BorderLayout.NORTH);
        
        return panel;
    }
    
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setOpaque(false);
        
        JButton generateReportBtn = ModernTheme.createModernButton("📊 Generate Report");
        JButton printBtn = ModernTheme.createSecondaryButton("🖨️ Print");
        JButton closeBtn = ModernTheme.createSecondaryButton("❌ Close");
        
        generateReportBtn.addActionListener(e -> generateReport());
        printBtn.addActionListener(e -> printReport());
        closeBtn.addActionListener(e -> dispose());
        
        panel.add(generateReportBtn);
        panel.add(printBtn);
        panel.add(closeBtn);
        
        return panel;
    }
    
    private void loadStudentData() {
        try {
            Student student = studentDAO.findById(studentId);
            if (student != null) {
                studentNameLabel.setText("Progress for: " + student.getName());
                
                // Load enrollments
                loadEnrollments(student);
                
                // Load quiz results
                loadQuizResults(student);
                
                // Update summary statistics
                updateSummaryStats(student);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading student data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void loadEnrollments(Student student) {
        coursesTableModel.setRowCount(0);
        
        try {
            List<Enrollment> enrollments = enrollmentDAO.findByStudentId(student.getStudentId());
            int totalProgress = 0;
            
            for (Enrollment enrollment : enrollments) {
                Course course = enrollment.getCourse();
                
                // Calculate progress percentage
                int progress = calculateCourseProgress(student.getStudentId(), course.getCourseId());
                totalProgress += progress;
                
                // Get latest quiz score
                int quizScore = getLatestQuizScore(student.getStudentId(), course.getCourseId());
                
                Object[] row = {
                    course.getCourseId(),
                    course.getTitle(),
                    enrollment.getEnrollmentDate(),
                    progress + "%",
                    quizScore + "%",
                    progress == 100 ? "Completed" : "In Progress"
                };
                
                coursesTableModel.addRow(row);
            }
            
            // Update overall progress
            if (!enrollments.isEmpty()) {
                overallProgressBar.setValue(totalProgress / enrollments.size());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading enrollments: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void loadQuizResults(Student student) {
        quizTableModel.setRowCount(0);
        
        try {
            List<QuizResult> results = quizDAO.findResultsByStudentId(student.getStudentId());
            
            for (QuizResult result : results) {
                Course course = result.getCourse();
                boolean passed = result.getScore() >= 70; // Assuming 70% is passing
                
                Object[] row = {
                    result.getQuizResultId(),
                    course.getTitle(),
                    result.getCompletionDate(),
                    result.getScore() + "%",
                    passed ? "PASS" : "FAIL",
                    passed ? "Available" : "N/A"
                };
                
                quizTableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading quiz results: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void updateSummaryStats(Student student) {
        try {
            List<Enrollment> enrollments = enrollmentDAO.findByStudentId(student.getStudentId());
            List<QuizResult> quizResults = quizDAO.findResultsByStudentId(student.getStudentId());
            
            int totalCourses = enrollments.size();
            int completedCourses = 0;
            int passedQuizzes = 0;
            int certificates = 0;
            
            for (Enrollment enrollment : enrollments) {
                int progress = calculateCourseProgress(student.getStudentId(), enrollment.getCourse().getCourseId());
                if (progress == 100) {
                    completedCourses++;
                }
            }
            
            for (QuizResult result : quizResults) {
                if (result.getScore() >= 70) { // Assuming 70% is passing
                    passedQuizzes++;
                    certificates++;
                }
            }
            
            // Update summary panel
            JPanel summaryPanel = (JPanel) ((JTabbedPane) getContentPane().getComponent(1)).getComponentAt(2);
            JPanel statsPanel = (JPanel) summaryPanel.getComponent(0);
            
            ((JLabel) statsPanel.getComponent(1)).setText(String.valueOf(totalCourses));
            ((JLabel) statsPanel.getComponent(3)).setText(String.valueOf(completedCourses));
            ((JLabel) statsPanel.getComponent(5)).setText(String.valueOf(passedQuizzes));
            ((JLabel) statsPanel.getComponent(7)).setText(String.valueOf(certificates));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private int calculateCourseProgress(int studentId, int courseId) {
        try {
            // Get total materials for the course
            List<MaterialCompletion> completions = materialDAO.findCompletionsByStudentAndCourse(studentId, courseId);
            int totalMaterials = materialDAO.findByCourseId(courseId).size();
            
            if (totalMaterials == 0) return 0;
            
            return (completions.size() * 100) / totalMaterials;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    private int getLatestQuizScore(int studentId, int courseId) {
        try {
            List<QuizResult> results = quizDAO.findResultsByStudentAndCourse(studentId, courseId);
            if (results.isEmpty()) return 0;
            
            // Return the latest result
            return results.get(results.size() - 1).getScore();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    private void generateReport() {
        JOptionPane.showMessageDialog(this, "Report generation functionality would be implemented here", "Generate Report", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void printReport() {
        JOptionPane.showMessageDialog(this, "Print functionality would be implemented here", "Print", JOptionPane.INFORMATION_MESSAGE);
    }
}