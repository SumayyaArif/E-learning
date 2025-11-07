package com.elearn.ui;

import com.elearn.dao.CertificateDAO;
import com.elearn.dao.impl.CertificateDAOImpl;
import com.elearn.model.Certificate;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AchievementsFrame extends JFrame {
    private final int studentId;
    private final CertificateDAO certificateDAO = new CertificateDAOImpl();
    private final DefaultListModel<String> listModel = new DefaultListModel<>();

    public AchievementsFrame(int studentId) {
        this.studentId = studentId;
        setTitle("Achievements");
        setSize(500, 400);
        setLocationRelativeTo(null);

        JList<String> list = new JList<>(listModel);
        add(new JScrollPane(list), BorderLayout.CENTER);

        load();
    }

    private void load() {
        listModel.clear();
        List<Certificate> certs = certificateDAO.findByStudentId(studentId);
        for (Certificate c : certs) {
            listModel.addElement("Certificate #" + c.getCertId() + " for Course ID " + c.getCourseId() + " — " + c.getStatus());
        }
    }
}


