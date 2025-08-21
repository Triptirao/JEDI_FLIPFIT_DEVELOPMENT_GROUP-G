package com.flipfit.business;

import com.flipfit.dao.CustomerDAO;
import com.flipfit.dao.GymOwnerDAO;
import com.flipfit.dao.UserDAO;
import com.flipfit.dao.GymCentreDAO;

import java.util.List;

public class CustomerService {

    private CustomerDAO customerDao;
    private GymOwnerDAO gymOwnerDao;
    private UserDAO userDao;
    private CustomerService customerService;
    private GymCentreDAO gymCentreDao;

    public CustomerService(CustomerDAO customerDao, UserDAO userDao, GymOwnerDAO gymOwnerDao, GymCentreDAO gymCentreDao) {
        this.customerDao = customerDao;
        this.userDao = userDao;
        this.gymOwnerDao = gymOwnerDao;
        this.gymCentreDao = gymCentreDao;
    }

    public List<String[]> viewBookedSlots(String customerId) {
        System.out.println("Fetching your booked slots...");
        return customerDao.getBookingsByCustomerId(customerId);
    }

    public List<String[]> viewCenters() {
        System.out.println("Fetching all available gym centers...");
        return gymCentreDao.getAllGyms();
    }

    public void makePayments(int paymentType, String paymentInfo) {
        System.out.println("Processing payment of type: " + paymentType + " with info: " + paymentInfo);
    }

    public void editCustomerDetails(String customerId, int choice, String newValue) {
        userDao.updateUserDetails(customerId, choice, newValue);
        System.out.println("Customer details updated successfully.");
    }
}
