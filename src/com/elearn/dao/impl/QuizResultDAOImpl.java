package com.elearn.dao.impl;

import com.elearn.dao.QuizResultDAO;
import com.elearn.db.DBConnection;
import com.elearn.model.QuizResult;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizResultDAOImpl implements QuizResultDAO {
    @Override
    public boolean saveResult(QuizResult result) {
        String sql = "INSERT INTO quiz_results(student_id, course_id, score) VALUES(?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, result.getStudentId());
            ps.setInt(2, result.getCourseId());
            ps.setInt(3, result.getScore());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) result.setResultId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<QuizResult> findByStudentAndCourse(int studentId, int courseId) {
        List<QuizResult> list = new ArrayList<>();
        String sql = "SELECT result_id, student_id, course_id, score, attempt_date FROM quiz_results WHERE student_id=? AND course_id=? ORDER BY attempt_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    QuizResult r = new QuizResult();
                    r.setResultId(rs.getInt("result_id"));
                    r.setStudentId(rs.getInt("student_id"));
                    r.setCourseId(rs.getInt("course_id"));
                    r.setScore(rs.getInt("score"));
                    Timestamp ts = rs.getTimestamp("attempt_date");
                    if (ts != null) r.setAttemptDate(ts.toLocalDateTime());
                    list.add(r);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}


