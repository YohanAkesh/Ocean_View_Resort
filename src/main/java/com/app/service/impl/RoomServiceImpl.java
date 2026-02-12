package com.app.service.impl;

import com.app.dao.RoomDAO;
import com.app.dao.impl.RoomDAOImpl;
import com.app.model.Room;
import com.app.service.IRoomService;
import java.util.List;

public class RoomServiceImpl implements IRoomService {
    private final RoomDAO roomDAO;

    public RoomServiceImpl() {
        this.roomDAO = new RoomDAOImpl();
    }

    @Override
    public boolean addRoom(String roomNumber, String roomType, int capacity, double pricePerNight) {
        if (roomDAO.roomExistsByNumber(roomNumber)) {
            System.out.println("Room number already exists!");
            return false;
        }

        boolean result = roomDAO.addRoom(roomNumber, roomType, capacity, pricePerNight);
        if (result) {
            System.out.println("✓ Room added successfully!");
        }
        return result;
    }

    @Override
    public List<Room> getAllRooms() {
        return roomDAO.getAllRooms();
    }

    @Override
    public Room getRoomById(int roomId) {
        return roomDAO.getRoomById(roomId);
    }

    @Override
    public boolean updateRoom(int roomId, String roomNumber, String roomType, int capacity, 
                             double pricePerNight, String status) {
        return roomDAO.updateRoom(roomId, roomNumber, roomType, capacity, pricePerNight, status);
    }

    @Override
    public boolean deleteRoom(int roomId) {
        return roomDAO.deleteRoom(roomId);
    }

    @Override
    public List<Room> getAvailableRooms() {
        return roomDAO.getAvailableRooms();
    }

    @Override
    public boolean updateRoomStatus(int roomId, String status) {
        return roomDAO.updateRoomStatus(roomId, status);
    }

    @Override
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
