package com.elearn.service;

import com.elearn.dao.CourseDAO;
import com.elearn.dao.EnrollmentDAO;
import com.elearn.dao.impl.CourseDAOImpl;
import com.elearn.dao.impl.EnrollmentDAOImpl;
import com.elearn.model.Course;

import java.util.List;

public class CourseService {
    private final CourseDAO courseDAO = new CourseDAOImpl();
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAOImpl();

    public List<Course> getAllCourses() { return courseDAO.findAll(); }
    public Course getCourseById(int id) { return courseDAO.findById(id); }

    public boolean enroll(int studentId, int courseId) {
        if (enrollmentDAO.isEnrolled(studentId, courseId)) return true;
        return enrollmentDAO.enroll(studentId, courseId);
    }
    
    public boolean isEnrolled(int studentId, int courseId) {
        return enrollmentDAO.isEnrolled(studentId, courseId);
    }
    
    public double getCompletionPercentage(int studentId, int courseId) {
        try {
            if (!isEnrolled(studentId, courseId)) return 0.0;

            // Get enrollment status (if implemented)
            String status = null;
            try { status = enrollmentDAO.getEnrollmentStatus(studentId, courseId); } catch (Throwable t) { /* ignore */ }
            if ("Completed".equals(status)) return 100.0;

            // Calculate based on materials completed and quiz taken (DAO methods may return defaults)
            double materialsCompleted = 0.0;
            try { materialsCompleted = enrollmentDAO.getMaterialCompletionPercentage(studentId, courseId); } catch (Throwable t) { }
            boolean quizTaken = false;
            try { quizTaken = enrollmentDAO.hasCompletedQuiz(studentId, courseId); } catch (Throwable t) { }

            // Weight: Materials 70%, Quiz 30%
            return materialsCompleted * 0.7 + (quizTaken ? 30.0 : 0.0);
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }
}

