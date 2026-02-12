package com.app.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Singleton instance
    private static DatabaseConnection instance;
    
    // Database configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "ocean_view_resort";
    private static final String DB_FULL_URL = DB_URL + DB_NAME;
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "S7adowless_4olf@76"; // Change this to your MySQL password
    
    // Static initializer to load the JDBC driver
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL JDBC Driver loaded successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Private constructor to prevent instantiation
    private DatabaseConnection() {
    }
    
    // Get singleton instance
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    // Get database connection
    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(DB_FULL_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
            throw e;
        }
    }

    // Close connection
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}
