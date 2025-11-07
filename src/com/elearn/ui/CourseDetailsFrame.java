package com.elearn.ui;

import com.elearn.dao.MaterialDAO;
import com.elearn.dao.QuizDAO;
import com.elearn.dao.EnrollmentDAO;
import com.elearn.dao.impl.MaterialDAOImpl;
import com.elearn.dao.impl.QuizDAOImpl;
import com.elearn.dao.impl.EnrollmentDAOImpl;
import com.elearn.model.Course;
import com.elearn.model.Material;
import com.elearn.model.QuizQuestion;
import com.elearn.model.Student;
import com.elearn.util.ModernTheme;

import javax.swing.*;
import java.awt.*;
import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.util.List;

public class CourseDetailsFrame extends JFrame {
    private final Course course;
    private final Student student;
    private final MaterialDAO materialDAO = new MaterialDAOImpl();
    private final QuizDAO quizDAO = new QuizDAOImpl();
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAOImpl();
    private final DefaultListModel<Material> materialsModel = new DefaultListModel<>();
    private final DefaultListModel<QuizQuestion> questionsModel = new DefaultListModel<>();

    public CourseDetailsFrame(Course course) {
        this(course, null);
    }
    
    public CourseDetailsFrame(Course course, Student student) {
        this.course = course;
        this.student = student;
        
        // Check enrollment for students
        if (student != null) {
            boolean isEnrolled = enrollmentDAO.isEnrolled(student.getStudentId(), course.getCourseId());
            if (!isEnrolled) {
                JOptionPane.showMessageDialog(null, 
                    "You must be enrolled in this course to view its details.", 
                    "Enrollment Required", 
                    JOptionPane.WARNING_MESSAGE);
                dispose();
                return;
            }
        }
        
        setTitle("Course: " + course.getTitle());
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        createUI();
        loadMaterials();
        loadQuestions();
    }
    
    private void createUI() {
        setLayout(new BorderLayout(10, 10));
        
        // Top panel with course info
        JPanel topPanel = createCourseInfoPanel();
        
        // Center panel with tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        
    // Materials tab
    JPanel materialsPanel = createMaterialsPanel();
    tabbedPane.addTab("📁 Materials", materialsPanel);
        
        // Quiz tab
        JPanel quizPanel = createQuizPanel();
        tabbedPane.addTab("📝 Quiz", quizPanel);
        
        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createCourseInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Course Information"));
        panel.setBackground(new Color(240, 248, 255));
        
        JTextArea info = new JTextArea();
        info.setEditable(false);
        info.setFont(new Font("Arial", Font.PLAIN, 14));
        info.setText("📚 " + course.getTitle() + "\n\n" +
                    "👨‍🏫 Instructor: " + course.getInstructor() + "\n\n" +
                    "📖 Description:\n" + course.getDescription());
        info.setBackground(panel.getBackground());
        
        panel.add(new JScrollPane(info), BorderLayout.CENTER);
        return panel;
    }
    
    // Video support removed — no video panel
    
    private JPanel createMaterialsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Course Materials"));
        
        JList<Material> materialsList = new JList<>(materialsModel);
        materialsList.setCellRenderer(new MaterialListRenderer());
        materialsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Material selected = materialsList.getSelectedValue();
                if (selected != null) {
                    // Show material info
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(materialsList);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton openMaterialBtn = new JButton("📂 Open Material");
        JButton downloadMaterialBtn = new JButton("⬇️ Download");
        
        openMaterialBtn.addActionListener(e -> {
            Material selected = materialsList.getSelectedValue();
            if (selected != null) {
                openFile(selected.getFilePath());
            } else {
                JOptionPane.showMessageDialog(this, "Please select a material", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        downloadMaterialBtn.addActionListener(e -> {
            Material selected = materialsList.getSelectedValue();
            if (selected != null) {
                // Implement download functionality
                JOptionPane.showMessageDialog(this, "Download functionality would be implemented here", "Download", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a material", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        buttonPanel.add(openMaterialBtn);
        buttonPanel.add(downloadMaterialBtn);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createQuizPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Course Quiz"));
        panel.setOpaque(false);
        
        JList<QuizQuestion> questionsList = new JList<>(questionsModel);
        questionsList.setCellRenderer(new QuestionListRenderer());
        
        JScrollPane scrollPane = new JScrollPane(questionsList);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton takeQuizBtn = ModernTheme.createModernButton("📝 Take Quiz");
        JButton viewQuestionsBtn = ModernTheme.createModernButton("👁️ View Questions");
        
        takeQuizBtn.addActionListener(e -> {
            if (questionsModel.getSize() > 0) {
                // Check if student is null (admin view) or enrolled
                if (student != null) {
                    new QuizAttemptFrame(student, course).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Student information not available", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "No quiz questions available for this course", "No Quiz", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        viewQuestionsBtn.addActionListener(e -> {
            if (questionsList.getSelectedValue() != null) {
                QuizQuestion question = questionsList.getSelectedValue();
                showQuestionDetails(question);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a question to view", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        buttonPanel.add(takeQuizBtn);
        buttonPanel.add(viewQuestionsBtn);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private void loadMaterials() {
        materialsModel.clear();
        try {
            List<Material> list = materialDAO.findByCourseId(course.getCourseId());
            for (Material m : list) materialsModel.addElement(m);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading materials: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadQuestions() {
        questionsModel.clear();
        try {
            List<QuizQuestion> list = quizDAO.findByCourseId(course.getCourseId());
            for (QuizQuestion q : list) questionsModel.addElement(q);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading questions: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showQuestionDetails(QuizQuestion question) {
        JDialog dialog = new JDialog(this, "Question Details", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        JTextArea questionArea = new JTextArea(3, 30);
        questionArea.setEditable(false);
        questionArea.setText(question.getQuestion());
        questionArea.setBackground(Color.WHITE);
        
        JPanel optionsPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        optionsPanel.add(new JLabel("A) " + question.getOptionA()));
        optionsPanel.add(new JLabel("B) " + question.getOptionB()));
        optionsPanel.add(new JLabel("C) " + question.getOptionC()));
        optionsPanel.add(new JLabel("D) " + question.getOptionD()));
        
        // Only show correct answer to admin
        if (student == null) {
            JLabel correctLabel = new JLabel("Correct Answer: " + question.getCorrectOption());
            correctLabel.setFont(new Font("Arial", Font.BOLD, 14));
            correctLabel.setForeground(Color.GREEN);
            panel.add(correctLabel, BorderLayout.SOUTH);
        }
        
        panel.add(new JScrollPane(questionArea), BorderLayout.NORTH);
        panel.add(optionsPanel, BorderLayout.CENTER);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }



    private void openFile(String path) {
        if (path == null || path.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No file path provided", "No File", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try {
            Desktop.getDesktop().open(new File(path));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error opening file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Custom renderers
    private static class MaterialListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Material) {
                Material material = (Material) value;
                setText("<html><b>📄 " + material.getFileName() + "</b><br>" + 
                       "<i>" + material.getFilePath() + "</i></html>");
            }
            return this;
        }
    }
    
    private class QuestionListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof QuizQuestion) {
                QuizQuestion question = (QuizQuestion) value;
                // Only show correct answer in list for admin
                if (student == null) {
                    setText("<html><b>Q" + (index + 1) + ":</b> " + 
                           question.getQuestion() + "<br>" +
                           "<i>Correct: " + question.getCorrectOption() + "</i></html>");
                } else {
                    setText("<html><b>Q" + (index + 1) + ":</b> " + 
                           question.getQuestion() + "</html>");
                }
            }
            return this;
        }
    }
}


