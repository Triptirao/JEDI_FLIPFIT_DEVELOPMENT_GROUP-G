package com.flipfit.bean;

public class GymOwner extends User {
    private String pan;
    private String aadhaar;
    private String gst;

    public GymOwner(String role, int userId, String fullName, String email, String password, long userPhone, String city, int pinCode, String pan, String aadhaar, String gst) {
        super(role, userId, fullName, email, password, userPhone, city, pinCode);
        this.pan = pan;
        this.aadhaar = aadhaar;
        this.gst = gst;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getAadhaar() {
        return aadhaar;
    }

    public void setAadhaar(String aadhaar) {
        this.aadhaar = aadhaar;
    }

    public String getGst() {
        return gst;
    }

    public void setGst(String gst) {
        this.gst = gst;
    }
}
