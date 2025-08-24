package com.flipfit.business;

import com.flipfit.bean.GymCentre;
import com.flipfit.bean.User;

import java.util.List;

/**
 * The AdminInterface defines the contract for all administrative operations.
 * Any class that implements this interface must provide the functionality
 * to manage gym centers, gym owners, and customers.
 *
 * @author
 */
public interface adminInterface {

    /**
     * Approves a pending gym center request.
     * @param gymId The ID of the gym center to approve.
     */
    void approveGymRequest(int gymId);

    /**
     * Approves a pending gym owner registration request.
     * @param email The email of the gym owner to approve.
     */
    void approveGymOwnerRequest(String email);

    /**
     * Fetches and displays a list of all pending gym requests.
     *
     * @return
     */
    List<GymCentre> viewPendingGyms();

    /**
     * Fetches and displays a list of all pending gym owner requests.
     *
     * @return
     */
    List<User> viewPendingGymOwners();

    /**
     * Fetches and displays a list of all approved and registered gyms.
     *
     * @return
     */
    List<GymCentre> viewAllGyms();

    /**
     * Fetches and displays a list of all approved and registered gym owners.
     *
     * @return
     */
    List<User> viewAllGymOwners();

    /**
     * Fetches and displays a list of all registered customers.
     *
     * @return
     */
    List<User> viewAllCustomers();

    /**
     * Deletes a user from the system by their ID.
     * @param userId The ID of the user to delete.
     */
    void deleteUserById(int userId);

    /**
     * Deletes a gym from the system by its ID.
     * @param gymId The ID of the gym to delete.
     */
    void deleteGymById(int gymId);
}
