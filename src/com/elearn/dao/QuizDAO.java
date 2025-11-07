package com.elearn.dao;

import com.elearn.model.QuizQuestion;
import java.util.List;

public interface QuizDAO {
    List<QuizQuestion> findByCourseId(int courseId);
    boolean create(QuizQuestion question);
    boolean update(QuizQuestion question);
    boolean delete(int quizId);
}


