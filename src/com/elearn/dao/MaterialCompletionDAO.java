package com.elearn.dao;

import com.elearn.model.MaterialCompletion;
import java.util.List;

public interface MaterialCompletionDAO {
    boolean markAsCompleted(int studentId, int materialId);
    boolean isCompleted(int studentId, int materialId);
    List<MaterialCompletion> findByStudentId(int studentId);
    List<MaterialCompletion> findByMaterialId(int materialId);
}
