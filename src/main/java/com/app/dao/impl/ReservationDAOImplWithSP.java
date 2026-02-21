package com.app.dao.impl;

import com.app.util.DatabaseConnection;

import java.sql.*;

/**
 * Enhanced Reservation DAO using Stored Procedures
 * Demonstrates use of CallableStatement for advanced database features
 */
public class ReservationDAOImplWithSP {

    /**
     * Create reservation using stored procedure
     * Demonstrates: CallableStatement, OUT parameters, business rule validation at DB level
     */
    public boolean createReservationUsingSP(int guestId, int roomId, Date checkIn, Date checkOut,
                                            int numberOfGuests, String specialRequests, int createdBy) {
        String sql = "{CALL sp_create_reservation(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            // Set IN parameters
            stmt.setInt(1, guestId);
            stmt.setInt(2, roomId);
            stmt.setDate(3, checkIn);
            stmt.setDate(4, checkOut);
            stmt.setInt(5, numberOfGuests);
            stmt.setString(6, specialRequests);
            stmt.setInt(7, createdBy);
            
            // Register OUT parameters
            stmt.registerOutParameter(8, Types.INTEGER);  // reservation_id
            stmt.registerOutParameter(9, Types.BOOLEAN);  // success
            stmt.registerOutParameter(10, Types.VARCHAR); // message
            
            // Execute stored procedure
            stmt.execute();
            
            // Get OUT parameters
            int reservationId = stmt.getInt(8);
            boolean success = stmt.getBoolean(9);
            String message = stmt.getString(10);
            
            System.out.println("SP Result - Success: " + success + ", Message: " + message);
            
            if (success) {
                System.out.println("Created reservation ID: " + reservationId);
            }
            
            return success;
            
        } catch (SQLException e) {
            System.err.println("Error calling sp_create_reservation: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update reservation status using stored procedure
     * Demonstrates: Business rules enforced at database level, automatic room status updates
     */
    public boolean updateReservationStatusUsingSP(int reservationId, String newStatus) {
        String sql = "{CALL sp_update_reservation_status(?, ?, ?, ?)}";
        
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            // Set IN parameters
            stmt.setInt(1, reservationId);
            stmt.setString(2, newStatus);
            
            // Register OUT parameters
            stmt.registerOutParameter(3, Types.BOOLEAN);  // success
            stmt.registerOutParameter(4, Types.VARCHAR);  // message
            
            // Execute stored procedure
            stmt.execute();
            
            // Get OUT parameters
            boolean success = stmt.getBoolean(3);
            String message = stmt.getString(4);
            
            System.out.println("Status Update - Success: " + success + ", Message: " + message);
            
            return success;
            
        } catch (SQLException e) {
            System.err.println("Error calling sp_update_reservation_status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Check-in guest using stored procedure
     * Demonstrates: Complex business logic with validations and multiple table updates
     */
    public boolean checkInGuestUsingSP(int reservationId) {
        String sql = "{CALL sp_checkin_guest(?, ?, ?)}";
        
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            // Set IN parameter
            stmt.setInt(1, reservationId);
            
            // Register OUT parameters
            stmt.registerOutParameter(2, Types.BOOLEAN);  // success
            stmt.registerOutParameter(3, Types.VARCHAR);  // message
            
            // Execute stored procedure
            stmt.execute();
            
            // Get OUT parameters
            boolean success = stmt.getBoolean(2);
            String message = stmt.getString(3);
            
            System.out.println("Check-in Result - Success: " + success + ", Message: " + message);
            
            return success;
            
        } catch (SQLException e) {
            System.err.println("Error calling sp_checkin_guest: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Check-out guest using stored procedure with loyalty discount calculation
     * Demonstrates: Function calls within stored procedures, OUT parameters with calculated values
     */
    public double checkOutGuestUsingSP(int reservationId) {
        String sql = "{CALL sp_checkout_guest(?, ?, ?, ?)}";
        
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            // Set IN parameter
            stmt.setInt(1, reservationId);
            
            // Register OUT parameters
            stmt.registerOutParameter(2, Types.BOOLEAN);  // success
            stmt.registerOutParameter(3, Types.VARCHAR);  // message
            stmt.registerOutParameter(4, Types.DECIMAL);  // final_amount
            
            // Execute stored procedure
            stmt.execute();
            
            // Get OUT parameters
            boolean success = stmt.getBoolean(2);
            String message = stmt.getString(3);
            double finalAmount = stmt.getDouble(4);
            
            System.out.println("Check-out Result - Success: " + success + ", Message: " + message);
            
            if (success) {
                System.out.println("Final amount after discount: $" + finalAmount);
                return finalAmount;
            }
            
            return 0.0;
            
        } catch (SQLException e) {
            System.err.println("Error calling sp_checkout_guest: " + e.getMessage());
            e.printStackTrace();
            return 0.0;
        }
    }

    /**
     * Use database function to check room availability
     * Demonstrates: Calling database functions from Java
     */
    public boolean checkRoomAvailabilityUsingFunction(int roomId, Date checkIn, Date checkOut) {
        String sql = "SELECT is_room_available(?, ?, ?) as is_available";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, roomId);
            stmt.setDate(2, checkIn);
            stmt.setDate(3, checkOut);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                boolean isAvailable = rs.getBoolean("is_available");
                System.out.println("Room " + roomId + " availability: " + isAvailable);
                return isAvailable;
            }
            
            return false;
            
        } catch (SQLException e) {
            System.err.println("Error calling is_room_available function: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Calculate reservation cost using database function
     * Demonstrates: Using database functions for calculations
     */
    public double calculateCostUsingFunction(int roomId, Date checkIn, Date checkOut) {
        String sql = "SELECT calculate_reservation_cost(?, ?, ?) as total_cost";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, roomId);
            stmt.setDate(2, checkIn);
            stmt.setDate(3, checkOut);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                double cost = rs.getDouble("total_cost");
                System.out.println("Calculated reservation cost: $" + cost);
                return cost;
            }
            
            return 0.0;
            
        } catch (SQLException e) {
            System.err.println("Error calling calculate_reservation_cost function: " + e.getMessage());
            e.printStackTrace();
            return 0.0;
        }
    }

    /**
     * Get loyalty discount for a guest using database function
     * Demonstrates: Business logic implemented as database function
     */
    public double getGuestLoyaltyDiscountUsingFunction(int guestId) {
        String sql = "SELECT calculate_loyalty_discount(?) as discount_percent";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, guestId);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                double discount = rs.getDouble("discount_percent");
                System.out.println("Guest loyalty discount: " + discount + "%");
                return discount;
            }
            
            return 0.0;
            
        } catch (SQLException e) {
            System.err.println("Error calling calculate_loyalty_discount function: " + e.getMessage());
            e.printStackTrace();
            return 0.0;
        }
    }

    /**
     * Get occupancy rate using database function
     * Demonstrates: Complex calculations in database
     */
    public double getOccupancyRateUsingFunction(Date startDate, Date endDate) {
        String sql = "SELECT get_occupancy_rate(?, ?) as occupancy_rate";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, startDate);
            stmt.setDate(2, endDate);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                double rate = rs.getDouble("occupancy_rate");
                System.out.println("Occupancy rate: " + rate + "%");
                return rate;
            }
            
            return 0.0;
            
        } catch (SQLException e) {
            System.err.println("Error calling get_occupancy_rate function: " + e.getMessage());
            e.printStackTrace();
            return 0.0;
        }
    }

    /**
     * Generate revenue report using stored procedure
     * Demonstrates: Complex reporting with stored procedures
     */
    public void generateRevenueReportUsingSP(Date startDate, Date endDate) {
        String sql = "{CALL sp_generate_revenue_report(?, ?)}";
        
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            // Set IN parameters
            stmt.setDate(1, startDate);
            stmt.setDate(2, endDate);
            
            // Execute and get result set
            ResultSet rs = stmt.executeQuery();
            
            System.out.println("\n===== REVENUE REPORT =====");
            System.out.println("Period: " + startDate + " to " + endDate);
            System.out.println("==========================");
            
            while (rs.next()) {
                Date reportDate = rs.getDate("report_date");
                int totalReservations = rs.getInt("total_reservations");
                double totalRevenue = rs.getDouble("total_revenue");
                double avgRevenue = rs.getDouble("average_revenue");
                int uniqueGuests = rs.getInt("unique_guests");
                int roomsUsed = rs.getInt("rooms_used");
                
                System.out.printf("%s | Reservations: %d | Revenue: $%.2f | Avg: $%.2f | Guests: %d | Rooms: %d%n",
                    reportDate, totalReservations, totalRevenue, avgRevenue, uniqueGuests, roomsUsed);
            }
            
        } catch (SQLException e) {
            System.err.println("Error calling sp_generate_revenue_report: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
