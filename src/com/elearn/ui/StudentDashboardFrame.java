package com.elearn.ui;

import com.elearn.model.Course;
import com.elearn.model.Student;
import com.elearn.service.CourseService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class StudentDashboardFrame extends JFrame {
    private final Student student;
    private final CourseService courseService = new CourseService();
    private final DefaultListModel<Course> courseListModel = new DefaultListModel<>();
    private final JList<Course> courseList = new JList<>(courseListModel);
    private final JButton enrollBtn = new JButton("Enroll in Selected Course");
    private final JButton quizBtn = new JButton("Take Quiz");
    private final JButton achievementsBtn = new JButton("Achievements");
    private final JButton materialsBtn = new JButton("📖 Reading Materials");
    private final JButton certificatesBtn = new JButton("🏆 My Certificates");

    public StudentDashboardFrame(Student student) {
        this.student = student;
        setTitle("Welcome, " + student.getName());
        setSize(720, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        courseList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Course) {
                    Course c = (Course) value;
                    setText(c.getCourseId() + ": " + c.getTitle() + " — " + c.getInstructor());
                }
                return this;
            }
        });

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Available Courses"));
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(courseList), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(certificatesBtn);
        bottom.add(materialsBtn);
        bottom.add(achievementsBtn);
        bottom.add(quizBtn);
        bottom.add(enrollBtn);
        add(bottom, BorderLayout.SOUTH);

        enrollBtn.addActionListener(e -> onEnroll());
        quizBtn.addActionListener(e -> onQuiz());
        achievementsBtn.addActionListener(e -> openAchievements());
        materialsBtn.addActionListener(e -> onMaterials());
        certificatesBtn.addActionListener(e -> onCertificates());

        loadCourses();
    }

    private void loadCourses() {
        courseListModel.clear();
        List<Course> courses = courseService.getAllCourses();
        for (Course c : courses) courseListModel.addElement(c);
    }

    private void onEnroll() {
        Course c = courseList.getSelectedValue();
        if (c == null) {
            JOptionPane.showMessageDialog(this, "Select a course first");
            return;
        }
        boolean ok = courseService.enroll(student.getStudentId(), c.getCourseId());
        JOptionPane.showMessageDialog(this, ok ? "Enrolled!" : "Already enrolled or failed");
    }

    private void onQuiz() {
        Course c = courseList.getSelectedValue();
        if (c == null) {
            JOptionPane.showMessageDialog(this, "Select a course first");
            return;
        }
        new QuizFrame(student.getStudentId(), c).setVisible(true);
    }

    private void openAchievements() {
        new StudentAchievementsFrame(student).setVisible(true);
    }

    private void onOpenCourse() {
        Course c = courseList.getSelectedValue();
        if (c == null) {
            JOptionPane.showMessageDialog(this, "Select a course first");
            return;
        }
        new CourseDetailsFrame(c).setVisible(true);
    }
    
    private void onMaterials() {
        new MaterialReadingFrame(student.getStudentId()).setVisible(true);
    }
    
    private void onCertificates() {
        new CertificateFrame(student.getStudentId()).setVisible(true);
    }
}


