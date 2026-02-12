package com.app.dao.impl;

import com.app.dao.ReservationDAO;
import com.app.model.Reservation;
import com.app.util.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAOImpl implements ReservationDAO {

    @Override
    public boolean createReservation(String reservationNumber, int guestId, int roomId,
                                    LocalDate checkInDate, LocalDate checkOutDate, int numberOfGuests,
                                    double totalPrice, String status, String specialRequests, int createdBy) {
        
        String insertQuery = "INSERT INTO reservations (guest_id, room_id, check_in_date, check_out_date, " +
                           "number_of_guests, total_cost, status, special_requests, created_by) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
            
            pstmt.setInt(1, guestId);
            pstmt.setInt(2, roomId);
            pstmt.setDate(3, Date.valueOf(checkInDate));
            pstmt.setDate(4, Date.valueOf(checkOutDate));
            pstmt.setInt(5, numberOfGuests);
            pstmt.setDouble(6, totalPrice);
            pstmt.setString(7, status);
            pstmt.setString(8, specialRequests);
            pstmt.setInt(9, createdBy);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            return false;
        }
    }

    @Override
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
        }

        return reservations;
    }

    @Override
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
        }

        return null;
    }

    @Override
    public List<Reservation> getReservationsByGuest(int guestId) {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT r.*, " +
                      "CONCAT(g.first_name, ' ', g.last_name) as guest_name, " +
                      "g.email, g.phone_number as contact_number, g.address " +
                      "FROM reservations r " +
                      "LEFT JOIN guests g ON r.guest_id = g.guest_id " +
                      "WHERE r.guest_id = ? " +
                      "ORDER BY r.check_in_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, guestId);
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
        }

        return reservations;
    }

    @Override
    public List<Reservation> getReservationsByStatus(String status) {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT r.*, " +
                      "CONCAT(g.first_name, ' ', g.last_name) as guest_name, " +
                      "g.email, g.phone_number as contact_number, g.address " +
                      "FROM reservations r " +
                      "LEFT JOIN guests g ON r.guest_id = g.guest_id " +
                      "WHERE r.status = ? " +
                      "ORDER BY r.check_in_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, status);
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
        }

        return reservations;
    }

    @Override
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

    @Override
    public boolean updateReservation(int reservationId, LocalDate checkInDate, LocalDate checkOutDate,
                                    int numberOfGuests, String specialRequests, double totalCost) {
        
        String query = "UPDATE reservations SET check_in_date = ?, check_out_date = ?, " +
                      "number_of_guests = ?, special_requests = ?, total_cost = ? WHERE reservation_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setDate(1, Date.valueOf(checkInDate));
            pstmt.setDate(2, Date.valueOf(checkOutDate));
            pstmt.setInt(3, numberOfGuests);
            pstmt.setString(4, specialRequests);
            pstmt.setDouble(5, totalCost);
            pstmt.setInt(6, reservationId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteReservation(int reservationId) {
        String query = "UPDATE reservations SET status = 'CANCELLED' WHERE reservation_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, reservationId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean isRoomAvailable(int roomId, LocalDate checkIn, LocalDate checkOut) {
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
                return rs.getInt("count") == 0;
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public int getReservationCount() {
        String query = "SELECT COUNT(*) as count FROM reservations";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        return 0;
    }
}
