package com.elearn.model;

import java.time.LocalDateTime;

public class Enrollment {
    private int enrollId;
    private int studentId;
    private int courseId;
    private LocalDateTime enrollDate;
    private String status; // In Progress / Completed

    public int getEnrollId() { return enrollId; }
    public void setEnrollId(int enrollId) { this.enrollId = enrollId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public LocalDateTime getEnrollDate() { return enrollDate; }
    public void setEnrollDate(LocalDateTime enrollDate) { this.enrollDate = enrollDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}


