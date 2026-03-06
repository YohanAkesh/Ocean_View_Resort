package com.app.service;

import com.app.model.Reservation;
import com.app.model.Room;
import com.app.util.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class ReservationService {
    private RoomService roomService;

    public ReservationService() {
        this.roomService = new RoomService();
    }

    // Generate a unique reservation number
    public String generateReservationNumber() {
        String query = "SELECT COUNT(*) as count FROM reservations";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                int count = rs.getInt("count") + 1;
                return String.format("RES%05d", count);
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        return "RES00001";
    }

    // Get all available rooms
    public List<Room> getAvailableRooms() {
        List<Room> rooms = roomService.getAllRooms();
        List<Room> availableRooms = new ArrayList<>();
        for (Room room : rooms) {
            if ("AVAILABLE".equals(room.getStatus())) {
                availableRooms.add(room);
            }
        }
        return availableRooms;
    }

    // Check if room is available for specific dates
    public boolean isRoomAvailable(int roomId, LocalDate checkIn, LocalDate checkOut) {
        // Proper overlap detection: new reservation overlaps if:
        // checkIn < existing.checkOut AND checkOut > existing.checkIn
        String query = "SELECT COUNT(*) as count FROM reservations " +
                      "WHERE room_id = ? " +
                      "AND status IN ('PENDING', 'CONFIRMED', 'CHECKED_IN') " +
                      "AND check_in_date < ? AND check_out_date > ?";
                      
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, roomId);
            pstmt.setDate(2, Date.valueOf(checkOut));
            pstmt.setDate(3, Date.valueOf(checkIn));
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt("count");
                System.out.println("Room " + roomId + " availability check: " + count + " overlapping reservations");
                return count == 0;
            }
        } catch (SQLException e) {
            System.err.println("Database error checking room availability: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Get available rooms for specific dates
    public List<Room> getAvailableRoomsForDates(LocalDate checkIn, LocalDate checkOut) {
        List<Room> availableRooms = new ArrayList<>();
        
        // Get all rooms with AVAILABLE status
        List<Room> allRooms = roomService.getAllRooms();
        
        for (Room room : allRooms) {
            // Check if room has AVAILABLE status and no overlapping reservations
            if ("AVAILABLE".equals(room.getStatus()) && isRoomAvailable(room.getRoomId(), checkIn, checkOut)) {
                availableRooms.add(room);
            }
        }
        
        return availableRooms;
    }

    // Create a new reservation with guest ID
    public boolean createReservation(int guestId, int roomId, LocalDate checkInDate,
                                    LocalDate checkOutDate, int numberOfGuests, 
                                    String specialRequests, int createdBy) {
        
        // Validate dates
        if (checkInDate.isAfter(checkOutDate) || checkInDate.isBefore(LocalDate.now())) {
            System.err.println("Invalid dates");
            return false;
        }

        // Check room availability
        if (!isRoomAvailable(roomId, checkInDate, checkOutDate)) {
            System.err.println("Room not available for selected dates");
            return false;
        }

        // Calculate total cost
        int numberOfNights = (int) ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        Room room = roomService.getRoomById(roomId);
        if (room == null) {
            return false;
        }
        double totalCost = numberOfNights * room.getPricePerNight();

        String insertQuery = "INSERT INTO reservations (guest_id, room_id, check_in_date, check_out_date, " +
                           "number_of_guests, total_cost, status, special_requests, created_by) " +
                           "VALUES (?, ?, ?, ?, ?, ?, 'PENDING', ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
            
            pstmt.setInt(1, guestId);
            pstmt.setInt(2, roomId);
            pstmt.setDate(3, Date.valueOf(checkInDate));
            pstmt.setDate(4, Date.valueOf(checkOutDate));
            pstmt.setInt(5, numberOfGuests);
            pstmt.setDouble(6, totalCost);
            pstmt.setString(7, specialRequests);
            pstmt.setInt(8, createdBy);

            int rowsAffected = pstmt.executeUpdate();
            
            // Update room status to OCCUPIED if reservation was created successfully
            if (rowsAffected > 0) {
                roomService.updateRoomStatus(roomId, "OCCUPIED");
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Get all reservations
    public List<Reservation> getAllReservations() {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT r.*, " +
                      "CONCAT(g.first_name, ' ', g.last_name) as guest_name, " +
                      "g.email, g.phone_number as contact_number, g.address, " +
                      "DATEDIFF(r.check_out_date, r.check_in_date) as number_of_nights " +
                      "FROM reservations r " +
                      "LEFT JOIN guests g ON r.guest_id = g.guest_id " +
                      "ORDER BY r.check_in_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                LocalDate checkIn = rs.getDate("check_in_date").toLocalDate();
                LocalDate checkOut = rs.getDate("check_out_date").toLocalDate();
                int nights = (int) ChronoUnit.DAYS.between(checkIn, checkOut);
                
                Reservation reservation = new Reservation(
                    rs.getInt("reservation_id"),
                    "RES" + String.format("%05d", rs.getInt("reservation_id")), // Generate reservation number
                    rs.getString("guest_name"),
                    rs.getString("address"),
                    rs.getString("contact_number"),
                    rs.getString("email"),
                    rs.getInt("room_id"),
                    checkIn,
                    checkOut,
                    nights,
                    rs.getInt("number_of_guests"),
                    rs.getDouble("total_cost"),
                    rs.getString("status"),
                    rs.getString("special_requests"),
                    rs.getInt("created_by")
                );
                reservations.add(reservation);
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }

        return reservations;
    }

    // Get reservation by ID
    public Reservation getReservationById(int reservationId) {
        String query = "SELECT r.*, " +
                      "CONCAT(g.first_name, ' ', g.last_name) as guest_name, " +
                      "g.email, g.phone_number as contact_number, g.address " +
                      "FROM reservations r " +
                      "LEFT JOIN guests g ON r.guest_id = g.guest_id " +
                      "WHERE r.reservation_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, reservationId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                LocalDate checkIn = rs.getDate("check_in_date").toLocalDate();
                LocalDate checkOut = rs.getDate("check_out_date").toLocalDate();
                int nights = (int) ChronoUnit.DAYS.between(checkIn, checkOut);
                
                return new Reservation(
                    rs.getInt("reservation_id"),
                    "RES" + String.format("%05d", rs.getInt("reservation_id")),
                    rs.getString("guest_name"),
                    rs.getString("address"),
                    rs.getString("contact_number"),
                    rs.getString("email"),
                    rs.getInt("room_id"),
                    checkIn,
                    checkOut,
                    nights,
                    rs.getInt("number_of_guests"),
                    rs.getDouble("total_cost"),
                    rs.getString("status"),
                    rs.getString("special_requests"),
                    rs.getInt("created_by")
                );
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    // Update reservation status
    public boolean updateReservationStatus(int reservationId, String status) {
        String query = "UPDATE reservations SET status = ? WHERE reservation_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, reservationId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            return false;
        }
    }

    // Update reservation details
    public boolean updateReservation(int reservationId, String guestName, String address,
                                    String contactNumber, String email, LocalDate checkInDate,
                                    LocalDate checkOutDate, String status) {
        
        Reservation existingReservation = getReservationById(reservationId);
        if (existingReservation == null) {
            return false;
        }

        // Calculate total cost
        Room room = roomService.getRoomById(existingReservation.getRoomId());
        if (room == null) {
            return false;
        }
        int numberOfNights = (int) ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        double totalCost = numberOfNights * room.getPricePerNight();

        // Only update dates, total cost, and status (guest info is in guests table)
        String query = "UPDATE reservations SET check_in_date = ?, check_out_date = ?, " +
                      "total_cost = ?, status = ? WHERE reservation_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setDate(1, Date.valueOf(checkInDate));
            pstmt.setDate(2, Date.valueOf(checkOutDate));
            pstmt.setDouble(3, totalCost);
            pstmt.setString(4, status);
            pstmt.setInt(5, reservationId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Delete reservation (cancel)
    public boolean deleteReservation(int reservationId) {
        // Get reservation details to find the room ID
        Reservation reservation = getReservationById(reservationId);
        if (reservation == null) {
            return false;
        }
        
        String query = "UPDATE reservations SET status = 'CANCELLED' WHERE reservation_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, reservationId);
            int rowsAffected = pstmt.executeUpdate();
            
            // Update room status to AVAILABLE if cancellation was successful
            if (rowsAffected > 0) {
                roomService.updateRoomStatus(reservation.getRoomId(), "AVAILABLE");
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            return false;
        }
    }

    // Get room name by ID
    public String getRoomNumber(int roomId) {
        Room room = roomService.getRoomById(roomId);
        return room != null ? room.getRoomNumber() : "N/A";
    }

    // Search reservations by guest name or reservation number
    public List<Reservation> searchReservations(String searchTerm) {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT r.*, " +
                      "CONCAT(g.first_name, ' ', g.last_name) as guest_name, " +
                      "g.email, g.phone_number as contact_number, g.address " +
                      "FROM reservations r " +
                      "LEFT JOIN guests g ON r.guest_id = g.guest_id " +
                      "WHERE CONCAT(g.first_name, ' ', g.last_name) LIKE ? " +
                      "OR g.email LIKE ? OR g.phone_number LIKE ? " +
                      "ORDER BY r.check_in_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                LocalDate checkIn = rs.getDate("check_in_date").toLocalDate();
                LocalDate checkOut = rs.getDate("check_out_date").toLocalDate();
                int nights = (int) ChronoUnit.DAYS.between(checkIn, checkOut);
                
                Reservation reservation = new Reservation(
                    rs.getInt("reservation_id"),
                    "RES" + String.format("%05d", rs.getInt("reservation_id")),
                    rs.getString("guest_name"),
                    rs.getString("address"),
                    rs.getString("contact_number"),
                    rs.getString("email"),
                    rs.getInt("room_id"),
                    checkIn,
                    checkOut,
                    nights,
                    rs.getInt("number_of_guests"),
                    rs.getDouble("total_cost"),
                    rs.getString("status"),
                    rs.getString("special_requests"),
                    rs.getInt("created_by")
                );
                reservations.add(reservation);
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }

        return reservations;
    }

    // Validate reservation input
    public String validateReservationInput(String guestName, String contactNumber, String email,
                                          String checkInStr, String checkOutStr) {
        if (guestName == null || guestName.trim().isEmpty()) {
            return "Guest name is required";
        }
        if (contactNumber == null || contactNumber.trim().isEmpty()) {
            return "Contact number is required";
        }
        if (email == null || email.trim().isEmpty()) {
            return "Email is required";
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return "Invalid email format";
        }
        if (checkInStr == null || checkInStr.trim().isEmpty()) {
            return "Check-in date is required";
        }
        if (checkOutStr == null || checkOutStr.trim().isEmpty()) {
            return "Check-out date is required";
        }
        
        try {
            LocalDate checkIn = LocalDate.parse(checkInStr);
            LocalDate checkOut = LocalDate.parse(checkOutStr);
            
            if (checkIn.isBefore(LocalDate.now())) {
                return "Check-in date cannot be in the past";
            }
            if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) {
                return "Check-out date must be after check-in date";
            }
        } catch (Exception e) {
            return "Invalid date format";
        }
        
        return null;
    }
}
