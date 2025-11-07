package com.elearn.model;

import java.time.LocalDateTime;

public class MaterialCompletion {
    private int completionId;
    private int studentId;
    private int materialId;
    private LocalDateTime completedDate;

    public int getCompletionId() { return completionId; }
    public void setCompletionId(int completionId) { this.completionId = completionId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getMaterialId() { return materialId; }
    public void setMaterialId(int materialId) { this.materialId = materialId; }

    public LocalDateTime getCompletedDate() { return completedDate; }
    public void setCompletedDate(LocalDateTime completedDate) { this.completedDate = completedDate; }
}
