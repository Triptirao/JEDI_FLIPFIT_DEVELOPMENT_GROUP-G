package com.flipfit.bean;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * The Booking class represents a single booking made by a customer for a gym slot.
 * It contains details such as the booking ID, customer ID, gym ID, slot ID, and booking status.
 *
 * @author
 */
public class Booking {

    /**
     * The unique identifier for the booking.
     */
    private int bookingId;

    /**
     * The unique identifier of the customer who made the booking.
     */
    private int customerId;

    /**
     * The unique identifier of the gym center for which the booking is made.
     */
    private int gymId;

    /**
     * The unique identifier of the specific slot being booked within the gym.
     */
    private int slotId;

    /**
     * The status of the booking (e.g., "BOOKED", "CANCELLED").
     */
    private String bookingStatus;

    /**
     * The date on which the gym slot is booked.
     */
    private LocalDate bookingDate;

    /**
     * The time at which the booking was made.
     */
    private LocalDateTime dateAndTimeOfBooking;

    /**
     * Constructs a new Booking object with a system-generated booking time.
     * This constructor is typically used when a new booking is created.
     *
     * @param customerId    The ID of the customer.
     * @param centreId      The ID of the gym center.
     * @param slotId        The ID of the booked slot.
     * @param booked        The initial status of the booking (e.g., "BOOKED").
     * @param date          The date of the booking.
     * @param now           The date and time when the booking was created.
     */
    public Booking(int customerId, int centreId, int slotId, String booked, LocalDate date, LocalDateTime now) {
        this.customerId = customerId;
        this.gymId = centreId;
        this.slotId = slotId;
        this.bookingStatus = booked;
        this.bookingDate = date;
        this.dateAndTimeOfBooking = now;
    }

    /**
     * Constructs a new Booking object with all details, including a pre-existing booking ID.
     * This constructor is typically used when retrieving a booking from a database.
     *
     * @param bookingId      The unique ID of the booking.
     * @param customerId     The ID of the customer.
     * @param gymId          The ID of the gym center.
     * @param slotId         The ID of the booked slot.
     * @param bookingStatus  The status of the booking.
     * @param date           The date of the booking.
     * @param now            The date and time when the booking was created.
     */
    public Booking(int bookingId, int customerId, int gymId, int slotId, String bookingStatus, LocalDate date, LocalDateTime now) {
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.gymId = gymId;
        this.slotId = slotId;
        this.bookingStatus = bookingStatus;
        this.bookingDate = date;
        this.dateAndTimeOfBooking = now;
    }

    /**
     * Gets the unique identifier of the booking.
     *
     * @return The booking ID.
     */
    public int getBookingId() {
        return bookingId;
    }

    /**
     * Sets the unique identifier of the booking.
     *
     * @param bookingId The new booking ID.
     */
    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    /**
     * Gets the customer's unique identifier.
     *
     * @return The customer ID.
     */
    public int getCustomerId() {
        return customerId;
    }

    /**
     * Sets the customer's unique identifier.
     *
     * @param customerId The new customer ID.
     */
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    /**
     * Gets the gym's unique identifier.
     *
     * @return The gym ID.
     */
    public int getGymId() {
        return gymId;
    }

    /**
     * Sets the gym's unique identifier.
     *
     * @param gymId The new gym ID.
     */
    public void setGymId(int gymId) {
        this.gymId = gymId;
    }

    /**
     * Gets the slot's unique identifier.
     *
     * @return The slot ID.
     */
    public int getSlotId() {
        return slotId;
    }

    /**
     * Sets the slot's unique identifier.
     *
     * @param slotId The new slot ID.
     */
    public void setSlotId(int slotId) {
        this.slotId = slotId;
    }

    /**
     * Gets the current status of the booking.
     *
     * @return The booking status.
     */
    public String getBookingStatus() {
        return bookingStatus;
    }

    /**
     * Sets the status of the booking.
     *
     * @param bookingStatus The new booking status.
     */
    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    /**
     * Gets the date on which the booking is scheduled.
     *
     * @return The booking date.
     */
    public LocalDate getBookingDate() {
        return bookingDate;
    }

    /**
     * Sets the date for the booking.
     *
     * @param bookingDate The new booking date.
     */
    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    /**
     * Gets the date and time when the booking was created.
     *
     * @return The booking date and time.
     */
    public LocalDateTime getDateAndTimeOfBooking() {
        return dateAndTimeOfBooking;
    }

    /**
     * Sets the date and time when the booking was created.
     *
     * @param dateAndTimeOfBooking The new booking date and time.
     */
    public void setDateAndTimeOfBooking(LocalDateTime dateAndTimeOfBooking) {
        this.dateAndTimeOfBooking = dateAndTimeOfBooking;
    }
}