package com.app.service.impl;

import com.app.dao.ReservationDAO;
import com.app.dao.impl.ReservationDAOImpl;
import com.app.model.Guest;
import com.app.model.Reservation;
import com.app.model.Room;
import com.app.service.IEmailService;
import com.app.service.IGuestService;
import com.app.service.IReservationService;
import com.app.service.IRoomService;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class ReservationServiceImpl implements IReservationService {
    private final ReservationDAO reservationDAO;
    private final IRoomService roomService;
    private final IEmailService emailService;
    private final IGuestService guestService;

    public ReservationServiceImpl() {
        this.reservationDAO = new ReservationDAOImpl();
        this.roomService = new RoomServiceImpl();
        this.emailService = new EmailServiceImpl();
        this.guestService = new GuestServiceImpl();
    }

    @Override
    public String generateReservationNumber() {
        int count = reservationDAO.getReservationCount() + 1;
        return String.format("RES%05d", count);
    }

    @Override
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

    @Override
    public boolean isRoomAvailable(int roomId, LocalDate checkIn, LocalDate checkOut) {
        boolean available = reservationDAO.isRoomAvailable(roomId, checkIn, checkOut);
        System.out.println("Room " + roomId + " availability check: " + (available ? "Available" : "Not available"));
        return available;
    }

    @Override
    public List<Room> getAvailableRoomsForDates(LocalDate checkIn, LocalDate checkOut) {
        List<Room> availableRooms = new ArrayList<>();
        List<Room> allRooms = roomService.getAllRooms();
        
        for (Room room : allRooms) {
            if ("AVAILABLE".equals(room.getStatus()) && isRoomAvailable(room.getRoomId(), checkIn, checkOut)) {
                availableRooms.add(room);
            }
        }
        
        return availableRooms;
    }

    @Override
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

        String reservationNumber = generateReservationNumber();
        boolean created = reservationDAO.createReservation(reservationNumber, guestId, roomId, checkInDate, 
                                               checkOutDate, numberOfGuests, totalCost, "PENDING", 
                                               specialRequests, createdBy);
        
        // Update room status to OCCUPIED and send confirmation email if reservation was created successfully
        if (created) {
            roomService.updateRoomStatus(roomId, "OCCUPIED");
            try {
                // Get guest details
                Guest guest = guestService.getGuestById(guestId);
                if (guest != null && guest.getEmail() != null && !guest.getEmail().trim().isEmpty()) {
                    // Create a temporary reservation object with all details for email
                    Reservation tempReservation = new Reservation(
                        reservationNumber,
                        guest.getFirstName() + " " + guest.getLastName(),
                        guest.getAddress(),
                        guest.getPhoneNumber(),
                        guest.getEmail(),
                        roomId,
                        checkInDate,
                        checkOutDate,
                        numberOfNights,
                        numberOfGuests,
                        totalCost,
                        specialRequests,
                        createdBy
                    );
                    
                    // Send email asynchronously to avoid blocking
                    new Thread(() -> {
                        boolean emailSent = emailService.sendReservationConfirmation(tempReservation, guest);
                        if (emailSent) {
                            System.out.println("Confirmation email sent to: " + guest.getEmail());
                        } else {
                            System.err.println("Failed to send confirmation email to: " + guest.getEmail());
                        }
                    }).start();
                }
            } catch (Exception e) {
                // Log error but don't fail the reservation
                System.err.println("Error sending confirmation email: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        return created;
    }

    @Override
    public List<Reservation> getAllReservations() {
        return reservationDAO.getAllReservations();
    }

    @Override
    public Reservation getReservationById(int reservationId) {
        return reservationDAO.getReservationById(reservationId);
    }

    @Override
    public boolean updateReservationStatus(int reservationId, String status) {
        return reservationDAO.updateReservationStatus(reservationId, status);
    }

    @Override
    public boolean updateReservation(int reservationId, String guestName, String address,
                                    String contactNumber, String email, LocalDate checkInDate,
                                    LocalDate checkOutDate, String status) {
        
        Reservation existingReservation = getReservationById(reservationId);
        if (existingReservation == null) {
            return false;
        }

        // Validate dates
        if (checkInDate.isAfter(checkOutDate)) {
            System.err.println("Check-out date must be after check-in date");
            return false;
        }

        Room room = roomService.getRoomById(existingReservation.getRoomId());
        if (room == null) {
            return false;
        }

        if (!existingReservation.getCheckInDate().equals(checkInDate) || 
            !existingReservation.getCheckOutDate().equals(checkOutDate)) {
            if (!isRoomAvailable(existingReservation.getRoomId(), checkInDate, checkOutDate)) {
                System.err.println("Room not available for the selected dates");
                return false;
            }
        }

        // Calculate total cost based on new dates
        int numberOfNights = (int) ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        double totalCost = numberOfNights * room.getPricePerNight();

        // Update reservation 
        return reservationDAO.updateReservation(reservationId, checkInDate, checkOutDate,
                                               existingReservation.getNumberOfGuests(), 
                                               existingReservation.getSpecialRequests(), 
                                               totalCost) &&
               reservationDAO.updateReservationStatus(reservationId, status);
    }

    @Override
    public boolean deleteReservation(int reservationId) {
        // Get reservation details to find the room ID
        Reservation reservation = reservationDAO.getReservationById(reservationId);
        if (reservation == null) {
            return false;
        }
        
        // Cancel the reservation
        boolean cancelled = reservationDAO.deleteReservation(reservationId);
        
        // Update room status to AVAILABLE if cancellation was successful
        if (cancelled) {
            roomService.updateRoomStatus(reservation.getRoomId(), "AVAILABLE");
        }
        
        return cancelled;
    }

    @Override
    public String getRoomNumber(int roomId) {
        Room room = roomService.getRoomById(roomId);
        return room != null ? room.getRoomNumber() : "N/A";
    }

    @Override
    public List<Reservation> searchReservations(String searchTerm) {
        List<Reservation> allReservations = getAllReservations();
        List<Reservation> filtered = new ArrayList<>();
        
        for (Reservation res : allReservations) {
            if (res.getGuestName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                res.getEmail().toLowerCase().contains(searchTerm.toLowerCase()) ||
                res.getContactNumber().contains(searchTerm)) {
                filtered.add(res);
            }
        }
        
        return filtered;
    }

    @Override
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
