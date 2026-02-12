package com.app.service;

import com.app.model.Guest;
import java.util.List;

public interface IGuestService {
    boolean addGuest(String firstName, String lastName, String email, String phoneNumber,
                    String address, String idType, String idNumber, String nationality, int createdBy);
    
    List<Guest> getAllGuests();
    
    Guest getGuestById(int guestId);
    
    boolean updateGuest(int guestId, String firstName, String lastName, String email,
                       String phoneNumber, String address, String idType, String idNumber,
                       String nationality);
    
    boolean deleteGuest(int guestId);
    
    List<Guest> searchGuests(String searchTerm);
    
    boolean guestExistsByEmail(String email);
    
    boolean guestExistsByIdNumber(String idNumber);
    
    String validateGuestInput(String firstName, String lastName, String email,
                             String phoneNumber, String idNumber);
}
