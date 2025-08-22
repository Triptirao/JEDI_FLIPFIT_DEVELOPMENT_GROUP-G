package com.flipfit.bean;

import java.time.LocalDateTime;

/**
 * The {@code Waitlist} class represents a customer's request to be placed on a waitlist
 * for a fully booked gym slot. It's a **Plain Old Java Object (POJO)** that captures
 * details about a waitlist entry, including the customer and the specific gym slot.
 *
 * @author YourName
 * @version 1.0
 * @since 2025-08-22
 */
public class Waitlist {

    /**
     * The unique identifier for the waitlist entry.
     */
    private String waitlistId;

    /**
     * The unique identifier of the customer who joined the waitlist.
     */
    private String customerId;

    /**
     * The unique identifier of the gym for the requested slot.
     */
    private String gymId;

    /**
     * The unique identifier of the fully booked slot that the customer is waiting for.
     */
    private String slotId;

    /**
     * The date and time when the customer's waitlist request was made.
     */
    private LocalDateTime requestDateTime;

    /**
     * Constructs a new, empty {@code Waitlist} object.
     * This constructor is used for creating an instance before setting its properties.
     */
    public Waitlist() {
        // Default constructor
    }

    /**
     * Constructs a new {@code Waitlist} object with all its properties initialized.
     *
     * @param waitlistId      The unique identifier for the waitlist entry.
     * @param customerId      The ID of the customer.
     * @param gymId           The ID of the gym.
     * @param slotId          The ID of the slot.
     * @param requestDateTime The date and time of the request.
     */
    public Waitlist(String waitlistId, String customerId, String gymId, String slotId, LocalDateTime requestDateTime) {
        this.waitlistId = waitlistId;
        this.customerId = customerId;
        this.gymId = gymId;
        this.slotId = slotId;
        this.requestDateTime = requestDateTime;
    }

    // Getters and Setters

    /**
     * Retrieves the unique identifier of the waitlist entry.
     *
     * @return The waitlist ID.
     */
    public String getWaitlistId() {
        return waitlistId;
    }

    /**
     * Sets the unique identifier of the waitlist entry.
     *
     * @param waitlistId The waitlist ID to set.
     */
    public void setWaitlistId(String waitlistId) {
        this.waitlistId = waitlistId;
    }

    /**
     * Retrieves the unique identifier of the customer.
     *
     * @return The customer ID.
     */
    public String getCustomerId() {
        return customerId;
    }

    /**
     * Sets the unique identifier of the customer.
     *
     * @param customerId The customer ID to set.
     */
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    /**
     * Retrieves the unique identifier of the gym.
     *
     * @return The gym ID.
     */
    public String getGymId() {
        return gymId;
    }

    /**
     * Sets the unique identifier of the gym.
     *
     * @param gymId The gym ID to set.
     */
    public void setGymId(String gymId) {
        this.gymId = gymId;
    }

    /**
     * Retrieves the unique identifier of the slot.
     *
     * @return The slot ID.
     */
    public String getSlotId() {
        return slotId;
    }

    /**
     * Sets the unique identifier of the slot.
     *
     * @param slotId The slot ID to set.
     */
    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    /**
     * Retrieves the date and time of the waitlist request.
     *
     * @return The request date and time.
     */
    public LocalDateTime getRequestDateTime() {
        return requestDateTime;
    }

    /**
     * Sets the date and time of the waitlist request.
     *
     * @param requestDateTime The request date and time to set.
     */
    public void setRequestDateTime(LocalDateTime requestDateTime) {
        this.requestDateTime = requestDateTime;
    }
}