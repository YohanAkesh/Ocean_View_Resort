package com.app.service;

import com.app.model.Guest;
import com.app.model.Reservation;

public interface IEmailService {

    boolean sendReservationConfirmation(Reservation reservation, Guest guest);

    boolean sendEmail(String to, String subject, String body);
}
