package com.flipfit.bean;

/**
 * The Customer class represents a customer user in the FlipFit system.
 * It contains details specific to a customer, such as their user ID and payment information.
 * This class is linked to the User class through the userId.
 *
 * @author
 */
public class Customer {

    /**
     * The unique identifier for the customer, which links to the User table.
     */
    private int userId;

    /**
     * An integer representing the type of payment method (e.g., 1 for credit card, 2 for UPI).
     */
    private int paymentType;

    /**
     * The details associated with the payment method (e.g., card number, UPI ID).
     */
    private String paymentInfo;

    /**
     * Constructs a new Customer object for a newly registered customer.
     *
     * @param userId The unique ID of the user.
     * @param paymentType The type of payment method.
     * @param paymentInfo The details of the payment method.
     */
    public Customer(int userId, int paymentType, String paymentInfo) {
        this.userId = userId;
        this.paymentType = paymentType;
        this.paymentInfo = paymentInfo;
    }

    /**
     * Retrieves the unique identifier of the user associated with this customer record.
     *
     * @return The user's ID.
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Sets the unique identifier for the user.
     *
     * @param userId The new user ID.
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Retrieves the integer code for the payment type.
     *
     * @return The payment type.
     */
    public int getPaymentType() {
        return paymentType;
    }

    /**
     * Sets the integer code for the payment type.
     *
     * @param paymentType The new payment type.
     */
    public void setPaymentType(int paymentType) {
        this.paymentType = paymentType;
    }

    /**
     * Retrieves the payment information string.
     *
     * @return The payment information.
     */
    public String getPaymentInfo() {
        return paymentInfo;
    }

    /**
     * Sets the payment information string.
     *
     * @param paymentInfo The new payment information.
     */
    public void setPaymentInfo(String paymentInfo) {
        this.paymentInfo = paymentInfo;
    }
}