package com.app.dao;

import com.app.model.Reservation;
import java.time.LocalDate;
import java.util.List;

public interface ReservationDAO {
    boolean createReservation(String reservationNumber, int guestId, int roomId, 
                            LocalDate checkInDate, LocalDate checkOutDate, int numberOfGuests,
                            double totalPrice, String status, String specialRequests, int createdBy);
    
    List<Reservation> getAllReservations();
    
    Reservation getReservationById(int reservationId);
    
    List<Reservation> getReservationsByGuest(int guestId);
    
    List<Reservation> getReservationsByStatus(String status);
    
    boolean updateReservationStatus(int reservationId, String status);
    
    boolean updateReservation(int reservationId, LocalDate checkInDate, LocalDate checkOutDate,
                            int numberOfGuests, String specialRequests, double totalCost);
    
    boolean deleteReservation(int reservationId);
    
    boolean isRoomAvailable(int roomId, LocalDate checkIn, LocalDate checkOut);
    
    int getReservationCount();
}
