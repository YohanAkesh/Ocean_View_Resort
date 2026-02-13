package com.app.controller;

import com.app.model.User;
import com.app.service.IGuestService;
import com.app.service.impl.GuestServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/GuestServlet")
public class GuestServlet extends HttpServlet {
    private IGuestService guestService;

    @Override
    public void init() throws ServletException {
        super.init();
        guestService = new GuestServiceImpl();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        String action = request.getParameter("action");

        if ("add".equals(action)) {
            handleAddGuest(request, response, user);
        } else if ("update".equals(action)) {
            handleUpdateGuest(request, response);
        } else if ("delete".equals(action)) {
            handleDeleteGuest(request, response);
        } else {
            response.sendRedirect("manage-guests.jsp?error=invalid_action");
        }
    }

    private void handleAddGuest(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException {

        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");
        String phoneNumber = request.getParameter("phoneNumber");
        String address = request.getParameter("address");
        String idType = request.getParameter("idType");
        String idNumber = request.getParameter("idNumber");
        String nationality = request.getParameter("nationality");

        // Validate input
        String error = guestService.validateGuestInput(firstName, lastName, email, phoneNumber, idNumber);

        if (error != null) {
            response.sendRedirect("add-guest.jsp?error=" + java.net.URLEncoder.encode(error, "UTF-8"));
            return;
        }

        // Add guest
        if (guestService.addGuest(firstName, lastName, email, phoneNumber, address,
                                 idType, idNumber, nationality, user.getUserId())) {
            response.sendRedirect("manage-guests.jsp?success=Guest registered successfully");
        } else {
            response.sendRedirect("add-guest.jsp?error=" +
                java.net.URLEncoder.encode("Guest with this email or ID already exists", "UTF-8"));
        }
    }

    private void handleUpdateGuest(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String guestIdStr = request.getParameter("guestId");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");
        String phoneNumber = request.getParameter("phoneNumber");
        String address = request.getParameter("address");
        String idType = request.getParameter("idType");
        String idNumber = request.getParameter("idNumber");
        String nationality = request.getParameter("nationality");

        // Validate input
        String error = guestService.validateGuestInput(firstName, lastName, email, phoneNumber, idNumber);

        if (error != null) {
            response.sendRedirect("manage-guests.jsp?error=" + java.net.URLEncoder.encode(error, "UTF-8"));
            return;
        }

        try {
            int guestId = Integer.parseInt(guestIdStr);
            if (guestService.updateGuest(guestId, firstName, lastName, email, phoneNumber,
                                        address, idType, idNumber, nationality)) {
                response.sendRedirect("manage-guests.jsp?success=Guest updated successfully");
            } else {
                response.sendRedirect("manage-guests.jsp?error=Failed to update guest");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("manage-guests.jsp?error=Invalid guest ID");
        }
    }

    private void handleDeleteGuest(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String guestIdStr = request.getParameter("guestId");

        try {
            int guestId = Integer.parseInt(guestIdStr);
            if (guestService.deleteGuest(guestId)) {
                response.sendRedirect("manage-guests.jsp?success=Guest deleted successfully");
            } else {
                response.sendRedirect("manage-guests.jsp?error=Failed to delete guest");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("manage-guests.jsp?error=Invalid guest ID");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("manage-guests.jsp");
    }
}
