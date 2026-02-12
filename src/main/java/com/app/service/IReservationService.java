package com.app.service;

import com.app.model.Reservation;
import com.app.model.Room;
import java.time.LocalDate;
import java.util.List;

public interface IReservationService {
    String generateReservationNumber();
    
    List<Room> getAvailableRooms();
    
    boolean isRoomAvailable(int roomId, LocalDate checkIn, LocalDate checkOut);
    
    List<Room> getAvailableRoomsForDates(LocalDate checkIn, LocalDate checkOut);
    
    boolean createReservation(int guestId, int roomId, LocalDate checkInDate,
                            LocalDate checkOutDate, int numberOfGuests, 
                            String specialRequests, int createdBy);
    
    List<Reservation> getAllReservations();
    
    Reservation getReservationById(int reservationId);
    
    boolean updateReservationStatus(int reservationId, String status);
    
    boolean updateReservation(int reservationId, String guestName, String address,
                            String contactNumber, String email, LocalDate checkInDate,
                            LocalDate checkOutDate, String status);
    
    boolean deleteReservation(int reservationId);
    
    String getRoomNumber(int roomId);
    
    List<Reservation> searchReservations(String searchTerm);
    
    String validateReservationInput(String guestName, String contactNumber, String email,
                                   String checkInStr, String checkOutStr);
}
