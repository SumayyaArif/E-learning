package com.elearn.ui;

import com.elearn.dao.CourseDAO;
import com.elearn.dao.QuizDAO;
import com.elearn.dao.impl.CourseDAOImpl;
import com.elearn.dao.impl.QuizDAOImpl;
import com.elearn.model.Course;
import com.elearn.model.QuizQuestion;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class QuizManagementFrame extends JFrame {
    private final CourseDAO courseDAO = new CourseDAOImpl();
    private final QuizDAO quizDAO = new QuizDAOImpl();
    private final DefaultListModel<Course> coursesModel = new DefaultListModel<>();
    private final DefaultListModel<QuizQuestion> questionsModel = new DefaultListModel<>();

    public QuizManagementFrame() {
        setTitle("Quiz Management");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        createUI();
        loadCourses();
    }
    
    private void createUI() {
        setLayout(new BorderLayout(10, 10));
        
        // Top panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addQuestionBtn = new JButton("➕ Add Question");
        JButton deleteQuestionBtn = new JButton("🗑️ Delete Question");
        
        addQuestionBtn.addActionListener(e -> onAddQuestion());
        deleteQuestionBtn.addActionListener(e -> onDeleteQuestion());
        
        topPanel.add(addQuestionBtn);
        topPanel.add(deleteQuestionBtn);
        
        // Center panel with split layout
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        
        // Left panel - Courses
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Courses"));
        JList<Course> coursesList = new JList<>(coursesModel);
        coursesList.setCellRenderer(new CourseListRenderer());
        coursesList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Course selected = coursesList.getSelectedValue();
                if (selected != null) {
                    loadQuestions(selected.getCourseId());
                }
            }
        });
        leftPanel.add(new JScrollPane(coursesList), BorderLayout.CENTER);
        
        // Right panel - Questions
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Quiz Questions"));
        JList<QuizQuestion> questionsList = new JList<>(questionsModel);
        questionsList.setCellRenderer(new QuestionListRenderer());
        rightPanel.add(new JScrollPane(questionsList), BorderLayout.CENTER);
        
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        splitPane.setDividerLocation(300);
        
        add(topPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
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
    
    private void loadQuestions(int courseId) {
        questionsModel.clear();
        try {
            List<QuizQuestion> questions = quizDAO.findByCourseId(courseId);
            for (QuizQuestion question : questions) {
                questionsModel.addElement(question);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading questions: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void onAddQuestion() {
        if (getSelectedCourse() == null) {
            JOptionPane.showMessageDialog(this, "Please select a course first", "No Course Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        QuizQuestionDialog dialog = new QuizQuestionDialog(this, "Add New Question", null, getSelectedCourse().getCourseId());
        dialog.setVisible(true);
        
        if (dialog.isOkPressed()) {
            QuizQuestion question = dialog.getQuestion();
            
            // Ensure the question has a correct answer selected
            if (question.getCorrectOption() == null || question.getCorrectOption().isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Please select a correct answer for the question.", 
                    "Validation Error", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                boolean saved = quizDAO.create(question);
                if (saved) {
                    JOptionPane.showMessageDialog(this, 
                        "Question added successfully with correct answer: " + question.getCorrectOption(), 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    loadQuestions(question.getCourseId());
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add question", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error adding question: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Edit question functionality removed as requested
    
    private void onDeleteQuestion() {
        QuizQuestion selected = getSelectedQuestion();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a question to delete", "No Question Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this question?", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean deleted = quizDAO.delete(selected.getQuizId());
                if (deleted) {
                    JOptionPane.showMessageDialog(this, "Question deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadQuestions(selected.getCourseId());
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete question", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting question: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private Course getSelectedCourse() {
        // Get the selected course from the courses list
        JList<Course> coursesList = (JList<Course>) ((JScrollPane) ((JPanel) ((JSplitPane) getContentPane().getComponent(1)).getLeftComponent()).getComponent(0)).getViewport().getView();
        return coursesList.getSelectedValue();
    }
    
    private QuizQuestion getSelectedQuestion() {
        // Get the selected question from the questions list
        JList<QuizQuestion> questionsList = (JList<QuizQuestion>) ((JScrollPane) ((JPanel) ((JSplitPane) getContentPane().getComponent(1)).getRightComponent()).getComponent(0)).getViewport().getView();
        return questionsList.getSelectedValue();
    }
    
    // Custom renderers
    private static class CourseListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Course) {
                Course course = (Course) value;
                setText("<html><b>" + course.getTitle() + "</b><br>" + 
                       "Instructor: " + course.getInstructor() + "</html>");
            }
            return this;
        }
    }
    
    private static class QuestionListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof QuizQuestion) {
                QuizQuestion question = (QuizQuestion) value;
                setText("<html><b>Q" + (index + 1) + ":</b> " + 
                       question.getQuestion() + "<br>" +
                       "<i>Correct: " + question.getCorrectOption() + "</i></html>");
            }
            return this;
        }
    }
}
