package com.elearn.model;

import java.time.LocalDateTime;

public class Certificate {
    private int certId;
    private int studentId;
    private int courseId;
    private LocalDateTime issueDate;
    private String status;

    public int getCertId() { return certId; }
    public void setCertId(int certId) { this.certId = certId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public LocalDateTime getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDateTime issueDate) { this.issueDate = issueDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}


