package com.app.model;

import java.time.LocalDate;

public class Bill {
    private int billId;
    private String billNumber;
    private int reservationId;
    private String reservationNumber;
    private String guestName;
    private String guestEmail;
    private String guestPhone;
    private String guestAddress;
    private String roomNumber;
    private String roomType;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int numberOfNights;
    private double pricePerNight;
    private double roomCharges;
    private double taxAmount;
    private double serviceCharge;
    private double totalAmount;
    private LocalDate billDate;
    private String generatedBy;

    // Full constructor
    public Bill(int billId, String billNumber, int reservationId, String reservationNumber,
                String guestName, String guestEmail, String guestPhone, String guestAddress,
                String roomNumber, String roomType, LocalDate checkInDate, LocalDate checkOutDate,
                int numberOfNights, double pricePerNight, double roomCharges, double taxAmount,
                double serviceCharge, double totalAmount, LocalDate billDate, String generatedBy) {
        this.billId = billId;
        this.billNumber = billNumber;
        this.reservationId = reservationId;
        this.reservationNumber = reservationNumber;
        this.guestName = guestName;
        this.guestEmail = guestEmail;
        this.guestPhone = guestPhone;
        this.guestAddress = guestAddress;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numberOfNights = numberOfNights;
        this.pricePerNight = pricePerNight;
        this.roomCharges = roomCharges;
        this.taxAmount = taxAmount;
        this.serviceCharge = serviceCharge;
        this.totalAmount = totalAmount;
        this.billDate = billDate;
        this.generatedBy = generatedBy;
    }

    // Constructor without billId (for new bills)
    public Bill(String billNumber, int reservationId, String reservationNumber,
                String guestName, String guestEmail, String guestPhone, String guestAddress,
                String roomNumber, String roomType, LocalDate checkInDate, LocalDate checkOutDate,
                int numberOfNights, double pricePerNight, double roomCharges, double taxAmount,
                double serviceCharge, double totalAmount, LocalDate billDate, String generatedBy) {
        this.billNumber = billNumber;
        this.reservationId = reservationId;
        this.reservationNumber = reservationNumber;
        this.guestName = guestName;
        this.guestEmail = guestEmail;
        this.guestPhone = guestPhone;
        this.guestAddress = guestAddress;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numberOfNights = numberOfNights;
        this.pricePerNight = pricePerNight;
        this.roomCharges = roomCharges;
        this.taxAmount = taxAmount;
        this.serviceCharge = serviceCharge;
        this.totalAmount = totalAmount;
        this.billDate = billDate;
        this.generatedBy = generatedBy;
    }

    // Getters and Setters
    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public String getReservationNumber() {
        return reservationNumber;
    }

    public void setReservationNumber(String reservationNumber) {
        this.reservationNumber = reservationNumber;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getGuestEmail() {
        return guestEmail;
    }

    public void setGuestEmail(String guestEmail) {
        this.guestEmail = guestEmail;
    }

    public String getGuestPhone() {
        return guestPhone;
    }

    public void setGuestPhone(String guestPhone) {
        this.guestPhone = guestPhone;
    }

    public String getGuestAddress() {
        return guestAddress;
    }

    public void setGuestAddress(String guestAddress) {
        this.guestAddress = guestAddress;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public int getNumberOfNights() {
        return numberOfNights;
    }

    public void setNumberOfNights(int numberOfNights) {
        this.numberOfNights = numberOfNights;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public double getRoomCharges() {
        return roomCharges;
    }

    public void setRoomCharges(double roomCharges) {
        this.roomCharges = roomCharges;
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public double getServiceCharge() {
        return serviceCharge;
    }

    public void setServiceCharge(double serviceCharge) {
        this.serviceCharge = serviceCharge;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDate getBillDate() {
        return billDate;
    }

    public void setBillDate(LocalDate billDate) {
        this.billDate = billDate;
    }

    public String getGeneratedBy() {
        return generatedBy;
    }

    public void setGeneratedBy(String generatedBy) {
        this.generatedBy = generatedBy;
    }

    @Override
    public String toString() {
        return "Bill{" +
                "billId=" + billId +
                ", billNumber='" + billNumber + '\'' +
                ", reservationId=" + reservationId +
                ", reservationNumber='" + reservationNumber + '\'' +
                ", guestName='" + guestName + '\'' +
                ", totalAmount=" + totalAmount +
                ", billDate=" + billDate +
                '}';
    }
}
