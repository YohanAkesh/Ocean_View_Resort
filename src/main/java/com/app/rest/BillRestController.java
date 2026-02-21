package com.app.rest;

import com.app.dto.ApiResponse;
import com.app.model.Bill;
import com.app.service.BillService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.ByteArrayOutputStream;

@Path("/bills")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BillRestController {
    
    private BillService billService;

    public BillRestController() {
        this.billService = new BillService();
    }

    /**
     * Generate bill from reservation
     * POST /api/bills/generate?reservationId=123&generatedBy=admin
     */
    @POST
    @Path("/generate")
    public Response generateBill(
            @QueryParam("reservationId") Integer reservationId,
            @QueryParam("generatedBy") String generatedBy) {
        try {
            if (reservationId == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(ApiResponse.error("Reservation ID is required"))
                        .build();
            }

            if (generatedBy == null || generatedBy.trim().isEmpty()) {
                generatedBy = "Staff";
            }

            // Generate bill from reservation
            Bill bill = billService.generateBillFromReservation(reservationId, generatedBy);

            if (bill == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(ApiResponse.error("Reservation not found"))
                        .build();
            }

            return Response.status(Response.Status.CREATED)
                    .entity(ApiResponse.success("Bill generated successfully", bill))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Failed to generate bill: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Download bill as PDF
     * GET /api/bills/{reservationId}/pdf?generatedBy=admin
     */
    @GET
    @Path("/{reservationId}/pdf")
    @Produces("application/pdf")
    public Response downloadBillPdf(
            @PathParam("reservationId") int reservationId,
            @QueryParam("generatedBy") String generatedBy) {
        try {
            if (generatedBy == null || generatedBy.trim().isEmpty()) {
                generatedBy = "Staff";
            }

            // Generate bill from reservation
            Bill bill = billService.generateBillFromReservation(reservationId, generatedBy);

            if (bill == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .type(MediaType.APPLICATION_JSON)
                        .entity(ApiResponse.error("Reservation not found"))
                        .build();
            }

            // Generate PDF
            ByteArrayOutputStream pdfStream = billService.generateBillPDF(bill);

            // Create streaming output
            StreamingOutput stream = output -> {
                pdfStream.writeTo(output);
                output.flush();
            };

            return Response.ok(stream)
                    .header("Content-Disposition", "attachment; filename=\"" + bill.getBillNumber() + ".pdf\"")
                    .header("Content-Type", "application/pdf")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(ApiResponse.error("Failed to generate PDF: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Get bill details (JSON format)
     * GET /api/bills/{reservationId}?generatedBy=admin
     */
    @GET
    @Path("/{reservationId}")
    public Response getBill(
            @PathParam("reservationId") int reservationId,
            @QueryParam("generatedBy") String generatedBy) {
        try {
            if (generatedBy == null || generatedBy.trim().isEmpty()) {
                generatedBy = "Staff";
            }

            // Generate bill from reservation
            Bill bill = billService.generateBillFromReservation(reservationId, generatedBy);

            if (bill == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(ApiResponse.error("Reservation not found"))
                        .build();
            }

            return Response.ok(ApiResponse.success("Bill retrieved successfully", bill)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ApiResponse.error("Failed to retrieve bill: " + e.getMessage()))
                    .build();
        }
    }
}
