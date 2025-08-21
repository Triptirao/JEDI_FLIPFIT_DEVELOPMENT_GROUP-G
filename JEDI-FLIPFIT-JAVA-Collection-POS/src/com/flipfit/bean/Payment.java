package com.flipfit.bean;

import java.time.LocalDateTime;

public class Payment {

    private String paymentId;
    private double amount;
    private LocalDateTime paymentDate;
    private String customerId;
    private String gymId;
    private String slotId;
    private String transactionStatus; // e.g., "Success", "Failed", "Pending"

    public Payment() {
        // Default constructor for creating an empty Payment object.
    }

    public Payment(String paymentId, double amount, LocalDateTime paymentDate,
                   String customerId, String gymId, String slotId, String transactionStatus) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.customerId = customerId;
        this.gymId = gymId;
        this.slotId = slotId;
        this.transactionStatus = transactionStatus;
    }

    // Getters and Setters

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
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

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }
}