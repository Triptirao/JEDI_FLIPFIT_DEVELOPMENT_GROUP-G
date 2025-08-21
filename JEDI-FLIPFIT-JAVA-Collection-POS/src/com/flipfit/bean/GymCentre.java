package com.flipfit.bean;
import java.util.ArrayList;
import java.util.List;

public class GymCentre {

    private int centreId;
    private int ownerId;
    private String name;
    private List<String> slots;
    private int capacity;
    private boolean approved;
    private String city;
    private String state;
    private String pincode;
    private List<String> facilities;
    public GymCentre() {
    }

    public GymCentre(int centreId, int ownerId, String name, List<String> slots, int capacity, boolean approved, String city, String state, String pincode, List<String> facilities) {
        this.centreId = centreId;
        this.ownerId = ownerId;
        this.name = name;
        this.slots = slots;
        this.capacity = capacity;
        this.approved = approved;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
        this.facilities = facilities;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getSlots() {
        return slots;
    }

    public void setSlots(List<String> slots) {
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

    public List<String> getFacilities() {
        return facilities;
    }

    public void setFacilities(List<String> facilities) {
        this.facilities = facilities;
    }
}