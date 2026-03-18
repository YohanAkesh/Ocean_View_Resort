package com.app.service.impl;

import com.app.model.Guest;
import com.app.model.Reservation;
import com.app.service.IEmailService;

import javax.mail.*;
import javax.mail.internet.*;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class EmailServiceImpl implements IEmailService {
    
    
    private static final String SMTP_HOST = "smtp.gmail.com"; 
    private static final String SMTP_PORT = "587"; 
    private static final String SMTP_USERNAME = "wolflone624@gmail.com"; 
    private static final String SMTP_PASSWORD = "hsdw hhxh tryw mcsj"; 
    private static final String FROM_EMAIL = "noreply@oceanviewresort.com"; 
    private static final String FROM_NAME = "Ocean View Resort";
    
    @Override
    public boolean sendReservationConfirmation(Reservation reservation, Guest guest) {
        try {
            String subject = "Reservation Confirmation - " + reservation.getReservationNumber();
            String body = buildReservationEmailBody(reservation, guest);
            
            return sendEmail(guest.getEmail(), subject, body);
        } catch (Exception e) {
            System.err.println("Error sending reservation confirmation: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean sendEmail(String to, String subject, String body) {
        try {
            // Set up mail server properties
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");
            
            // Create authenticator
            Authenticator auth = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SMTP_USERNAME, SMTP_PASSWORD);
                }
            };
            
            // Create session
            Session session = Session.getInstance(props, auth);
            
            // Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL, FROM_NAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setContent(body, "text/html; charset=utf-8");
            
            // Send message
            Transport.send(message);
            
            System.out.println("Email sent successfully to: " + to);
            return true;
            
        } catch (Exception e) {
            System.err.println("Error sending email to " + to + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Build HTML email body for reservation confirmation
     */
    private String buildReservationEmailBody(Reservation reservation, Guest guest) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        
        StringBuilder body = new StringBuilder();
        body.append("<!DOCTYPE html>");
        body.append("<html><head><style>");
        body.append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }");
        body.append(".container { max-width: 600px; margin: 0 auto; padding: 20px; }");
        body.append(".header { background-color: #0066cc; color: white; padding: 20px; text-align: center; }");
        body.append(".content { background-color: #f9f9f9; padding: 20px; }");
        body.append(".details { background-color: white; padding: 15px; margin: 10px 0; border-left: 4px solid #0066cc; }");
        body.append(".footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }");
        body.append("</style></head><body>");
        
        body.append("<div class='container'>");
        body.append("<div class='header'>");
        body.append("<h1>🏨 Ocean View Resort</h1>");
        body.append("<h2>Reservation Confirmation</h2>");
        body.append("</div>");
        
        body.append("<div class='content'>");
        body.append("<p>Dear ").append(guest.getFirstName()).append(" ").append(guest.getLastName()).append(",</p>");
        body.append("<p>Thank you for choosing Ocean View Resort! Your reservation has been confirmed.</p>");
        
        body.append("<div class='details'>");
        body.append("<h3>Reservation Details</h3>");
        body.append("<p><strong>Reservation Number:</strong> ").append(reservation.getReservationNumber()).append("</p>");
        body.append("<p><strong>Guest Name:</strong> ").append(guest.getFirstName()).append(" ").append(guest.getLastName()).append("</p>");
        body.append("<p><strong>Check-in Date:</strong> ").append(reservation.getCheckInDate().format(formatter)).append("</p>");
        body.append("<p><strong>Check-out Date:</strong> ").append(reservation.getCheckOutDate().format(formatter)).append("</p>");
        body.append("<p><strong>Number of Nights:</strong> ").append(reservation.getNumberOfNights()).append("</p>");
        body.append("<p><strong>Number of Guests:</strong> ").append(reservation.getNumberOfGuests()).append("</p>");
        body.append("<p><strong>Total Cost:</strong> $").append(String.format("%.2f", reservation.getTotalCost())).append("</p>");
        if (reservation.getSpecialRequests() != null && !reservation.getSpecialRequests().trim().isEmpty()) {
            body.append("<p><strong>Special Requests:</strong> ").append(reservation.getSpecialRequests()).append("</p>");
        }
        body.append("</div>");
        
        body.append("<div class='details'>");
        body.append("<h3>Contact Information</h3>");
        body.append("<p><strong>Phone:</strong> ").append(guest.getPhoneNumber()).append("</p>");
        body.append("<p><strong>Email:</strong> ").append(guest.getEmail()).append("</p>");
        body.append("</div>");
        
        body.append("<p>We look forward to welcoming you to Ocean View Resort!</p>");
        body.append("<p>If you have any questions or need to modify your reservation, ");
        body.append("please contact us or reference your reservation number.</p>");
        body.append("</div>");
        
        body.append("<div class='footer'>");
        body.append("<p>Ocean View Resort | 123 Beach Galle | Phone: (555) 123-4567</p>");
        body.append("<p>This is an automated message. Please do not reply to this email.</p>");
        body.append("</div>");
        
        body.append("</div>");
        body.append("</body></html>");
        
        return body.toString();
    }
}
