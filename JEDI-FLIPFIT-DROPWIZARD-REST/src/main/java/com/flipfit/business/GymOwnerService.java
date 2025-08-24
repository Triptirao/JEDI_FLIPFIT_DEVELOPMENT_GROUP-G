package com.flipfit.business;

import com.flipfit.bean.Booking;
import com.flipfit.bean.Customer;
import com.flipfit.bean.GymCentre;
import com.flipfit.bean.GymOwner;
import com.flipfit.bean.User;
import com.flipfit.bean.Payment;
import com.flipfit.bean.Slot;
import com.flipfit.dao.CustomerDAO;
import com.flipfit.dao.GymOwnerDAO;
import com.flipfit.dao.UserDAO;
import com.flipfit.exception.MismatchinputException;
import com.flipfit.exception.MissingValueException;
import com.flipfit.exception.AccessDeniedException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The GymOwnerService class provides the business logic for all operations
 * a gym owner can perform.
 */
public class GymOwnerService {

    private final GymOwnerDAO gymOwnerDao;
    private final UserDAO userDao;
    private final CustomerDAO customerDao;

    public GymOwnerService(UserDAO userDao, CustomerDAO customerDao, GymOwnerDAO gymOwnerDao) {
        this.gymOwnerDao = gymOwnerDao;
        this.userDao = userDao;
        this.customerDao = customerDao;
    }

    /**
     * Adds a new gym center to the system and its corresponding slots.
     * @param gymData A GymCentre object containing the details of the new gym center.
     */
    public void addCentre(GymCentre gymData) {
        // The service logic is now to call the DAO method and handle errors.
        int gymId = gymOwnerDao.addGym(gymData);
        if (gymId != -1) {
            Slot newSlot = new Slot(gymId, gymData.getCapacity());
            gymOwnerDao.addSlots(newSlot);
        } else {
            throw new MismatchinputException("Gym registration failed due to an internal error.");
        }
    }

    /**
     * Retrieves the details of all gym centers owned by a specific gym owner.
     * @param ownerId The unique ID of the gym owner.
     * @return A list of GymCentre objects.
     */
    public List<GymCentre> viewGymDetails(int ownerId) {
        return gymOwnerDao.getGymsByOwnerId(ownerId);
    }

    /**
     * Retrieves all bookings for a specific gym owned by the given owner.
     * Includes a check to ensure the owner has permission to view the gym's bookings.
     * @param ownerId The ID of the gym owner.
     * @param gymId The ID of the gym.
     * @return A List of Booking objects for the specified gym.
     * @throws MismatchinputException if the owner does not own the gym.
     */
    public List<Booking> viewBookings(int ownerId, int gymId) throws MismatchinputException {
        // Validate that the gym belongs to the owner.
        if (!gymOwnerDao.validateGymId(ownerId, gymId)) {
            throw new MismatchinputException("Gym with ID " + gymId + " does not belong to owner with ID " + ownerId + ".");
        }
        return gymOwnerDao.getAllBookingsByGymId(gymId);
    }

    /**
     * Retrieves the list of all customers.
     * @return A list of User objects representing all customers.
     */
    public List<User> viewCustomers() {
        return userDao.getAllCustomers();
    }

    /**
     * Retrieves the payment history for all customers.
     * @return A list of Payment objects.
     */
    public List<Payment> viewPayments() {
        // This method should be implemented in the DAO to fetch payment data
        return gymOwnerDao.viewPayments();
    }

    /**
     * Updates the details for a gym owner. This method is designed to
     * work with a REST API, accepting a User and GymOwner object directly.
     * @param ownerId The ID of the gym owner whose details are to be updated.
     * @param updatedUser The User object with the new values.
     * @param updatedGymOwner The GymOwner object with the new values.
     * @return The updated GymOwner object.
     */
    public GymOwner editGymOwnerDetails(int ownerId, User updatedUser, GymOwner updatedGymOwner) {
        Optional<User> userOptional = userDao.getUserById(ownerId);
        Optional<GymOwner> gymOwnerOptional = gymOwnerDao.getGymOwnerById(ownerId);

        if (userOptional.isPresent() && gymOwnerOptional.isPresent()) {
            User user = userOptional.get();
            GymOwner gymOwner = gymOwnerOptional.get();

            // Update the User details based on the GymOwner object's fields
            if (updatedUser.getFullName() != null && !updatedUser.getFullName().isEmpty()) {
                user.setFullName(updatedUser.getFullName());
            }
            if (updatedUser.getEmail() != null && !updatedUser.getEmail().isEmpty()) {
                user.setEmail(updatedUser.getEmail());
            }
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                user.setPassword(updatedUser.getPassword());
            }
            if (updatedUser.getUserPhone() != 0) {
                user.setUserPhone(updatedUser.getUserPhone());
            }
            if (updatedUser.getCity() != null && !updatedUser.getCity().isEmpty()) {
                user.setCity(updatedUser.getCity());
            }
            if (updatedUser.getPinCode() != 0) {
                user.setPinCode(updatedUser.getPinCode());
            }

            // Update the GymOwner specific details
            if (updatedGymOwner.getPan() != null && !updatedGymOwner.getPan().isEmpty()) {
                gymOwner.setPan(updatedGymOwner.getPan());
            }
            if (updatedGymOwner.getAadhaar() != null && !updatedGymOwner.getAadhaar().isEmpty()) {
                gymOwner.setAadhaar(updatedGymOwner.getAadhaar());
            }
            if (updatedGymOwner.getGst() != null && !updatedGymOwner.getGst().isEmpty()) {
                gymOwner.setGst(updatedGymOwner.getGst());
            }

            userDao.updateUser(user);
            gymOwnerDao.updateGymOwnerDetails(gymOwner);

            // Create and return a new GymOwner object that combines the updated User and GymOwner data
            GymOwner finalUpdatedOwner = new GymOwner(
                    user.getUserId(),
                    gymOwner.getPan(),
                    gymOwner.getAadhaar(),
                    gymOwner.getGst(),
                    gymOwner.isApproved()
            );

            return finalUpdatedOwner;
        } else {
            throw new MissingValueException("Error: User or Gym Owner not found.");
        }

    }
}
