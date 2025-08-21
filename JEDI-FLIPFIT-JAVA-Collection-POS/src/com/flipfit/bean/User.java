package com.flipfit.bean;

public class User extends Role {
    private int userId;
    private String fullName;
    private String email;
    private String password;
    private long userPhone;
    private String city;
    private int pinCode;

    public User(String role, int userId, String fullName, String email, String password, long userPhone, String city, int pinCode) {
        super(role);
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

    public String getName() {
        return fullName;
    }

    public void setName(String fullName) {
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
}
