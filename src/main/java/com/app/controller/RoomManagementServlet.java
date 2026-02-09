package com.app.controller;

import com.app.model.User;
import com.app.service.RoomService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/RoomManagementServlet")
public class RoomManagementServlet extends HttpServlet {
    private RoomService roomService;

    @Override
    public void init() throws ServletException {
        super.init();
        roomService = new RoomService();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        AuthenticationService authService = new AuthenticationService();
        
        if (!authService.isAdmin(user)) {
            response.sendRedirect("dashboard.jsp?error=unauthorized");
            return;
        }

        String action = request.getParameter("action");

        if ("add".equals(action)) {
            handleAddRoom(request, response);
        } else if ("update".equals(action)) {
            handleUpdateRoom(request, response);
        } else if ("delete".equals(action)) {
            handleDeleteRoom(request, response);
        } else {
            response.sendRedirect("manage-rooms.jsp?error=invalid_action");
        }
    }

    private void handleAddRoom(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String roomNumber = request.getParameter("roomNumber");
        String roomType = request.getParameter("roomType");
        String capacityStr = request.getParameter("capacity");
        String priceStr = request.getParameter("price");

        String error = roomService.validateRoomInput(roomNumber, roomType, capacityStr, priceStr);

        if (error != null) {
            response.sendRedirect("add-room.jsp?error=" + java.net.URLEncoder.encode(error, "UTF-8"));
            return;
        }

        int capacity = Integer.parseInt(capacityStr);
        double price = Double.parseDouble(priceStr);

        if (roomService.addRoom(roomNumber, roomType, capacity, price)) {
            response.sendRedirect("manage-rooms.jsp?success=Room added successfully");
        } else {
            response.sendRedirect("add-room.jsp?error=Room number already exists or database error");
        }
    }

    private void handleUpdateRoom(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String roomIdStr = request.getParameter("roomId");
        String roomNumber = request.getParameter("roomNumber");
        String roomType = request.getParameter("roomType");
        String capacityStr = request.getParameter("capacity");
        String priceStr = request.getParameter("price");
        String status = request.getParameter("status");

        String error = roomService.validateRoomInput(roomNumber, roomType, capacityStr, priceStr);

        if (error != null) {
            response.sendRedirect("manage-rooms.jsp?error=" + java.net.URLEncoder.encode(error, "UTF-8"));
            return;
        }

        try {
            int roomId = Integer.parseInt(roomIdStr);
            int capacity = Integer.parseInt(capacityStr);
            double price = Double.parseDouble(priceStr);

            if (roomService.updateRoom(roomId, roomNumber, roomType, capacity, price, status)) {
                response.sendRedirect("manage-rooms.jsp?success=Room updated successfully");
            } else {
                response.sendRedirect("manage-rooms.jsp?error=Failed to update room");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("manage-rooms.jsp?error=Invalid input");
        }
    }

    private void handleDeleteRoom(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String roomIdStr = request.getParameter("roomId");

        try {
            int roomId = Integer.parseInt(roomIdStr);
            if (roomService.deleteRoom(roomId)) {
                response.sendRedirect("manage-rooms.jsp?success=Room deleted successfully");
            } else {
                response.sendRedirect("manage-rooms.jsp?error=Failed to delete room");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("manage-rooms.jsp?error=Invalid room ID");
        }
    }
}
