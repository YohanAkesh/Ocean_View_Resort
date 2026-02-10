package com.app.controller;

import com.app.model.Bill;
import com.app.service.BillService;
import com.itextpdf.text.DocumentException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@WebServlet("/GenerateBillServlet")
public class GenerateBillServlet extends HttpServlet {
    private BillService billService;

    @Override
    public void init() throws ServletException {
        super.init();
        billService = new BillService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String reservationIdStr = request.getParameter("reservationId");
        if (reservationIdStr == null || reservationIdStr.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Reservation ID is required");
            return;
        }

        try {
            int reservationId = Integer.parseInt(reservationIdStr);
            String generatedBy = (String) session.getAttribute("username");
            if (generatedBy == null) {
                generatedBy = "Staff";
            }

            // Generate bill from reservation
            Bill bill = billService.generateBillFromReservation(reservationId, generatedBy);
            
            if (bill == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Reservation not found");
                return;
            }

            // Generate PDF
            ByteArrayOutputStream pdfStream = billService.generateBillPDF(bill);

            // Set response headers for PDF download
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", 
                "attachment; filename=\"" + bill.getBillNumber() + ".pdf\"");
            response.setContentLength(pdfStream.size());

            // Write PDF to response output stream
            OutputStream out = response.getOutputStream();
            pdfStream.writeTo(out);
            out.flush();
            out.close();

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid reservation ID format");
        } catch (DocumentException e) {
            System.err.println("Error generating PDF: " + e.getMessage());
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Error generating PDF document");
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "An unexpected error occurred");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
