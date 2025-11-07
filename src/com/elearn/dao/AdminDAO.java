package com.elearn.dao;

import com.elearn.model.Admin;

public interface AdminDAO {
    Admin findByUsernameAndPassword(String username, String password);
}


