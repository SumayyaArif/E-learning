package com.elearn.config;

public final class DBConfig {
    private DBConfig() {}

    // Try different common MySQL configurations
    public static final String JDBC_URL = "jdbc:mysql://localhost:3306/cms?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    public static final String JDBC_USER = "root";
    public static final String JDBC_PASSWORD = ""; // Try empty password first, then "root", then "password"
    
    // Alternative configurations if the default fails
    public static final String[] POSSIBLE_PASSWORDS = {"", "root", "password", "admin", "123456"};
}


