package com.app.service.impl;

import com.app.dao.GuestDAO;
import com.app.dao.impl.GuestDAOImpl;
import com.app.model.Guest;
import com.app.service.IGuestService;
import java.util.List;

public class GuestServiceImpl implements IGuestService {
    private final GuestDAO guestDAO;

    public GuestServiceImpl() {
        this.guestDAO = new GuestDAOImpl();
    }

    @Override
    public boolean addGuest(String firstName, String lastName, String email, String phoneNumber,
                          String address, String idType, String idNumber, String nationality, int createdBy) {
        
        // Check if guest already exists 
        if (guestExistsByEmail(email)) {
            System.err.println("Guest with this email already exists");
            return false;
        }
        
        if (guestExistsByIdNumber(idNumber)) {
            System.err.println("Guest with this ID number already exists");
            return false;
        }

        boolean result = guestDAO.addGuest(firstName, lastName, email, phoneNumber, 
                                          address, idType, idNumber, nationality, createdBy);
        if (result) {
            System.out.println("✓ Guest registered successfully!");
        }
        return result;
    }

    @Override
    public List<Guest> getAllGuests() {
        return guestDAO.getAllGuests();
    }

    @Override
    public Guest getGuestById(int guestId) {
        return guestDAO.getGuestById(guestId);
    }

    @Override
    public boolean updateGuest(int guestId, String firstName, String lastName, String email,
                              String phoneNumber, String address, String idType, String idNumber,
                              String nationality) {
        return guestDAO.updateGuest(guestId, firstName, lastName, email, phoneNumber, 
                                   address, idType, idNumber, nationality);
    }

    @Override
    public boolean deleteGuest(int guestId) {
        return guestDAO.deleteGuest(guestId);
    }

    @Override
    public List<Guest> searchGuests(String searchTerm) {
        return guestDAO.searchGuests(searchTerm);
    }

    @Override
    public boolean guestExistsByEmail(String email) {
        return guestDAO.guestExistsByEmail(email);
    }

    @Override
    public boolean guestExistsByIdNumber(String idNumber) {
        return guestDAO.guestExistsByIdNumber(idNumber);
    }

    @Override
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
