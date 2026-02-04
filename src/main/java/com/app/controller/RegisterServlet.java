package com.app.controller;

import com.app.model.User;
import com.app.service.AuthenticationService;
import com.app.util.InputValidator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

//Resgistration handler
@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    private AuthenticationService authService;

    @Override
    public void init() throws ServletException {
        super.init();
        authService = new AuthenticationService();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check if user is admin
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        User currentUser = (User) session.getAttribute("user");
        if (!authService.isAdmin(currentUser)) {
            response.sendRedirect("dashboard.jsp?error=unauthorized");
            return;
        }
        
        // Get form parameters
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String fullName = request.getParameter("fullName");
        String role = request.getParameter("role");
        
        // Validate input
        String error = null;
        
        if (username == null || username.trim().isEmpty()) {
            error = "Username is required";
        } else if (!InputValidator.isValidUsername(username)) {
            error = "Username must be at least 3 characters and contain only alphanumeric characters";
        } else if (password == null || password.trim().isEmpty()) {
            error = "Password is required";
        } else if (!InputValidator.isValidPassword(password)) {
            error = "Password must be at least 6 characters";
        } else if (fullName == null || fullName.trim().isEmpty()) {
            error = "Full name is required";
        } else if (!InputValidator.isValidName(fullName)) {
            error = "Full name must contain only letters";
        } else if (role == null || role.trim().isEmpty()) {
            error = "Role is required";
        } else if (!role.equals("ADMIN") && !role.equals("STAFF")) {
            error = "Invalid role selected";
        }
        
        if (error != null) {
            response.sendRedirect("register.jsp?error=" + java.net.URLEncoder.encode(error, "UTF-8"));
            return;
        }
        
        // Register the staff member
        if (authService.registerStaff(username, password, fullName, role)) {
            response.sendRedirect("register.jsp?success=true");
        } else {
            response.sendRedirect("register.jsp?error=" + 
                java.net.URLEncoder.encode("Username already exists or registration failed", "UTF-8"));
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check if user is logged in and is admin
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        User currentUser = (User) session.getAttribute("user");
        if (!authService.isAdmin(currentUser)) {
            response.sendRedirect("dashboard.jsp?error=unauthorized");
            return;
        }
        
        // Forward to register.jsp
        request.getRequestDispatcher("register.jsp").forward(request, response);
    }
}
