package com.flipfit.bean;

public class Admin extends Role {

    private int adminId;
    private String emailId;
    private String password;

    public Admin(String role, int adminId, String emailId, String password) {
        super(role);
        this.adminId = adminId;
        this.emailId = emailId;
        this.password = password;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
