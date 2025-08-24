package com.flipfit.bean;

/**
 * The {@code Role} class represents the role of a user within the FlipFit system.
 * It's a simple **Plain Old Java Object (POJO)** that holds a single string value
 * representing the user's role (e.g., "Customer", "Gym Owner", "Admin").
 * <p>
 * This class includes a parameterized constructor and standard getter and setter
 * methods for the {@code role} field.
 * </p>
 *
 * @author YourName
 * @version 1.0
 * @since 2025-08-22
 */
public class Role {

    /**
     * The role of the user, such as "Customer", "Gym Owner", or "Admin".
     */
    private String role;

    /**
     * Constructs a new {@code Role} object with the specified role.
     *
     * @param role The role string to be assigned to this object.
     */
    public Role(String role) {
        this.role = role;
    }

    /**
     * Retrieves the role of the user.
     *
     * @return The role string.
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the role of the user.
     *
     * @param role The new role string to set.
     */
    public void setRole(String role) {
        this.role = role;
    }
}