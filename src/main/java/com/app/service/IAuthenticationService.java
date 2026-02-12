package com.app.service;

import com.app.model.User;

public interface IAuthenticationService {
    User login(String username, String password);
    
    boolean registerStaff(String username, String password, String fullName, String email, String role);
    
    boolean isAdmin(User user);
    
    boolean isStaff(User user);
    
    boolean isUser(User user);
    
    boolean userExists(String username);
    
    boolean isValidUsername(String username);
    
    boolean isValidPassword(String password);
    
    boolean isValidName(String name);
    
    boolean isValidEmail(String email);
    
    String validateStaffRegistration(String username, String password, String fullName, 
                                    String email, String role);
}
