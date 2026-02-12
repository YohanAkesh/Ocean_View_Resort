package com.app.service.impl;

import com.app.dao.UserDAO;
import com.app.dao.impl.UserDAOImpl;
import com.app.model.User;
import com.app.service.IAuthenticationService;

public class AuthenticationServiceImpl implements IAuthenticationService {
    private final UserDAO userDAO;

    public AuthenticationServiceImpl() {
        this.userDAO = new UserDAOImpl();
    }

    @Override
    public User login(String username, String password) {
        User user = userDAO.getUserByUsername(username);
        
        if (user != null && user.getPassword().equals(password)) {
            System.out.println("\n✓ Login successful! Welcome, " + user.getFullName());
            return user;
        }
        
        System.out.println("\n✗ Invalid username or password!");
        return null;
    }

    @Override
    public boolean registerStaff(String username, String password, String fullName, String email, String role) {
        System.out.println("\n[DEBUG] Starting staff registration for username: " + username);
        
        if (userDAO.userExists(username)) {
            System.out.println("\n✗ Username already exists! Please try a different username.");
            return false;
        }
        
        System.out.println("[DEBUG] Username is available, proceeding with insert");
        
        boolean result = userDAO.createUser(username, password, fullName, role, email);
        
        if (result) {
            System.out.println("\n✓ Staff member registered successfully!");
            System.out.println("Username: " + username + ", Role: " + role);
        } else {
            System.out.println("[DEBUG] Insert failed");
        }
        
        return result;
    }

    @Override
    public boolean isAdmin(User user) {
        return user != null && "ADMIN".equalsIgnoreCase(user.getRole());
    }

    @Override
    public boolean isStaff(User user) {
        return user != null && "STAFF".equalsIgnoreCase(user.getRole());
    }

    @Override
    public boolean isUser(User user) {
        return user != null && "USER".equalsIgnoreCase(user.getRole());
    }

    @Override
    public boolean userExists(String username) {
        return userDAO.userExists(username);
    }

    @Override
    public boolean isValidUsername(String username) {
        return username != null && username.length() >= 3 && username.matches("^[a-zA-Z0-9_]+$");
    }

    @Override
    public boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    @Override
    public boolean isValidName(String name) {
        return name != null && !name.isEmpty() && name.matches("^[a-zA-Z\\s]+$");
    }

    @Override
    public boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    @Override
    public String validateStaffRegistration(String username, String password, String fullName, 
                                           String email, String role) {
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
        if (email == null || email.trim().isEmpty()) {
            return "Email is required";
        }
        if (!isValidEmail(email)) {
            return "Invalid email format";
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
