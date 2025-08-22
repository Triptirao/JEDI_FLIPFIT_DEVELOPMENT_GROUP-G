package com.flipfit.bean;

public class User {
    private int userId;
    private String fullName;
    private String email;
    private String password;
    private long userPhone;
    private String city;
    private int pinCode;
    private String role; // Field to hold the role after it's determined from the database

    // Constructor for creating a new User object (before insertion into the database)
    public User(String fullName, String email, String password, long userPhone, String city, int pinCode) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.userPhone = userPhone;
        this.city = city;
        this.pinCode = pinCode;
    }

    // Constructor for creating a User object from a database record
    public User(int userId, String fullName, String email, String password, long userPhone, String city, int pinCode) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.userPhone = userPhone;
        this.city = city;
        this.pinCode = pinCode;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(long userPhone) {
        this.userPhone = userPhone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getPinCode() {
        return pinCode;
    }

    public void setPinCode(int pinCode) {
        this.pinCode = pinCode;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}