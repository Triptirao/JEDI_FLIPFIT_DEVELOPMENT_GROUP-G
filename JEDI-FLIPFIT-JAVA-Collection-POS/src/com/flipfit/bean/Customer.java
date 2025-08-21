package com.flipfit.bean;

public class Customer extends User {
    private int paymentType;
    private String paymentInfo;

    public Customer(String role, int userId, String fullName, String email, String password, long userPhone, String city, int pinCode, int paymentType, String paymentInfo) {
        super(role, userId, fullName, email, password, userPhone, city, pinCode);
        this.paymentType = paymentType;
        this.paymentInfo = paymentInfo;
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
