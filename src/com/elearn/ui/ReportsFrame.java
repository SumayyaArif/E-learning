package com.elearn.ui;

import com.elearn.dao.EnrollmentDAO;
import com.elearn.dao.CourseDAO;
import com.elearn.dao.StudentDAO;
import com.elearn.dao.impl.EnrollmentDAOImpl;
import com.elearn.dao.impl.CourseDAOImpl;
import com.elearn.dao.impl.StudentDAOImpl;
import com.elearn.model.Course;
import com.elearn.model.Student;
import com.elearn.model.Enrollment;
import com.elearn.model.Certificate;
import com.elearn.service.CourseService;
import com.elearn.util.ModernTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ReportsFrame extends JFrame {
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAOImpl();
    private final CourseDAO courseDAO = new CourseDAOImpl();
    private final StudentDAO studentDAO = new StudentDAOImpl();
    private final CourseService courseService = new CourseService();
    private final com.elearn.dao.CertificateDAO certificateDAO = new com.elearn.dao.impl.CertificateDAOImpl();
    
    private JTable enrollmentTable;
    private DefaultTableModel tableModel;
    private JComboBox<Course> courseFilter;
    private JComboBox<String> statusFilter;
    private final DefaultComboBoxModel<Course> courseModel = new DefaultComboBoxModel<>();

    public ReportsFrame() {
        setTitle("Course Enrollment Reports");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ModernTheme.styleFrame(this);
        
        createUI();
        loadData();
    }

    private void createUI() {
        setLayout(new BorderLayout(10, 10));

        // Header Panel with Filters
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Course Filter
        JLabel courseLabel = new JLabel("Course:");
        courseFilter = new JComboBox<>(courseModel);
        courseFilter.setPreferredSize(new Dimension(300, 30));
        courseFilter.addActionListener(e -> filterData());

        // Status Filter
        JLabel statusLabel = new JLabel("Status:");
        statusFilter = new JComboBox<>(new String[]{"All", "Completed", "In Progress"});
        statusFilter.setPreferredSize(new Dimension(150, 30));
        statusFilter.addActionListener(e -> filterData());

        headerPanel.add(courseLabel);
        headerPanel.add(courseFilter);
        headerPanel.add(Box.createHorizontalStrut(20));
        headerPanel.add(statusLabel);
        headerPanel.add(statusFilter);

        // Table
        String[] columns = {
            "Student ID", "Student Name", "Course", "Enrollment Date", 
            "Materials Completed", "Quiz Score", "Status", "Certificates"
        };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        enrollmentTable = new JTable(tableModel);
        enrollmentTable.setRowHeight(30);
        enrollmentTable.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(enrollmentTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Summary Panel
        JPanel summaryPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(summaryPanel, BorderLayout.SOUTH);
    }

    private void loadData() {
        try {
            // Load courses for filter
            courseModel.removeAllElements();
            courseModel.addElement(new Course(-1, "All Courses", "", "")); // Add "All" option
            List<Course> courses = courseDAO.findAll();
            for (Course course : courses) {
                courseModel.addElement(course);
            }

            refreshTable();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading data: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        try {
            Course selectedCourse = (Course) courseFilter.getSelectedItem();
            String selectedStatus = (String) statusFilter.getSelectedItem();

            List<Enrollment> enrollments;
            if (selectedCourse != null && selectedCourse.getCourseId() != -1) {
                enrollments = enrollmentDAO.findByCourseId(selectedCourse.getCourseId());
            } else {
                enrollments = enrollmentDAO.findAll();
            }

            for (Enrollment enrollment : enrollments) {
                Student student = studentDAO.findById(enrollment.getStudentId());
                Course course = courseDAO.findById(enrollment.getCourseId());
                
                if (student == null || course == null) continue;

                // Calculate completion percentage
                double completionPercentage = courseService.getCompletionPercentage(
                    enrollment.getStudentId(), 
                    enrollment.getCourseId()
                );

                String status = completionPercentage >= 100 ? "Completed" : "In Progress";
                
                // Apply status filter
                if (!"All".equals(selectedStatus) && !selectedStatus.equals(status)) {
                    continue;
                }

                // Fetch certificates for student
                List<Certificate> certs = certificateDAO.findByStudentId(student.getStudentId());
                int certCount = certs != null ? certs.size() : 0;

                Object[] row = {
                    student.getStudentId(),
                    student.getName(),
                    course.getTitle(),
                    enrollment.getEnrollDate(),
                    String.format("%.1f%%", completionPercentage),
                    // Quiz score: Enrollment does not always track quiz score here, show N/A
                    "N/A",
                    status,
                    certCount
                };
                
                tableModel.addRow(row);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error refreshing data: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void filterData() {
        refreshTable();
    }
}