package com.flipfit.bean;

public class Customer {
    private int userId;
    private int paymentType;
    private String paymentInfo;

    // Constructor for creating a new Customer record during registration
    public Customer(int userId, int paymentType, String paymentInfo) {
        this.userId = userId;
        this.paymentType = paymentType;
        this.paymentInfo = paymentInfo;
    }


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(int paymentType) {
        this.paymentType = paymentType;
    }

    public String getPaymentInfo() {
        return paymentInfo;
    }

    public void setPaymentInfo(String paymentInfo) {
        this.paymentInfo = paymentInfo;
    }
}