package com.flipfit.bean;

import java.time.LocalDateTime;

public class Waitlist {

    private String waitlistId;
    private String customerId;
    private String gymId;
    private String slotId;
    private LocalDateTime requestDateTime;

    public Waitlist() {
        // Default constructor
    }

    public Waitlist(String waitlistId, String customerId, String gymId, String slotId, LocalDateTime requestDateTime) {
        this.waitlistId = waitlistId;
        this.customerId = customerId;
        this.gymId = gymId;
        this.slotId = slotId;
        this.requestDateTime = requestDateTime;
    }

    // Getters and Setters

    public String getWaitlistId() {
        return waitlistId;
    }

    public void setWaitlistId(String waitlistId) {
        this.waitlistId = waitlistId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getGymId() {
        return gymId;
    }

    public void setGymId(String gymId) {
        this.gymId = gymId;
    }

    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public LocalDateTime getRequestDateTime() {
        return requestDateTime;
    }

    public void setRequestDateTime(LocalDateTime requestDateTime) {
        this.requestDateTime = requestDateTime;
    }
}