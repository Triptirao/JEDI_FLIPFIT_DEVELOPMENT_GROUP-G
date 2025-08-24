package com.flipfit.bean;

/**
 * The Admin class represents an administrator user in the FlipFit system.
 * It extends the Role class to inherit role-based properties and adds
 * specific attributes for an admin, such as admin ID, email, and password.
 *
 * @author
 */
public class Admin extends Role {

    /**
     * The unique identifier for the admin.
     */
    private int adminId;

    /**
     * The email address of the admin, used for login.
     */
    private String emailId;

    /**
     * The password for the admin's account.
     */
    private String password;

    /**
     * Constructs a new Admin object with the specified details.
     *
     * @param role      The role of the user, which is "Admin".
     * @param adminId   The unique ID of the admin.
     * @param emailId   The email address of the admin.
     * @param password  The password for the admin's account.
     */
    public Admin(String role, int adminId, String emailId, String password) {
        super(role);
        this.adminId = adminId;
        this.emailId = emailId;
        this.password = password;
    }

    /**
     * Retrieves the unique ID of the admin.
     *
     * @return The admin's ID.
     */
    public int getAdminId() {
        return adminId;
    }

    /**
     * Sets the unique ID for the admin.
     *
     * @param adminId The new admin ID.
     */
    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    /**
     * Retrieves the email address of the admin.
     *
     * @return The admin's email ID.
     */
    public String getEmailId() {
        return emailId;
    }

    /**
     * Sets the email address for the admin.
     *
     * @param emailId The new email ID.
     */
    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    /**
     * Retrieves the password of the admin's account.
     *
     * @return The admin's password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password for the admin's account.
     *
     * @param password The new password.
     */
    public void setPassword(String password) {
        this.password = password;
    }
}