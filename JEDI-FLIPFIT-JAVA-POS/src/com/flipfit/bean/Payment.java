package com.flipfit.bean;

import java.time.LocalDateTime;

/**
 * The {@code Payment} class represents a payment transaction in the FlipFit system.
 * It encapsulates details about a payment, including its unique ID, amount, date,
 * and associated customer, gym, and slot IDs.
 * <p>
 * This class is a **Plain Old Java Object (POJO)** and is designed to hold data.
 * It includes a default constructor and a parameterized constructor, along with
 * standard getter and setter methods for all its fields.
 * </p>
 *
 * @author YourName
 * @version 1.0
 * @since 2025-08-22
 */
public class Payment {

    /**
     * The unique identifier for the payment transaction.
     */
    private String paymentId;

    /**
     * The amount of the payment.
     */
    private double amount;

    /**
     * The date and time when the payment was made.
     */
    private LocalDateTime paymentDate;

    /**
     * The unique identifier of the customer who made the payment.
     */
    private String customerId;

    /**
     * The unique identifier of the gym for which the payment was made.
     */
    private String gymId;

    /**
     * The unique identifier of the slot for which the payment was made.
     */
    private String slotId;

    /**
     * The status of the payment transaction (e.g., "Success", "Failed", "Pending").
     */
    private String transactionStatus;

    /**
     * Constructs a new, empty {@code Payment} object.
     * This constructor is useful for creating an instance before setting its properties.
     */
    public Payment() {
        // Default constructor for creating an empty Payment object.
    }

    /**
     * Constructs a new {@code Payment} object with all its properties initialized.
     *
     * @param paymentId         The unique identifier for the payment.
     * @param amount            The amount of the payment.
     * @param paymentDate       The date and time of the payment.
     * @param customerId        The ID of the customer.
     * @param gymId             The ID of the gym.
     * @param slotId            The ID of the slot.
     * @param transactionStatus The status of the transaction.
     */
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

    /**
     * Gets the unique identifier of the payment.
     *
     * @return The payment ID.
     */
    public String getPaymentId() {
        return paymentId;
    }

    /**
     * Sets the unique identifier of the payment.
     *
     * @param paymentId The payment ID to set.
     */
    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    /**
     * Gets the amount of the payment.
     *
     * @return The payment amount.
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Sets the amount of the payment.
     *
     * @param amount The payment amount to set.
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     * Gets the date and time when the payment was made.
     *
     * @return The payment date and time.
     */
    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    /**
     * Sets the date and time of the payment.
     *
     * @param paymentDate The payment date and time to set.
     */
    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    /**
     * Gets the unique identifier of the customer who made the payment.
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
     * Gets the unique identifier of the gym for which the payment was made.
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
     * Gets the unique identifier of the slot for which the payment was made.
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
     * Gets the status of the payment transaction.
     *
     * @return The transaction status (e.g., "Success", "Failed", "Pending").
     */
    public String getTransactionStatus() {
        return transactionStatus;
    }

    /**
     * Sets the status of the payment transaction.
     *
     * @param transactionStatus The transaction status to set.
     */
    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }
}