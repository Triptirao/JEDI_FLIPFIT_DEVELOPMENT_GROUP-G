package com.flipfit.bean;

/**
 * The GymCentre class represents a gym center managed by a gym owner in the FlipFit application.
 * It holds details about the gym, including its location, capacity, and approval status.
 *
 * @author
 */
public class GymCentre {

    /**
     * The unique identifier for the gym center.
     */
    private int centreId;

    /**
     * The ID of the gym owner who owns this center.
     */
    private int ownerId;

    /**
     * The name of the gym center.
     */
    private String centreName;

    /**
     * A string representation of the available slots for this gym.
     * This may represent a serialized list or a simple description.
     */
    private String slots;

    /**
     * The maximum number of people the gym can accommodate at one time.
     */
    private int capacity;

    /**
     * The cost of booking one slot of the gym.
     */
    private int cost;

    /**
     * The approval status of the gym center.
     * 'true' if the admin has approved the center, 'false' otherwise.
     */
    private boolean approved;

    /**
     * The city where the gym center is located.
     */
    private String city;

    /**
     * The state where the gym center is located.
     */
    private String state;

    /**
     * The postal code for the gym's location.
     */
    private String pincode;

    /**
     * A string containing a list of facilities offered at the gym.
     */
    private String facilities;

    public GymCentre() {}

    /**
     * Constructs a new GymCentre object with all details.
     * This constructor is used when retrieving a gym center record from the database.
     *
     * @param centreId   The unique ID of the gym center.
     * @param ownerId    The ID of the owner.
     * @param centreName The name of the gym.
     * @param slots      The slots available at the gym.
     * @param capacity   The capacity of the gym.
     * @param cost       The cost of a slot.
     * @param approved   The approval status.
     * @param city       The city of the gym.
     * @param state      The state of the gym.
     * @param pincode    The pincode of the gym's location.
     * @param facilities The list of facilities.
     */
    public GymCentre(int centreId, int ownerId, String centreName, String slots, int capacity, int cost, boolean approved, String city, String state, String pincode, String facilities) {
        this.centreId = centreId;
        this.ownerId = ownerId;
        this.centreName = centreName;
        this.slots = slots;
        this.capacity = capacity;
        this.cost = cost;
        this.approved = approved;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
        this.facilities = facilities;
    }

    /**
     * Constructs a new GymCentre object for a new gym that does not yet have a generated ID.
     * This constructor is typically used before a gym is saved to the database for the first time.
     *
     * @param ownerId    The ID of the owner.
     * @param centreName The name of the gym.
     * @param slots      The slots available at the gym.
     * @param capacity   The capacity of the gym.
     * @param cost       The cost of a slot.
     * @param approved   The approval status.
     * @param city       The city of the gym.
     * @param state      The state of the gym.
     * @param pincode    The pincode of the gym's location.
     * @param facilities The list of facilities.
     */
    public GymCentre(int ownerId, String centreName, String slots, int capacity, int cost, boolean approved, String city, String state, String pincode, String facilities) {
        this.ownerId = ownerId;
        this.centreName = centreName;
        this.slots = slots;
        this.capacity = capacity;
        this.cost = cost;
        this.approved = approved;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
        this.facilities = facilities;
    }

    // Getters and Setters with JavaDoc comments

    /**
     * Retrieves the unique ID of the gym center.
     *
     * @return The gym center's ID.
     */
    public int getCentreId() {
        return centreId;
    }

    /**
     * Sets the unique ID for the gym center.
     *
     * @param centreId The new gym center ID.
     */
    public void setCentreId(int centreId) {
        this.centreId = centreId;
    }

    /**
     * Retrieves the ID of the gym owner.
     *
     * @return The owner's ID.
     */
    public int getOwnerId() {
        return ownerId;
    }

    /**
     * Sets the ID of the gym owner.
     *
     * @param ownerId The new owner ID.
     */
    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    /**
     * Retrieves the name of the gym center.
     *
     * @return The gym center's name.
     */
    public String getCentreName() {
        return centreName;
    }

    /**
     * Sets the name of the gym center.
     *
     * @param centreName The new gym center name.
     */
    public void setCentreName(String centreName) {
        this.centreName = centreName;
    }

    /**
     * Retrieves the string representation of available slots.
     *
     * @return The slots string.
     */
    public String getSlots() {
        return slots;
    }

    /**
     * Sets the string representation of available slots.
     *
     * @param slots The new slots string.
     */
    public void setSlots(String slots) {
        this.slots = slots;
    }

    /**
     * Retrieves the capacity of the gym center.
     *
     * @return The capacity.
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Sets the capacity of the gym center.
     *
     * @param capacity The new capacity.
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    /**
     * Retrieves the cost of one slot.
     *
     * @return The cost.
     */
    public int getCost() {return  cost;}

    /**
     * Sets the cost of one slot.
     *
     * @param cost The new cost.
     */
    public void setCost(int cost) {this.cost = cost;}

    /**
     * Checks if the gym center has been approved by an admin.
     *
     * @return 'true' if approved, 'false' otherwise.
     */
    public boolean isApproved() {
        return approved;
    }

    /**
     * Sets the approval status of the gym center.
     *
     * @param approved The new approval status.
     */
    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    /**
     * Retrieves the city where the gym is located.
     *
     * @return The city name.
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the city for the gym's location.
     *
     * @param city The new city.
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Retrieves the state where the gym is located.
     *
     * @return The state name.
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the state for the gym's location.
     *
     * @param state The new state.
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Retrieves the pincode for the gym's location.
     *
     * @return The pincode.
     */
    public String getPincode() {
        return pincode;
    }

    /**
     * Sets the pincode for the gym's location.
     *
     * @param pincode The new pincode.
     */
    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    /**
     * Retrieves the list of facilities offered at the gym.
     *
     * @return The facilities string.
     */
    public String getFacilities() {
        return facilities;
    }

    /**
     * Sets the list of facilities for the gym.
     *
     * @param facilities The new facilities string.
     */
    public void setFacilities(String facilities) {
        this.facilities = facilities;
    }
}