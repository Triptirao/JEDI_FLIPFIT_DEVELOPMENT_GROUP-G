package com.flipfit.bean;

public class GymOwner {
    private int userId;
    private String pan;
    private String aadhaar;
    private String gst;
    private boolean isApproved; // Added to match a common database field for gym owners

    // Constructor for creating a new GymOwner (used during registration)
    public GymOwner(int userId, String pan, String aadhaar, String gst) {
        this.userId = userId;
        this.pan = pan;
        this.aadhaar = aadhaar;
        this.gst = gst;
        this.isApproved = false; // New owners are not approved by default
    }

    // Constructor for retrieving a GymOwner from the database
    public GymOwner(int userId, String pan, String aadhaar, String gst, boolean isApproved) {
        this.userId = userId;
        this.pan = pan;
        this.aadhaar = aadhaar;
        this.gst = gst;
        this.isApproved = isApproved;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }
}