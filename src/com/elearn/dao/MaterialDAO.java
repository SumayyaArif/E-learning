package com.elearn.dao;

import com.elearn.model.Material;
import java.util.List;

public interface MaterialDAO {
    List<Material> findByCourseId(int courseId);
    Material findById(int materialId);
    boolean create(Material material);
    boolean delete(int materialId);
}


