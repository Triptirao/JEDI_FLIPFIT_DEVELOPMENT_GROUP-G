package com.flipfit.bean;

public class Admin extends User {

    public Admin(int id, String role, int userId, String fullName, String email, String password, long userPhone, String city, int pinCode) {
        super(id, role, userId, fullName, email, password, userPhone, city, pinCode);
    }
}
