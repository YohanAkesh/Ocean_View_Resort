package com.app.rest;

import com.app.dto.ApiResponse;
import com.app.dto.GuestRequest;
import com.app.model.Guest;
import com.app.service.IGuestService;
import com.app.service.impl.GuestServiceImpl;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/guests")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GuestRestController {
    
    private IGuestService guestService;

    public GuestRestController() {
        this.guestService = new GuestServiceImpl();
    }

    /**
     * Get all guests
     * GET /api/guests
     */
    @GET
    public Response getAllGuests() {
        try {
            List<Guest> guests = guestService.getAllGuests();
            return Response.ok(ApiResponse.success("Guests retrieved successfully", guests)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Failed to retrieve guests: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Get guest by ID
     * GET /api/guests/{id}
     */
    @GET
    @Path("/{id}")
    public Response getGuestById(@PathParam("id") int guestId) {
        try {
            Guest guest = guestService.getGuestById(guestId);
            
            if (guest != null) {
                return Response.ok(ApiResponse.success("Guest retrieved successfully", guest)).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(ApiResponse.error("Guest not found"))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Failed to retrieve guest: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Search guests by name
     * GET /api/guests/search?name=john
     */
    @GET
    @Path("/search")
    public Response searchGuests(@QueryParam("name") String name) {
        try {
            if (name == null || name.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ApiResponse.error("Search name is required"))
                        .build();
            }

            List<Guest> guests = guestService.searchGuests(name);
            return Response.ok(ApiResponse.success("Search completed", guests)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Failed to search guests: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Add new guest
     * POST /api/guests
     */
    @POST
    public Response addGuest(GuestRequest guestRequest) {
        try {
            // Validate input
            String error = guestService.validateGuestInput(
                guestRequest.getFirstName(),
                guestRequest.getLastName(),
                guestRequest.getEmail(),
                guestRequest.getPhoneNumber(),
                guestRequest.getIdNumber()
            );

            if (error != null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ApiResponse.error(error))
                        .build();
            }

            // Add guest (using a default user ID of 1 for API calls - you may want to implement proper authentication)
            boolean success = guestService.addGuest(
                guestRequest.getFirstName(),
                guestRequest.getLastName(),
                guestRequest.getEmail(),
                guestRequest.getPhoneNumber(),
                guestRequest.getAddress(),
                guestRequest.getIdType(),
                guestRequest.getIdNumber(),
                guestRequest.getNationality(),
                1 // Default user ID - should be replaced with authenticated user
            );

            if (success) {
                return Response.status(Response.Status.CREATED)
                        .entity(ApiResponse.success("Guest added successfully", null))
                        .build();
            } else {
                return Response.status(Response.Status.CONFLICT)
                        .entity(ApiResponse.error("Guest with this email or ID already exists"))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Failed to add guest: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Update guest
     * PUT /api/guests/{id}
     */
    @PUT
    @Path("/{id}")
    public Response updateGuest(@PathParam("id") int guestId, GuestRequest guestRequest) {
        try {
            // Validate input
            String error = guestService.validateGuestInput(
                guestRequest.getFirstName(),
                guestRequest.getLastName(),
                guestRequest.getEmail(),
                guestRequest.getPhoneNumber(),
                guestRequest.getIdNumber()
            );

            if (error != null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ApiResponse.error(error))
                        .build();
            }

            // Update guest
            boolean success = guestService.updateGuest(
                guestId,
                guestRequest.getFirstName(),
                guestRequest.getLastName(),
                guestRequest.getEmail(),
                guestRequest.getPhoneNumber(),
                guestRequest.getAddress(),
                guestRequest.getIdType(),
                guestRequest.getIdNumber(),
                guestRequest.getNationality()
            );

            if (success) {
                return Response.ok(ApiResponse.success("Guest updated successfully", null)).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(ApiResponse.error("Guest not found or update failed"))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Failed to update guest: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Delete guest
     * DELETE /api/guests/{id}
     */
    @DELETE
    @Path("/{id}")
    public Response deleteGuest(@PathParam("id") int guestId) {
        try {
            boolean success = guestService.deleteGuest(guestId);

            if (success) {
                return Response.ok(ApiResponse.success("Guest deleted successfully", null)).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(ApiResponse.error("Guest not found or delete failed"))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Failed to delete guest: " + e.getMessage()))
                    .build();
        }
    }
}
