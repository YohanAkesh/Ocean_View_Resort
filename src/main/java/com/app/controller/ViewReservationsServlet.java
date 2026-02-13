package com.app.controller;

import com.app.model.Reservation;
import com.app.service.IReservationService;
import com.app.service.impl.ReservationServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/ViewReservationsServlet")
public class ViewReservationsServlet extends HttpServlet {
    private IReservationService reservationService;

    @Override
    public void init() throws ServletException {
        super.init();
        reservationService = new ReservationServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // Get reservations
        List<Reservation> reservations;
        String searchTerm = request.getParameter("search");
        
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            reservations = reservationService.searchReservations(searchTerm);
            request.setAttribute("searchTerm", searchTerm);
        } else {
            reservations = reservationService.getAllReservations();
        }

        request.setAttribute("reservations", reservations);
        request.setAttribute("reservationService", reservationService);
        
        request.getRequestDispatcher("staff-reservations-view.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
