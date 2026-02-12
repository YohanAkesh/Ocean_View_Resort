package com.app.dao.impl;

import com.app.dao.UserDAO;
import com.app.model.User;
import com.app.util.DatabaseConnection;
import java.sql.*;

public class UserDAOImpl implements UserDAO {

    @Override
    public User getUserByUsername(String username) {
        String query = "SELECT user_id, username, password, full_name, role FROM users WHERE username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new User(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("full_name"),
                    rs.getString("role")
                );
            }
            
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        
        return null;
    }

    @Override
    public boolean createUser(String username, String password, String fullName, String role, String email) {
        String insertQuery = "INSERT INTO users (username, password, full_name, role, email) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, fullName);
            pstmt.setString(4, role);
            pstmt.setString(5, email);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            return false;
        }
    }

    @Override
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

    @Override
    public boolean updateUser(int userId, String username, String password, String fullName, String role, String email) {
        String query = "UPDATE users SET username = ?, password = ?, full_name = ?, role = ?, email = ? WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, fullName);
            pstmt.setString(4, role);
            pstmt.setString(5, email);
            pstmt.setInt(6, userId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteUser(int userId) {
        String query = "DELETE FROM users WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, userId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            return false;
        }
    }
}
