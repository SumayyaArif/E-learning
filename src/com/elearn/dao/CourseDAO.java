package com.elearn.dao;

import com.elearn.model.Course;
import java.util.List;

public interface CourseDAO {
    List<Course> findAll();
    Course findById(int courseId);
    boolean create(Course course);
    boolean update(Course course);
    boolean delete(int courseId);
}


