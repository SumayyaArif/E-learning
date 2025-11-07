package com.elearn.dao;

import com.elearn.model.QuizResult;
import java.util.List;

public interface QuizResultDAO {
    boolean saveResult(QuizResult result);
    List<QuizResult> findByStudentAndCourse(int studentId, int courseId);
}


