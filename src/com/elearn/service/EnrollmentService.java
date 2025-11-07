package com.elearn.service;

import com.elearn.dao.*;
import com.elearn.dao.impl.*;
import com.elearn.model.*;
import com.elearn.util.CertificateGenerator;

import java.io.File;
import java.util.List;

/**
 * Service for managing course enrollments and completion tracking
 */
public class EnrollmentService {
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAOImpl();
    private final MaterialDAO materialDAO = new MaterialDAOImpl();
    private final MaterialCompletionDAO completionDAO = new MaterialCompletionDAOImpl();
    private final QuizResultDAO quizResultDAO = new QuizResultDAOImpl();
    private final CertificateDAO certificateDAO = new CertificateDAOImpl();
    private final StudentDAO studentDAO = new StudentDAOImpl();
    private final CourseDAO courseDAO = new CourseDAOImpl();

    /**
     * Enroll a student in a course
     */
    public boolean enrollStudent(int studentId, int courseId) {
        if (enrollmentDAO.isEnrolled(studentId, courseId)) {
            return true; // Already enrolled
        }
        return enrollmentDAO.enroll(studentId, courseId);
    }

    /**
     * Check if student has completed all materials for a course
     */
    public boolean hasCompletedAllMaterials(int studentId, int courseId) {
        List<Material> materials = materialDAO.findByCourseId(courseId);
        
        if (materials.isEmpty()) {
            return true; // No materials to complete
        }
        
        for (Material material : materials) {
            if (!completionDAO.isCompleted(studentId, material.getMaterialId())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if student has passed the quiz for a course
     */
    public boolean hasPassedQuiz(int studentId, int courseId) {
        List<QuizResult> results = quizResultDAO.findByStudentAndCourse(studentId, courseId);
        
        for (QuizResult result : results) {
            if (result.getScore() >= 60) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if student can take quiz (all materials completed)
     */
    public boolean canTakeQuiz(int studentId, int courseId) {
        return hasCompletedAllMaterials(studentId, courseId);
    }

    /**
     * Check if course is completed and issue certificate if eligible
     */
    public boolean checkAndCompleteCourse(int studentId, int courseId) {
        // Check if already completed
        List<Enrollment> enrollments = enrollmentDAO.findByStudentId(studentId);
        for (Enrollment e : enrollments) {
            if (e.getCourseId() == courseId && "Completed".equals(e.getStatus())) {
                return true; // Already completed
            }
        }

        // Check completion criteria
        boolean materialsCompleted = hasCompletedAllMaterials(studentId, courseId);
        boolean quizPassed = hasPassedQuiz(studentId, courseId);

        if (materialsCompleted && quizPassed) {
            // Mark enrollment as completed
            enrollmentDAO.updateStatus(studentId, courseId, "Completed");
            
            // Issue certificate if not already issued
            issueCertificateIfNotExists(studentId, courseId);
            
            return true;
        }
        
        return false;
    }

    /**
     * Issue certificate if it doesn't already exist
     */
    private void issueCertificateIfNotExists(int studentId, int courseId) {
        List<Certificate> existing = certificateDAO.findByStudentId(studentId);
        
        // Check if certificate already exists for this course
        for (Certificate cert : existing) {
            if (cert.getCourseId() == courseId && "Issued".equals(cert.getStatus())) {
                return; // Certificate already exists
            }
        }
        
        // Issue new certificate
        certificateDAO.issueCertificate(studentId, courseId);
        
        // Generate certificate image
        try {
            Student student = studentDAO.findById(studentId);
            Course course = courseDAO.findById(courseId);
            
            String studentName = student != null ? student.getName() : "Student " + studentId;
            String courseTitle = course != null ? course.getTitle() : "Course " + courseId;
            
            File certificateFile = CertificateGenerator.generatePng(studentName, courseTitle, "certificates");
            System.out.println("Certificate generated: " + certificateFile.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("Error generating certificate image: " + e.getMessage());
        }
    }

    /**
     * Get enrollment progress for a student in a course
     */
    public EnrollmentProgress getProgress(int studentId, int courseId) {
        EnrollmentProgress progress = new EnrollmentProgress();
        progress.setStudentId(studentId);
        progress.setCourseId(courseId);
        
        // Get total and completed materials
        List<Material> materials = materialDAO.findByCourseId(courseId);
        int totalMaterials = materials.size();
        int completedMaterials = 0;
        
        for (Material material : materials) {
            if (completionDAO.isCompleted(studentId, material.getMaterialId())) {
                completedMaterials++;
            }
        }
        
        progress.setTotalMaterials(totalMaterials);
        progress.setCompletedMaterials(completedMaterials);
        
        // Get quiz status
        boolean quizPassed = hasPassedQuiz(studentId, courseId);
        progress.setQuizPassed(quizPassed);
        
        // Calculate overall progress percentage
        int progressPercentage = 0;
        if (totalMaterials > 0) {
            progressPercentage = (completedMaterials * 70) / totalMaterials; // 70% for materials
        }
        if (quizPassed) {
            progressPercentage += 30; // 30% for quiz
        }
        
        progress.setProgressPercentage(progressPercentage);
        
        return progress;
    }

    /**
     * Inner class to represent enrollment progress
     */
    public static class EnrollmentProgress {
        private int studentId;
        private int courseId;
        private int totalMaterials;
        private int completedMaterials;
        private boolean quizPassed;
        private int progressPercentage;

        public int getStudentId() { return studentId; }
        public void setStudentId(int studentId) { this.studentId = studentId; }
        
        public int getCourseId() { return courseId; }
        public void setCourseId(int courseId) { this.courseId = courseId; }
        
        public int getTotalMaterials() { return totalMaterials; }
        public void setTotalMaterials(int totalMaterials) { this.totalMaterials = totalMaterials; }
        
        public int getCompletedMaterials() { return completedMaterials; }
        public void setCompletedMaterials(int completedMaterials) { this.completedMaterials = completedMaterials; }
        
        public boolean isQuizPassed() { return quizPassed; }
        public void setQuizPassed(boolean quizPassed) { this.quizPassed = quizPassed; }
        
        public int getProgressPercentage() { return progressPercentage; }
        public void setProgressPercentage(int progressPercentage) { this.progressPercentage = progressPercentage; }
        
        public boolean isCompleted() {
            return completedMaterials == totalMaterials && quizPassed;
        }
    }
}
