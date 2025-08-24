package com.flipfit.bean;

/**
 * The GymOwner class represents a gym owner user in the FlipFit system.
 * It contains specific details for a gym owner, such as their unique user ID
 * and business-related identification numbers like PAN, Aadhaar, and GST.
 *
 * @author
 */
public class GymOwner {

    /**
     * The unique identifier for the gym owner, which links to the User table.
     */
    private int userId;

    /**
     * The Permanent Account Number (PAN) of the gym owner.
     */
    private String pan;

    /**
     * The Aadhaar number of the gym owner, a unique Indian identification number.
     */
    private String aadhaar;

    /**
     * The Goods and Services Tax Identification Number (GSTIN) of the gym.
     */
    private String gst;

    /**
     * The approval status of the gym owner's profile.
     * 'true' if the admin has approved the owner, 'false' otherwise.
     */
    private boolean isApproved;

    public GymOwner() {

    }

    /**
     * Constructs a new GymOwner object. This constructor is used during the registration
     * process for a new gym owner, where the profile is not yet approved.
     *
     * @param userId  The unique ID of the user.
     * @param pan     The PAN number of the gym owner.
     * @param aadhaar The Aadhaar number of the gym owner.
     * @param gst     The GST number of the gym.
     */
    public GymOwner(int userId, String pan, String aadhaar, String gst) {
        this.userId = userId;
        this.pan = pan;
        this.aadhaar = aadhaar;
        this.gst = gst;
        this.isApproved = false; // New owners are not approved by default
    }

    /**
     * Constructs a GymOwner object with a pre-existing approval status.
     * This constructor is typically used when retrieving an existing gym owner's
     * details from the database.
     *
     * @param userId     The unique ID of the user.
     * @param pan        The PAN number of the gym owner.
     * @param aadhaar    The Aadhaar number of the gym owner.
     * @param gst        The GST number of the gym.
     * @param isApproved The approval status of the owner.
     */
    public GymOwner(int userId, String pan, String aadhaar, String gst, boolean isApproved) {
        this.userId = userId;
        this.pan = pan;
        this.aadhaar = aadhaar;
        this.gst = gst;
        this.isApproved = isApproved;
    }

    /**
     * Retrieves the unique user ID of the gym owner.
     *
     * @return The user ID.
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Sets the unique user ID for the gym owner.
     *
     * @param userId The new user ID.
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Retrieves the PAN number of the gym owner.
     *
     * @return The PAN number.
     */
    public String getPan() {
        return pan;
    }

    /**
     * Sets the PAN number for the gym owner.
     *
     * @param pan The new PAN number.
     */
    public void setPan(String pan) {
        this.pan = pan;
    }

    /**
     * Retrieves the Aadhaar number of the gym owner.
     *
     * @return The Aadhaar number.
     */
    public String getAadhaar() {
        return aadhaar;
    }

    /**
     * Sets the Aadhaar number for the gym owner.
     *
     * @param aadhaar The new Aadhaar number.
     */
    public void setAadhaar(String aadhaar) {
        this.aadhaar = aadhaar;
    }

    /**
     * Retrieves the GST number of the gym.
     *
     * @return The GST number.
     */
    public String getGst() {
        return gst;
    }

    /**
     * Sets the GST number for the gym.
     *
     * @param gst The new GST number.
     */
    public void setGst(String gst) {
        this.gst = gst;
    }

    /**
     * Checks if the gym owner's profile has been approved.
     *
     * @return 'true' if approved, 'false' otherwise.
     */
    public boolean isApproved() {
        return isApproved;
    }

    /**
     * Sets the approval status for the gym owner.
     *
     * @param approved The new approval status.
     */
    public void setApproved(boolean approved) {
        isApproved = approved;
    }

}