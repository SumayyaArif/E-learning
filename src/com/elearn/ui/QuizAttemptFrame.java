package com.elearn.ui;

import com.elearn.dao.QuizDAO;
import com.elearn.dao.impl.QuizDAOImpl;
import com.elearn.model.Course;
import com.elearn.model.Quiz;
import com.elearn.model.Question;
import com.elearn.model.Student;
import com.elearn.util.ModernTheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizAttemptFrame extends JFrame {
    private final Student student;
    private final Course course;
    private final QuizDAO quizDAO = new QuizDAOImpl();
    private Quiz quiz;
    private List<Question> questions;
    private Map<Integer, String> userAnswers = new HashMap<>();
    private JPanel questionsPanel;
    private JButton submitButton;
    private JLabel timerLabel;
    private Timer timer;
    private int timeRemaining = 0;

    public QuizAttemptFrame(Student student, Course course) {
        this.student = student;
        this.course = course;
        
        setTitle("Quiz - " + course.getTitle());
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ModernTheme.styleFrame(this);
        
        loadQuiz();
        createUI();
        startTimer();
    }
    
    private void loadQuiz() {
        quiz = quizDAO.getQuizByCourseId(course.getId());
        if (quiz != null) {
            questions = quizDAO.getQuestionsByQuizId(quiz.getId());
            timeRemaining = quiz.getTimeLimit() * 60;
        } else {
            questions = new ArrayList<>();
        }
    }
    
    private void createUI() {
        setLayout(new BorderLayout(10, 10));
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = ModernTheme.createHeadingLabel("Quiz: " + course.getTitle());
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        timerLabel = new JLabel("Time Remaining: " + formatTime(timeRemaining));
        timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timerLabel.setForeground(Color.RED);
        headerPanel.add(timerLabel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        questionsPanel = new JPanel();
        questionsPanel.setLayout(new BoxLayout(questionsPanel, BoxLayout.Y_AXIS));
        questionsPanel.setOpaque(false);
        
        if (questions.isEmpty()) {
            JLabel noQuestionsLabel = new JLabel("No questions available for this quiz.");
            noQuestionsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            questionsPanel.add(noQuestionsLabel);
        } else {
            for (int i = 0; i < questions.size(); i++) {
                Question question = questions.get(i);
                JPanel questionPanel = createQuestionPanel(i + 1, question);

                // CRITICAL: Wrap to force full width
                JPanel wrapper = new JPanel(new BorderLayout());
                wrapper.setOpaque(false);
                wrapper.add(questionPanel, BorderLayout.CENTER);
                questionsPanel.add(wrapper);
                questionsPanel.add(Box.createVerticalStrut(20));
            }
        }
        
        JScrollPane scrollPane = new JScrollPane(questionsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
        
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setOpaque(false);
        
        submitButton = ModernTheme.createModernButton("Submit Quiz");
        submitButton.addActionListener(e -> submitQuiz());
        
        JButton cancelButton = ModernTheme.createSecondaryButton("Cancel");
        cancelButton.addActionListener(e -> confirmExit());
        
        actionPanel.add(submitButton);
        actionPanel.add(cancelButton);
        
        add(actionPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createQuestionPanel(int questionNumber, Question question) {
        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ModernTheme.ACCENT_COLOR, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        outerPanel.setBackground(new Color(245, 245, 250));

        JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
        innerPanel.setOpaque(false);

        JTextArea questionLabel = new JTextArea(questionNumber + ". " + question.getText());
        questionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        questionLabel.setEditable(false);
        questionLabel.setOpaque(false);
        questionLabel.setLineWrap(true);
        questionLabel.setWrapStyleWord(true);
        // Make wrapping effective by giving width hints
        questionLabel.setColumns(60); // width hint for wrapping
        questionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        questionLabel.setBorder(null);
        questionLabel.setFocusable(false);
        // Allow it to expand to the container width
        questionLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        panel.add(questionLabel);
        panel.add(Box.createVerticalStrut(10));
        innerPanel.add(Box.createVerticalStrut(12));

        ButtonGroup group = new ButtonGroup();
        String[] options = {question.getOptionA(), question.getOptionB(), question.getOptionC(), question.getOptionD()};
        String[] optionLabels = {"A", "B", "C", "D"};

        for (int i = 0; i < options.length; i++) {
            JRadioButton radioButton = new JRadioButton(optionLabels[i] + ". " + options[i]);
            radioButton.setFont(new Font("Arial", Font.PLAIN, 13));
            radioButton.setOpaque(false);

            final int optionIndex = i;
            final int questionId = question.getId();
            radioButton.addActionListener(e -> userAnswers.put(questionId, optionLabels[optionIndex]));

            group.add(radioButton);
            innerPanel.add(radioButton);
            innerPanel.add(Box.createVerticalStrut(6));
        }

        outerPanel.add(innerPanel, BorderLayout.CENTER);
        return outerPanel;
    }
    
    private void startTimer() {
        if (timeRemaining <= 0) return;
        
        timer = new Timer(1000, e -> {
            timeRemaining--;
            timerLabel.setText("Time Remaining: " + formatTime(timeRemaining));
            
            if (timeRemaining <= 0) {
                timer.stop();
                JOptionPane.showMessageDialog(this, 
                    "Time's up! Your quiz will be submitted automatically.", 
                    "Time Expired", JOptionPane.WARNING_MESSAGE);
                submitQuiz();
            }
        });
        timer.start();
    }
    
    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", minutes, secs);
    }
    
    private void submitQuiz() {
        if (timer != null) timer.stop();
        
        int total = questions.size();
        int answered = userAnswers.size();
        
        if (answered < total) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "You have only answered " + answered + " out of " + total + " questions.\nAre you sure?",
                "Confirm Submission", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                if (timeRemaining > 0) timer.start();
                return;
            }
        }
        
        int correct = 0;
        for (Question q : questions) {
            String ans = userAnswers.get(q.getId());
            if (ans != null && ans.equals(q.getCorrectOption())) correct++;
        }
        
        double score = total > 0 ? (correct * 100.0 / total) : 0;
        boolean passed = score >= 70.0;
        
        quizDAO.saveQuizAttempt(student.getId(), quiz.getId(), (int) score, passed);
        
        String msg = "Quiz completed!\n\nScore: " + String.format("%.1f", score) + "%\nCorrect: " + correct + " / " + total + "\n\n";
        if (passed) {
            msg += "Congratulations! You passed.\nCertificate generated.";
            generateCertificate(score);
        } else {
            msg += "Failed. Required: 70%";
        }
        
        JOptionPane.showMessageDialog(this, msg, "Result", JOptionPane.INFORMATION_MESSAGE);
        dispose();
        
        if (passed) {
            new CertificateGeneratorFrame(student, course, (int) score).setVisible(true);
        }
    }
    
    private void generateCertificate(double score) {}
    
    private void confirmExit() {
        if (timer != null) timer.stop();
        int confirm = JOptionPane.showConfirmDialog(this,
            "Exit? Progress will be lost.", "Confirm Exit", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
        } else if (timeRemaining > 0) {
            timer.start();
        }
    }
}