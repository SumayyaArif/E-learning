package com.elearn.dao.impl;

import com.elearn.dao.CourseDAO;
import com.elearn.db.DBConnection;
import com.elearn.model.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAOImpl implements CourseDAO {
    @Override
    public List<Course> findAll() {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT course_id, title, description, instructor, image_path FROM courses";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Course findById(int courseId) {
        String sql = "SELECT course_id, title, description, instructor, image_path FROM courses WHERE course_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean create(Course course) {
        String sql = "INSERT INTO courses(title, description, instructor, image_path) VALUES(?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, course.getTitle());
            ps.setString(2, course.getDescription());
            ps.setString(3, course.getInstructor());
            ps.setString(4, course.getImagePath());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) course.setCourseId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(Course course) {
        String sql = "UPDATE courses SET title=?, description=?, instructor=?, image_path=? WHERE course_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, course.getTitle());
            ps.setString(2, course.getDescription());
            ps.setString(3, course.getInstructor());
            ps.setString(4, course.getImagePath());
            ps.setInt(5, course.getCourseId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int courseId) {
        String sql = "DELETE FROM courses WHERE course_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Course map(ResultSet rs) throws SQLException {
        Course c = new Course();
        c.setCourseId(rs.getInt("course_id"));
        c.setTitle(rs.getString("title"));
        c.setDescription(rs.getString("description"));
        c.setInstructor(rs.getString("instructor"));
        c.setImagePath(rs.getString("image_path"));
        return c;
    }
}
