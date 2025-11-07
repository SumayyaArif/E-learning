package com.elearn.service;

import com.elearn.dao.CertificateDAO;
import com.elearn.dao.CourseDAO;
import com.elearn.dao.EnrollmentDAO;
import com.elearn.dao.QuizDAO;
import com.elearn.dao.QuizResultDAO;
import com.elearn.dao.StudentDAO;
import com.elearn.dao.impl.CertificateDAOImpl;
import com.elearn.dao.impl.CourseDAOImpl;
import com.elearn.dao.impl.EnrollmentDAOImpl;
import com.elearn.dao.impl.QuizDAOImpl;
import com.elearn.dao.impl.QuizResultDAOImpl;
import com.elearn.dao.impl.StudentDAOImpl;
import com.elearn.model.Course;
import com.elearn.model.QuizQuestion;
import com.elearn.model.QuizResult;
import com.elearn.model.Student;

import java.util.List;
import com.elearn.util.CertificateGenerator;
import java.io.File;

public class QuizService {
    private final QuizDAO quizDAO = new QuizDAOImpl();
    private final QuizResultDAO resultDAO = new QuizResultDAOImpl();
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAOImpl();
    private final CertificateDAO certificateDAO = new CertificateDAOImpl();
    private final StudentDAO studentDAO = new StudentDAOImpl();
    private final CourseDAO courseDAO = new CourseDAOImpl();
    private final EnrollmentService enrollmentService = new EnrollmentService();

    public List<QuizQuestion> getQuestions(int courseId) {
        return quizDAO.findByCourseId(courseId);
    }

    /**
     * Evaluate quiz answers, save result, and handle course completion
     * @return score as percentage
     */
    public int evaluateAndPersist(int studentId, int courseId, List<Character> answers) {
        List<QuizQuestion> questions = getQuestions(courseId);
        
        // Handle empty quiz
        if (questions.isEmpty()) {
            return 0;
        }
        
        // Calculate score
        int correct = 0;
        for (int i = 0; i < questions.size() && i < answers.size(); i++) {
            String correctAnswer = questions.get(i).getCorrectOption().toUpperCase().trim();
            String userAnswer = Character.toString(answers.get(i)).toUpperCase().trim();
            
            if (correctAnswer.equals(userAnswer)) {
                correct++;
            }
        }
        
        int score = (int) Math.round((correct * 100.0) / questions.size());

        // Save quiz result
        QuizResult result = new QuizResult();
        result.setStudentId(studentId);
        result.setCourseId(courseId);
        result.setScore(score);
        resultDAO.saveResult(result);

        // Check if student passed (60% or higher)
        if (score >= 60) {
            // Check if all materials are completed and issue certificate
            enrollmentService.checkAndCompleteCourse(studentId, courseId);
        }
        
        return score;
    }

    /**
     * Check if student can take quiz (must complete all materials first)
     */
    public boolean canTakeQuiz(int studentId, int courseId) {
        return enrollmentService.canTakeQuiz(studentId, courseId);
    }

    /**
     * Get best quiz score for a student in a course
     */
    public int getBestScore(int studentId, int courseId) {
        List<QuizResult> results = resultDAO.findByStudentAndCourse(studentId, courseId);
        int bestScore = 0;
        
        for (QuizResult result : results) {
            if (result.getScore() > bestScore) {
                bestScore = result.getScore();
            }
        }
        
        return bestScore;
    }

    /**
     * Check if student has passed the quiz
     */
    public boolean hasPassed(int studentId, int courseId) {
        return getBestScore(studentId, courseId) >= 60;
    }
}
