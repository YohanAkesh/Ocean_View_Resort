package com.app.service;

import com.app.model.User;
import com.app.util.DatabaseConnection;
import java.sql.*;

public class AuthenticationService {
   //user authentication 
    public User login(String username, String password) {
        String query = "SELECT user_id, username, password, full_name, role FROM users WHERE username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                
                if (storedPassword.equals(password)) {
                    User user = new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("full_name"),
                        rs.getString("role")
                    );
                    
                    System.out.println("\n✓ Login successful! Welcome, " + user.getFullName());
                    return user;
                }
            }
            
            System.out.println("\n✗ Invalid username or password!");
            return null;
            
        } catch (SQLException e) {
            System.err.println("Database error during login: " + e.getMessage());
            return null;
        }
    }

    //Staff Registration 
    public boolean registerStaff(String username, String password, String fullName, String role) {
        // Already registered user check
        String checkQuery = "SELECT COUNT(*) as count FROM users WHERE username = ?";
        String insertQuery = "INSERT INTO users (username, password, full_name, role) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            
            // Check username 
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, username);
                ResultSet rs = checkStmt.executeQuery();
                rs.next();
                
                if (rs.getInt("count") > 0) {
                    System.out.println("\n✗ Username already exists! Please try a different username.");
                    return false;
                }
            }
            
            // Insert new staff 
            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                insertStmt.setString(1, username);
                insertStmt.setString(2, password);
                insertStmt.setString(3, fullName);
                insertStmt.setString(4, role);
                
                int rowsAffected = insertStmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    System.out.println("\n✓ Staff member registered successfully!");
                    System.out.println("Username: " + username + ", Role: " + role);
                    return true;
                }
            }
            
            return false;
            
        } catch (SQLException e) {
            System.err.println("Database error during registration: " + e.getMessage());
            return false;
        }
    }

    //Admin role
    public boolean isAdmin(User user) {
        return user != null && "ADMIN".equalsIgnoreCase(user.getRole());
    }
    //Staff Role
    public boolean isStaff(User user) {
        return user != null && "STAFF".equalsIgnoreCase(user.getRole());
    }
    //User Role
    public boolean isUser(User user) {
        return user != null && "USER".equalsIgnoreCase(user.getRole());
    }

    public boolean userExists(String username) {
        String query = "SELECT COUNT(*) as count FROM users WHERE username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            
            return rs.getInt("count") > 0;
            
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            return false;
        }
    }

    public boolean isValidUsername(String username) {
        return username != null && username.length() >= 3 && username.matches("^[a-zA-Z0-9_]+$");
    }

    public boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    public boolean isValidName(String name) {
        return name != null && !name.isEmpty() && name.matches("^[a-zA-Z\\s]+$");
    }

    public boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    public String validateStaffRegistration(String username, String password, String fullName, String role) {
        if (username == null || username.trim().isEmpty()) {
            return "Username is required";
        }
        if (!isValidUsername(username)) {
            return "Username must be at least 3 characters and contain only alphanumeric characters";
        }
        if (password == null || password.trim().isEmpty()) {
            return "Password is required";
        }
        if (!isValidPassword(password)) {
            return "Password must be at least 6 characters";
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            return "Full name is required";
        }
        if (!isValidName(fullName)) {
            return "Full name must contain only letters";
        }
        if (role == null || role.trim().isEmpty()) {
            return "Role is required";
        }
        if (!role.equals("ADMIN") && !role.equals("STAFF")) {
            return "Invalid role selected";
        }
        return null;
    }
}
