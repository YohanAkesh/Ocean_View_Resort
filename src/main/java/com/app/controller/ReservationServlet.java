package com.app.controller;

import com.app.model.User;
import com.app.model.Guest;
import com.app.service.ReservationService;
import com.app.service.GuestService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/ReservationServlet")
public class ReservationServlet extends HttpServlet {
    private ReservationService reservationService;
    private GuestService guestService;

    @Override
    public void init() throws ServletException {
        super.init();
        reservationService = new ReservationService();
        guestService = new GuestService();
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

        if ("create".equals(action)) {
            handleCreateReservation(request, response, user);
        } else if ("update".equals(action)) {
            handleUpdateReservation(request, response);
        } else if ("updateStatus".equals(action)) {
            handleUpdateStatus(request, response);
        } else if ("cancel".equals(action)) {
            handleCancelReservation(request, response);
        } else {
            response.sendRedirect("staff-reservations.jsp?error=invalid_action");
        }
    }

    private void handleCreateReservation(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        String guestIdStr = request.getParameter("guestId");
        String roomIdStr = request.getParameter("roomId");
        String checkInStr = request.getParameter("checkInDate");
        String checkOutStr = request.getParameter("checkOutDate");
        String numberOfGuestsStr = request.getParameter("numberOfGuests");
        String specialRequests = request.getParameter("specialRequests");

        // Validate guest selection
        if (guestIdStr == null || guestIdStr.trim().isEmpty()) {
            response.sendRedirect("add-reservation.jsp?error=" + 
                java.net.URLEncoder.encode("Please select a guest", "UTF-8"));
            return;
        }

        if (roomIdStr == null || roomIdStr.trim().isEmpty()) {
            response.sendRedirect("add-reservation.jsp?error=" + 
                java.net.URLEncoder.encode("Please select a room", "UTF-8"));
            return;
        }

        try {
            int guestId = Integer.parseInt(guestIdStr);
            int roomId = Integer.parseInt(roomIdStr);
            int numberOfGuests = numberOfGuestsStr != null && !numberOfGuestsStr.trim().isEmpty() 
                ? Integer.parseInt(numberOfGuestsStr) : 1;
            
            // Verify guest exists
            Guest guest = guestService.getGuestById(guestId);
            if (guest == null) {
                response.sendRedirect("add-reservation.jsp?error=" + 
                    java.net.URLEncoder.encode("Guest not found", "UTF-8"));
                return;
            }

            // Validate dates
            if (checkInStr == null || checkInStr.trim().isEmpty() || 
                checkOutStr == null || checkOutStr.trim().isEmpty()) {
                response.sendRedirect("add-reservation.jsp?error=" + 
                    java.net.URLEncoder.encode("Please select check-in and check-out dates", "UTF-8"));
                return;
            }

            LocalDate checkInDate = LocalDate.parse(checkInStr);
            LocalDate checkOutDate = LocalDate.parse(checkOutStr);

            // Validate dates
            if (checkInDate.isBefore(LocalDate.now())) {
                response.sendRedirect("add-reservation.jsp?error=" + 
                    java.net.URLEncoder.encode("Check-in date cannot be in the past", "UTF-8"));
                return;
            }

            if (checkOutDate.isBefore(checkInDate) || checkOutDate.isEqual(checkInDate)) {
                response.sendRedirect("add-reservation.jsp?error=" + 
                    java.net.URLEncoder.encode("Check-out date must be after check-in date", "UTF-8"));
                return;
            }

            // Check room availability
            if (!reservationService.isRoomAvailable(roomId, checkInDate, checkOutDate)) {
                String roomNumber = reservationService.getRoomNumber(roomId);
                String dateRange = checkInDate + " to " + checkOutDate;
                response.sendRedirect("add-reservation.jsp?error=" + 
                    java.net.URLEncoder.encode("Room " + roomNumber + " is already booked for " + dateRange + 
                    ". Please select different dates or choose another room.", "UTF-8"));
                return;
            }

            // Create reservation
            boolean success = reservationService.createReservation(
                guestId, roomId, checkInDate, checkOutDate, 
                numberOfGuests, specialRequests, user.getUserId()
            );

            if (success) {
                response.sendRedirect("staff-reservations.jsp?success=Reservation created successfully");
            } else {
                response.sendRedirect("add-reservation.jsp?error=" + 
                    java.net.URLEncoder.encode("Failed to create reservation. Please try again or contact support.", "UTF-8"));
            }

        } catch (NumberFormatException e) {
            response.sendRedirect("add-reservation.jsp?error=" + 
                java.net.URLEncoder.encode("Invalid guest or room selection", "UTF-8"));
        } catch (Exception e) {
            response.sendRedirect("add-reservation.jsp?error=" + 
                java.net.URLEncoder.encode("Error: " + e.getMessage(), "UTF-8"));
        }
    }

