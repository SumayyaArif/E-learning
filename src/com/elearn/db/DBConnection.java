package com.elearn.db;

import com.elearn.config.DBConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBConnection {
    private static volatile boolean driverLoaded = false;

    private DBConnection() {}

    private static void ensureDriverLoaded() {
        if (driverLoaded) return;
        synchronized (DBConnection.class) {
            if (driverLoaded) return;
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                driverLoaded = true;
            } catch (ClassNotFoundException e) {
                // Fallback for older artifact names
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    driverLoaded = true;
                } catch (ClassNotFoundException ex) {
                    // leave as false; connection attempt will throw a clear error
                }
            }
        }
    }

    public static Connection getConnection() throws SQLException {
        ensureDriverLoaded();
        
        // Try the default password first
        try {
            return DriverManager.getConnection(DBConfig.JDBC_URL, DBConfig.JDBC_USER, DBConfig.JDBC_PASSWORD);
        } catch (SQLException e) {
            // If default fails, try other common passwords
            for (String password : DBConfig.POSSIBLE_PASSWORDS) {
                if (password.equals(DBConfig.JDBC_PASSWORD)) continue; // Skip the one we already tried
                try {
                    return DriverManager.getConnection(DBConfig.JDBC_URL, DBConfig.JDBC_USER, password);
                } catch (SQLException ignored) {
                    // Continue to next password
                }
            }
            // If all passwords fail, throw the original exception
            throw e;
        }
    }
}


