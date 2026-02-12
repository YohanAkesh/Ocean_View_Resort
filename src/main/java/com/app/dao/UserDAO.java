package com.app.dao;

import com.app.model.User;

public interface UserDAO {
    User getUserByUsername(String username);
    
    boolean createUser(String username, String password, String fullName, String role, String email);
    
    boolean userExists(String username);
    
    boolean updateUser(int userId, String username, String password, String fullName, String role, String email);
    
    boolean deleteUser(int userId);
}
