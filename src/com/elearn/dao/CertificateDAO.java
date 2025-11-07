package com.elearn.dao;

import com.elearn.model.Certificate;
import java.util.List;

public interface CertificateDAO {
    boolean issueCertificate(int studentId, int courseId);
    List<Certificate> findByStudentId(int studentId);
}


