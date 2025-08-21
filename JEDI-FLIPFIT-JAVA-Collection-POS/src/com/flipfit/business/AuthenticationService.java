package com.flipfit.business;

import com.flipfit.dao.UserDAO;

public class AuthenticationService {

    private UserDAO userDao;

    public AuthenticationService() {
        this.userDao = this.userDao;
    }

    public AuthenticationService(UserDAO userDao) {
        this.userDao = userDao;
    }

    public String[] login(String email, String password) {
        // Authenticate user by checking against all users in the DAO
        for (String[] user : userDao.getAllUsers()) {
            if (user[3].equals(email) && user[4].equals(password)) {
                return user;
            }
        }
        return null;
    }

    public void registerCustomer(String name, String email, String password, String phone) {
        // Here you would generate a unique ID for the new customer
        String newId = String.valueOf(userDao.getAllUsers().size() + 1);
        String[] newCustomer = {"CUSTOMER", newId, name, email, password, phone, "N/A", "N/A"};
        userDao.getAllUsers().add(newCustomer);
        System.out.println("Customer registration received for " + name);
    }

    public void registerGymOwner(String name, String email, String password, String phone, String aadhaar, String pan, String gst) {
        // Here you would generate a unique ID for the new gym owner
        String newId = String.valueOf(userDao.getAllUsers().size() + 1);
        String[] newOwner = {"OWNER", newId, name, email, password, phone, "N/A", "N/A", pan, aadhaar, gst, "false"};
        userDao.getAllUsers().add(newOwner);
        System.out.println("Gym owner registration received for " + name);
    }
}
