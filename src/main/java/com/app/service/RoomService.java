package com.app.service;

import com.app.model.Room;
import com.app.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomService {

    public boolean addRoom(String roomNumber, String roomType, int capacity, double pricePerNight) {
        String checkQuery = "SELECT COUNT(*) as count FROM rooms WHERE room_number = ?";
        String insertQuery = "INSERT INTO rooms (room_number, room_type, capacity, price_per_night, status) VALUES (?, ?, ?, ?, 'AVAILABLE')";

        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, roomNumber);
                ResultSet rs = checkStmt.executeQuery();
                rs.next();

                if (rs.getInt("count") > 0) {
                    System.out.println("Room number already exists!");
                    return false;
                }
            }

            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                insertStmt.setString(1, roomNumber);
                insertStmt.setString(2, roomType);
                insertStmt.setInt(3, capacity);
                insertStmt.setDouble(4, pricePerNight);

                int rowsAffected = insertStmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("✓ Room added successfully!");
                    return true;
                }
            }

            return false;

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            return false;
        }
    }

    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        String query = "SELECT room_id, room_number, room_type, capacity, price_per_night, status FROM rooms";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Room room = new Room(
                    rs.getInt("room_id"),
                    rs.getString("room_number"),
                    rs.getString("room_type"),
                    rs.getInt("capacity"),
                    rs.getDouble("price_per_night"),
                    rs.getString("status")
                );
                rooms.add(room);
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }

        return rooms;
    }

    public Room getRoomById(int roomId) {
        String query = "SELECT room_id, room_number, room_type, capacity, price_per_night, status FROM rooms WHERE room_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, roomId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Room(
                    rs.getInt("room_id"),
                    rs.getString("room_number"),
                    rs.getString("room_type"),
                    rs.getInt("capacity"),
                    rs.getDouble("price_per_night"),
                    rs.getString("status")
                );
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }

        return null;
    }

    public boolean updateRoom(int roomId, String roomNumber, String roomType, int capacity, double pricePerNight, String status) {
        String updateQuery = "UPDATE rooms SET room_number = ?, room_type = ?, capacity = ?, price_per_night = ?, status = ? WHERE room_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {

            pstmt.setString(1, roomNumber);
            pstmt.setString(2, roomType);
            pstmt.setInt(3, capacity);
            pstmt.setDouble(4, pricePerNight);
            pstmt.setString(5, status);
            pstmt.setInt(6, roomId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteRoom(int roomId) {
        String deleteQuery = "DELETE FROM rooms WHERE room_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {

            pstmt.setInt(1, roomId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            return false;
        }
    }

    public List<Room> getAvailableRooms() {
        List<Room> rooms = new ArrayList<>();
        String query = "SELECT room_id, room_number, room_type, capacity, price_per_night, status FROM rooms WHERE status = 'AVAILABLE'";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Room room = new Room(
                    rs.getInt("room_id"),
                    rs.getString("room_number"),
                    rs.getString("room_type"),
                    rs.getInt("capacity"),
                    rs.getDouble("price_per_night"),
                    rs.getString("status")
                );
                rooms.add(room);
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }

        return rooms;
    }

    public boolean updateRoomStatus(int roomId, String status) {
        String updateQuery = "UPDATE rooms SET status = ? WHERE room_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, roomId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            return false;
        }
    }

    public String validateRoomInput(String roomNumber, String roomType, String capacityStr, String priceStr) {
        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            return "Room number is required";
        }
        if (roomType == null || roomType.trim().isEmpty()) {
            return "Room type is required";
        }
        if (capacityStr == null || capacityStr.trim().isEmpty()) {
            return "Capacity is required";
        }
        if (priceStr == null || priceStr.trim().isEmpty()) {
            return "Price is required";
        }

        try {
            int capacity = Integer.parseInt(capacityStr);
            if (capacity <= 0) {
                return "Capacity must be greater than 0";
            }

            double price = Double.parseDouble(priceStr);
            if (price <= 0) {
                return "Price must be greater than 0";
            }
        } catch (NumberFormatException e) {
            return "Capacity and price must be valid numbers";
        }

        return null;
    }
}