    private void handleUpdateReservation(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        String reservationIdStr = request.getParameter("reservationId");
        String guestName = request.getParameter("guestName");
        String address = request.getParameter("address");
        String contactNumber = request.getParameter("contactNumber");
        String email = request.getParameter("email");
        String checkInStr = request.getParameter("checkInDate");
        String checkOutStr = request.getParameter("checkOutDate");
        String status = request.getParameter("status");

        // Validate input
        String error = reservationService.validateReservationInput(
            guestName, contactNumber, email, checkInStr, checkOutStr
        );

        if (error != null) {
            response.sendRedirect("staff-reservations.jsp?error=" + java.net.URLEncoder.encode(error, "UTF-8"));
            return;
        }

        try {
            int reservationId = Integer.parseInt(reservationIdStr);
            LocalDate checkInDate = LocalDate.parse(checkInStr);
            LocalDate checkOutDate = LocalDate.parse(checkOutStr);

            boolean success = reservationService.updateReservation(
                reservationId, guestName, address, contactNumber, email,
                checkInDate, checkOutDate, status
            );

            if (success) {
                response.sendRedirect("staff-reservations.jsp?success=Reservation updated successfully");
            } else {
                response.sendRedirect("staff-reservations.jsp?error=" + 
                    java.net.URLEncoder.encode("Failed to update reservation", "UTF-8"));
            }

        } catch (Exception e) {
            response.sendRedirect("staff-reservations.jsp?error=" + 
                java.net.URLEncoder.encode("Error: " + e.getMessage(), "UTF-8"));
        }
    }

    private void handleUpdateStatus(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        String reservationIdStr = request.getParameter("reservationId");
        String status = request.getParameter("status");

        if (reservationIdStr == null || status == null) {
            response.sendRedirect("staff-reservations.jsp?error=Invalid parameters");
            return;
        }

        try {
            int reservationId = Integer.parseInt(reservationIdStr);
            boolean success = reservationService.updateReservationStatus(reservationId, status);

            if (success) {
                response.sendRedirect("staff-reservations.jsp?success=Status updated successfully");
            } else {
                response.sendRedirect("staff-reservations.jsp?error=Failed to update status");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect("staff-reservations.jsp?error=Invalid reservation ID");
        }
    }

    private void handleCancelReservation(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        String reservationIdStr = request.getParameter("reservationId");

        if (reservationIdStr == null) {
            response.sendRedirect("staff-reservations.jsp?error=Invalid reservation ID");
            return;
        }

        try {
            int reservationId = Integer.parseInt(reservationIdStr);
            boolean success = reservationService.deleteReservation(reservationId);

            if (success) {
                response.sendRedirect("staff-reservations.jsp?success=Reservation cancelled successfully");
            } else {
                response.sendRedirect("staff-reservations.jsp?error=Failed to cancel reservation");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect("staff-reservations.jsp?error=Invalid reservation ID");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirect GET requests to the reservations page
        response.sendRedirect("staff-reservations.jsp");
    }
}
