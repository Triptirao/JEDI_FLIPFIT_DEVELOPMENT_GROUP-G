package com.flipfit.bean;

import java.time.LocalDate;
import java.time.LocalTime;

public class Booking {
    private int bookingId;
    private int customerId;
    private int gymId;
    private int slotId;
    private String bookingStatus;
    private LocalDate bookingDate;
    private LocalTime bookingTime;

    public Booking() {
    }

    public Booking(int bookingId, int customerId, int gymId, int slotId, String bookingStatus, LocalDate bookingDate, LocalTime bookingTime) {
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.gymId = gymId;
        this.slotId = slotId;
        this.bookingStatus = bookingStatus;
        this.bookingDate = bookingDate;
        this.bookingTime = bookingTime;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getGymId() {
        return gymId;
    }

    public void setGymId(int gymId) {
        this.gymId = gymId;
    }

    public int getSlotId() {
        return slotId;
    }

    public void setSlotId(int slotId) {
        this.slotId = slotId;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public LocalTime getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(LocalTime bookingTime) {
        this.bookingTime = bookingTime;
    }
}