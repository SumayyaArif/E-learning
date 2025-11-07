package com.elearn.dao;

import com.elearn.model.Student;
import java.util.List;

public interface StudentDAO {
    Student findById(int studentId);
    Student findByEmailAndPassword(String email, String password);
    boolean updateLastLogin(int studentId);
    boolean create(Student student);
    List<Student> findAll();
}


