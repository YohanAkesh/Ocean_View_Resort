package com.app.service;

import com.app.model.Guest;
import com.app.model.Reservation;

public interface IEmailService {
    /**
     * Send reservation confirmation email to guest
     * @param reservation The reservation details
     * @param guest The guest information
     * @return true if email sent successfully, false otherwise
     */
    boolean sendReservationConfirmation(Reservation reservation, Guest guest);
    
    /**
     * Send generic email
     * @param to Recipient email address
     * @param subject Email subject
     * @param body Email body content
     * @return true if email sent successfully, false otherwise
     */
    boolean sendEmail(String to, String subject, String body);
}
