package com.elearn.model;

import java.time.LocalDateTime;

public class QuizResult {
    private int resultId;
    private int studentId;
    private int courseId;
    private int score;
    private LocalDateTime attemptDate;

    public int getResultId() { return resultId; }
    public void setResultId(int resultId) { this.resultId = resultId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public LocalDateTime getAttemptDate() { return attemptDate; }
    public void setAttemptDate(LocalDateTime attemptDate) { this.attemptDate = attemptDate; }
}


