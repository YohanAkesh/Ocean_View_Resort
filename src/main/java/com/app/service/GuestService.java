package com.app.service;

import com.app.model.Guest;
import com.app.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GuestService {

    // Add a new guest
    public boolean addGuest(String firstName, String lastName, String email, String phoneNumber,
                          String address, String idType, String idNumber, String nationality, int createdBy) {
        
        // Check if guest already exists by email or ID number
        if (guestExistsByEmail(email)) {
            System.err.println("Guest with this email already exists");
            return false;
        }
        
        if (guestExistsByIdNumber(idNumber)) {
            System.err.println("Guest with this ID number already exists");
            return false;
        }

        String insertQuery = "INSERT INTO guests (first_name, last_name, email, phone_number, " +
                           "address, id_type, id_number, nationality, created_by) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {

            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setString(4, phoneNumber);
            pstmt.setString(5, address);
            pstmt.setString(6, idType);
            pstmt.setString(7, idNumber);
            pstmt.setString(8, nationality);
            pstmt.setInt(9, createdBy);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✓ Guest registered successfully!");
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            return false;
        }
    }

    // Get all guests
    public List<Guest> getAllGuests() {
        List<Guest> guests = new ArrayList<>();
        String query = "SELECT * FROM guests ORDER BY first_name, last_name";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Guest guest = new Guest(
                    rs.getInt("guest_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"),
                    rs.getString("phone_number"),
                    rs.getString("address"),
                    rs.getString("id_type"),
                    rs.getString("id_number"),
                    rs.getString("nationality"),
                    rs.getInt("created_by")
                );
                guests.add(guest);
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }

        return guests;
    }

    // Get guest by ID
    public Guest getGuestById(int guestId) {
        String query = "SELECT * FROM guests WHERE guest_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, guestId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Guest(
                    rs.getInt("guest_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"),
                    rs.getString("phone_number"),
                    rs.getString("address"),
                    rs.getString("id_type"),
                    rs.getString("id_number"),
                    rs.getString("nationality"),
                    rs.getInt("created_by")
                );
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }

        return null;
    }

    // Update guest
    public boolean updateGuest(int guestId, String firstName, String lastName, String email,
                              String phoneNumber, String address, String idType, String idNumber,
                              String nationality) {
        
        String query = "UPDATE guests SET first_name = ?, last_name = ?, email = ?, " +
                      "phone_number = ?, address = ?, id_type = ?, id_number = ?, nationality = ? " +
                      "WHERE guest_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setString(4, phoneNumber);
            pstmt.setString(5, address);
            pstmt.setString(6, idType);
            pstmt.setString(7, idNumber);
            pstmt.setString(8, nationality);
            pstmt.setInt(9, guestId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            return false;
        }
    }

    // Delete guest
    public boolean deleteGuest(int guestId) {
        String query = "DELETE FROM guests WHERE guest_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, guestId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            return false;
        }
    }

    // Search guests
    public List<Guest> searchGuests(String searchTerm) {
        List<Guest> guests = new ArrayList<>();
        String query = "SELECT * FROM guests WHERE first_name LIKE ? OR last_name LIKE ? " +
                      "OR email LIKE ? OR phone_number LIKE ? OR id_number LIKE ? " +
                      "ORDER BY first_name, last_name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);
            pstmt.setString(5, searchPattern);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Guest guest = new Guest(
                    rs.getInt("guest_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"),
                    rs.getString("phone_number"),
                    rs.getString("address"),
                    rs.getString("id_type"),
                    rs.getString("id_number"),
                    rs.getString("nationality"),
                    rs.getInt("created_by")
                );
                guests.add(guest);
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }

        return guests;
    }

    // Check if guest exists by email
    public boolean guestExistsByEmail(String email) {
        String query = "SELECT COUNT(*) as count FROM guests WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            rs.next();

            return rs.getInt("count") > 0;

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            return false;
        }
    }

    // Check if guest exists by ID number
    public boolean guestExistsByIdNumber(String idNumber) {
        String query = "SELECT COUNT(*) as count FROM guests WHERE id_number = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, idNumber);
            ResultSet rs = pstmt.executeQuery();
            rs.next();

            return rs.getInt("count") > 0;

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            return false;
        }
    }

    // Validate guest input
    public String validateGuestInput(String firstName, String lastName, String email,
                                    String phoneNumber, String idNumber) {
        if (firstName == null || firstName.trim().isEmpty()) {
            return "First name is required";
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            return "Last name is required";
        }
        if (email == null || email.trim().isEmpty()) {
            return "Email is required";
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return "Invalid email format";
        }
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return "Phone number is required";
        }
        if (idNumber == null || idNumber.trim().isEmpty()) {
            return "ID number is required";
        }

        return null;
    }
}
