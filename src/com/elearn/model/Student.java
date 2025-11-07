package com.elearn.model;

import java.time.LocalDateTime;

public class Student {
    private int studentId;
    private String name;
    private String email;
    private String password;
    private LocalDateTime dateJoined;
    private LocalDateTime lastLogin;
    private boolean active = true;  // Default to true

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public LocalDateTime getDateJoined() { return dateJoined; }
    public void setDateJoined(LocalDateTime dateJoined) { this.dateJoined = dateJoined; }
    
    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() {
        return studentId + " - " + name + " (" + email + ")";
    }
}


