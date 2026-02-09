package com.app.controller;

import com.app.model.User;
import com.app.service.AuthenticationService;

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
        
        // Validate input using service
        String error = authService.validateStaffRegistration(username, password, fullName, role);
        
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
