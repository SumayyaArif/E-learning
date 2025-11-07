package com.elearn.dao.impl;

import com.elearn.dao.QuizDAO;
import com.elearn.db.DBConnection;
import com.elearn.model.QuizQuestion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizDAOImpl implements QuizDAO {
    @Override
    public List<QuizQuestion> findByCourseId(int courseId) {
        List<QuizQuestion> list = new ArrayList<>();
        String sql = "SELECT quiz_id, course_id, question, optionA, optionB, optionC, optionD, correct_option FROM quiz WHERE course_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    QuizQuestion q = new QuizQuestion();
                    q.setQuizId(rs.getInt("quiz_id"));
                    q.setCourseId(rs.getInt("course_id"));
                    q.setQuestion(rs.getString("question"));
                    q.setOptionA(rs.getString("optionA"));
                    q.setOptionB(rs.getString("optionB"));
                    q.setOptionC(rs.getString("optionC"));
                    q.setOptionD(rs.getString("optionD"));
                    q.setCorrectOption(rs.getString("correct_option"));
                    list.add(q);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean create(QuizQuestion question) {
        String sql = "INSERT INTO quiz(course_id, question, optionA, optionB, optionC, optionD, correct_option) VALUES(?,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, question.getCourseId());
            ps.setString(2, question.getQuestion());
            ps.setString(3, question.getOptionA());
            ps.setString(4, question.getOptionB());
            ps.setString(5, question.getOptionC());
            ps.setString(6, question.getOptionD());
            ps.setString(7, question.getCorrectOption());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(QuizQuestion question) {
        String sql = "UPDATE quiz SET course_id=?, question=?, optionA=?, optionB=?, optionC=?, optionD=?, correct_option=? WHERE quiz_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, question.getCourseId());
            ps.setString(2, question.getQuestion());
            ps.setString(3, question.getOptionA());
            ps.setString(4, question.getOptionB());
            ps.setString(5, question.getOptionC());
            ps.setString(6, question.getOptionD());
            ps.setString(7, question.getCorrectOption());
            ps.setInt(8, question.getQuizId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int quizId) {
        String sql = "DELETE FROM quiz WHERE quiz_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quizId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}


