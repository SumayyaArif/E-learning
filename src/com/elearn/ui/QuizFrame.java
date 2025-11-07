package com.elearn.ui;

import com.elearn.model.Course;
import com.elearn.model.QuizQuestion;
import com.elearn.service.QuizService;
import com.elearn.dao.EnrollmentDAO;
import com.elearn.dao.impl.EnrollmentDAOImpl;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class QuizFrame extends JFrame {
    private final int studentId;
    private final Course course;
    private final QuizService quizService = new QuizService();
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAOImpl();
    private final List<ButtonGroup> groups = new ArrayList<>();

    public QuizFrame(int studentId, Course course) {
        this.studentId = studentId;
        this.course = course;
        
        // Check enrollment before allowing quiz access
        if (!enrollmentDAO.isEnrolled(studentId, course.getCourseId())) {
            JOptionPane.showMessageDialog(null, 
                "You must be enrolled in this course to take its quiz.", 
                "Enrollment Required", 
                JOptionPane.WARNING_MESSAGE);
            dispose();
            return;
        }
        setTitle("Quiz: " + course.getTitle());
        setSize(700, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        List<QuizQuestion> questions = quizService.getQuestions(course.getCourseId());
        int qNum = 1;
        for (QuizQuestion q : questions) {
            JPanel qp = new JPanel();
            qp.setLayout(new BoxLayout(qp, BoxLayout.Y_AXIS));
            qp.setBorder(BorderFactory.createTitledBorder("Q" + qNum + ". " + q.getQuestion()));
            ButtonGroup group = new ButtonGroup();
            JRadioButton a = new JRadioButton("A) " + q.getOptionA());
            JRadioButton b = new JRadioButton("B) " + q.getOptionB());
            JRadioButton c = new JRadioButton("C) " + q.getOptionC());
            JRadioButton d = new JRadioButton("D) " + q.getOptionD());
            group.add(a); group.add(b); group.add(c); group.add(d);
            qp.add(a); qp.add(b); qp.add(c); qp.add(d);
            groups.add(group);
            content.add(qp);
            qNum++;
        }

        JButton submit = new JButton("Submit Quiz");
        submit.addActionListener(e -> onSubmit());

        add(new JScrollPane(content), BorderLayout.CENTER);
        add(submit, BorderLayout.SOUTH);
    }

    private void onSubmit() {
        List<Character> answers = new ArrayList<>();
        for (ButtonGroup g : groups) {
            answers.add(getSelected(g));
        }
        int score = quizService.evaluateAndPersist(studentId, course.getCourseId(), answers);
        JOptionPane.showMessageDialog(this, "Your score: " + score + "%" + (score >= 60 ? "\nCertificate issued!" : "\nYou need 60% or higher to get a certificate."));
        dispose();
    }

    private char getSelected(ButtonGroup g) {
        for (AbstractButton b : java.util.Collections.list(g.getElements())) {
            if (b.isSelected()) {
                String t = b.getText();
                return t.charAt(0);
            }
        }
        return 'X';
    }
}


