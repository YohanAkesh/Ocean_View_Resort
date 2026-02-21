package com.app.rest;

import com.app.dto.ApiResponse;
import com.app.dto.RoomRequest;
import com.app.model.Room;
import com.app.service.IRoomService;
import com.app.service.IReservationService;
import com.app.service.impl.RoomServiceImpl;
import com.app.service.impl.ReservationServiceImpl;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.List;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomRestController {
    
    private IRoomService roomService;
    private IReservationService reservationService;

    public RoomRestController() {
        this.roomService = new RoomServiceImpl();
        this.reservationService = new ReservationServiceImpl();
    }

    /**
     * Get all rooms
     * GET /api/rooms
     */
    @GET
    public Response getAllRooms() {
        try {
            List<Room> rooms = roomService.getAllRooms();
            return Response.ok(ApiResponse.success("Rooms retrieved successfully", rooms)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Failed to retrieve rooms: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Get room by ID
     * GET /api/rooms/{id}
     */
    @GET
    @Path("/{id}")
    public Response getRoomById(@PathParam("id") int roomId) {
        try {
            Room room = roomService.getRoomById(roomId);
            
            if (room != null) {
                return Response.ok(ApiResponse.success("Room retrieved successfully", room)).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(ApiResponse.error("Room not found"))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Failed to retrieve room: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Get available rooms
     * GET /api/rooms/available
     */
    @GET
    @Path("/available")
    public Response getAvailableRooms() {
        try {
            List<Room> rooms = roomService.getAvailableRooms();
            return Response.ok(ApiResponse.success("Available rooms retrieved successfully", rooms)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Failed to retrieve available rooms: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Get available rooms for dates
     * GET /api/rooms/available-for-dates?checkIn=2024-01-01&checkOut=2024-01-05
     */
    @GET
    @Path("/available-for-dates")
    public Response getAvailableRoomsForDates(
            @QueryParam("checkIn") String checkIn,
            @QueryParam("checkOut") String checkOut) {
        try {
            if (checkIn == null || checkIn.trim().isEmpty() || 
                checkOut == null || checkOut.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ApiResponse.error("Check-in and check-out dates are required"))
                        .build();
            }

            // Parse dates and get available rooms
            try {
                LocalDate checkInDate = LocalDate.parse(checkIn);
                LocalDate checkOutDate = LocalDate.parse(checkOut);
                List<Room> rooms = reservationService.getAvailableRoomsForDates(checkInDate, checkOutDate);
                return Response.ok(ApiResponse.success("Available rooms retrieved successfully", rooms)).build();
            } catch (java.time.format.DateTimeParseException e) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ApiResponse.error("Invalid date format. Use YYYY-MM-DD"))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Failed to retrieve available rooms: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Add new room
     * POST /api/rooms
     */
    @POST
    public Response addRoom(RoomRequest roomRequest) {
        try {
            // Validate input
            String error = roomService.validateRoomInput(
                roomRequest.getRoomNumber(),
                roomRequest.getRoomType(),
                String.valueOf(roomRequest.getCapacity()),
                String.valueOf(roomRequest.getPrice())
            );

            if (error != null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ApiResponse.error(error))
                        .build();
            }

            // Add room
            boolean success = roomService.addRoom(
                roomRequest.getRoomNumber(),
                roomRequest.getRoomType(),
                roomRequest.getCapacity(),
                roomRequest.getPrice()
            );

            if (success) {
                return Response.status(Response.Status.CREATED)
                        .entity(ApiResponse.success("Room added successfully", null))
                        .build();
            } else {
                return Response.status(Response.Status.CONFLICT)
                        .entity(ApiResponse.error("Room number already exists"))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Failed to add room: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Update room
     * PUT /api/rooms/{id}
     */
    @PUT
    @Path("/{id}")
    public Response updateRoom(@PathParam("id") int roomId, RoomRequest roomRequest) {
        try {
            // Validate input
            String error = roomService.validateRoomInput(
                roomRequest.getRoomNumber(),
                roomRequest.getRoomType(),
                String.valueOf(roomRequest.getCapacity()),
                String.valueOf(roomRequest.getPrice())
            );

            if (error != null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ApiResponse.error(error))
                        .build();
            }

            // Update room
            boolean success = roomService.updateRoom(
                roomId,
                roomRequest.getRoomNumber(),
                roomRequest.getRoomType(),
                roomRequest.getCapacity(),
                roomRequest.getPrice(),
                roomRequest.getStatus()
            );

            if (success) {
                return Response.ok(ApiResponse.success("Room updated successfully", null)).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(ApiResponse.error("Room not found or update failed"))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Failed to update room: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Update room status
     * PATCH /api/rooms/{id}/status
     */
    @PATCH
    @Path("/{id}/status")
    public Response updateRoomStatus(@PathParam("id") int roomId, @QueryParam("status") String status) {
        try {
            if (status == null || status.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ApiResponse.error("Status is required"))
                        .build();
            }

            boolean success = roomService.updateRoomStatus(roomId, status);

            if (success) {
                return Response.ok(ApiResponse.success("Room status updated successfully", null)).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(ApiResponse.error("Room not found or status update failed"))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Failed to update room status: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Delete room
     * DELETE /api/rooms/{id}
     */
    @DELETE
    @Path("/{id}")
    public Response deleteRoom(@PathParam("id") int roomId) {
        try {
            boolean success = roomService.deleteRoom(roomId);

            if (success) {
                return Response.ok(ApiResponse.success("Room deleted successfully", null)).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(ApiResponse.error("Room not found or delete failed"))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Failed to delete room: " + e.getMessage()))
                    .build();
        }
    }
}
