package com.app.controller;

import com.app.model.Room;
import com.app.model.User;
import com.app.service.AuthenticationService;
import com.app.service.RoomService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/ManageRoomsPageServlet")
public class ManageRoomsPageServlet extends HttpServlet {
    private RoomService roomService;
    private AuthenticationService authService;

    @Override
    public void init() throws ServletException {
        super.init();
        roomService = new RoomService();
        authService = new AuthenticationService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        if (!authService.isAdmin(user)) {
            response.sendRedirect("dashboard.jsp?error=unauthorized");
            return;
        }

        // Get all rooms through service
        List<Room> rooms = roomService.getAllRooms();
        request.setAttribute("rooms", rooms);
        
        request.getRequestDispatcher("manage-rooms-view.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
