package com.elearn.service;

import com.elearn.dao.AdminDAO;
import com.elearn.dao.StudentDAO;
import com.elearn.dao.impl.AdminDAOImpl;
import com.elearn.dao.impl.StudentDAOImpl;
import com.elearn.model.Admin;
import com.elearn.model.Student;

public class AuthService {
    private final StudentDAO studentDAO = new StudentDAOImpl();
    private final AdminDAO adminDAO = new AdminDAOImpl();

    public Student loginStudent(String email, String password) {
        return studentDAO.findByEmailAndPassword(email, password);
    }

    public boolean registerStudent(String name, String email, String password) {
        Student s = new Student();
        s.setName(name);
        s.setEmail(email);
        s.setPassword(password);
        return studentDAO.create(s);
    }

    public Admin loginAdmin(String username, String password) {
        return adminDAO.findByUsernameAndPassword(username, password);
    }
}


