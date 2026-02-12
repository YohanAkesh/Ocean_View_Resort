package com.app.service;

import com.app.model.Room;
import java.util.List;

public interface IRoomService {
    boolean addRoom(String roomNumber, String roomType, int capacity, double pricePerNight);
    
    List<Room> getAllRooms();
    
    Room getRoomById(int roomId);
    
    boolean updateRoom(int roomId, String roomNumber, String roomType, int capacity, 
                      double pricePerNight, String status);
    
    boolean deleteRoom(int roomId);
    
    List<Room> getAvailableRooms();
    
    boolean updateRoomStatus(int roomId, String status);
    
    String validateRoomInput(String roomNumber, String roomType, String capacityStr, String priceStr);
}
