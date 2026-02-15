package com.app.service;

import com.app.model.Bill;
import com.app.model.Reservation;
import com.app.model.Room;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BillService {
    private static final double TAX_RATE = 0.10;
    private static final double SERVICE_CHARGE_RATE = 0.05; 
    
    private ReservationService reservationService;
    private RoomService roomService;

    public BillService() {
        this.reservationService = new ReservationService();
        this.roomService = new RoomService();
    }
    //Generate a Bill object from a reservation
    public Bill generateBillFromReservation(int reservationId, String generatedBy) {
        Reservation reservation = reservationService.getReservationById(reservationId);
        if (reservation == null) {
            return null;
        }

        Room room = roomService.getRoomById(reservation.getRoomId());
        if (room == null) {
            return null;
        }

        // Calculate charges
        double roomCharges = reservation.getNumberOfNights() * room.getPricePerNight();
        double serviceCharge = roomCharges * SERVICE_CHARGE_RATE;
        double taxableAmount = roomCharges + serviceCharge;
        double taxAmount = taxableAmount * TAX_RATE;
        double totalAmount = taxableAmount + taxAmount;

        // Generate bill number
        String billNumber = generateBillNumber();

        // Create and return Bill object
        return new Bill(
            billNumber,
            reservation.getReservationId(),
            reservation.getReservationNumber(),
            reservation.getGuestName(),
            reservation.getEmail(),
            reservation.getContactNumber(),
            reservation.getAddress(),
            room.getRoomNumber(),
            room.getRoomType(),
            reservation.getCheckInDate(),
            reservation.getCheckOutDate(),
            reservation.getNumberOfNights(),
            room.getPricePerNight(),
            roomCharges,
            taxAmount,
            serviceCharge,
            totalAmount,
            LocalDate.now(),
            generatedBy
        );
    }
     // Generate a unique bill number
    private String generateBillNumber() {
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String dateStr = now.format(formatter);
        long timestamp = System.currentTimeMillis() % 10000;
        return String.format("BILL-%s-%04d", dateStr, timestamp);
    }
     //Generate PDF document for a bill
    public ByteArrayOutputStream generateBillPDF(Bill bill) throws DocumentException {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);

        document.open();

        // Define fonts
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, BaseColor.DARK_GRAY);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.WHITE);
        Font subHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.DARK_GRAY);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.BLACK);
        Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

        try {
            // Header Section - Resort Name
            Paragraph resortName = new Paragraph("Ocean View Resort", titleFont);
            resortName.setAlignment(Element.ALIGN_CENTER);
            resortName.setSpacingAfter(5);
            document.add(resortName);

            // Resort Address
            Paragraph address = new Paragraph("123 Beach Road, Paradise Island | Phone: +1-555-0123 | Email: info@oceanviewresort.com", normalFont);
            address.setAlignment(Element.ALIGN_CENTER);
            address.setSpacingAfter(20);
            document.add(address);

            // Horizontal line
            LineSeparator line = new LineSeparator();
            line.setLineColor(new BaseColor(102, 126, 234));
            document.add(new Chunk(line));
            document.add(Chunk.NEWLINE);

            // Bill Title and Number
            Paragraph billTitle = new Paragraph("INVOICE", new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.DARK_GRAY));
            billTitle.setAlignment(Element.ALIGN_CENTER);
            billTitle.setSpacingAfter(10);
            document.add(billTitle);

            // Bill Information Table
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setSpacingBefore(10);
            infoTable.setSpacingAfter(20);
            infoTable.setWidths(new int[]{1, 1});

            // Left column - Bill details
            PdfPCell leftCell = new PdfPCell();
            leftCell.setBorder(Rectangle.NO_BORDER);
            leftCell.setPadding(10);
            
            leftCell.addElement(new Paragraph("Bill Number: " + bill.getBillNumber(), boldFont));
            leftCell.addElement(new Paragraph("Bill Date: " + bill.getBillDate().format(dateFormatter), normalFont));
            leftCell.addElement(new Paragraph("Reservation #: " + bill.getReservationNumber(), normalFont));
            
            infoTable.addCell(leftCell);

            // Right column - Guest details
            PdfPCell rightCell = new PdfPCell();
            rightCell.setBorder(Rectangle.NO_BORDER);
            rightCell.setPadding(10);
            
            rightCell.addElement(new Paragraph("Bill To:", boldFont));
            rightCell.addElement(new Paragraph(bill.getGuestName(), normalFont));
            rightCell.addElement(new Paragraph(bill.getGuestAddress(), normalFont));
            rightCell.addElement(new Paragraph("Phone: " + bill.getGuestPhone(), normalFont));
            rightCell.addElement(new Paragraph("Email: " + bill.getGuestEmail(), normalFont));
            
            infoTable.addCell(rightCell);

            document.add(infoTable);

            // Reservation Details Section
            Paragraph detailsHeader = new Paragraph("Reservation Details", subHeaderFont);
            detailsHeader.setSpacingBefore(10);
            detailsHeader.setSpacingAfter(10);
            document.add(detailsHeader);

            // Details Table
            PdfPTable detailsTable = new PdfPTable(2);
            detailsTable.setWidthPercentage(100);
            detailsTable.setSpacingAfter(20);

            addTableRow(detailsTable, "Room Number:", bill.getRoomNumber(), normalFont, boldFont);
            addTableRow(detailsTable, "Room Type:", bill.getRoomType(), normalFont, boldFont);
            addTableRow(detailsTable, "Check-In Date:", bill.getCheckInDate().format(dateFormatter), normalFont, boldFont);
            addTableRow(detailsTable, "Check-Out Date:", bill.getCheckOutDate().format(dateFormatter), normalFont, boldFont);
            addTableRow(detailsTable, "Number of Nights:", String.valueOf(bill.getNumberOfNights()), normalFont, boldFont);

            document.add(detailsTable);

            // Charges Section
            Paragraph chargesHeader = new Paragraph("Charges Breakdown", subHeaderFont);
            chargesHeader.setSpacingBefore(10);
            chargesHeader.setSpacingAfter(10);
            document.add(chargesHeader);

            // Charges Table
            PdfPTable chargesTable = new PdfPTable(4);
            chargesTable.setWidthPercentage(100);
            chargesTable.setWidths(new int[]{3, 1, 1, 2});

            // Table Header
            BaseColor headerColor = new BaseColor(102, 126, 234);
            addChargesHeaderCell(chargesTable, "Description", headerFont, headerColor);
            addChargesHeaderCell(chargesTable, "Quantity", headerFont, headerColor);
            addChargesHeaderCell(chargesTable, "Rate", headerFont, headerColor);
            addChargesHeaderCell(chargesTable, "Amount", headerFont, headerColor);

            // Room charges row
            addChargesRow(chargesTable, "Room Charges (" + bill.getRoomType() + ")", 
                         String.valueOf(bill.getNumberOfNights()), 
                         String.format("$%.2f", bill.getPricePerNight()),
                         String.format("$%.2f", bill.getRoomCharges()),
                         normalFont);

            document.add(chargesTable);

            // Subtotal and additional charges
            PdfPTable summaryTable = new PdfPTable(2);
            summaryTable.setWidthPercentage(50);
            summaryTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
            summaryTable.setSpacingBefore(20);
            summaryTable.setWidths(new int[]{2, 1});

            addSummaryRow(summaryTable, "Room Charges:", String.format("$%.2f", bill.getRoomCharges()), normalFont, false);
            addSummaryRow(summaryTable, "Service Charge (5%):", String.format("$%.2f", bill.getServiceCharge()), normalFont, false);
            addSummaryRow(summaryTable, "Tax (10%):", String.format("$%.2f", bill.getTaxAmount()), normalFont, false);
            
            // Total row with background
            PdfPCell totalLabelCell = new PdfPCell(new Phrase("Total Amount:", totalFont));
            totalLabelCell.setBorder(Rectangle.NO_BORDER);
            totalLabelCell.setBackgroundColor(new BaseColor(240, 240, 240));
            totalLabelCell.setPadding(10);
            totalLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            summaryTable.addCell(totalLabelCell);

            PdfPCell totalAmountCell = new PdfPCell(new Phrase(String.format("$%.2f", bill.getTotalAmount()), totalFont));
            totalAmountCell.setBorder(Rectangle.NO_BORDER);
            totalAmountCell.setBackgroundColor(new BaseColor(240, 240, 240));
            totalAmountCell.setPadding(10);
            totalAmountCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            summaryTable.addCell(totalAmountCell);

            document.add(summaryTable);

            // Footer
            Paragraph footer = new Paragraph("\nThank you for choosing Ocean View Resort!", 
                                            new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.DARK_GRAY));
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(30);
            document.add(footer);

            Paragraph terms = new Paragraph("Terms & Conditions: Payment is due upon receipt. Late payments may incur additional charges.", 
                                           new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.GRAY));
            terms.setAlignment(Element.ALIGN_CENTER);
            terms.setSpacingBefore(20);
            document.add(terms);

        } finally {
            document.close();
        }

        return baos;
    }

    // Helper methods for PDF generation
    private void addTableRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(5);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(5);
        table.addCell(valueCell);
    }

    private void addChargesHeaderCell(PdfPTable table, String text, Font font, BaseColor color) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(color);
        cell.setPadding(8);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private void addChargesRow(PdfPTable table, String description, String quantity, String rate, String amount, Font font) {
        PdfPCell cell1 = new PdfPCell(new Phrase(description, font));
        cell1.setPadding(8);
        cell1.setBorder(Rectangle.BOTTOM);
        table.addCell(cell1);

        PdfPCell cell2 = new PdfPCell(new Phrase(quantity, font));
        cell2.setPadding(8);
        cell2.setBorder(Rectangle.BOTTOM);
        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell2);

        PdfPCell cell3 = new PdfPCell(new Phrase(rate, font));
        cell3.setPadding(8);
        cell3.setBorder(Rectangle.BOTTOM);
        cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cell3);

        PdfPCell cell4 = new PdfPCell(new Phrase(amount, font));
        cell4.setPadding(8);
        cell4.setBorder(Rectangle.BOTTOM);
        cell4.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cell4);
    }

    private void addSummaryRow(PdfPTable table, String label, String value, Font font, boolean isBold) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, font));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(5);
        labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, font));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(5);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(valueCell);
    }
}
