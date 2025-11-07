package com.elearn.dao;

import com.elearn.model.Enrollment;
import java.util.List;

public interface EnrollmentDAO {
    boolean enroll(int studentId, int courseId);
    boolean updateStatus(int studentId, int courseId, String status);
    List<Enrollment> findByStudentId(int studentId);
    boolean isEnrolled(int studentId, int courseId);
    List<Enrollment> findAll();
    List<Enrollment> findByCourseId(int courseId);
    String getEnrollmentStatus(int studentId, int courseId);
    double getMaterialCompletionPercentage(int studentId, int courseId);
    boolean hasCompletedQuiz(int studentId, int courseId);
}


