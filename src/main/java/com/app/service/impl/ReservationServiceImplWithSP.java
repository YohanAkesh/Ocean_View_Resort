package com.app.service.impl;

import com.app.dao.impl.ReservationDAOImplWithSP;
import com.app.service.IReservationService;
import com.app.model.Reservation;
import com.app.model.Room;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Enhanced Reservation Service using Stored Procedures
 * Demonstrates integration of advanced database features into service layer
 */
public class ReservationServiceImplWithSP implements IReservationService {
    
    private ReservationDAOImplWithSP daoWithSP;
    private ReservationServiceImpl standardService;
    
    public ReservationServiceImplWithSP() {
        this.daoWithSP = new ReservationDAOImplWithSP();
        this.standardService = new ReservationServiceImpl();
    }
    
    /**
     * Create reservation using stored procedure with advanced validations
     */
    public boolean createReservationWithSP(int guestId, int roomId, LocalDate checkInDate,
                                           LocalDate checkOutDate, int numberOfGuests,
                                           String specialRequests, int createdBy) {
        // Convert LocalDate to java.sql.Date
        Date checkIn = Date.valueOf(checkInDate);
        Date checkOut = Date.valueOf(checkOutDate);
        
        // Call stored procedure which handles all validations
        return daoWithSP.createReservationUsingSP(
            guestId, roomId, checkIn, checkOut, 
            numberOfGuests, specialRequests, createdBy
        );
    }
    
    /**
     * Check room availability using database function
     */
    public boolean isRoomAvailableUsingFunction(int roomId, LocalDate checkIn, LocalDate checkOut) {
        return daoWithSP.checkRoomAvailabilityUsingFunction(
            roomId,
            Date.valueOf(checkIn), 
            Date.valueOf(checkOut)
        );
    }
    
    /**
     * Calculate reservation cost using database function
     */
    public double calculateReservationCostUsingFunction(int roomId, LocalDate checkIn, LocalDate checkOut) {
        return daoWithSP.calculateCostUsingFunction(
            roomId, 
            Date.valueOf(checkIn), 
            Date.valueOf(checkOut)
        );
    }
    
    /**
     * Get guest loyalty discount using database function
     */
    public double getGuestLoyaltyDiscount(int guestId) {
        return daoWithSP.getGuestLoyaltyDiscountUsingFunction(guestId);
    }
    
    /**
     * Check-in guest using stored procedure
     */
    public boolean checkInGuest(int reservationId) {
        return daoWithSP.checkInGuestUsingSP(reservationId);
    }
    
    /**
     * Check-out guest using stored procedure
     * Returns final amount after applying loyalty discount
     */
    public double checkOutGuest(int reservationId) {
        return daoWithSP.checkOutGuestUsingSP(reservationId);
    }
    
    /**
     * Update reservation status using stored procedure
     */
    public boolean updateReservationStatusWithSP(int reservationId, String newStatus) {
        return daoWithSP.updateReservationStatusUsingSP(reservationId, newStatus);
    }
    
    /**
     * Get occupancy rate for reporting
     */
    public double getOccupancyRate(LocalDate startDate, LocalDate endDate) {
        return daoWithSP.getOccupancyRateUsingFunction(
            Date.valueOf(startDate), 
            Date.valueOf(endDate)
        );
    }
    
    /**
     * Generate revenue report using stored procedure
     */
    public void generateRevenueReport(LocalDate startDate, LocalDate endDate) {
        daoWithSP.generateRevenueReportUsingSP(
            Date.valueOf(startDate), 
            Date.valueOf(endDate)
        );
    }
    
    // Implement remaining IReservationService methods using standard service
    
    @Override
    public String generateReservationNumber() {
        return standardService.generateReservationNumber();
    }

    @Override
    public List<Room> getAvailableRooms() {
        return standardService.getAvailableRooms();
    }

    @Override
    public boolean isRoomAvailable(int roomId, LocalDate checkIn, LocalDate checkOut) {
        // Can use either function or standard method
        return isRoomAvailableUsingFunction(roomId, checkIn, checkOut);
    }

    @Override
    public List<Room> getAvailableRoomsForDates(LocalDate checkIn, LocalDate checkOut) {
        return standardService.getAvailableRoomsForDates(checkIn, checkOut);
    }

    @Override
    public boolean createReservation(int guestId, int roomId, LocalDate checkInDate,
                                    LocalDate checkOutDate, int numberOfGuests,
                                    String specialRequests, int createdBy) {
        // Use stored procedure version for better validation
        return createReservationWithSP(guestId, roomId, checkInDate, checkOutDate,
                                      numberOfGuests, specialRequests, createdBy);
    }

    @Override
    public List<Reservation> getAllReservations() {
        return standardService.getAllReservations();
    }

    @Override
    public Reservation getReservationById(int reservationId) {
        return standardService.getReservationById(reservationId);
    }

    @Override
    public boolean updateReservationStatus(int reservationId, String status) {
        // Use stored procedure version for business rule enforcement
        return updateReservationStatusWithSP(reservationId, status);
    }

    @Override
    public boolean updateReservation(int reservationId, String guestName, String address,
                                    String contactNumber, String email, LocalDate checkInDate,
                                    LocalDate checkOutDate, String status) {
        return standardService.updateReservation(reservationId, guestName, address,
                                            contactNumber, email, checkInDate, checkOutDate, status);
    }

    @Override
    public boolean deleteReservation(int reservationId) {
        return standardService.deleteReservation(reservationId);
    }

    @Override
    public String getRoomNumber(int roomId) {
        return standardService.getRoomNumber(roomId);
    }

    @Override
    public List<Reservation> searchReservations(String searchTerm) {
        return standardService.searchReservations(searchTerm);
    }

    @Override
    public String validateReservationInput(String guestName, String contactNumber, String email,
                                          String checkInStr, String checkOutStr) {
        return standardService.validateReservationInput(guestName, contactNumber, email, checkInStr, checkOutStr);
    }
}
