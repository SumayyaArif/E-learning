package com.elearn.ui;

import com.elearn.dao.StudentDAO;
import com.elearn.dao.EnrollmentDAO;
import com.elearn.dao.impl.StudentDAOImpl;
import com.elearn.dao.impl.EnrollmentDAOImpl;
import com.elearn.model.Student;
import com.elearn.model.Enrollment;
import com.elearn.model.Course;
import com.elearn.service.CourseService;
import com.elearn.util.ModernTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentManagementFrame extends JFrame {
    private final StudentDAO studentDAO = new StudentDAOImpl();
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAOImpl();
    private final CourseService courseService = new CourseService();
    private JTable studentsTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> filterComboBox;
    private JCheckBox onlyEnrolledCheckBox;

    public StudentManagementFrame() {
        setTitle("Student Management");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ModernTheme.styleFrame(this);
        
        createUI();
        // default: load all students
        loadStudents();
    }

    // Overloaded constructor to open with 'Only Enrolled' preselected
    public StudentManagementFrame(boolean onlyEnrolled) {
        this();
        if (onlyEnrolled && onlyEnrolledCheckBox != null) {
            onlyEnrolledCheckBox.setSelected(true);
            loadStudents();
        }
    }
    
    private void createUI() {
        setLayout(new BorderLayout(10, 10));
        
        // Header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Main content - Students table
        JPanel contentPanel = createContentPanel();
        add(contentPanel, BorderLayout.CENTER);
        
        // Footer panel
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        
        JLabel titleLabel = ModernTheme.createHeadingLabel("Student Management");
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setOpaque(false);
        
        searchField = new JTextField(20);
        JButton searchButton = ModernTheme.createSecondaryButton("🔍 Search");
        
    filterComboBox = new JComboBox<>(new String[]{"All Students", "Active Students", "Inactive Students"});
    onlyEnrolledCheckBox = new JCheckBox("Only Enrolled");
    onlyEnrolledCheckBox.setOpaque(false);

    searchButton.addActionListener(e -> searchStudents());
    onlyEnrolledCheckBox.addActionListener(e -> searchStudents());

    searchPanel.add(new JLabel("Filter:"));
    searchPanel.add(filterComboBox);
    searchPanel.add(onlyEnrolledCheckBox);
    searchPanel.add(searchField);
    searchPanel.add(searchButton);
        
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(searchPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createContentPanel() {
        JPanel panel = ModernTheme.createCardPanel();
        
        // Create table model with columns
        String[] columns = {"ID", "Name", "Email", "Courses Enrolled", "Last Login", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        studentsTable = new JTable(tableModel);
        studentsTable.setRowHeight(30);
        studentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(studentsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setOpaque(false);
        
        JButton viewDetailsBtn = ModernTheme.createModernButton("👁️ View Details");
        JButton viewProgressBtn = ModernTheme.createSecondaryButton("📊 View Progress");
        JButton exportBtn = ModernTheme.createSecondaryButton("📤 Export Data");
        JButton closeBtn = ModernTheme.createSecondaryButton("❌ Close");
        
        viewDetailsBtn.addActionListener(e -> viewStudentDetails());
        viewProgressBtn.addActionListener(e -> viewStudentProgress());
        exportBtn.addActionListener(e -> exportStudentData());
        closeBtn.addActionListener(e -> dispose());
        
        panel.add(viewDetailsBtn);
        panel.add(viewProgressBtn);
        panel.add(exportBtn);
        panel.add(closeBtn);
        
        return panel;
    }
    
    private void loadStudents() {
        tableModel.setRowCount(0);
        
        try {
            List<Student> students = studentDAO.findAll();
            
            for (Student student : students) {
                int coursesEnrolled = enrollmentDAO.findByStudentId(student.getStudentId()).size();

                // If 'Only Enrolled' is checked, skip students with zero enrollments
                if (onlyEnrolledCheckBox != null && onlyEnrolledCheckBox.isSelected() && coursesEnrolled == 0) {
                    continue;
                }
                
                Object[] row = {
                    student.getStudentId(),
                    student.getName(),
                    student.getEmail(),
                    coursesEnrolled,
                    student.getLastLogin() != null ? student.getLastLogin() : "Never",
                    student.isActive() ? "Active" : "Inactive"
                };
                
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading students: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void searchStudents() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        String filter = (String) filterComboBox.getSelectedItem();
        
        tableModel.setRowCount(0);
        
        try {
            List<Student> students = studentDAO.findAll();
            
            for (Student student : students) {
                // Apply filters
                if (!searchTerm.isEmpty() && 
                    !student.getName().toLowerCase().contains(searchTerm) && 
                    !student.getEmail().toLowerCase().contains(searchTerm)) {
                    continue;
                }
                
                if ("Active Students".equals(filter) && !student.isActive()) {
                    continue;
                }
                
                if ("Inactive Students".equals(filter) && student.isActive()) {
                    continue;
                }
                
                int coursesEnrolled = enrollmentDAO.findByStudentId(student.getStudentId()).size();

                // Apply "Only Enrolled" filter
                if (onlyEnrolledCheckBox != null && onlyEnrolledCheckBox.isSelected() && coursesEnrolled == 0) {
                    continue;
                }
                
                Object[] row = {
                    student.getStudentId(),
                    student.getName(),
                    student.getEmail(),
                    coursesEnrolled,
                    student.getLastLogin() != null ? student.getLastLogin() : "Never",
                    student.isActive() ? "Active" : "Inactive"
                };
                
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error searching students: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void viewStudentDetails() {
        int selectedRow = studentsTable.getSelectedRow();
        if (selectedRow >= 0) {
            int studentId = (int) tableModel.getValueAt(selectedRow, 0);
            try {
                Student student = studentDAO.findById(studentId);
                if (student != null) {
                        // Create a details dialog
                        JDialog dialog = new JDialog(this, "Student Details", true);
                        dialog.setSize(800, 500);
                        dialog.setLocationRelativeTo(this);

                        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
                        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                        // Student info panel
                        JPanel infoPanel = new JPanel(new GridLayout(4, 2, 5, 5));
                        infoPanel.add(new JLabel("Student ID:"));
                        infoPanel.add(new JLabel(String.valueOf(student.getStudentId())));
                        infoPanel.add(new JLabel("Name:"));
                        infoPanel.add(new JLabel(student.getName()));
                        infoPanel.add(new JLabel("Email:"));
                        infoPanel.add(new JLabel(student.getEmail()));
                        infoPanel.add(new JLabel("Status:"));
                        infoPanel.add(new JLabel(student.isActive() ? "Active" : "Inactive"));

                        mainPanel.add(infoPanel, BorderLayout.NORTH);

                        // Enrollments panel
                        JPanel enrollmentsPanel = new JPanel(new BorderLayout());
                        enrollmentsPanel.setBorder(BorderFactory.createTitledBorder("Enrolled Courses"));

                        String[] columns = {"Course", "Enrollment Date", "Materials Progress", "Quiz Status", "Overall Status"};
                        DefaultTableModel model = new DefaultTableModel(columns, 0) {
                            @Override
                            public boolean isCellEditable(int row, int column) {
                                return false;
                            }
                        };

                        JTable enrollmentsTable = new JTable(model);
                        enrollmentsTable.setRowHeight(25);

                        // Load enrollments
                        List<Enrollment> enrollments = enrollmentDAO.findByStudentId(studentId);
                        for (Enrollment enrollment : enrollments) {
                            Course course = courseService.getCourseById(enrollment.getCourseId());
                            if (course != null) {
                                double progress = courseService.getCompletionPercentage(studentId, enrollment.getCourseId());
                                String status = progress >= 100 ? "Completed" : "In Progress";
                                String quizStatus = enrollment.getStatus().equals("Completed") ? "Passed" : "Not Completed";

                                Object[] row = {
                                    course.getTitle(),
                                    enrollment.getEnrollDate().toLocalDate(),
                                    String.format("%.1f%%", progress),
                                    quizStatus,
                                    status
                                };
                                model.addRow(row);
                            }
                        }

                        enrollmentsPanel.add(new JScrollPane(enrollmentsTable), BorderLayout.CENTER);
                        mainPanel.add(enrollmentsPanel, BorderLayout.CENTER);

                        // Close button
                        JButton closeButton = new JButton("Close");
                        closeButton.addActionListener(e -> dialog.dispose());
                        mainPanel.add(closeButton, BorderLayout.SOUTH);

                        dialog.add(mainPanel);
                        dialog.setVisible(true);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error loading student details: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a student", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void viewStudentProgress() {
        int selectedRow = studentsTable.getSelectedRow();
        if (selectedRow >= 0) {
            int studentId = (int) tableModel.getValueAt(selectedRow, 0);
            new StudentProgressFrame(studentId).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a student", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void exportStudentData() {
        JOptionPane.showMessageDialog(this, "Export functionality would be implemented here", "Export", JOptionPane.INFORMATION_MESSAGE);
    }
}