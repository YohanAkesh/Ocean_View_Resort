package com.app.controller;

import com.app.model.Room;
import com.app.service.ReservationService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/AddReservationPageServlet")
public class AddReservationPageServlet extends HttpServlet {
    private ReservationService reservationService;

    @Override
    public void init() throws ServletException {
        super.init();
        reservationService = new ReservationService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Get available rooms through service
        List<Room> availableRooms = reservationService.getAvailableRooms();
        request.setAttribute("availableRooms", availableRooms);
        
        request.getRequestDispatcher("add-reservation-view.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
