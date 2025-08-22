package com.flipfit.bean;

public class GymCentre {
    private int centreId;
    private int ownerId;
    private String centreName;
    private String slots;
    private int capacity;
    private boolean approved;
    private String city;
    private String state;
    private String pincode;
    private String facilities;

    public GymCentre(int centreId, int ownerId, String centreName, String slots, int capacity, boolean approved, String city, String state, String pincode, String facilities) {
        this.centreId = centreId;
        this.ownerId = ownerId;
        this.centreName = centreName;
        this.slots = slots;
        this.capacity = capacity;
        this.approved = approved;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
        this.facilities = facilities;
    }

    // You may also need a constructor for creating new gyms, which don't have an ID yet
    public GymCentre(int ownerId, String centreName, String slots, int capacity, boolean approved, String city, String state, String pincode, String facilities) {
        this.ownerId = ownerId;
        this.centreName = centreName;
        this.slots = slots;
        this.capacity = capacity;
        this.approved = approved;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
        this.facilities = facilities;
    }

    // Add getters and setters for all fields
    // ...
    public int getCentreId() {
        return centreId;
    }

    public void setCentreId(int centreId) {
        this.centreId = centreId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getCentreName() {
        return centreName;
    }

    public void setCentreName(String centreName) {
        this.centreName = centreName;
    }

    public String getSlots() {
        return slots;
    }

    public void setSlots(String slots) {
        this.slots = slots;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getFacilities() {
        return facilities;
    }

    public void setFacilities(String facilities) {
        this.facilities = facilities;
    }
}