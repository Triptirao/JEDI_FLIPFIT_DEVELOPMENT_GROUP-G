package com.flipfit.business;

import com.flipfit.dao.CustomerDAO;
import com.flipfit.dao.GymOwnerDAO;
import java.util.List;

public class CustomerService {

    private CustomerDAO customerDao;
    private GymOwnerDAO gymOwnerDao;

    public CustomerService() {
        this.customerDao = new CustomerDAO();
        this.gymOwnerDao = new GymOwnerDAO();
    }

    public List<String[]> viewBookedSlots(String customerId) {
        System.out.println("Fetching your booked slots...");
        return customerDao.getBookingsByCustomerId(customerId);
    }

    public List<String[]> viewCenters() {
        System.out.println("Fetching all available gym centers...");
        return gymOwnerDao.getGymsByOwnerId(null); // Returning all gyms for simplicity
    }

    public void makePayments(int paymentType, String paymentInfo) {
        // Business logic for processing a payment.
        System.out.println("Processing payment of type: " + paymentType + " with info: " + paymentInfo);
    }

    public void editDetails() {
        // Business logic to update the customer's profile details.
        System.out.println("Allowing customer to edit their details...");
    }
}
