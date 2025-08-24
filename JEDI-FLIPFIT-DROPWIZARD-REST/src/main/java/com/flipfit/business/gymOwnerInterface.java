package com.flipfit.business;

import com.flipfit.bean.GymCentre;
import com.flipfit.bean.User;

import java.util.List;
import java.util.Scanner;

/**
 * The GymOwnerInterface defines the contract for all gym owner-related operations.
 * Any class that implements this interface must provide the functionality
 * to manage gym centers, view customer information, and edit owner details.
 *
 * @author
 */
public interface gymOwnerInterface {

    /**
     * Adds a new gym center to the system.
     * @param gymData A GymCentre object containing the details of the new gym center.
     */
    void addCentre(GymCentre gymData);

    /**
     * Retrieves and displays the details of all gym centers owned by a specific gym owner.
     * @param ownerId The unique ID of the gym owner.
     */
    void viewGymDetails(int ownerId);

    /**
     * Retrieves and displays the list of customers for the gym owner's centers.
     */
    void viewCustomers();

    /**
     * Retrieves and displays the payment history for the gym owner.
     */
    void viewPayments();

    /**
     * Updates a specific detail for the gym owner.
     * @param ownerId The ID of the gym owner whose details are to be updated.
     * @param choice An integer representing the detail to be updated (e.g., 1 for name, 2 for email).
     * @param newValue The new value for the selected detail.
     */
    void editGymOwnerDetails(int ownerId, int choice, String newValue);

    /**
     * Displays the main menu for a logged-in gym owner and handles user input.
     * @param loggedInOwnerId The ID of the currently logged-in gym owner.
     */
    void displayGymOwnerMenu(int loggedInOwnerId);
}
